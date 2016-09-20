package com.gustavok.peach;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gustavok.peach.tabs.senators.SenatorsArrayAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class SenatorsManager implements SenatorsCallbackInterface {
    private static final int VOTE_POSITION_YES = 0;
    private static final int VOTE_POSITION_NO = 1;
    private static final int VOTE_POSITION_ABSTINENT = 2;
    private static final int VOTE_POSITION_ABSENCE = 3;
    private static final int VOTE_POSITION_NONE = 4;
    private static final int VOTE_POSITION_UNKNOWN = 5;
    private static final String TAG = "SenatorsManager";
    private static final SenatorsManager INSTANCE = new SenatorsManager();
    private static SenatorDbHelper mDbHelper;
    private final List<Senator> senators = new ArrayList<>();
    private SenatorsArrayAdapter senatorsArrayAdapter;
    private View votingView;
    private Context context;
    private RelativeLayout loadingLayout;
    private ProgressBar progressBar;

    private SenatorsManager() {
    }

    public static SenatorsManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void onProgress(long bytesWritten, long totalSize) {
        Log.d(TAG, String.format(Locale.getDefault(), "Received %d bytes from total %d", bytesWritten, totalSize));
        if (loadingLayout.getVisibility() == View.VISIBLE) {
            progressBar.setProgress((int) ((bytesWritten / totalSize) * 100));
        }
    }

    @Override
    public void onSuccess(Senator[] senators) {
        Log.d(TAG, String.format(Locale.getDefault(), "Received %d senators", senators.length));
        this.senators.clear();
        this.senators.addAll(Arrays.asList(senators));
        this.senatorsArrayAdapter.clear();
        for (Senator s : this.senators) {
            this.senatorsArrayAdapter.add(s);
        }
        updateVotes();
        loadingLayout.setVisibility(View.GONE);
    }

    public void init() {
        mDbHelper = new SenatorDbHelper(context);
        if (dbExists()) {
            loadSenatorsFromDb();
        }
        Log.d(TAG, "Calling REST...");
        RestClient.getSenatorsList(this);
    }

    public void setLoadingViews(RelativeLayout loadingLayout, ProgressBar progressBar) {
        this.loadingLayout = loadingLayout;
        this.progressBar = progressBar;
    }

    private void insertSenator(Senator senator) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(SenatorDbHelper.SenatorEntry.COLUMN_NAME_ID, senator.getId());
        values.put(SenatorDbHelper.SenatorEntry.COLUMN_NAME_NAME, senator.getNome());
        values.put(SenatorDbHelper.SenatorEntry.COLUMN_NAME_PARTY, senator.getPartido());
        values.put(SenatorDbHelper.SenatorEntry.COLUMN_NAME_STATE, senator.getEstado());
        values.put(SenatorDbHelper.SenatorEntry.COLUMN_NAME_VOTE1, senator.getVoto());
        values.put(SenatorDbHelper.SenatorEntry.COLUMN_NAME_VOTE2, senator.getVoto2());
        values.put(SenatorDbHelper.SenatorEntry.COLUMN_NAME_URL, senator.getUrl());

        // Insert the new row, returning the primary key value of the new row
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.insertWithOnConflict(SenatorDbHelper.SenatorEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    private boolean dbExists() {
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String query = "SELECT count(*) FROM " + SenatorDbHelper.SenatorEntry.TABLE_NAME;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            Log.d(TAG, "Rows found in DB: " + count);
            return (count > 0);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void setVotingView(View votingView) {
        this.votingView = votingView;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<Senator> setArrayAdapter(SenatorsArrayAdapter senatorsArrayAdapter) {
        Log.d(TAG, "Initializing array adapter with " + senatorsArrayAdapter.getCount() + " senators");
        this.senatorsArrayAdapter = senatorsArrayAdapter;
        this.senatorsArrayAdapter.notifyDataSetChanged();
        return senators;
    }

    private void loadSenatorsFromDb() {
        int id;
        int vote1;
        int vote2;
        String name;
        String party;
        String state;
        String url;
        SQLiteDatabase sqliteDatabase = null;
        Cursor cursor = null;
        try {
            sqliteDatabase = mDbHelper.getReadableDatabase();
            cursor = sqliteDatabase.query(SenatorDbHelper.SenatorEntry.TABLE_NAME, null, null, null, null, null, null);
            while (cursor.moveToNext()) {
                id = cursor.getInt(cursor.getColumnIndex(SenatorDbHelper.SenatorEntry.COLUMN_NAME_ID));
                name = cursor.getString(cursor.getColumnIndex(SenatorDbHelper.SenatorEntry.COLUMN_NAME_NAME));
                party = cursor.getString(cursor.getColumnIndex(SenatorDbHelper.SenatorEntry.COLUMN_NAME_PARTY));
                state = cursor.getString(cursor.getColumnIndex(SenatorDbHelper.SenatorEntry.COLUMN_NAME_STATE));
                url = cursor.getString(cursor.getColumnIndex(SenatorDbHelper.SenatorEntry.COLUMN_NAME_URL));
                vote1 = cursor.getInt(cursor.getColumnIndex(SenatorDbHelper.SenatorEntry.COLUMN_NAME_VOTE1));
                vote2 = cursor.getInt(cursor.getColumnIndex(SenatorDbHelper.SenatorEntry.COLUMN_NAME_VOTE2));
                senators.add(new Senator(id, name, party, state, vote1, vote2, url));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (sqliteDatabase != null) {
                sqliteDatabase.close();
            }
        }
    }

    public void updateVotes() {
        Log.d(TAG, "Updating votes...");
        if (senatorsArrayAdapter != null) {
            Log.d(TAG, String.format("Updating senators list with %d senators", senatorsArrayAdapter.getCount()));
            senatorsArrayAdapter.notifyDataSetChanged();
        }

        if (votingView != null) {
            Log.d(TAG, "Updating voting view");
            int[] votes = countVotes(senators);
            int countYes = votes[VOTE_POSITION_YES];
            int countNo = votes[VOTE_POSITION_NO];
            int countAbstention = votes[VOTE_POSITION_ABSTINENT];
            int countAbsence = votes[VOTE_POSITION_ABSENCE];
            int countNone = votes[VOTE_POSITION_NONE];
            int countUnknown = votes[VOTE_POSITION_UNKNOWN];
            int total = countYes + countNo + countAbstention + countAbsence + countUnknown + countNone;
            Log.d(TAG, String.format("Updating senatorVotes to (%d) yes=%d; no=%d; abstention=%d; absence=%d; none=%d; unknown=%d",
                    total, countYes, countNo, countAbstention, countAbsence, countNone, countUnknown));

            TextView tvYes = (TextView) votingView.findViewById(R.id.voting_count_yes);
            TextView tvNo = (TextView) votingView.findViewById(R.id.voting_count_no);
            TextView tvAbstention = (TextView) votingView.findViewById(R.id.voting_count_abstention);
            TextView tvAbsence = (TextView) votingView.findViewById(R.id.voting_count_absence);
            tvYes.setText(String.format(Locale.getDefault(), "%d", countYes));
            tvNo.setText(String.format(Locale.getDefault(), "%d", countNo));
            tvAbstention.setText(String.format(Locale.getDefault(), "%d", countAbstention));
            tvAbsence.setText(String.format(Locale.getDefault(), "%d", countAbsence));

            TextView tvPercentYes = (TextView) votingView.findViewById(R.id.voting_percentage_yes);
            TextView tvPercentNo = (TextView) votingView.findViewById(R.id.voting_percentage_no);

            int totalValid = countYes + countNo + countAbstention + countAbsence;
            double percentYes = 0f;
            double percentNo = 0f;
            if (totalValid > 0) {
                percentYes = ((double) countYes / totalValid) * 100f;
                percentNo = ((double) countNo / totalValid) * 100f;
            }
            tvPercentYes.setText(context.getString(R.string.voting_percentage_text, percentYes));
            tvPercentNo.setText(context.getString(R.string.voting_percentage_text, percentNo));
        }
        for (Senator s : senators) {
            insertSenator(s);
        }
    }

    private int[] countVotes(List<Senator> senators) {
        int[] count = new int[6];
        for (Senator s : senators) {
            switch (s.getVoto2()) {
                case Constants.VOTE_YES:
                    ++count[VOTE_POSITION_YES];
                    break;
                case Constants.VOTE_NO:
                    ++count[VOTE_POSITION_NO];
                    break;
                case Constants.VOTE_ABSTENTION:
                    ++count[VOTE_POSITION_ABSTINENT];
                    break;
                case Constants.VOTE_ABSENCE:
                    ++count[VOTE_POSITION_ABSENCE];
                    break;
                case Constants.VOTE_NONE:
                    ++count[VOTE_POSITION_NONE];
                    break;
                default:
                    ++count[VOTE_POSITION_UNKNOWN];
                    break;
            }
        }
        return count;
    }

    public void updateVote(int id, int vote2) {
        for (Senator s : senators) {
            if (s.getId() == id) {
                s.setVoto2(vote2);
            }
        }
        String whereClause = String.format(Locale.getDefault(), "%s=%d", SenatorDbHelper.SenatorEntry.COLUMN_NAME_ID, id);
        ContentValues values = new ContentValues();
        values.put(SenatorDbHelper.SenatorEntry.COLUMN_NAME_VOTE2, vote2);
        SQLiteDatabase sqliteDatabase = mDbHelper.getWritableDatabase();
        int updated = sqliteDatabase.update(SenatorDbHelper.SenatorEntry.TABLE_NAME, values, whereClause, null);
        Log.d(TAG, "Updated " + updated + " rows");
    }

    public List<Senator> getAllSenators() {
        return this.senators;
    }

    public void updateVotesFromFirebase() {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SenatorsManager.getInstance().updateVotes();
            }
        });
    }
}

package com.gustavok.peach;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
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
    private static final int VOTE_POSITION_UNKNOWN = 4;
    private static final String TAG = "SenatorsManager";
    private static final SenatorsManager INSTANCE = new SenatorsManager();
    private final List<Senator> senators = new ArrayList<>();
    private SenatorsArrayAdapter senatorsArrayAdapter;
    private View votingView;
    private Context context;

    private SenatorsManager() {
    }

    public static SenatorsManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void onSuccess(Senator[] senators) {
        Log.d(TAG, String.format(Locale.getDefault(), "Received %d senators", senators.length));
        this.senators.clear();
        this.senators.addAll(Arrays.asList(senators));
        updateVotes();
        for (Senator s : senators) {
            insertSenator(s);
        }
    }

    public void init() {
        if (dbExists()) {
            loadSenatorsFromDb();
        } else {
            Log.d(TAG, "Calling REST...");
            RestClient.getSenatorsList(this);
        }
    }

    private long insertSenator(Senator senator) {
        SenatorDbHelper mDbHelper = new SenatorDbHelper(context);

        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(SenatorDbHelper.SenatorEntry.COLUMN_NAME_ID, senator.getId());
        values.put(SenatorDbHelper.SenatorEntry.COLUMN_NAME_NAME, senator.getNome());
        values.put(SenatorDbHelper.SenatorEntry.COLUMN_NAME_PARTY, senator.getPartido());
        values.put(SenatorDbHelper.SenatorEntry.COLUMN_NAME_STATE, senator.getEstado());
        values.put(SenatorDbHelper.SenatorEntry.COLUMN_NAME_VOTE, senator.getVoto());
        values.put(SenatorDbHelper.SenatorEntry.COLUMN_NAME_URL, senator.getUrl());

        // Insert the new row, returning the primary key value of the new row
        return db.insert(SenatorDbHelper.SenatorEntry.TABLE_NAME, null, values);
    }

    private boolean dbExists() {
        SenatorDbHelper mDbHelper = new SenatorDbHelper(context);

        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String query = "SELECT count(*) FROM " + SenatorDbHelper.SenatorEntry.TABLE_NAME;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            Log.d(TAG, "My count was: " + count);
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
        this.senatorsArrayAdapter = senatorsArrayAdapter;
        this.senatorsArrayAdapter.notifyDataSetChanged();
        return senators;
    }

    private void loadSenatorsFromDb() {
        int id;
        int vote;
        String name;
        String party;
        String state;
        String url;
        SQLiteDatabase sqliteDatabase = null;
        Cursor cursor = null;
        SenatorDbHelper dbHelper = new SenatorDbHelper(context);
        try {
            sqliteDatabase = dbHelper.getReadableDatabase();
            cursor = sqliteDatabase.query(SenatorDbHelper.SenatorEntry.TABLE_NAME, null, null, null, null, null, null);
            while (cursor.moveToNext()) {
                id = cursor.getInt(cursor.getColumnIndex(SenatorDbHelper.SenatorEntry.COLUMN_NAME_ID));
                name = cursor.getString(cursor.getColumnIndex(SenatorDbHelper.SenatorEntry.COLUMN_NAME_NAME));
                party = cursor.getString(cursor.getColumnIndex(SenatorDbHelper.SenatorEntry.COLUMN_NAME_PARTY));
                state = cursor.getString(cursor.getColumnIndex(SenatorDbHelper.SenatorEntry.COLUMN_NAME_STATE));
                url = cursor.getString(cursor.getColumnIndex(SenatorDbHelper.SenatorEntry.COLUMN_NAME_URL));
                vote = cursor.getInt(cursor.getColumnIndex(SenatorDbHelper.SenatorEntry.COLUMN_NAME_VOTE));
                senators.add(new Senator(id, name, party, state, vote, url));
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
            Log.d(TAG, "Updating senators list");
            senatorsArrayAdapter.notifyDataSetChanged();
        }

        if (votingView != null) {
            Log.d(TAG, "Updating voting view");
            int[] votes = countVotes(senators);
            int countYes = votes[VOTE_POSITION_YES];
            int countNo = votes[VOTE_POSITION_NO];
            int countAbstention = votes[VOTE_POSITION_ABSTINENT];
            int countAbsence = votes[VOTE_POSITION_ABSENCE];
            int countUnknown = votes[VOTE_POSITION_UNKNOWN];
            int total = countYes + countNo + countAbstention + countAbsence + countUnknown;

            TextView tvYes = (TextView) votingView.findViewById(R.id.voting_count_yes);
            TextView tvNo = (TextView) votingView.findViewById(R.id.voting_count_no);
            TextView tvAbstention = (TextView) votingView.findViewById(R.id.voting_count_abstention);
            TextView tvAbsence = (TextView) votingView.findViewById(R.id.voting_count_absence);
            Log.d(TAG, String.format("Updating senatorVotes to (%d) yes=%d; no=%d; abstention=%d; absence=%d; unknown=%d",
                    total, countYes, countNo, countAbstention, countAbsence, countUnknown));
            tvYes.setText(String.format(Locale.getDefault(), "%d", countYes));
            tvNo.setText(String.format(Locale.getDefault(), "%d", countNo));
            tvAbstention.setText(String.format(Locale.getDefault(), "%d", countAbstention));
            tvAbsence.setText(String.format(Locale.getDefault(), "%d", countAbsence));
        }
    }

    private int[] countVotes(List<Senator> senators) {
        int[] count = new int[5];
        for (Senator s : senators) {
            switch (s.getVoto()) {
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
                case Constants.VOTE_UNKNOWN:
                    ++count[VOTE_POSITION_UNKNOWN];
                    break;
            }
        }
        return count;
    }

    public void updateVote(int id, int vote) {
        for (Senator s : senators) {
            if (s.getId() == id) {
                s.setVoto(vote);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        senatorsArrayAdapter.notifyDataSetChanged();
                        updateVotes();
                    }
                });
            }
        }
        String whereClause = String.format(Locale.getDefault(), "%s=%d", SenatorDbHelper.SenatorEntry.COLUMN_NAME_ID, id);
        ContentValues values = new ContentValues();
        values.put(SenatorDbHelper.SenatorEntry.COLUMN_NAME_VOTE, vote);
        SenatorDbHelper dbHelper = new SenatorDbHelper(context);
        SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();
        int updated = sqliteDatabase.update(SenatorDbHelper.SenatorEntry.TABLE_NAME, values, whereClause, null);
        Log.d(TAG, "Updated " + updated + " rows");
    }
}

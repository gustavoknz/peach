package com.gustavok.peach;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gustavok.peach.tabs.senators.SenatorsArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class SenatorsManager implements SenatorsCallbackInterface {
    private static final int VOTE_POSITION_YES = 0;
    private static final int VOTE_POSITION_NO = 1;
    private static final int VOTE_POSITION_ABSTINENT = 2;
    private static final int VOTE_POSITION_ABSENCE = 3;
    private static final int VOTE_POSITION_UNKNOWN = 4;
    private static final String TAG = "SenatorsManager";
    private static final String JSON_FILE_NAME = "senators.json";
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
    public void onSuccess(Senator[] senatorVotes) {
        Log.d(TAG, String.format(Locale.getDefault(), "Received %d votes", senatorVotes.length));
        setSenatorVotes(senatorVotes);
        senatorsArrayAdapter.notifyDataSetChanged();
        updateVotes(senatorVotes);
    }

    public void init() {
        Log.d(TAG, "Loading from static JSON file");
        loadFromStaticJson();
        RestClient.getSenatorsList(this);
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

    public void setSenatorVotes(Senator[] senatorVotes) {
        for (Senator s1 : senators) {
            for (Senator s2 : senatorVotes) {
                if (s1.getId() == s2.getId()) {
                    s1.setVoto(s2.getVoto());
                    break;
                }
            }
        }
    }

    private void updateVotes(Senator[] senatorVotes) {
        Log.d(TAG, "Updating votes...");
        int[] votes = countVotes(senatorVotes);
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

        if (total >= Constants.TOTAL_VOTES) {
            buildNotification(String.format(Locale.getDefault(), context.getString(R.string.notification_message), countYes, countNo));
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setTitle(context.getString(R.string.dialog_title));
            alertDialogBuilder
                    .setCancelable(true)
                    .setNeutralButton(context.getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            if (countYes > countNo + countAbstention + countAbsence + countUnknown) {
                alertDialogBuilder.setMessage("Dilma será definitivamente afastada do cargo da presidência.");
            } else {
                alertDialogBuilder.setMessage("Este processo de impeachment será arquivado.");
            }
            //AlertDialog alertDialog = alertDialogBuilder.create();
            //alertDialog.show();
        }
    }

    private void buildNotification(String text) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_logo)
                        .setContentTitle(context.getResources().getString(R.string.app_name))
                        .setContentText(text);
        Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }

    private int[] countVotes(Senator[] senators) {
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

    //region Load info from static json
    private void loadFromStaticJson() {
        Log.d(TAG, "Loading senators from JSON");
        String jsonString = getJSONString();

        try {
            JSONArray jsonArray = new JSONObject(jsonString).getJSONArray("senadores");
            Senator[] senatorsArray = new Gson().fromJson(jsonArray.toString(), Senator[].class);
            Collections.addAll(senators, senatorsArray);
            Log.d(TAG, String.format("Got %d senators from JSON", senators.size()));
        } catch (JSONException e) {
            Log.e(TAG, "Error loading JSON", e);
        }
    }

    private String getJSONString() {
        String str = "";
        try {
            AssetManager assetManager = context.getAssets();
            InputStream in = assetManager.open(JSON_FILE_NAME);
            InputStreamReader isr = new InputStreamReader(in);
            char[] inputBuffer = new char[100];

            int charRead;
            while ((charRead = isr.read(inputBuffer)) > 0) {
                str += String.copyValueOf(inputBuffer, 0, charRead);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error loading JSON", e);
        }

        return str;
    }

    public String addVote(int id, int vote) {
        for (Senator s : senators) {
            if (s.getId() == id) {
                s.setVoto(vote);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        senatorsArrayAdapter.notifyDataSetChanged();
                        updateVotes(senators.toArray(new Senator[senators.size()]));
                    }
                });
                return s.getNome();
            }
        }
        return null;
    }
    //endregion
}

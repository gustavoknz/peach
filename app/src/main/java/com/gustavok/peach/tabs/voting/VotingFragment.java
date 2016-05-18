package com.gustavok.peach.tabs.voting;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gustavok.peach.MainActivity;
import com.gustavok.peach.R;
import com.gustavok.peach.RestClient;
import com.gustavok.peach.Senator;
import com.gustavok.peach.SenatorsCallbackInterface;
import com.gustavok.peach.SenatorsManager;
import com.gustavok.peach.VotingUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class VotingFragment extends Fragment implements SenatorsCallbackInterface {
    private static final int VOTE_POSITION_YES = 0;
    private static final int VOTE_POSITION_NO = 1;
    private static final int VOTE_POSITION_ABSTINENT = 2;
    private static final int VOTE_POSITION_ABSENCE = 3;
    private static final int VOTE_POSITION_UNKNOWN = 4;
    private static final int PULLING_INTERVAL = 15 * 1000;
    private static final String TAG = "VotingFragment";
    private View votingView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        votingView = inflater.inflate(R.layout.voting_layout, container, false);

        if (VotingUtils.isVotingGoingOn()) {
            final Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Searching for new votes...");

                    if (VotingUtils.isVotingGoingOn()) {
                        RestClient.getAllVotes(VotingFragment.this);
                        handler.postDelayed(this, PULLING_INTERVAL);
                    } else {
                        Log.d(TAG, "Voting finished");
                    }
                }
            };
            handler.postDelayed(runnable, 1);
        }

        return votingView;
    }

    @Override
    public void onSuccess(Senator[] senators) {
        Log.d(TAG, "Callback successfully called");
        SenatorsManager.getInstance().setSenatorVotes(senators);
        updateVotes(senators);
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
        Log.d(TAG, String.format("Updating senatorVotes to (%d) yes=%d; no=%d; abstention=%d; absence:%d; unknown:%d",
                total, countYes, countNo, countAbstention, countAbsence, countUnknown));
        tvYes.setText(String.format(Locale.getDefault(), "%d", countYes));
        tvNo.setText(String.format(Locale.getDefault(), "%d", countNo));
        tvAbstention.setText(String.format(Locale.getDefault(), "%d", countAbstention));
        tvAbsence.setText(String.format(Locale.getDefault(), "%d", countAbsence));

        if (total >= VotingUtils.TOTAL_VOTES) {
            buildNotification(String.format(Locale.getDefault(), "Votação encerrada! Sim: %d; Não: %d", countYes, countNo));
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
            alertDialogBuilder.setTitle("Votação encerrada");
            alertDialogBuilder
                    .setCancelable(true)
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            if (countYes > countNo + countAbstention + countAbsence + countUnknown) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, VotingUtils.REMOVED_DAYS);
                SimpleDateFormat sdfFinal = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
                String dateOut = sdfFinal.format(calendar.getTime());

                alertDialogBuilder.setMessage("Dilma será afastada do seu cargo e terá seu julgamento final até o dia " + dateOut);
            } else {
                alertDialogBuilder.setMessage("Este processo será arquivado.");
            }
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            VotingUtils.votingFinished();
        }
    }

    private void buildNotification(String text) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getContext())
                        .setSmallIcon(R.mipmap.ic_logo)
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setContentText(text);
        Intent resultIntent = new Intent(getContext(), MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }

    private int[] countVotes(Senator[] senators) {
        int[] count = new int[5];
        for (Senator s : senators) {
            if (s.getVoto() == VotingUtils.YES) {
                ++count[VOTE_POSITION_YES];
            } else if (s.getVoto() == VotingUtils.NO) {
                ++count[VOTE_POSITION_NO];
            } else if (s.getVoto() == VotingUtils.ABSTENTION) {
                ++count[VOTE_POSITION_ABSTINENT];
            } else if (s.getVoto() == VotingUtils.ABSENCE) {
                ++count[VOTE_POSITION_ABSENCE];
            } else if (s.getVoto() == VotingUtils.UNKNOWN) {
                ++count[VOTE_POSITION_UNKNOWN];
            }
        }
        return count;
    }
}

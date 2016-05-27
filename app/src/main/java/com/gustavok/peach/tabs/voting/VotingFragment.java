package com.gustavok.peach.tabs.voting;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.gustavok.peach.Senator;
import com.gustavok.peach.SenatorsCallbackInterface;
import com.gustavok.peach.SenatorsManager;
import com.gustavok.peach.Constants;

import java.util.Locale;

public class VotingFragment extends Fragment implements SenatorsCallbackInterface {
    private static final int VOTE_POSITION_YES = 0;
    private static final int VOTE_POSITION_NO = 1;
    private static final int VOTE_POSITION_ABSTINENT = 2;
    private static final int VOTE_POSITION_ABSENCE = 3;
    private static final int VOTE_POSITION_UNKNOWN = 4;
    private static final String TAG = "VotingFragment";
    private View votingView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        votingView = inflater.inflate(R.layout.voting_layout, container, false);
        SenatorsManager.getInstance().setVotingView(votingView);

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

        if (total >= Constants.TOTAL_VOTES) {
            buildNotification(String.format(Locale.getDefault(), getString(R.string.notification_message), countYes, countNo));
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
            alertDialogBuilder.setTitle(getString(R.string.dialog_title));
            alertDialogBuilder
                    .setCancelable(true)
                    .setNeutralButton(getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            if (countYes > countNo + countAbstention + countAbsence + countUnknown) {
                alertDialogBuilder.setMessage("Dilma será afastada do seu cargo e terá seu julgamento final em até 180 dias.");
            } else {
                alertDialogBuilder.setMessage("Este processo será arquivado.");
            }
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
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
}

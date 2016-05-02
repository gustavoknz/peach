package com.gustavok.peach;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VotingFragment extends Fragment {
    private static final int PULLING_INTERVAL = 5 * 1000;
    private static final String TAG = "VotingFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Handler handler = new Handler();
        final View votingView = inflater.inflate(R.layout.voting_layout, container, false);
        if (VotingSingleton.isVotingGoingOn()) {
            Runnable runnable = new Runnable() {
                public void run() {
                    updateVotes(RestClient.getAllVotes(), votingView);
                }
            };
            handler.postDelayed(runnable, PULLING_INTERVAL);
        }

        return votingView;
    }

    private void updateVotes(List<Senator> votes, View votingView) {
        int countYes = getCount(votes, Vote.YES);
        int countNo = getCount(votes, Vote.NO);
        int countAbstention = getCount(votes, Vote.ABSTENTION);
        int countAbsence = getCount(votes, Vote.ABSENCE);

        TextView tvYes = (TextView) votingView.findViewById(R.id.voting_count_yes);
        TextView tvNo = (TextView) votingView.findViewById(R.id.voting_count_no);
        //Vote.ABSTENTIONS
        //Vote.ABSENCES
        Log.d(TAG, String.format("Updating votes to yes=%d; no=%d; abstention=%d; absence:%d",
                countYes, countNo, countAbstention, countAbsence));
        tvYes.setText(countYes);
        tvNo.setText(countNo);

        if (countYes + countNo + countAbstention + countAbsence >= VotingSingleton.TOTAL_VOTES) {
            DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault());
            String dateOut = dateFormatter.format(new Date());

            //Dilma is out till dateOut


            /*Calendar c = Calendar.getInstance();
            c.add(Calendar.MONTH, VotingSingleton.REMOVED);
            SimpleDateFormat sdf = new SimpleDateFormat();
            sdf.format(c.getTime());*/
        }
    }

    private int getCount(List<Senator> votes, Vote vote) {
        int count = 0;
        for (Senator s : votes) {
            if (vote.toString().equals(s.getVoto())) {
                ++count;
            }
        }
        return count;
    }
}

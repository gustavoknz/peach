package com.gustavok.peach;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
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
                    RestResponse response = RestClient.getAllVotes();
                    String json = response.getResponse().toString();
                    Senator[] senatorsArray = new Gson().fromJson(json, Senator[].class);
                    List<Senator> senatorVotes = new ArrayList<>();
                    Collections.addAll(senatorVotes, senatorsArray);

                    updateVotes(senatorVotes, votingView);
                }
            };
            handler.postDelayed(runnable, PULLING_INTERVAL);
        }

        return votingView;
    }

    private void updateVotes(List<Senator> senatorVotes, View votingView) {
        int countYes = getCount(senatorVotes, VoteEnum.YES);
        int countNo = getCount(senatorVotes, VoteEnum.NO);
        int countAbstention = getCount(senatorVotes, VoteEnum.ABSTENTION);
        int countAbsence = getCount(senatorVotes, VoteEnum.ABSENCE);

        TextView tvYes = (TextView) votingView.findViewById(R.id.voting_count_yes);
        TextView tvNo = (TextView) votingView.findViewById(R.id.voting_count_no);
        //VoteEnum.ABSTENTIONS
        //VoteEnum.ABSENCES
        Log.d(TAG, String.format("Updating senatorVotes to yes=%d; no=%d; abstention=%d; absence:%d",
                countYes, countNo, countAbstention, countAbsence));
        tvYes.setText(countYes);
        tvNo.setText(countNo);

        if (countYes + countNo + countAbstention + countAbsence >= VotingSingleton.TOTAL_VOTES) {
            if (countYes > countNo) {
                DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault());
                String dateOut = dateFormatter.format(new Date());

                //Dilma is out till dateOut

            } else {
                // The process will be archived

            }



            /*Calendar c = Calendar.getInstance();
            c.add(Calendar.MONTH, VotingSingleton.REMOVED);
            SimpleDateFormat sdf = new SimpleDateFormat();
            sdf.format(c.getTime());*/
        }
    }

    private int getCount(List<Senator> votes, VoteEnum voteEnum) {
        int count = 0;
        for (Senator s : votes) {
            if (voteEnum.toString().equals(s.getVoto())) {
                ++count;
            }
        }
        return count;
    }
}

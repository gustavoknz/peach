package com.gustavok.peach;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class VotingFragment extends Fragment {
    private static final int PULLING_INTERVAL = 15 * 1000;
    private static final String TAG = "VotingFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View votingView = inflater.inflate(R.layout.voting_layout, container, false);
        //if (VotingSingleton.isVotingGoingOn()) {

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Searching for new votes...");
                Toast.makeText(getContext(), "checking...", Toast.LENGTH_SHORT).show();
                RestClient.getAllVotes(votingView, getContext());
                handler.postDelayed(this, PULLING_INTERVAL);
            }
        };
        handler.postDelayed(runnable, 1);
        //}

        return votingView;
    }
}

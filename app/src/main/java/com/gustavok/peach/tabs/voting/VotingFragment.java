package com.gustavok.peach.tabs.voting;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gustavok.peach.R;
import com.gustavok.peach.SenatorsManager;

public class VotingFragment extends Fragment {
    private static final String TAG = "VotingFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View votingView = inflater.inflate(R.layout.voting_layout, container, false);
        SenatorsManager.getInstance().setVotingView(votingView);
        SenatorsManager.getInstance().updateVotes();
        return votingView;
    }
}

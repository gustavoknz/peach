package com.gustavok.peach;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

//TODO: filtros
public class SenatorsListFragment extends ListFragment {
    private static final int UPDATE_LIST_INTERVAL = 10000;
    private static final String TAG = "SenatorsListFragment";
    private SenatorsArrayAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "getAllSenators is being called");
        List<Senator> senatorsList = SenatorsManager.getInstance().getVotes();
        Log.d(TAG, "getAllSenators called");

        View view = inflater.inflate(R.layout.senators_list_layout, container, false);
        Log.d(TAG, String.format("Building screen with %d senators", senatorsList.size()));

        adapter = new SenatorsArrayAdapter(getActivity(), senatorsList);
        ListView listView = (ListView) view.findViewById(android.R.id.list);
        listView.setAdapter(adapter);

        if (VotingUtils.isVotingGoingOn()) {
            final Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Updating adapter...");

                    if (VotingUtils.isVotingGoingOn()) {
                        adapter.notifyDataSetChanged();
                        handler.postDelayed(this, UPDATE_LIST_INTERVAL);
                    } else {
                        Log.d(TAG, "Voting ended. No need to update");
                    }
                }
            };
            handler.postDelayed(runnable, UPDATE_LIST_INTERVAL);
        }

        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Senator item = adapter.getItem(position);
        Log.i(TAG, String.format("Senator (%s) clicked. position: %d; id: %d", item.getNome(), position, id));

        LayoutInflater factory = LayoutInflater.from(v.getContext());

        RelativeLayout dialogView = (RelativeLayout) factory.inflate(R.layout.dialog_view, null);

        TextView nameView = (TextView) dialogView.findViewById(R.id.dialog_senator_name);
        nameView.setText(item.getNome());
        ImageView imageView = (ImageView) dialogView.findViewById(R.id.dialog_senator_image);
        Picasso.with(getContext()).load(item.getUrl()).into(imageView);
        TextView statePartyView = (TextView) dialogView.findViewById(R.id.dialog_senator_state_party);
        statePartyView.setText(String.format(Locale.getDefault(), "%s-%s", item.getPartido(), item.getEstado()));
        TextView voteView = (TextView) dialogView.findViewById(R.id.dialog_senator_vote);
        if (item.getVoto() == VotingUtils.YES) {
            voteView.setText(R.string.dialog_voting_yes);
        } else if (item.getVoto() == VotingUtils.NO) {
            voteView.setText(R.string.dialog_voting_no);
        } else if (item.getVoto() == VotingUtils.ABSTENTION) {
            voteView.setText(R.string.dialog_voting_abstention);
        } else if (item.getVoto() == VotingUtils.ABSENCE) {
            voteView.setText(R.string.dialog_voting_absence);
        } else if (item.getVoto() == VotingUtils.DEFAULT_VALUE) {
            voteView.setText(R.string.dialog_voting_not_yet);
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder
                .setCancelable(true)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        // TODO: not working
        final Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
        positiveButtonLL.gravity = Gravity.CENTER;
        positiveButton.setLayoutParams(positiveButtonLL);
    }
}

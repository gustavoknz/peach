package com.gustavok.peach;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SenatorsListFragment extends ListFragment {
    private static final int UPDATE_LIST_INTERVAL = 10000;
    private static final String TAG = "SenatorsListFragment";

    private SenatorsArrayAdapter adapter;
    private Spinner spinnerParty;
    private Spinner spinnerState;
    private Spinner spinnerVote;
    private List<Senator> senatorsList;
    private List<Senator> originalSenatorsList;

    private String constraintParty;
    private String constraintState;
    private int constraintVote;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "getAllSenators is being called");
        senatorsList = SenatorsManager.getInstance().getVotes();
        originalSenatorsList = new ArrayList<>(senatorsList);
        View view = inflater.inflate(R.layout.senators_list_layout, container, false);

        adapter = new SenatorsArrayAdapter(getActivity(), R.layout.senator_item_layout, senatorsList);
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

        spinnerParty = (Spinner) view.findViewById(R.id.spinner_parties);
        spinnerState = (Spinner) view.findViewById(R.id.spinner_states);
        spinnerVote = (Spinner) view.findViewById(R.id.spinner_votes);

        spinnerParty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedParty = (String) spinnerParty.getSelectedItem();
                Log.d(TAG, String.format("Selected position: %d; id: %d; party=%s", position, id, selectedParty));

                if (position == 0) {
                    constraintParty = null;
                } else {
                    constraintParty = selectedParty;
                }
                updateList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "No party selected");
            }
        });
        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedState = (String) spinnerState.getSelectedItem();
                Log.d(TAG, String.format("Selected position: %d; id: %d; state=%s", position, id, selectedState));

                if (position == 0) {
                    constraintState = null;
                } else {
                    constraintState = selectedState;
                }
                updateList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "No state selected");
            }
        });
        spinnerVote.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedVote = (String) spinnerVote.getSelectedItem();
                Log.d(TAG, String.format("Selected position: %d; id: %d; vote=%s", position, id, selectedVote));

                switch (position) {
                    case 0:
                        constraintVote = VotingUtils.CONSTRAINT_ALL;
                        break;
                    case 1:
                        constraintVote = VotingUtils.CONSTRAINT_NO_VOTE;
                        break;
                    case 2:
                        constraintVote = VotingUtils.CONSTRAINT_YES;
                        break;
                    case 3:
                        constraintVote = VotingUtils.CONSTRAINT_NO;
                        break;
                    case 4:
                        constraintVote = VotingUtils.CONSTRAINT_ABSTENTION;
                        break;
                    case 5:
                        constraintVote = VotingUtils.CONSTRAINT_ABSENCE;
                        break;
                }
                updateList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "No vote selected");
            }
        });

        return view;
    }

    private void updateList() {
        Log.d(TAG, String.format(Locale.getDefault(), "FILTERING party=%s; state=%s; vote=%s",
                constraintParty, constraintState, constraintVote));
        Log.d(TAG, String.format(Locale.getDefault(), "Starting filter with %d senators", originalSenatorsList.size()));
        senatorsList.clear();
        senatorsList.addAll(originalSenatorsList);

        for (Senator senator : originalSenatorsList) {
            if (constraintParty != null) {
                if (!constraintParty.equals(senator.getPartido())) {
                    senatorsList.remove(senator);
                }
            }
            if (constraintState != null) {
                if (!constraintState.equals(senator.getEstado())) {
                    senatorsList.remove(senator);
                }
            }
            switch (constraintVote) {
                case VotingUtils.CONSTRAINT_ALL:
                    break;
                case VotingUtils.CONSTRAINT_NO_VOTE:
                    if (senator.getVoto() != VotingUtils.DEFAULT_VALUE) {
                        senatorsList.remove(senator);
                    }
                    break;
                case VotingUtils.CONSTRAINT_YES:
                    if (senator.getVoto() != VotingUtils.YES) {
                        senatorsList.remove(senator);
                    }
                    break;
                case VotingUtils.CONSTRAINT_NO:
                    if (senator.getVoto() != VotingUtils.NO) {
                        senatorsList.remove(senator);
                    }
                    break;
                case VotingUtils.CONSTRAINT_ABSTENTION:
                    if (senator.getVoto() != VotingUtils.ABSTENTION) {
                        senatorsList.remove(senator);
                    }
                    break;
                case VotingUtils.CONSTRAINT_ABSENCE:
                    if (senator.getVoto() != VotingUtils.ABSENCE) {
                        senatorsList.remove(senator);
                    }
                    break;
            }
        }
        Log.d(TAG, String.format(Locale.getDefault(), "Finish filter with %d senators", senatorsList.size()));
        adapter.notifyDataSetChanged();
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
        // final Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        // LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
        // positiveButtonLL.gravity = Gravity.CENTER;
        // positiveButton.setLayoutParams(positiveButtonLL);
    }
}

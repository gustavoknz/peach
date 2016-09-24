package com.gustavok.peach.tabs.senators;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.gustavok.peach.Constants;
import com.gustavok.peach.R;
import com.gustavok.peach.Senator;
import com.gustavok.peach.SenatorsManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SenatorsListFragment extends ListFragment {
    private static final String TAG = "SenatorsListFragment";

    private final List<Senator> viewSenatorsList = new ArrayList<>();
    private SenatorsArrayAdapter adapter;
    private Spinner spinnerParty;
    private Spinner spinnerState;
    private Spinner spinnerVote1;
    private Spinner spinnerVote2;

    private String constraintParty;
    private String constraintState;
    private int constraintVote1;
    private int constraintVote2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.senators_list_layout, container, false);

        Log.d(TAG, "Setting adapter with " + viewSenatorsList.size() + " senators");
        adapter = new SenatorsArrayAdapter(getActivity(), R.layout.senator_item_layout, viewSenatorsList);
        ListView listView = (ListView) view.findViewById(android.R.id.list);
        listView.setAdapter(adapter);

        viewSenatorsList.addAll(SenatorsManager.getInstance().setArrayAdapter(adapter));
        SenatorsManager.getInstance().updateVotes();

        spinnerParty = (Spinner) view.findViewById(R.id.spinner_parties);
        spinnerState = (Spinner) view.findViewById(R.id.spinner_states);
        spinnerVote1 = (Spinner) view.findViewById(R.id.spinner_votes1);
        spinnerVote2 = (Spinner) view.findViewById(R.id.spinner_votes2);

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
        spinnerVote1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedVote = (String) spinnerVote1.getSelectedItem();
                Log.d(TAG, String.format("Selected position: %d; id: %d; vote1=%s", position, id, selectedVote));

                constraintVote1 = position;
                updateList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "No vote1 selected");
            }
        });
        spinnerVote2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedVote = (String) spinnerVote2.getSelectedItem();
                Log.d(TAG, String.format("Selected position: %d; id: %d; vote2=%s", position, id, selectedVote));

                constraintVote2 = position;
                updateList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "No vote2 selected");
            }
        });

        return view;
    }

    private void updateList() {
        Log.d(TAG, String.format(Locale.getDefault(), "FILTERING party=%s; state=%s; vote1=%s; vote2=%s",
                constraintParty, constraintState, constraintVote1, constraintVote2));
        List<Senator> senatorsList = SenatorsManager.getInstance().getAllSenators();
        Log.d(TAG, String.format(Locale.getDefault(), "Starting filter with %d senators", senatorsList.size()));
        viewSenatorsList.clear();
        viewSenatorsList.addAll(senatorsList);

        for (Senator senator : senatorsList) {
            if (constraintParty != null) {
                if (!constraintParty.equals(senator.getPartido())) {
                    viewSenatorsList.remove(senator);
                }
            }
            if (constraintState != null) {
                if (!constraintState.equals(senator.getEstado())) {
                    viewSenatorsList.remove(senator);
                }
            }
            switch (constraintVote1) {
                case Constants.VOTE1_CONSTRAINT_ALL:
                    break;
                case Constants.VOTE1_CONSTRAINT_YES:
                    if (senator.getVoto() != Constants.VOTE_YES) {
                        viewSenatorsList.remove(senator);
                    }
                    break;
                case Constants.VOTE1_CONSTRAINT_NO:
                    if (senator.getVoto() != Constants.VOTE_NO) {
                        viewSenatorsList.remove(senator);
                    }
                    break;
                case Constants.VOTE1_CONSTRAINT_ABSTENTION:
                    if (senator.getVoto() != Constants.VOTE_ABSTENTION) {
                        viewSenatorsList.remove(senator);
                    }
                    break;
                case Constants.VOTE1_CONSTRAINT_ABSENCE:
                    if (senator.getVoto() != Constants.VOTE_ABSENCE) {
                        viewSenatorsList.remove(senator);
                    }
                    break;
                case Constants.VOTE1_CONSTRAINT_NONE:
                    if (senator.getVoto() != Constants.VOTE_NONE) {
                        viewSenatorsList.remove(senator);
                    }
                    break;
            }
            switch (constraintVote2) {
                case Constants.VOTE2_CONSTRAINT_ALL:
                    break;
                case Constants.VOTE2_CONSTRAINT_WAITING:
                    if (senator.getVoto2() != Constants.VOTE_DEFAULT_VALUE) {
                        viewSenatorsList.remove(senator);
                    }
                    break;
                case Constants.VOTE2_CONSTRAINT_YES:
                    if (senator.getVoto2() != Constants.VOTE_YES) {
                        viewSenatorsList.remove(senator);
                    }
                    break;
                case Constants.VOTE2_CONSTRAINT_NO:
                    if (senator.getVoto2() != Constants.VOTE_NO) {
                        viewSenatorsList.remove(senator);
                    }
                    break;
                case Constants.VOTE2_CONSTRAINT_ABSTENTION:
                    if (senator.getVoto2() != Constants.VOTE_ABSTENTION) {
                        viewSenatorsList.remove(senator);
                    }
                    break;
                case Constants.VOTE2_CONSTRAINT_ABSENCE:
                    if (senator.getVoto2() != Constants.VOTE_ABSENCE) {
                        viewSenatorsList.remove(senator);
                    }
                    break;
                case Constants.VOTE2_CONSTRAINT_NONE:
                    if (senator.getVoto2() != Constants.VOTE_NONE) {
                        viewSenatorsList.remove(senator);
                    }
                    break;
                case Constants.VOTE2_CONSTRAINT_CHANGE:
                    if ((senator.getVoto() == senator.getVoto2()) || (senator.getVoto2() == Constants.VOTE_DEFAULT_VALUE)) {
                        viewSenatorsList.remove(senator);
                    }
                    break;
            }
        }
        Log.d(TAG, String.format(Locale.getDefault(), "Finish filter with %d senators", viewSenatorsList.size()));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Senator item = adapter.getItem(position);
        if (item == null) {
            Log.d(TAG, String.format("Clicked item (%d) is null", position));
            return;
        }
        Log.i(TAG, String.format("Senator (%s) clicked. position: %d; id: %d", item.getNome(), position, id));

        LayoutInflater factory = LayoutInflater.from(v.getContext());

        @SuppressLint("InflateParams")
        LinearLayout dialogView = (LinearLayout) factory.inflate(R.layout.dialog_view, null);

        TextView nameView = (TextView) dialogView.findViewById(R.id.dialog_senator_name);
        nameView.setText(item.getNome());
        ImageView imageView = (ImageView) dialogView.findViewById(R.id.dialog_senator_image);
        Picasso.with(getContext()).load(item.getUrl()).into(imageView);
        TextView statePartyView = (TextView) dialogView.findViewById(R.id.dialog_senator_state_party);
        statePartyView.setText(String.format(Locale.getDefault(), "%s-%s", item.getPartido(), item.getEstado()));
        TextView vote1View = (TextView) dialogView.findViewById(R.id.dialog_senator_vote1);
        switch (item.getVoto()) {
            case Constants.VOTE_YES:
                vote1View.setText(R.string.dialog_voting_yes);
                break;
            case Constants.VOTE_NO:
                vote1View.setText(R.string.dialog_voting_no);
                break;
            case Constants.VOTE_ABSTENTION:
                vote1View.setText(R.string.dialog_voting_abstention);
                break;
            case Constants.VOTE_ABSENCE:
                vote1View.setText(R.string.dialog_voting_absence);
                break;
            case Constants.VOTE_NONE:
                vote1View.setText(R.string.dialog_voting_none);
                break;
            case Constants.VOTE_DEFAULT_VALUE:
                vote1View.setText(R.string.dialog_voting_not_yet);
                break;
        }
        TextView vote2View = (TextView) dialogView.findViewById(R.id.dialog_senator_vote2);
        switch (item.getVoto2()) {
            case Constants.VOTE_YES:
                vote2View.setText(R.string.dialog_voting_yes);
                break;
            case Constants.VOTE_NO:
                vote2View.setText(R.string.dialog_voting_no);
                break;
            case Constants.VOTE_ABSTENTION:
                vote2View.setText(R.string.dialog_voting_abstention);
                break;
            case Constants.VOTE_ABSENCE:
                vote2View.setText(R.string.dialog_voting_absence);
                break;
            case Constants.VOTE_NONE:
                vote2View.setText(R.string.dialog_voting_none);
                break;
            case Constants.VOTE_DEFAULT_VALUE:
                vote2View.setText(R.string.dialog_voting_not_yet);
                break;
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}

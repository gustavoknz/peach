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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class SenatorsListFragment extends ListFragment {
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

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Updating adapter...");
                Toast.makeText(getContext(), "updating adapter...", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
                handler.postDelayed(this, 7000);
            }
        };
        handler.postDelayed(runnable, 7000);

        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i(TAG, String.format("Senator clicked. position: %d; id: %d", position, id));

        Senator item = adapter.getItem(position);
        Log.d(TAG, "Selected senator " + item.getNome());

        LayoutInflater factory = LayoutInflater.from(v.getContext());

        ScrollView dialogView = (ScrollView) factory.inflate(R.layout.dialog_view, null);

        TextView nameView = (TextView) dialogView.findViewById(R.id.dialog_senator_name);
        nameView.setText(item.getNome());
        ImageView imageView = (ImageView) dialogView.findViewById(R.id.dialog_senator_image);
        Picasso.with(getContext()).load(item.getUrl()).into(imageView);
        TextView statePartyView = (TextView) dialogView.findViewById(R.id.dialog_senator_state_party);
        statePartyView.setText(String.format(Locale.getDefault(), "%s-%s", item.getPartido(), item.getEstado()));
        TextView voteView = (TextView) dialogView.findViewById(R.id.dialog_senator_vote);
        voteView.setText("Ainda não votou esse vagabundo!");

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
    }
}

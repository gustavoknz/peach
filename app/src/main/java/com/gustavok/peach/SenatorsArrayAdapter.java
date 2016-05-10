package com.gustavok.peach;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public final class SenatorsArrayAdapter extends ArrayAdapter<Senator> {
    private static final String TAG = "SenatorsArrayAdapter";

    public SenatorsArrayAdapter(Activity context, List<Senator> senatorsListView) {
        super(context, R.layout.senator_item_layout, senatorsListView);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //http://stackoverflow.com/questions/13970397/android-listview-not-working-images-are-changing-when-scrollling-the-list
        // Check if an existing view is being reused, otherwise inflate the view
        //if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.senator_item_layout, parent, false);
        //}
        Senator sen = getItem(position);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.senator_image);
        TextView tvName = (TextView) convertView.findViewById(R.id.senator_name);
        TextView tvPartyState = (TextView) convertView.findViewById(R.id.senator_party_state);
        ImageView voteView = (ImageView) convertView.findViewById(R.id.senator_vote);
        Picasso.with(getContext()).load(sen.getUrl()).into(imageView);
        tvName.setText(sen.getNome());
        tvPartyState.setText(String.format("%s - %s", sen.getPartido(), sen.getEstado()));

        Log.d(TAG, String.format(Locale.getDefault(), "Senator id=%d (%s) voted %d", sen.getId(), sen.getNome(), sen.getVoto()));
        if (VotingUtils.YES == sen.getVoto()) {
            voteView.setImageResource(R.drawable.vote_yes);
        } else if (VotingUtils.NO == sen.getVoto()) {
            voteView.setImageResource(R.drawable.vote_no);
        } else if (VotingUtils.ABSENCE == sen.getVoto()) {
            voteView.setImageResource(R.drawable.vote_absence);
        } else if (VotingUtils.ABSTENTION == sen.getVoto()) {
            voteView.setImageResource(R.drawable.vote_abstention);
        }

        return convertView;
    }
}
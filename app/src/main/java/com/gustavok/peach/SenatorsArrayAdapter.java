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

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public final class SenatorsArrayAdapter extends ArrayAdapter<Senator> {

    private static final String TAG = "InteractiveArrayAdapter";
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR"));

    public SenatorsArrayAdapter(Activity context, List<Senator> senatorsListView) {
        super(context, R.layout.senator_item_layout, senatorsListView);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Senator sen = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.senator_item_layout, parent, false);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.senator_image);
        TextView tvName = (TextView) convertView.findViewById(R.id.senator_name);
        TextView tvHome = (TextView) convertView.findViewById(R.id.senator_party_state);
        ImageView voteView = (ImageView) convertView.findViewById(R.id.senator_vote);
        Picasso.with(getContext()).load(sen.getUrl()).into(imageView);
        tvName.setText(sen.getNome());
        tvHome.setText(String.format("%s - %s", sen.getPartido(), sen.getEstado()));
        /*switch (new Random().nextInt(4)) {
            case 0:
                voteView.setImageResource(R.drawable.vote_yes);
                break;
            case 1:
                voteView.setImageResource(R.drawable.vote_no);
                break;
            case 2:
                voteView.setImageResource(R.drawable.vote_absence);
                break;
            case 3:
                voteView.setImageResource(R.drawable.vote_abstention);
                break;
        }*/
        Log.d(TAG, "Senator id " + sen.getId() + " voted " + sen.getVoto() + "; YES=" + VoteEnum.YES.getVote());
        if (Integer.toString(VoteEnum.YES.getVote()).equals(sen.getVoto())) {
            voteView.setImageResource(R.drawable.vote_yes);
        } else if (Integer.toString(VoteEnum.NO.getVote()).equals(sen.getVoto())) {
            voteView.setImageResource(R.drawable.vote_no);
        } else if (Integer.toString(VoteEnum.ABSENCE.getVote()).equals(sen.getVoto())) {
            voteView.setImageResource(R.drawable.vote_absence);
        } else if (Integer.toString(VoteEnum.ABSTENTION.getVote()).equals(sen.getVoto())) {
            voteView.setImageResource(R.drawable.vote_abstention);
        }

        return convertView;
    }
}
package com.gustavok.peach.tabs.senators;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gustavok.peach.R;
import com.gustavok.peach.Senator;
import com.gustavok.peach.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public final class SenatorsArrayAdapter extends ArrayAdapter<Senator> {
    private static final String TAG = "SenatorsArrayAdapter";
    private final Context context;
    private final int layoutResId;
    private List<Senator> data = null;

    public SenatorsArrayAdapter(Activity context, int layoutResId, List<Senator> data) {
        super(context, layoutResId, data);
        this.context = context;
        this.layoutResId = layoutResId;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SenatorHolder holder;

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(layoutResId, parent, false);

            holder = new SenatorHolder();
            holder.imageUrl = (ImageView) convertView.findViewById(R.id.senator_image);
            holder.name = (TextView) convertView.findViewById(R.id.senator_name);
            holder.partyState = (TextView) convertView.findViewById(R.id.senator_party_state);
            holder.vote1 = (ImageView) convertView.findViewById(R.id.senator_vote1);
            holder.vote2 = (ImageView) convertView.findViewById(R.id.senator_vote2);
            convertView.setTag(holder);
        } else {
            holder = (SenatorHolder) convertView.getTag();
        }
        Senator sen = data.get(position);
        Picasso.with(context).load(sen.getUrl()).into(holder.imageUrl);
        holder.name.setText(sen.getNome());
        holder.partyState.setText(String.format("%s - %s", sen.getPartido(), sen.getEstado()));

        Log.d(TAG, String.format(Locale.getDefault(), "Senator id=%d (%s) voted 11/5 %d and %d now", sen.getId(), sen.getNome(), sen.getVoto(), sen.getVoto2()));
        holder.vote1.setImageResource(0);
        if (Constants.VOTE_YES == sen.getVoto()) {
            holder.vote1.setImageResource(R.drawable.vote_yes);
        } else if (Constants.VOTE_NO == sen.getVoto()) {
            holder.vote1.setImageResource(R.drawable.vote_no);
        } else if (Constants.VOTE_ABSTENTION == sen.getVoto()) {
            holder.vote1.setImageResource(R.drawable.vote_abstention);
        } else if (Constants.VOTE_ABSENCE == sen.getVoto()) {
            holder.vote1.setImageResource(R.drawable.vote_absence);
        }
        holder.vote2.setImageResource(0);
        if (Constants.VOTE_YES == sen.getVoto2()) {
            holder.vote2.setImageResource(R.drawable.vote_yes);
        } else if (Constants.VOTE_NO == sen.getVoto2()) {
            holder.vote2.setImageResource(R.drawable.vote_no);
        } else if (Constants.VOTE_ABSTENTION == sen.getVoto2()) {
            holder.vote2.setImageResource(R.drawable.vote_abstention);
        } else if (Constants.VOTE_ABSENCE == sen.getVoto2()) {
            holder.vote2.setImageResource(R.drawable.vote_absence);
        }

        return convertView;
    }

    class SenatorHolder {
        ImageView imageUrl;
        TextView name;
        TextView partyState;
        ImageView vote1;
        ImageView vote2;
    }
}
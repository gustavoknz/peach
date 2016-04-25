package com.gustavok.peach;

import android.app.Activity;
import android.app.AlarmManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SenatorsArrayAdapter extends ArrayAdapter<Senator> {

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
        TextView tvName = (TextView) convertView.findViewById(R.id.senator_name);
        TextView tvHome = (TextView) convertView.findViewById(R.id.senator_party_state);
        tvName.setText(sen.getName());
        tvHome.setText(String.format("%s-%s", sen.getParty(), sen.getState()));

        return convertView;
    }
}
package com.gustavok.peach;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

public class SenatorsListFragment extends ListFragment {
    private static final String TAG = "SenatorsListFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "getAllSenators is being called");
        new RestClient().getAllSenators();
        Log.d(TAG, "getAllSenators called");
        View view = inflater.inflate(R.layout.senators_list_layout, container, false);
        ListView listView = (ListView) view.findViewById(android.R.id.list);
        List<Senator> senatorsList = SenatorsManager.getInstance().getSenatorsList();
        SenatorsArrayAdapter adapter = new SenatorsArrayAdapter(getActivity(), senatorsList);
        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i(TAG, String.format("Senator clicked. position: %d; id: %d", position, id));
    }
}

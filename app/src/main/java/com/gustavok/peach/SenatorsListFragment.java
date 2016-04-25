package com.gustavok.peach;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class SenatorsListFragment extends ListFragment {
    private static final String TAG = "SenatorsListFragment";

    /*@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ArrayAdapter<Senator> adapter = new InteractiveArrayAdapter(getActivity(), senatorsList);
        //setListAdapter(adapter);


        List<Senator> senatorsList = SenatorsManager.getInstance().getSenatorsList();

        // Create the adapter to convert the array to views
        SenatorsArrayAdapter adapter = new SenatorsArrayAdapter(getActivity(), senatorsList);

        // Attach the adapter to a ListView
        View view = getActivity().getfindViewById(R.layout);
        ListView listView = (ListView) view.findViewById(R.id.list_senators);
        listView.setAdapter(adapter);
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.senators_list_layout, container, false);
        ListView listView = (ListView) view.findViewById(android.R.id.list);
        List<Senator> senatorsList = SenatorsManager.getInstance().getSenatorsList();
        SenatorsArrayAdapter adapter = new SenatorsArrayAdapter(getActivity(), senatorsList);
        listView.setAdapter(adapter);

        /*
        TextView tvName = (TextView) layoutView.findViewById(R.id.senator_name);
        tvName.setText("Truiton Fragment #");
        TextView tvPs = (TextView) layoutView.findViewById(R.id.senator_party_state);
        tvPs.setText("Truiton Fragment #");*/
        return view;
    }

    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.senators_list_layout, container, false);

        ListView lv1 = (ListView) rootView.findViewById(android.R.id.list);

        View view = inflater.inflate(R.layout.senator_item_layout, null);
        TextView tvPartyState = (TextView)view.findViewById(R.id.senator_party_state);
        tvPartyState.setText(" - ");

        lv1.setAdapter(new ArrayAdapter<>(lv1.getContext(), R.layout.senator_item_layout, R.id.senator_name, senatorsList));

        return rootView;
    }*/

        /*List<Senator> list = SenatorsManager.getInstance().getSenatorsList();
        List<String> listNames = new ArrayList<>();
        for (Senator s : list) {
            listNames.add(s.getName());
        }
        Log.d(TAG, "List names: " + listNames); 
        setListAdapter(new ArrayAdapter<Senator>(this, R.layout.senators_list_layout, listNames)); 
        ListView listView = getListView();
        listView.setTextFilterEnabled(true); 
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;*/

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i(TAG, String.format("Senator clicked. position: %d; id: %d", position, id));
    }
}

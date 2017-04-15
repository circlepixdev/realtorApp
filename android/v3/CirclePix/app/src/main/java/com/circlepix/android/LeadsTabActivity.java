package com.circlepix.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.circlepix.android.ui.RecyclerAdapter;
import com.circlepix.android.ui.RecyclerItemClickListener;
import com.circlepix.android.ui.RecyclerViewHeader;

import java.util.ArrayList;

/**
 * Created by keuahnlumanog1 on 4/23/16.
 */
public class LeadsTabActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leads_tab);

    }

    public static class PlaceholderFragment extends Fragment {
        // Store instance variables
        private static final String TAB_POSITION = "tab_position";
        private String title;
        private int page;
        private RecyclerAdapter mAdapter;

        // newInstance constructor for creating fragment with arguments
        public static PlaceholderFragment newInstance(int tabPosition) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(TAB_POSITION, tabPosition);
            fragment.setArguments(args);

//            Bundle args = new Bundle();
//            args.putInt("someInt", page);
//            args.putString("someTitle", title);
//            fragment.setArguments(args);
            return fragment;
        }

        // Store instance variables based on arguments passed
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //       page = getArguments().getInt("someInt", 0);
            //       title = getArguments().getString("someTitle");
        }

        // Inflate the view for the fragment based on layout XML
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            Bundle args = getArguments();
            int tabPosition = args.getInt(TAB_POSITION);

            ArrayList<String> items = new ArrayList<String>();
            for (int i = 0; i < 20; i++) {
                items.add("Leads: " + tabPosition + " item #" + i);
            }

            View v =  inflater.inflate(R.layout.fragment_leads_tab, container, false);
            RecyclerView recyclerView = (RecyclerView)v.findViewById(R.id.recyclerview);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(getActivity().getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
//                            try {
//                                mAdapter.setSelected(position);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                            Toast.makeText(getActivity().getApplicationContext(), "Item clicked: pos "+ position, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity().getApplicationContext(), LeadSelectedActivity.class);
                            startActivity(intent);
                        }
                    })
            );

            mAdapter = new RecyclerAdapter(items);
            recyclerView.setAdapter(mAdapter);
            //recyclerView.setAdapter(new RecyclerAdapter(items));

            RecyclerViewHeader header = (RecyclerViewHeader) v.findViewById(R.id.header);
            header.attachTo(recyclerView);

            return v;

//            View view = inflater.inflate(R.layout.fragment_listings_tab, container, false);
//            TextView tvLabel = (TextView) view.findViewById(R.id.textView);
//            tvLabel.setText(page + " -- " + title);
//            return view;
        }
    }
}
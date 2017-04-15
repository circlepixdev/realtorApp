package com.circlepix.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by Keuahn on 8/23/2016.
 */
public class CustomerSupportActivityTab extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_support_tab);

    }

    public static class PlaceholderFragment extends Fragment {
        // Store instance variables
        private static final String TAB_POSITION = "tab_position";
        private LinearLayout email;


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

            View v =  inflater.inflate(R.layout.fragment_customer_support_tab, container, false);
//            View v =  inflater.inflate(R.layout.fab_sample, container, false);
            email = (LinearLayout)v.findViewById(R.id.email);

            email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(Intent.ACTION_SEND);
//                    intent.setType("plain/text");
//                    intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "support@circlepix.com" });
//                    startActivity(Intent.createChooser(intent, ""));

                }
            });


            return v;
        }
    }
}
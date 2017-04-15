package com.circlepix.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.circlepix.android.beans.AgentData;
import com.circlepix.android.data.Presentation;
import com.circlepix.android.ui.PresentationsTabAdapter;
import com.circlepix.android.ui.RecyclerAdapter;
import com.circlepix.android.ui.RecyclerItemClickListener;
import com.circlepix.android.ui.RecyclerViewHeader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by keuahnlumanog1 on 4/23/16.
 */
public class PresentationsTabActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentations_tab);

    }

    public static class PlaceholderFragment extends Fragment {
        // Store instance variables
        private CirclePixAppState appState;
        private AgentData agentData;
        private PresentationsTabAdapter adapter;
        private List<Presentation> presentations;
        private static final String TAB_POSITION = "tab_position";
        private ProgressBar progressBar;
        private RecyclerAdapter mAdapter;

        private FrameLayout tabsLayout;
        private FrameLayout addNewPresentationTabLayout;
        private FrameLayout presentationOptionsLayout;
        private LinearLayout addNewPresentationLayout;

        private ImageView cancelOptions;

        private int selectedPos;
        private boolean hasInternetConnection;

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
            setHasOptionsMenu(true);


            appState = ((CirclePixAppState)getActivity().getApplicationContext());
            appState.setContextForPreferences(getActivity());
        }

        @Override
        public void onPrepareOptionsMenu(Menu menu) {
            menu.findItem(R.id.action_settings).setVisible(true);
            super.onPrepareOptionsMenu(menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            switch (id) {
                case R.id.action_settings_default:
                    return true;
                case R.id.action_settings:
                Toast.makeText(getActivity().getApplicationContext(), "Global Presentation Settings", Toast.LENGTH_SHORT).show();
                return true;
            }

            return super.onOptionsItemSelected(item);
        }

        // Inflate the view for the fragment based on layout XML
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            Bundle args = getArguments();
            int tabPosition = args.getInt(TAB_POSITION);

            ArrayList<String> items = new ArrayList<String>();
            for (int i = 0; i < 20; i++) {
                items.add("Presentations Test: " + tabPosition + " item #" + i);
            }

            View v =  inflater.inflate(R.layout.fragment_presentations_tab, container, false);
            tabsLayout = (FrameLayout) v.findViewById(R.id.tabs);
            addNewPresentationTabLayout = (FrameLayout) v.findViewById(R.id.add_new_presentation_tab_layout);
            addNewPresentationLayout = (LinearLayout) v.findViewById(R.id.add_new_presentation);
            presentationOptionsLayout = (FrameLayout) v.findViewById(R.id.presentation_options);
            cancelOptions = (ImageView) v.findViewById(R.id.cancel_options);
//            progressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
//            progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY);

            tabsLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Animation slideUp = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_up);
                    tabsLayout.startAnimation(slideUp);
                    tabsLayout.setVisibility(View.VISIBLE);
                }
            }, 200);

            agentData = AgentData.getInstance();
            Log.v("realtor ", agentData.getRealtor().getId());
//            GetPresentationIds.runCommand(agentData.getRealtor().getId());

            ConnectivityManager manager = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnectedOrConnecting()){

                hasInternetConnection = true;
            }else {
                hasInternetConnection = false;
            }

           /* if(hasInternetConnection == true){
                final PresentationDataSource ds = new PresentationDataSource(getActivity());

                Thread syncThread = new Thread() {
                    public void run() {
                     //   SyncPresentations.runCommand(getActivity(), realtorIdStr, firstRun, ds, null);
                    }
                };
                syncThread.start();
            }*/

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

                      //      Intent intent = new Intent(getActivity().getApplicationContext(), LeadSelectedActivity.class);
                      //      startActivity(intent);

                            if(addNewPresentationTabLayout.isShown()){
                                // Hide addNewPresentationTabLayout and show listingOptionsLayout
                                Animation slideDown = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_down);
                                addNewPresentationTabLayout.startAnimation(slideDown);
                                addNewPresentationTabLayout.setVisibility(View.GONE);
                                addNewPresentationTabLayout.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Animation slideUp = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_up);
                                        presentationOptionsLayout.startAnimation(slideUp);
                                        presentationOptionsLayout.setVisibility(View.VISIBLE);

                                        cancelOptions.startAnimation(slideUp);
                                        cancelOptions.setVisibility(View.VISIBLE);
                                    }
                                }, 150);
                                selectedPos = position;
                            }else{
                                if(selectedPos != position){
                                    Animation slideDown = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_down);
                                    presentationOptionsLayout.startAnimation(slideDown);
                                    presentationOptionsLayout.setVisibility(View.GONE);


                                    presentationOptionsLayout.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Animation slideUp = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_up);
                                            presentationOptionsLayout.startAnimation(slideUp);
                                            presentationOptionsLayout.setVisibility(View.VISIBLE);


                                        }
                                    }, 150);
                                }
                                selectedPos = position;

                            }
                        }
                    })
            );

            mAdapter = new RecyclerAdapter(items);
            recyclerView.setAdapter(mAdapter);
            // recyclerView.setAdapter(new RecyclerAdapter(items));

//            RecyclerViewHeader header = (RecyclerViewHeader) v.findViewById(R.id.header);
//            header.attachTo(recyclerView);

            addBtnOnClickListener();

            //handle hardware back button
            v.setFocusableInTouchMode(true);
            v.requestFocus();
            v.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {

                    if( keyCode == KeyEvent.KEYCODE_BACK ) {
                        Log.v("hey", "onKey Back listener is working!!!");

                        appState.setFirstRun(false);
                        Animation slideDown = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_down);
                        tabsLayout.startAnimation(slideDown);
                        tabsLayout.setVisibility(View.GONE);

                        if(cancelOptions.isShown()){
                            cancelOptions.startAnimation(slideDown);
                            cancelOptions.setVisibility(View.GONE);
                        }

                        Intent intent = new Intent(getActivity(), HomeActivity.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

                        return true;
                    } else {
                        return false;
                    }
                }
            });
            return v;

//            View view = inflater.inflate(R.layout.fragment_listings_tab, container, false);
//            TextView tvLabel = (TextView) view.findViewById(R.id.textView);
//            tvLabel.setText(page + " -- " + title);
//            return view;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public void addBtnOnClickListener(){
            addNewPresentationLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   /* Bundle bundle = ActivityOptions.makeCustomAnimation(getActivity(),
                            R.anim.slide_in_left,
                            R.anim.slide_out_left).toBundle();

                    isNewPres = true;

                    Intent wizardActivity = new Intent(getActivity(), WizardMainActivity.class);
                   startActivityForResult(wizardActivity, 0, bundle);*/

                    showAlertDialog("Add New Presentation", "TODO: AddNewPresentation page");
                }
            });

            cancelOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAddNewListing();
                }
            });
        }

        private void showAlertDialog(String title, String msg){
            final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle(title);
            alert.setMessage(msg);
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Do nothing
                    dialog.dismiss();
                }
            });
            alert.show();
        }

        private void showAddNewListing(){
            if(!addNewPresentationTabLayout.isShown()){
                // Hide addNewListingLayout and show listingOptionsLayout
                Animation slideDown = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_down);
                presentationOptionsLayout.startAnimation(slideDown);
                presentationOptionsLayout.setVisibility(View.GONE);
   //             cancelOptions.startAnimation(slideDown);
                cancelOptions.setVisibility(View.GONE);
                cancelOptions.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Animation slideUp = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_up);
                        addNewPresentationTabLayout.startAnimation(slideUp);
                        addNewPresentationTabLayout.setVisibility(View.VISIBLE);

                    }
                }, 150);
            }
        }
    }


}
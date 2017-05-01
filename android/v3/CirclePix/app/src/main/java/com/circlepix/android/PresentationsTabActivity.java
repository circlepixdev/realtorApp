package com.circlepix.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.circlepix.android.beans.AgentData;
import com.circlepix.android.data.Presentation;
import com.circlepix.android.data.PresentationDataSource;
import com.circlepix.android.presentations.BackgroundMusicService;
import com.circlepix.android.presentations.PresentationSequencingSet;
import com.circlepix.android.presentations.PresentationStart;
import com.circlepix.android.sync.commands.DeletePresentation;
import com.circlepix.android.sync.commands.SyncPresentations;
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
        private RecyclerView recyclerView;
        private PresentationsTabAdapter adapter;
        private List<Presentation> presentations;
        private SwipeRefreshLayout swipeRefreshLayout;
        private RelativeLayout noPresentations;
        private static final String TAB_POSITION = "tab_position";
        private ProgressBar progressBar;
        private LinearLayoutManager mLayoutManager;
      //  private RecyclerAdapter mAdapter;
        private boolean isFirstRun;
        public boolean deletedOnFirstRun;

        private FrameLayout tabsLayout;
        private FrameLayout addNewPresentationTabLayout;
        private FrameLayout presentationOptionsLayout;
        private LinearLayout addNewPresentationLayout;

        private ImageView cancelOptions;
        private ImageView presentationPlay;
        private ImageView presentationEdit;
        private ImageView presentationDelete;
        private ImageView presentationShare;

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
        public void onResume(){
            super.onResume();

            appState.setActiveClassName("");

            if(appState.isFirstRun()){
                ConnectivityManager manager = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = manager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnectedOrConnecting()){

                    hasInternetConnection = true;
                }else {
                    hasInternetConnection = false;
                }

                if(hasInternetConnection == true && appState.getActiveClassName().equalsIgnoreCase("PresentationsTabActivity")) {
                    Log.v("sync pres?", "from presentationstabctivity");
                    final String realtorIdStr = agentData.getRealtor().getId();
                    final PresentationDataSource ds = new PresentationDataSource(getActivity());
                    // final boolean firstRun = (extras != null) ? extras.getBoolean("isFirstRun") : false;
                    Thread syncThread = new Thread() {
                        public void run() {
//                        SyncPresentations.runCommand(getActivity(), realtorIdStr,  ds, null);
                            SyncPresentations.runCommand(getActivity(), realtorIdStr, ds, new Runnable() {
                                @Override
                                public void run() {

                                    Log.v("Last Item totalSize?", "after pres");
                                }
                            });
                        }
                    };
                    syncThread.start();
                }
            }

            refreshList();
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
                  //  Toast.makeText(getActivity().getApplicationContext(), "Global Presentation Settings", Toast.LENGTH_SHORT).show();
                    Intent playActivity = new Intent(getActivity(), GlobalSettingsActivity.class);
                    startActivity(playActivity);
                    getActivity().overridePendingTransition( R.anim.slide_in_left, R.anim.slide_out_left);
                    appState.setActiveClassName("PresentationsTabActivity");
                    return true;
            }

            return super.onOptionsItemSelected(item);
        }

        // Inflate the view for the fragment based on layout XML
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            Bundle args = getArguments();
            int tabPosition = args.getInt(TAB_POSITION);


            View v =  inflater.inflate(R.layout.fragment_presentations_tab, container, false);
            tabsLayout = (FrameLayout) v.findViewById(R.id.tabs);
            addNewPresentationTabLayout = (FrameLayout) v.findViewById(R.id.add_new_presentation_tab_layout);
            addNewPresentationLayout = (LinearLayout) v.findViewById(R.id.add_new_presentation);
            presentationOptionsLayout = (FrameLayout) v.findViewById(R.id.presentation_options);
            noPresentations = (RelativeLayout) v.findViewById(R.id.no_presentations);
            cancelOptions = (ImageView) v.findViewById(R.id.cancel_options);
            presentationPlay = (ImageView) v.findViewById(R.id.presentation_play);
            presentationEdit = (ImageView) v.findViewById(R.id.presentation_edit);
            presentationDelete = (ImageView) v.findViewById(R.id.presentation_delete);
            presentationShare = (ImageView) v.findViewById(R.id.presentation_share);
//            progressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
//            progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY);

            swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_layout);
            swipeRefreshLayout.setOnRefreshListener(onRefreshListener);
            swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
            swipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.WHITE);
            swipeRefreshLayout.setRefreshing(false);
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



            recyclerView = (RecyclerView)v.findViewById(R.id.recyclerview);
            mLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(getActivity().getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {

                            // Handle higlight for selected item
                            try {
                                Presentation selectedPresentation =  presentations.get(position);

                                if(selectedPresentation.isSelected() == true){
                                    Log.v("Already selected pres", selectedPresentation.getName());

                                //    cancelOptions.performClick();
                                    cancelOptionsAction();
                                }

                                adapter.setSelected(position);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

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

                        appState.clearSharedPreferences();

                        appState.setActiveClassName("");
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


        SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                ConnectivityManager manager = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = manager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnectedOrConnecting()){

                    hasInternetConnection = true;
                }else {
                    hasInternetConnection = false;
                }

                if(hasInternetConnection == true) {
                    Log.v("sync pres?", "from presentationstabctivity");
                    final String realtorIdStr = agentData.getRealtor().getId();
                    final PresentationDataSource ds = new PresentationDataSource(getActivity());
                    // final boolean firstRun = (extras != null) ? extras.getBoolean("isFirstRun") : false;
                    Thread syncThread = new Thread() {
                        public void run() {
//                        SyncPresentations.runCommand(getActivity(), realtorIdStr,  ds, null);
                            SyncPresentations.runCommand(getActivity(), realtorIdStr, ds, new Runnable() {
                                @Override
                                public void run() {

                                    Log.v("Last Item totalSize?", "after pres");
                                }
                            });
                        }
                    };
                    syncThread.start();
                }
            }
        };

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

                 //   showAlertDialog("Add New Presentation", "TODO: AddNewPresentation page");
                    Bundle bundle = ActivityOptions.makeCustomAnimation(getActivity(),
                            R.anim.slide_in_left,
                            R.anim.slide_out_left).toBundle();

                 //   isNewPres = true;

                    Intent wizardActivity = new Intent(getActivity(), WizardMainActivity.class);
                    startActivity(wizardActivity);
                    getActivity().overridePendingTransition( R.anim.slide_in_left, R.anim.slide_out_left);
                    appState.setActiveClassName("PresentationsTabActivity");

//                    Intent wizardActivity = new Intent(getActivity(), WizardMainActivity.class);
//                    startActivityForResult(wizardActivity, 0, bundle);
//                    getActivity().finish();
//                    appState.setActiveClassName("PresentationsTabActivity");
                }
            });

            cancelOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  //  showAddNewListing();
                    cancelOptionsAction();
                    adapter.setSelected(selectedPos);
                }
            });

            presentationPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Presentation presentation =  presentations.get(selectedPos);

                    PresentationSequencingSet.setSelectedPresentations(presentation);
                    PresentationSequencingSet.isPause = false;

                    Intent playActivity = new Intent(getActivity(), PresentationStart.class);
                    startActivity(playActivity);
                   // getActivity().finish();

                    appState.setActiveClassName("PresentationsTabActivity");

                    appState.clearSharedPreferences();
                    appState.setActionBarStat(true);

                    appState.setBgMusic(presentation.getMusic().toString());
                    Log.v("appGetMusic", appState.getBgMusic());
                    Log.v("aud.getPos", String.valueOf(appState.getAudioServiceCurrentPos()));
                    getActivity().startService(new Intent(getActivity(), BackgroundMusicService.class));

                }
            });


            presentationEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Presentation p = presentations.get(selectedPos);

                    final long presentationId = p.getId();

                    Intent wizardActivity = new Intent(getActivity(), WizardMainActivity.class);
                    wizardActivity.putExtra("presentationId", presentationId);
                    startActivity(wizardActivity);
                    getActivity().overridePendingTransition( R.anim.slide_in_left, R.anim.slide_out_left);
                    appState.setActiveClassName("PresentationsTabActivity");


//                    Intent wizardActivity = new Intent(context, WizardMainActivity.class);
//                    wizardActivity.putExtra("presentationId", presentationId);
//                    context.startActivity(wizardActivity, bundle);
                }
            });



            presentationDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteItem(v, selectedPos);
                }
            });

            presentationShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendItem(selectedPos);
                }
            });
        }

        private void cancelOptionsAction (){

            showAddNewListing();

        }

//        private void cancelOptionsAction (int position){
//
//            try {
//                cancelOptions.performClick();
//                adapter.setSelected(position);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//
//        }

        public void deleteItem(final View v, final int position) {
            Presentation p = presentations.get(position);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle("Delete Presentation");
            alertDialogBuilder
                    .setMessage("\"" + p.getName().toString() + "\"" +" will be deleted.")
                    .setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    // Delete the item from the database
                                    PresentationDataSource dao = new PresentationDataSource(getActivity());
                                    dao.open(true);
                                    Presentation p = presentations.get(position);
                                    dao.deletePresentation(p);

                                    DeletePresentation.runCommand(AgentData.getInstance().getRealtor().getId(), p.getGuid(), null);
                                    dao.close();

                                    if (isFirstRun) {
                                        deletedOnFirstRun = true;
                                    } else {
                                        deletedOnFirstRun = false;
                                    }

                                    SharedPreferences mPref;
                                    SharedPreferences.Editor mEditor;

                                    mPref = getActivity().getApplicationContext().getSharedPreferences("presentation", Context.MODE_PRIVATE);
                                    mEditor = mPref.edit();

                                    mEditor.clear();
                                    mEditor.commit();


                                    cancelOptions.performClick();
                                    refreshList();

                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();


        }

        public void sendItem(int position) {
            // Use the share intent to share the link
            PresentationDataSource dao = new PresentationDataSource(getActivity());
            dao.open(false);
            Presentation p = presentations.get(position);
            dao.close();

            String subject = "StarMarketing Presentation";
            String msg = "http://www.circlepix.com/realtorPresentation/?id=" + p.getGuid() + "\n\n" + "You have been sent a presentation. Click the link above to view the presentation. ";
            //"http://www.circlepix.com/present/show/" + p.getGuid();


            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

            // Add data to the intent, the receiving app will decide what to do with it.
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, msg);
            startActivity(Intent.createChooser(intent, "How do you want to share?"));
            appState.setActiveClassName("PresentationsTabActivity");
            // finish(); --comment this out so it will just stya on the same page(Listing Presentations)
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


        private void refreshList() {


            final PresentationDataSource dao = new PresentationDataSource(getActivity());
            dao.open(false);

            presentations = dao.getAllPresentations();

//        if (presentations.size() == 0 && isFirstRun) {
            if (presentations.size() == 0 && isFirstRun && deletedOnFirstRun == false) {

                // If the list is empty
                Presentation p = new Presentation();
                p.setName("Sample Presentation");
                dao.close();
                dao.open(true);
                dao.createPresentation(p);
                presentations = dao.getAllPresentations();
                noPresentations.setVisibility(View.GONE);

            }else if (presentations.size() == 0){
                noPresentations.setVisibility(View.VISIBLE);
            }else{
                noPresentations.setVisibility(View.GONE);
            }

            dao.close();



            adapter = new PresentationsTabAdapter(getActivity(), presentations); //presentations
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();


            /*adapter = new
                    PresentationAdapter(PresentationsActivity.this, presentations, PresentationsActivity.this); //presentations

            swipeListView.setAdapter(adapter);


            if(appState.isNewPres()){
                swipeListView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //        swipeListView.setFriction(ViewConfiguration.getScrollFriction() * 5000);
                        //swipeListView.smoothScrollToPosition(presentations.size() - 1);
                        swipeListView.smoothScrollToPositionFromTop(presentations.size() - 1, appState.getTop());
                    }
                }, 100L);


                appState.clearPresentationsActivitySP();
            }else{
                //restore index and position
                swipeListView.setSelectionFromTop(appState.getIndex(), appState.getTop());
            }*/


        }
    }


}
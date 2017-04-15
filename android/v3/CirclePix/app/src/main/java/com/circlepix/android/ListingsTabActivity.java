package com.circlepix.android;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.circlepix.android.beans.AgentData;
import com.circlepix.android.beans.Listing;
import com.circlepix.android.beans.ListingDescription;
import com.circlepix.android.beans.ListingInformation;
import com.circlepix.android.helpers.LoginHelper;
import com.circlepix.android.ui.ListingsTabAdapter;
import com.circlepix.android.ui.RecyclerItemClickListener;
import com.circlepix.android.ui.RecyclerViewHeader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by keuahnlumanog1 on 4/20/16.
 */

public class ListingsTabActivity extends AppCompatActivity {

    private PlaceholderFragment frag;
    private Listing l;
    public static int selectedPosGlobal = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listings_tab);
    }

    public static class PlaceholderFragment extends Fragment {
        // Store instance variables
        private CirclePixAppState appState;
        private static final String TAB_POSITION = "tab_position";
        private String title;
        private int page;
        private View v;
        private AgentData agentData;
        private ListingInformation selectedListing;
        private ListingsTabAdapter mAdapter;
        private RecyclerView recyclerView;
        private RecyclerViewHeader header;
        private RelativeLayout noListing;
        private CoordinatorLayout coordinatorLayout;
        private SwipeRefreshLayout swipeRefreshLayout;
        private TextView text;
        private int previousTotal = 0;
        private boolean loading = true;
        private int visibleThreshold = 5;
        private int firstVisibleItem, visibleItemCount, totalItemCount;
        private LinearLayoutManager mLayoutManager;
        private FrameLayout tabsLayout;
        private FrameLayout addNewListingTabLayout;
        private FrameLayout listingOptionsLayout;
        private LinearLayout addNewListingLayout;
        private ImageView cancelOptions;

        private ImageView listingEdit;
//        private ImageView listingBuyUpgrade;
        private ImageView listingAddPhotos;
        private ImageView listingAddVideo;
        private ImageView listingShare;

        private int selectedPos;
        private final int SELECT_VIDEO = 1;
        private final int CAPTURE_VIDEO = 101;

        private String selectedVideoPath;
        private Uri captureVideoFileUri;

        final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
        final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 143;

        public ViewGroup rootView;
        private GetActiveListings getActiveListingsTask;

        // newInstance constructor for creating fragment with arguments
        public static PlaceholderFragment newInstance(int tabPosition) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(TAB_POSITION, tabPosition);
            fragment.setArguments(args);

            return fragment;
        }

        // Store instance variables based on arguments passed
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(false);

            agentData = AgentData.getInstance();
            appState = ((CirclePixAppState)getActivity().getApplicationContext());
            appState.setContextForPreferences(getActivity());
        }

        @Override
        public void onPrepareOptionsMenu(Menu menu) {
            menu.findItem(R.id.action_settings).setVisible(false);
            super.onPrepareOptionsMenu(menu);
        }

        @Override
        public void onResume() {
            Log.v("DEBUG", "onResume of ListingsTab Fragment");


            super.onResume();
        }

        // Inflate the view for the fragment based on layout XML
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            v = inflater.inflate(R.layout.fragment_listings_tab, container, false);
            rootView = container;
            coordinatorLayout = (CoordinatorLayout )v.findViewById(R.id.coordinator);
            tabsLayout = (FrameLayout)v.findViewById(R.id.tabs);
            addNewListingTabLayout = (FrameLayout) v.findViewById(R.id.add_new_listing_tab_layout);
            addNewListingLayout = (LinearLayout)v.findViewById(R.id.add_new_listing);
            listingOptionsLayout = (FrameLayout)v.findViewById(R.id.listing_options);
            noListing = (RelativeLayout) v.findViewById(R.id.no_listing);
            text = (TextView) v.findViewById(R.id.header_title);
            cancelOptions = (ImageView)v.findViewById(R.id.cancel_options);
            listingEdit = (ImageView)v.findViewById(R.id.listing_edit);
//            listingBuyUpgrade = (ImageView)v.findViewById(R.id.listing_buy_upgrades);
            listingAddPhotos = (ImageView)v.findViewById(R.id.listing_add_photos);
            listingAddVideo = (ImageView)v.findViewById(R.id.listing_add_video);
            listingShare = (ImageView)v.findViewById(R.id.listing_share);

            swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_layout);
            swipeRefreshLayout.setOnRefreshListener(onRefreshListener);
            swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
            swipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.WHITE);

            tabsLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Animation slideUp = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_up);
                    tabsLayout.startAnimation(slideUp);
                    tabsLayout.setVisibility(View.VISIBLE);
                }
            }, 200);

            //comment this out for test on getActiveListing
            /*if (agentData.getListings() == null || agentData.getListings().size() == 0) {

                noListing.setVisibility(View.VISIBLE);

                String msg = agentData.isOfflineMode() ?
                        "There are no listings to display in offline mode." :
                        "Presently there are no active listings for your account.";
            } else {

                noListing.setVisibility(View.GONE);
            }*/

            Log.v("call getactivelistings", "true");
            getActiveListingsTask = new GetActiveListings(v);

            if(Build.VERSION.SDK_INT >= 11) {
                getActiveListingsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }else {
                getActiveListingsTask.execute();
            }

            return v;
        }

        SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener(){

            @Override
            public void onRefresh() {
                //simulate doing something
                getActiveListingsTask = new GetActiveListings(v);

                if(Build.VERSION.SDK_INT >= 11) {
                    getActiveListingsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }else {
                    getActiveListingsTask.execute();
                }

            /*    new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        //done
                        swipeRefreshLayout.setRefreshing(false);
                    }

                }, 2000);*/
            }};


        protected class GetActiveListings extends AsyncTask<Context, Integer, String>{

            private View _view;
            public GetActiveListings(View view){
                this._view = view; // this you can use ahead wherever required
            }


            @Override
            protected void onPreExecute() {
               // swipeRefreshLayout.setRefreshing(true);
                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                    }
                });
                Log.v("getactivelistings", "true");
            }

            @Override
            protected String doInBackground(Context... params) {

                String BASE_URL = "http://stag-mobile.circlepix.com/api/listing.php?method=getActiveListings&realtorId=%s";
//                String BASE_URL = "http://keuahn.circlepix.dev/api/listing.php?method=getActiveListings&realtorId=%s";
                String urlString = String.format(BASE_URL, agentData.getRealtor().getId());
                String responseString = null;
                String status="";
                String message="";
                Boolean hasSelfServeStat=false;
                ArrayList<ListingInformation> listings = new ArrayList<>();
                ArrayList<ListingInformation> listing = new ArrayList<>();

                Log.i("urlstring", urlString);

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(urlString)
                        .build();

                try {

                    Call call = client.newCall(request);
                    final Response response = call.execute();

                    if (!response.isSuccessful()) {
                        Log.i("Response code", " " + response.code());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showAlertDialog("Error!", response.message());
                            }
                        });
                    }

                    responseString = response.body().string();
                    Log.i("Response code", response.code() + " ");
                    Log.v("OkHTTP Results: ", responseString);

                    try {

                        JSONObject Jobject = new JSONObject(responseString);
                        JSONObject data =  Jobject.getJSONObject("data");

                        status = Jobject.getString("status");
                        message = Jobject.getString("message");


                        JSONArray Jarray = data.getJSONArray("activeListings");
                        if(Jarray.equals(null)){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showAlertDialog("Error!", response.message());
                                }
                            });
                        }else{
                            for (int i = 0; i < Jarray.length(); i++) {
                                ListingInformation listingInformation = new ListingInformation();

                                JSONObject object = Jarray.getJSONObject(i);
                                JSONObject listingObj =  object.getJSONObject("listing");

                                if(!listingObj.isNull("homeId")){
                                    listingInformation.setId(listingObj.getString("homeId"));
                                    Log.v("homeId get: ", String.valueOf(listingInformation.getId()));
                                }
                                if(!listingObj.isNull("code")){
                                    listingInformation.setCode(listingObj.getString("code"));
                                }
                                if(!listingObj.isNull("address")){
                                    listingInformation.setAddress(listingObj.getString("address"));
                                    listingInformation.setAddressLine1(listingObj.getString("address"));
                                }
                                if(!listingObj.isNull("zipCode")){
                                    listingInformation.setZipcode(listingObj.getString("zipCode"));
                                }
                                if(!listingObj.isNull("city")){
                                    listingInformation.setCity(listingObj.getString("city"));
                                }
                                if(!listingObj.isNull("county")){
                                    listingInformation.setCounty(listingObj.getString("county"));
                                }
                                if(!listingObj.isNull("state")){
                                    listingInformation.setState(listingObj.getString("state"));
                                }
                                if(!listingObj.isNull("propertyType")){
                                    listingInformation.setPropertyType(listingObj.getString("propertyType"));
                                }
                                if(!listingObj.isNull("price")){
                                    listingInformation.setPrice(listingObj.getString("price"));
                                }
                                if(!listingObj.isNull("listingType")){
                                    listingInformation.setListingType(listingObj.getString("listingType"));
                                }
                                if(!listingObj.isNull("squareFootage")){
                                    listingInformation.setSquareFootage(listingObj.getString("squareFootage"));
                                }
                                if(!listingObj.isNull("bedrooms")){
                                    listingInformation.setBedrooms(listingObj.getString("bedrooms"));
                                }
                                if(!listingObj.isNull("fullBaths")){
                                    listingInformation.setFullBaths(listingObj.getString("fullBaths"));
                                }
                                if(!listingObj.isNull("threeQuaterBaths")){
                                    listingInformation.setThreeQuaterBaths(listingObj.getString("threeQuaterBaths"));
                                }
                                if(!listingObj.isNull("halfBaths")){
                                    listingInformation.setHalfBaths(listingObj.getString("halfBaths"));
                                    Log.v("halfbaths: ", String.valueOf(listingInformation.getHalfBaths()));
                                }
                                if(!listingObj.isNull("quarterBaths")){
                                    listingInformation.setQuarterBaths(listingObj.getString("quarterBaths"));
                                }
                                if(!listingObj.isNull("mlsNum")){
                                    listingInformation.setMlsNum(listingObj.getString("mlsNum"));
                                }
                                if(!listingObj.isNull("altmlsNum")){
                                    listingInformation.setAltmlsNum(listingObj.getString("altmlsNum"));
                                }
                                if(!listingObj.isNull("listingImage")){
                                    listingInformation.setImage(listingObj.getString("listingImage"));
                                }


                                if (!listingObj.isNull("listingDesc")) {
                               // if(!listingObj.getString("listingDesc").equals(null)){

                                    JSONArray listingDescArray = listingObj.getJSONArray("listingDesc");
                                    ArrayList<ListingDescription> tempListingDescriptions = new ArrayList<ListingDescription>();


                                        for (int j = 0; j < listingDescArray.length(); j++) {
                                            // Instance of checks that we have an object
                                            // In the cases where nothing is returned (in the format []), this will prevent the following code from executing and crashing
                                            if (listingDescArray.get(j) instanceof JSONObject) {

                                              //  JSONObject listingDescArrayObject = listingDescArray.getJSONObject(i);
                                              //  JSONObject listingDescObj = listingDescArrayObject.getJSONObject("listing");

                                                JSONObject listingDescObj  = listingDescArray.getJSONObject(j);
                                                ListingDescription listingDescription = new ListingDescription();

                                                if (!listingDescObj.getString("TDSCid").equals(null)) {
                                                    listingDescription.setTDSCid(listingDescObj.getString("TDSCid"));
                                                }

                                                if (!listingDescObj.getString("TDSCtourId").equals(null)) {
                                                    listingDescription.setTDSCtourId(listingDescObj.getString("TDSCtourId"));
                                                }

                                                if (!listingDescObj.getString("TDSCorder").equals(null)) {
                                                    listingDescription.setTDSCorder(listingDescObj.getString("TDSCorder"));
                                                }

                                                if (!listingDescObj.getString("TDSCvalue").equals(null)) {
                                                    listingDescription.setTDSCvalue(listingDescObj.getString("TDSCvalue"));
                                                }

                                                tempListingDescriptions.add(listingDescription);
                                            }
                                            listingInformation.setListingDesc(tempListingDescriptions);
                                        }

                                }


                                if(!object.getString("tourURL").equals(null)){
                                    listingInformation.setTourURL(object.getString("tourURL"));
                                }

                                if(!object.getString("socialMediaSites").equals(null)){
                                    listingInformation.setSocialMediaSites(object.getString("socialMediaSites"));
                                }

                                if(!object.getString("realEstateSites").equals(null)){
                                    listingInformation.setRealEstateSites(object.getString("realEstateSites"));
                                }
                                listingInformation.setAddressLine2(listingInformation.getCity() + ", " + listingInformation.getState() + " " + listingInformation.getZipcode());

                                listing.add(listingInformation); // arraylist of # of listings
                            }

                            agentData.setListingInformation(listing);

                            ArrayList<ListingDescription> listingDescriptions = new ArrayList<>();
                            ArrayList<ListingDescription> tempListingDescriptions = new ArrayList<>();
                            for(ListingInformation list : agentData.getListingInformation()){

                                Log.v("homeId: ", String.valueOf(list.getId()));
                                Log.v("code: ", String.valueOf(list.getCode()));

                                if(list.getListingDesc() != null){
                                    tempListingDescriptions.addAll(list.getListingDesc());
                                }


                            }
                            listingDescriptions = tempListingDescriptions;

                            for(ListingDescription listDescription : listingDescriptions){
                                Log.v("TDSCid: ", String.valueOf(listDescription.getTDSCid()));
                                Log.v("TDSCorder: ", String.valueOf(listDescription.getTDSCorder()));
                                Log.v("TDSCvalue: ", String.valueOf(listDescription.getTDSCvalue()));

                            }


                       /* for (int i =0; i< listing.size(); i++){
                            ListingInformation j = listing.get(i);

                            Log.v("homeId: ", String.valueOf(j.getId()));
                            Log.v("code: ", String.valueOf(j.getCode()));
                        }*/


                            Log.v("status: ", status);
                            Log.v("message: ", message);
                        }

                    }
                    catch (final JSONException e) {
                        Log.v("Error: ", e.getLocalizedMessage());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showAlertDialog("Error!", e.getLocalizedMessage().toString());
                            }
                        });
                    }


                   /* try {

                        JSONObject Jobject = new JSONObject(responseString);
                        JSONObject hasSelfServe =  Jobject.getJSONObject("data");
                        hasSelfServeStat = Boolean.valueOf(hasSelfServe.getString("hasSelfServe")); //store this value to sharedpreference so you don't need
                        // to check it again next time (maybe only on opening the listing page only)
                        status = Jobject.getString("status");
                        message = Jobject.getString("message");

                          *//*  JSONArray Jarray = Jobject.getJSONArray("data");

                            for (int i = 0; i < Jarray.length(); i++) {
                                JSONObject object = Jarray.getJSONObject(i);
                                hasSelfServe =  object.getString("hasSelfServe");
                            }*//*

                        Log.v("status: ", status);
                        Log.v("message: ", message);
                        Log.v("hasSelfServe: ", String.valueOf(hasSelfServeStat));
                    }
                    catch (JSONException e) {
                        Log.v("Error: ", e.getLocalizedMessage());
                    }

                    if(hasSelfServeStat == true){
                        Intent appSettingsIntent = new Intent(getActivity(), CreateListingActivity.class);
                        startActivity(appSettingsIntent);
                    }else{
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showAlertDialog("Self Serve Membership Required!", "Visit www.circlepix.com/tools and get a membership contract");
                            }
                        });

                    }
*/
                } catch (final IOException e) {
                    Log.d("LOGCAT", "Failed!");
                 //   Log.e("LOGCAT", (e.getMessage() == null) ? e.getMessage() : e.toString());

                   /* swipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                            showAlertDialog("Error","error");
                        }
                    });*/

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                            showAlertDialog("Error",(e.getMessage() == null) ? e.getMessage() : e.toString());
                        }
                    });
                }

                return responseString;
            }


            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                swipeRefreshLayout.setRefreshing(false);

                if (agentData.getListingInformation() == null || agentData.getListingInformation().size() == 0) {

                    noListing.setVisibility(View.VISIBLE);

                    String msg = agentData.isOfflineMode() ?
                            "There are no listings to display in offline mode." :
                            "Presently there are no active listings for your account.";
                } else {

                    noListing.setVisibility(View.GONE);
                    showListings(_view);
                }
            }
        }



        private void showListings(View v){


            recyclerView = (RecyclerView) v.findViewById(R.id.recyclerview);
            mLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(getActivity().getApplicationContext(),  new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            selectedPosGlobal = position;
                           // selectedListing = agentData.getListings().get(position);

                            selectedListing = agentData.getListingInformation().get(position);
                            //  selectUploadShare(getContext());

                            if(addNewListingTabLayout.isShown()){
                                // Hide addNewListingTabLayout and show listingOptionsLayout
                                Animation slideDown = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_down);
                                addNewListingTabLayout.startAnimation(slideDown);
                                addNewListingTabLayout.setVisibility(View.GONE);
                                addNewListingTabLayout.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Animation slideUp = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_up);
                                        listingOptionsLayout.startAnimation(slideUp);
                                        listingOptionsLayout.setVisibility(View.VISIBLE);

                                        cancelOptions.startAnimation(slideUp);
                                        cancelOptions.setVisibility(View.VISIBLE);
                                    }
                                }, 150);
                                selectedPos = position;
                            }else{
                                if(selectedPos != position){
                                    Animation slideDown = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_down);
                                    listingOptionsLayout.startAnimation(slideDown);
                                    listingOptionsLayout.setVisibility(View.GONE);


                                    listingOptionsLayout.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Animation slideUp = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_up);
                                            listingOptionsLayout.startAnimation(slideUp);
                                            listingOptionsLayout.setVisibility(View.VISIBLE);


                                        }
                                    }, 150);
                                }
                                selectedPos = position;

                            }

                        }
                    })
            );
          //  mAdapter = new ListingsTabAdapter(getActivity(), agentData.getListings());
            mAdapter = new ListingsTabAdapter(getActivity(), agentData.getListingInformation());

           /* //This is the code to provide a sectioned list
            List<SimpleSectionedRecyclerViewAdapter.Section> sections =
                    new ArrayList<SimpleSectionedRecyclerViewAdapter.Section>();

            //Sections
            sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0,getString(R.string.activity_description) ));
            //sections.add(new SimpleSectionedRecyclerViewAdapter.Section(2,"Section 2"));

            //Add your adapter to the sectionAdapter
            SimpleSectionedRecyclerViewAdapter.Section[] dummy = new SimpleSectionedRecyclerViewAdapter.Section[sections.size()];
            SimpleSectionedRecyclerViewAdapter mSectionedAdapter = new
                    SimpleSectionedRecyclerViewAdapter(getActivity().getApplicationContext(),R.layout.section,R.id.section_text, mAdapter);
            mSectionedAdapter.setSections(sections.toArray(dummy));

            //Apply this adapter to the RecyclerView
            recyclerView.setAdapter(mSectionedAdapter);*/

            recyclerView.setAdapter(mAdapter);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    visibleItemCount = recyclerView.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if (totalItemCount > previousTotal) {
                            loading = false;
                            previousTotal = totalItemCount;
                        }
                    }
                    if (!loading && (totalItemCount - visibleItemCount)
                            <= (firstVisibleItem + visibleThreshold)) {
                        // End has been reached

                        Log.i("Yaeye!", "end called");

                        // Do something

                        loading = true;
                    }
                }
            });

       //     header = (RecyclerViewHeader) v.findViewById(R.id.header);
       //     header.attachTo(recyclerView);

            // put all Button listneres here
            addBtnOnClickLinstener();

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
        }



        private void addBtnOnClickLinstener(){
            addNewListingLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkSelfServe(agentData.getRealtor().getId());
                    //Intent appSettingsIntent = new Intent(getActivity(), CreateListingActivity.class);
                    //startActivity(appSettingsIntent);
                }
            });

            listingEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // showAlertDialog("Edit Listing", "TODO: EditListing page");
                    Intent editListingIntent = new Intent(getActivity(), CreateListingActivity.class);
                    editListingIntent.putExtra("selectedListingPosition", selectedPos);
                    startActivity(editListingIntent);
                }
            });

            //not yet available for this version
   /*         listingBuyUpgrade.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAlertDialog("Buy Upgrade", "TODO: BuyUpgrade page");
                }
            });*/

            listingAddPhotos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAlertDialog("Add Photos", "TODO: AddPhotos page");
                }
            });

            listingAddVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chooseVideoSource(getContext());
                }
            });

            listingShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String subject = "This is just a sample text";
                    String msg = selectedListing.getTourURL() + "\n\nYou have been sent a listing tour. Click the link above to view the tour.";

                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                    // Add data to the intent, the receiving app will decide what to do with it.
                    intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                    intent.putExtra(Intent.EXTRA_TEXT, msg);
                    startActivity(Intent.createChooser(intent, "How do you want to share?"));
                }
            });


            cancelOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAddNewListing();
                }
            });
        }

        private void showAddNewListing(){
            if(!addNewListingTabLayout.isShown()){
                // Hide addNewListingTabLayout and show listingOptionsLayout
                Animation slideDown = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_down);
                listingOptionsLayout.startAnimation(slideDown);
                listingOptionsLayout.setVisibility(View.GONE);
         //       cancelOptions.startAnimation(slideDown);
                cancelOptions.setVisibility(View.GONE);
                cancelOptions.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Animation slideUp = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_up);
                        addNewListingTabLayout.startAnimation(slideUp);
                        addNewListingTabLayout.setVisibility(View.VISIBLE);

                    }
                }, 150);
            }
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

        private void showInternetAlert(){

            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "No internet connection!", Snackbar.LENGTH_LONG)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    });

            // Changing message text color
            snackbar.setActionTextColor(Color.RED);

            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);

            snackbar.show();

        }

        public void checkSelfServe(final String realtorId){

            Thread networkThread = new Thread() {

                public void run() {
                  //  String BASE_URL = "http://keuahn.circlepix.dev/api/listing.php?method=checkSelfServe&realtorId=%s";
                    String BASE_URL = "http://stag-mobile.circlepix.com/api/listing.php?method=checkSelfServe&realtorId=%s";
                    String urlString = String.format(BASE_URL, realtorId);
                    String responseString = null;
                    String status="";
                    String message="";
                    Boolean hasSelfServeStat=false;

                    Log.i("urlstring", urlString);

                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(urlString)
                            .build();

                    try {

                        Call call = client.newCall(request);
                        Response response = call.execute();

                        if (!response.isSuccessful()) {
                            Log.i("Response code", " " + response.code());
                        }

                        responseString = response.body().string();
                        Log.i("Response code", response.code() + " ");
                        Log.v("OkHTTP Results: ", responseString);

                        try {

                            JSONObject Jobject = new JSONObject(responseString);
                            JSONObject hasSelfServe =  Jobject.getJSONObject("data");
                            hasSelfServeStat = Boolean.valueOf(hasSelfServe.getString("hasSelfServe")); //store this value to sharedpreference so you don't need
                                                                                                        // to check it again next time (maybe only on opening the listing page only)
                            status = Jobject.getString("status");
                            message = Jobject.getString("message");

                          /*  JSONArray Jarray = Jobject.getJSONArray("data");

                            for (int i = 0; i < Jarray.length(); i++) {
                                JSONObject object = Jarray.getJSONObject(i);
                                hasSelfServe =  object.getString("hasSelfServe");
                            }*/

                            Log.v("status: ", status);
                            Log.v("message: ", message);
                            Log.v("hasSelfServe: ", String.valueOf(hasSelfServeStat));
                        }
                        catch (final JSONException e) {
                            Log.v("Error: ", e.getLocalizedMessage());
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showAlertDialog("Error", e.getLocalizedMessage().toString());
                                }
                            });
                        }

                        if(hasSelfServeStat == true){
                            Intent appSettingsIntent = new Intent(getActivity(), CreateListingActivity.class);
                            startActivity(appSettingsIntent);
                        }else{
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showAlertDialog("Self Serve Membership Required!", "Visit www.circlepix.com/tools and get a membership contract");
                                }
                            });

                        }

                    } catch (final IOException e) {
                        Log.d("LOGCAT", "Failed!");
                        Log.e("LOGCAT", (e.getMessage() == null) ? e.getMessage() : e.toString());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showAlertDialog("Error", (e.getMessage() == null) ? e.getMessage() : e.toString());
                            }
                        });

                    }

                    //  return responseString;
                }
            };
            networkThread.start();

        }


        public void selectUploadShare(final Context context) {

            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(R.layout.alertdialog_listings);

            LinearLayout uploadMedia = (LinearLayout) dialog.findViewById(R.id.uploadMediaLayout);
            uploadMedia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                  //  selectVideo(v);
                    chooseVideoSource(context);
                }
            });

            LinearLayout shareMedia = (LinearLayout) dialog.findViewById(R.id.shareMediaLayout);
            shareMedia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                    String subject = "This is just a sample text";
                    String msg = "This is just a sample text. You have been sent a listing tour.";

                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                    // Add data to the intent, the receiving app will decide what to do with it.
                    intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                    intent.putExtra(Intent.EXTRA_TEXT, msg);
                    startActivity(Intent.createChooser(intent, "How do you want to share?"));

                }
            });


            TextView cancel = (TextView) dialog.findViewById(R.id.textCancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                }
            });

            dialog.show();

        }

        public void chooseVideoSource(final Context context) {

            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(R.layout.alertdialog_video_source);

            LinearLayout youTubeURL = (LinearLayout) dialog.findViewById(R.id.youTubeUrlLayout);
            youTubeURL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Toast.makeText(getActivity().getApplicationContext(), "TODO: Go to Enter YouTube page", Toast.LENGTH_SHORT).show();

                }
            });

            LinearLayout recordVideo = (LinearLayout) dialog.findViewById(R.id.recordVideoLayout);
            recordVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                    if (Build.VERSION.SDK_INT >= 23){
                        // Marshmallow+

                        //   requestPermissions(new String[] {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
                        showCameraWithCheck();
                    } else {
                        // Pre-Marshmallow
                        captureVideo();
                    }

                }
            });

            LinearLayout selectVideo = (LinearLayout) dialog.findViewById(R.id.selectVideoLayout);
            selectVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (Build.VERSION.SDK_INT >= 23) {
                        // Marshmallow+
                        readFromStorageWithCheck();
                        //   showCameraWithCheck();
                    } else {
                        // Pre-Marshmallow
                        selectVideoFromStorage();
                    }

                }
            });


            TextView cancel = (TextView) dialog.findViewById(R.id.textCancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                }
            });

            dialog.show();

        }

        private void showCameraWithCheck() {
            List<String> permissionsNeeded = new ArrayList<String>();

            final List<String> permissionsList = new ArrayList<String>();
            if (!addPermission(permissionsList, Manifest.permission.CAMERA))
                permissionsNeeded.add("Camera");
            if (!addPermission(permissionsList, Manifest.permission.RECORD_AUDIO))
                permissionsNeeded.add("Microphone");
            if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                permissionsNeeded.add("Storage");

            if (permissionsList.size() > 0) {
                if (permissionsNeeded.size() > 0) {
                    // Need Rationale
                    String message = "Allow CirclePix to access to your " + permissionsNeeded.get(0);
                    for (int i = 1; i < permissionsNeeded.size(); i++) {
                        message = message + ", " + permissionsNeeded.get(i);
                    }
                    showMessageOKCancel(null, "OK", "Cancel", message + " to record and upload video.",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissions( permissionsList.toArray(new String[permissionsList.size()]),
                                            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                                }
                            });
                    return;
                }
                ActivityCompat.requestPermissions(getActivity(), permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);

                Toast.makeText(getActivity(), "Deny with Never ask again: SHOWN", Toast.LENGTH_SHORT)
                        .show();

                return;
            }

            captureVideo();
        }

        private void readFromStorageWithCheck() {
            int hasWriteContactsPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showMessageOKCancel(null, "OK", "Cancel", "Allow CirclePix to access to your Storage to upload video from your Gallery.",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                                            REQUEST_CODE_ASK_PERMISSIONS);

                                }
                            });
                    return;
                }

                //Show with "Never ask again"
                ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);

                appState.setNeverAskAgain(true);
                Toast.makeText(getActivity(), "Deny with Never ask again: SHOWN", Toast.LENGTH_SHORT)
                        .show();

                return;
            }
            selectVideoFromStorage();
        }

        private void showMessageOKCancel(String title, String positiveBtnMsg, String negativeBtnMsg, String message, DialogInterface.OnClickListener okListener) {
            new AlertDialog.Builder(getActivity())
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(positiveBtnMsg, okListener)
                    .setNegativeButton(negativeBtnMsg, null)
                    .create()
                    .show();
        }

        private boolean addPermission(List<String> permissionsList, String permission) {
            if (ContextCompat.checkSelfPermission(getActivity() ,permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
                // Check for Rationale Option
                if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission))
                    return false;
            }
            return true;
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            Log.v ("request code", String.valueOf(requestCode));
            switch (requestCode) {
                case REQUEST_CODE_ASK_PERMISSIONS:
                {
                    Log.v ("SINGLE PERMISSION", "captureVideo");
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Permission Granted

                        selectVideoFromStorage();
                    } else {
                        // Permission Denied

                        boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE );
                        if (!showRationale) {
                            // user denied flagging NEVER ASK AGAIN
                            // you can either enable some fall back,
                            // disable features of your app
                            // or open another dialog explaining
                            // again the permission and directing to
                            // the app setting
                            Toast.makeText(getActivity(), "Denied with Never ask again: TRUE", Toast.LENGTH_SHORT)
                                    .show();

                            showMessageOKCancel("Allow CirclePix to use your phone's storage?", "App Settings", "Not now",
                                    "This lets CirclePix store and access information like videos on your phone and its SD card.\n\n" +
                                            "To enable this, click App Settings below and activate Storage under the Permissions menu.",
                            new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent();
                                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                        intent.setData(uri);
                                        startActivity(intent);

                                    }
                            });

                         /*
                            final Snackbar snackbar = Snackbar.make(rootView, "Storage permission is denied.", Snackbar.LENGTH_LONG);
                            snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
                            snackbar.setAction("Settings", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (getActivity() == null) {
                                        return;
                                    }
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                }
                            });
                            snackbar.show();*/
                        } else {
                            Toast.makeText(getActivity(), "READ_EXTERNAL_PERMISSION Denied", Toast.LENGTH_SHORT)
                                    .show();
                            // this is a good place to explain the user
                            // why you need the permission and ask if he want
                            // to accept it (the rationale)
                        }

                    }

                }break;

                case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                {
                    Log.v ("MULTIPLE PERMISSIONS", "captureVideo");
                    Map<String, Integer> perms = new HashMap<String, Integer>();
                    // Initial
                    perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                    perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
                    perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                    // Fill with results
                    for (int i = 0; i < permissions.length; i++) {
                        perms.put(permissions[i], grantResults[i]);
                    }
                    // Check for CAMERA
                    Log.v ("opening", "captureVideo");
                    if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        // All Permissions Granted

                        captureVideo();

                    } else {
                        // Permission Denied

                        for (int i = 0, len = permissions.length; i < len; i++) {
                            String permission = permissions[i];
                            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                                boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission);
                                if (!showRationale) {
                                    // user denied flagging NEVER ASK AGAIN
                                    // you can either enable some fall back,
                                    // disable features of your app
                                    // or open another dialog explaining
                                    // again the permission and directing to
                                    // the app setting

                                    Toast.makeText(getActivity(), "Permission: " + permission + "is Denied with Never ask again", Toast.LENGTH_SHORT)
                                            .show();

                                    showMessageOKCancel("Allow CirclePix to use camera, microphone and storage?", "App Settings", "Not now",
                                            "This lets CirclePix capture video, record audio, store and access information like videos on your phone and its SD card.\n\n" +
                                                    "To enable this, click App Settings below and activate Camera, Microphone, and Storage under the Permissions menu.",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent();
                                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                                intent.setData(uri);
                                                startActivity(intent);

                                            }
                                    });

                                   /* Snackbar snackbar = Snackbar.make(rootView, "Some permission is denied.", Snackbar.LENGTH_LONG);
                                    snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
                                    snackbar.setAction("Settings", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (getActivity() == null) {
                                                return;
                                            }
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                            intent.setData(uri);
                                            startActivity(intent);
                                        }
                                    });
                                    snackbar.show();*/
                                    /*Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);*/


                               /* } else if (Manifest.permission.WRITE_CONTACTS.equals(permission)) {
                                    showRationale(permission, R.string.permission_denied_contacts);
                                    // user denied WITHOUT never ask again
                                    // this is a good place to explain the user
                                    // why you need the permission and ask if he want
                                    // to accept it (the rationale)*/
                                } else {
                                    Toast.makeText(getActivity(), "Permission: " + permission + "is Denied", Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        }
                    }
                }break;
                default:
                    Log.v ("DEFAULT", "captureVideo");
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }




        public void captureVideo(){
            Log.v ("opened", "captureVideo");
            File mediaFile = new File(Environment.getExternalStorageDirectory() + File.separator + "CirclePix" + File.separator);
            if (!mediaFile.exists()) {
                mediaFile.mkdirs();
            }

            final String fname = "vid_" + System.currentTimeMillis() + ".mp4";//Utils.getUniqueImageFilename();

            final File sdImageMainDirectory = new File(mediaFile, fname);

            Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            // Add file uri to intent
            captureVideoFileUri = Uri.fromFile(sdImageMainDirectory);
            takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, captureVideoFileUri);
            startActivityForResult(takeVideoIntent, CAPTURE_VIDEO);
        }  //ends here

        public void selectVideoFromStorage(){

            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a Video"), SELECT_VIDEO);
        }


        public void selectVideo(View v) {


            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());

            // set title
            alertDialogBuilder.setTitle("Video Source");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Would you like to record a new video or select from existing videos?")
                    .setPositiveButton("Record", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

					/*// Capture video using native camera app intent
                    // Store to a specific file since it's coming through as null in onActivityResult
					File mediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/myvideo.mp4");
					Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

					// Add file uri to intent
					captureVideoFileUri = Uri.fromFile(mediaFile);
					takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, captureVideoFileUri);
				    startActivityForResult(takeVideoIntent, CAPTURE_VIDEO);*/

                            if (Build.VERSION.SDK_INT >= 23){
                                // Marshmallow+

                             //   requestPermissions(new String[] {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
                                showCameraWithCheck();
                            } else {
                                // Pre-Marshmallow
                                captureVideo();
                            }


                         /*   if( hasCameraPermission == true){
                                File mediaFile = new File(Environment.getExternalStorageDirectory() + File.separator + "CirclePix" + File.separator);
                                if (!mediaFile.exists()) {
                                    mediaFile.mkdirs();
                                }

                                final String fname = "vid_" + System.currentTimeMillis() + ".mp4";//Utils.getUniqueImageFilename();

                                final File sdImageMainDirectory = new File(mediaFile, fname);

                                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                                // Add file uri to intent
                                captureVideoFileUri = Uri.fromFile(sdImageMainDirectory);
                                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, captureVideoFileUri);
                                startActivityForResult(takeVideoIntent, CAPTURE_VIDEO);
                            }
*/

                        }
                    })
                    .setNeutralButton("Existing", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            // Lookup existing video

                            if (Build.VERSION.SDK_INT >= 23) {
                                // Marshmallow+
                                readFromStorageWithCheck();
                             //   showCameraWithCheck();
                            } else {
                                // Pre-Marshmallow
                                selectVideoFromStorage();
                            }



                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();

        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {

            Log.d("LOGCAT", "resultCode is " + resultCode + " requestCode is " + requestCode);
            Log.d("LOGCAT", "result_ok is " + RESULT_OK);

            if ((resultCode == RESULT_OK && requestCode == SELECT_VIDEO)) {

                Uri selectedVideoUri = data.getData();

                Log.d("LOGCAT", "data is " + data.getData());
                selectedVideoPath = getPath(getActivity(), selectedVideoUri);

                Log.d("LOGCAT", "Video path is this: " + selectedVideoPath);

//				int responseCode = uploadToServer("" + selectedVideoPath,
//						"tour", "555555");
//
//				if (responseCode == 200) {
//					Log.d("LOGCAT", "Server communication successful");
//					Toast.makeText(getApplicationContext(),
//							"Server communication successful",
//							Toast.LENGTH_SHORT).show();
//				} else {
//					Log.d("LOGCAT", "Upload failed");
//					Toast.makeText(getApplicationContext(), "Upload failed",
//							Toast.LENGTH_SHORT).show();
//				}

                Intent intent = new Intent(getActivity().getApplicationContext(), VideoUploadActivity.class); //VideoUploadActivity.class);
                intent.putExtra("objectType", "tour");
                intent.putExtra("videoPath", selectedVideoPath);
                intent.putExtra("id", selectedListing.getId());
                intent.putExtra("code", selectedListing.getCode());
                intent.putExtra("line1", selectedListing.getAddressLine1());
                intent.putExtra("line2", selectedListing.getAddressLine2());
                intent.putExtra("imgURL", selectedListing.getImage());
                intent.putExtra("type", "listing");
                startActivity(intent);

            /*    try {
                    JSONObject props = new JSONObject();
                    props.put("Select Existing Video Selected", true);
                    mixpanel.track("ListingsTabActivity - Select Existing Video called", props);
                } catch (JSONException e) {
                    Log.e("MYAPP", "Unable to add properties to JSONObject", e);
                }*/

            } else if (requestCode == CAPTURE_VIDEO) {
                if (resultCode == RESULT_OK) {

                    if (captureVideoFileUri != null) {
                        Log.d("LOGCAT", "Video saved to:\n" + captureVideoFileUri);
                        Log.d("LOGCAT", "Video path:\n" + captureVideoFileUri.getPath());

                        //solution to recently taken videos not showing in gallery unless device is restarted
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            File f = new File(captureVideoFileUri.getPath());
                            Uri contentUri = Uri.fromFile(f);
                            mediaScanIntent.setData(contentUri);
                            getActivity().sendBroadcast(mediaScanIntent);
                        } else {
                            getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse(captureVideoFileUri.getPath())));
                        }

                        Intent intent = new Intent(getActivity().getApplicationContext(), VideoUploadActivity.class); //VideoUploadActivity.class);
                        intent.putExtra("objectType", "tour");
                        intent.putExtra("videoPath", captureVideoFileUri.getPath());
                        intent.putExtra("id", selectedListing.getId());
                        intent.putExtra("code", selectedListing.getCode());
                        intent.putExtra("line1", selectedListing.getAddressLine1());
                        intent.putExtra("line2", selectedListing.getAddressLine2());
                        intent.putExtra("imgURL", selectedListing.getImage());
                        intent.putExtra("type", "listing");
                        startActivity(intent);

                      /*  try {
                            JSONObject props = new JSONObject();
                            props.put("Capture Video Selected", true);
                            mixpanel.track("ListingsTabActivity - Capture Video called", props);
                        } catch (JSONException e) {
                            Log.e("MYAPP", "Unable to add properties to JSONObject", e);
                        }*/
                    }

                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(getActivity().getApplicationContext(), "Video recording cancelled.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Failed to record video.", Toast.LENGTH_SHORT).show();
                }
            }

            if (resultCode == 2) {
                getActivity().finish();
            }
        }

        @Override
        public void onDestroy() {
            if (recyclerView != null) {
                recyclerView.setAdapter(null);
            }
            super.onDestroy();
        }


        //getPath starts here
        @TargetApi(Build.VERSION_CODES.KITKAT)
        public static String getPath(final Context context, final Uri uri) {

            final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

            // DocumentProvider
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/"
                                + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"),
                            Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{split[1]};

                    return getDataColumn(context, contentUri, selection,
                            selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }

            return null;
        }

        /**
         * Get the value of the data column for this Uri. This is useful for
         * MediaStore Uris, and other file-based ContentProviders.
         *
         * @param context       The context.
         * @param uri           The Uri to query.
         * @param selection     (Optional) Filter used in the query.
         * @param selectionArgs (Optional) Selection arguments used in the query.
         * @return The value of the _data column, which is typically a file path.
         */
        public static String getDataColumn(Context context, Uri uri,
                                           String selection, String[] selectionArgs) {

            Cursor cursor = null;
            final String column = "_data";
            final String[] projection = {column};

            try {
                cursor = context.getContentResolver().query(uri, projection,
                        selection, selectionArgs, null);
                if (cursor != null && cursor.moveToFirst()) {
                    final int column_index = cursor.getColumnIndexOrThrow(column);
                    return cursor.getString(column_index);
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
            return null;
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is ExternalStorageProvider.
         */
        public static boolean isExternalStorageDocument(Uri uri) {
            return "com.android.externalstorage.documents".equals(uri
                    .getAuthority());
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is DownloadsProvider.
         */
        public static boolean isDownloadsDocument(Uri uri) {
            return "com.android.providers.downloads.documents".equals(uri
                    .getAuthority());
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is MediaProvider.
         */
        public static boolean isMediaDocument(Uri uri) {
            return "com.android.providers.media.documents".equals(uri
                    .getAuthority());
        }
        //ends here
    }

}
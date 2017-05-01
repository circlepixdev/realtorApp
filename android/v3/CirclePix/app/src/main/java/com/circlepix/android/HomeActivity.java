package com.circlepix.android;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.circlepix.android.beans.AgentData;
import com.circlepix.android.beans.AgentProfile;
import com.circlepix.android.data.Presentation;
import com.circlepix.android.data.PresentationDataSource;
import com.circlepix.android.helpers.CropCircleTransformation;
import com.circlepix.android.helpers.Globals;
import com.circlepix.android.helpers.NetworkChangeReceiver;
import com.circlepix.android.helpers.NetworkUtil;
import com.circlepix.android.sync.commands.SyncPresentations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Keuahn on 8/27/2016.
 *
 * This is the latest Activity - not MainActivity
 *
 * */

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private CirclePixAppState appState;

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private View navHeaderLayout;
  //  private RelativeLayout drawerHeader;
    private LinearLayout agentInfo;

    private FragmentManager manager;
    private FragmentTransaction transaction;

    private LinearLayout tabsLayout;
    public static ImageView listingsTab;
    private ImageView presentationsTab;
    private ImageView customerSupportTab;

    private LinearLayout logoutLayout;
    private Button logoutBtn;
    public static LinearLayout networkStatLayout;
    public static LinearLayout networkStatBG;
    public static TextView networkStatus;

    private AgentData agentData;
    private ImageView agentImage;
    private TextView agentName;
    private TextView agentEmail;
    private ProgressBar progressBar;
    private ImageView circlePixLogo;
    private boolean hasInternetConnection;
    private GetAgentProfileInformation getAgentProfileInformation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navHeaderLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
//        drawerHeader = (RelativeLayout) navHeaderLayout.findViewById(R.id.drawer_header);
        agentInfo = (LinearLayout) navHeaderLayout.findViewById(R.id.agent_info);

        tabsLayout = (LinearLayout) findViewById(R.id.tabs_layout);
        listingsTab = (ImageView) findViewById(R.id.btn_listings);
        presentationsTab = (ImageView) findViewById(R.id.btn_presentations);
        customerSupportTab = (ImageView) findViewById(R.id.btn_customer_support);

        agentName = (TextView) navHeaderLayout.findViewById(R.id.agent_name);
        agentImage = (ImageView) navHeaderLayout.findViewById(R.id.agent_img);
        agentEmail = (TextView) navHeaderLayout.findViewById(R.id.agent_email);
        progressBar = (ProgressBar) navHeaderLayout.findViewById(R.id.progressBar);
        logoutLayout = (LinearLayout) findViewById(R.id.logout_layout);
        logoutBtn = (Button)findViewById(R.id.logout_button);
        circlePixLogo = (ImageView)findViewById(R.id.circlepix_logo);
        networkStatLayout = (LinearLayout) findViewById(R.id.networkStatLayout);
        networkStatBG  = (LinearLayout) findViewById(R.id.networkStatBG);
        networkStatus = (TextView) findViewById(R.id.networkStatus);

        // Get global shared data
        agentData = AgentData.getInstance();

        // Setup application class
        appState = ((CirclePixAppState)getApplicationContext());
        appState.setContextForPreferences(this);

      //  appState.setFirstRun(true);

        // Get the realtor data for use later
        Bundle extras = getIntent().getExtras();
        String responseString = null;

//        if (extras != null) {
//
//            responseString = extras.getString("responseString");
//            Log.v("responseString Home not nul", "ji");
//        } else {
//            SharedPreferences settings = getSharedPreferences(AccountActivity.PREFS_NAME, 0);
//            responseString = settings.getString("lastResponse", "");
//            Log.v("responseString Home nul", settings.getString("lastResponse", ""));
//        }

        SharedPreferences settings = getSharedPreferences(AccountActivity.PREFS_NAME, 0);
        responseString = settings.getString("lastResponse", "");
        Log.v("responseString Home nul", settings.getString("lastResponse", ""));

        if(responseString != null){
            agentData.parseResponseString(responseString);
            Log.v("responseString !nul", agentData.getRealtor().getId());

            if(isNetworkAvailable() && agentData.getRealtor().getId() != ""){
                // Get Agent Information
                getAgentProfileInformation = new GetAgentProfileInformation();

                if(Build.VERSION.SDK_INT >= 11) {
                    getAgentProfileInformation.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }else {
                    getAgentProfileInformation.execute();
                }

            }
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        MainPromosActivity.PlaceholderFragment frg = MainPromosActivity.PlaceholderFragment.newInstance(0);

        manager = getSupportFragmentManager();//create an instance of fragment manager
        transaction = manager.beginTransaction();//create an instance of Fragment-transaction
        transaction.add(R.id.container, frg, "PROMOS_TAG");
        transaction.commit();

        navigationView.setNavigationItemSelectedListener(this);
 /*       drawerHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(profileIntent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });
*/

        circlePixLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    appState.setActiveClassName("");

                    MainPromosActivity.PlaceholderFragment test = (MainPromosActivity.PlaceholderFragment) getSupportFragmentManager().findFragmentByTag("PROMOS_TAG");



                    //replace with our main home page - Promos
                    MainPromosActivity.PlaceholderFragment frg = MainPromosActivity.PlaceholderFragment.newInstance(0);
                    transaction = manager.beginTransaction();

                    if (test != null && test.isVisible()) {
                        //MainPromosActivity is showing so need to put slide_in_right & slide_out_right animation

                        // Replace whatever is in the fragment_container view with this fragment,
                        // and add the transaction to the back stack if needed
                        transaction.replace(R.id.container, frg, "PROMOS_TAG");
                        transaction.addToBackStack(null);

                        // Commit the transaction
                        transaction.commit();
                    }
                    else {
                        Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                        tabsLayout.startAnimation(slideUp);
                        tabsLayout.setVisibility(View.VISIBLE);
                        
                        // Add animation in transition
                        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right);
                        // Replace whatever is in the fragment_container view with this fragment,
                        // and add the transaction to the back stack if needed
                        transaction.replace(R.id.container, frg, "PROMOS_TAG");
                        transaction.addToBackStack(null);

                        // Commit the transaction
                        transaction.commit();

                    }

                }
            }
        });

        agentInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(profileIntent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });

        agentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(profileIntent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });


        listingsTab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //hide tab layout
                if(tabsLayout.isShown()){
                    hideView(tabsLayout);
                }
            //    Animation slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
            //    tabsLayout.startAnimation(slideDown);

                // Create new fragment and transaction
                ListingsTabActivity.PlaceholderFragment frg = ListingsTabActivity.PlaceholderFragment.newInstance(0);
                transaction = manager.beginTransaction();

                // Add animation in transition
                transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack if needed
                transaction.replace(R.id.container, frg, "LISTINGS_TAG");
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();

               /* Intent wizardActivity = new Intent(HomeActivity.this, ListingsTabActivity.class);
                startActivity(wizardActivity);*/
            }
        });

        presentationsTab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //hide tab layout
                if(tabsLayout.isShown()){
                    hideView(tabsLayout);
                }
                //    Animation slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
                //    tabsLayout.startAnimation(slideDown);

                // Create new fragment and transaction
                PresentationsTabActivity.PlaceholderFragment frg = PresentationsTabActivity.PlaceholderFragment.newInstance(0);
                transaction = manager.beginTransaction();

                // Add animation in transition
                transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack if needed
                transaction.replace(R.id.container, frg, "PRESENTATIONS_TAG");
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        });

        customerSupportTab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //hide tab layout
                if(tabsLayout.isShown()){
                    hideView(tabsLayout);
                }
                //    Animation slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
                //    tabsLayout.startAnimation(slideDown);

                // Create new fragment and transaction
                CustomerSupportActivityTab.PlaceholderFragment frg = CustomerSupportActivityTab.PlaceholderFragment.newInstance(0);
                transaction = manager.beginTransaction();

                // Add animation in transition
                transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack if needed
                transaction.replace(R.id.container, frg, "CUSTOMER_SUPPORT_TAG");
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        });


        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmLogout();
            }
        });

        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do nothing
                // This is just a dummy onClickListener so that the items on NavigationDrawer which is behind this layout will not be clickable if you click on this area
            }
        });




//        setAgentDetails();
    }

    public void onResume(){
        super.onResume();

        // Get global shared data
        agentData = AgentData.getInstance();

        // Setup application class
        appState = ((CirclePixAppState)getApplicationContext());
        appState.setContextForPreferences(this);

        if(!appState.isFirstRun()){
            Log.v("isFirstRun?", String.valueOf(appState.isFirstRun()));
      //      appState.setFirstRun(false);


            String TAG_NAME = "";
            Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
            tabsLayout.startAnimation(slideUp);
            tabsLayout.setVisibility(View.VISIBLE);

            if(appState.getActiveClassName().equalsIgnoreCase("ListingsTabActivity")){
                //if(tabsLayout.isShown()){
                    hideView(tabsLayout);
               // }

                ListingsTabActivity.PlaceholderFragment frg = ListingsTabActivity.PlaceholderFragment.newInstance(0);
                transaction = manager.beginTransaction();

                // Add animation in transition
                //transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack if needed
                transaction.replace(R.id.container, frg, "LISTINGS_TAG");
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();

            }else if(appState.getActiveClassName().equalsIgnoreCase("PresentationsTabActivity")){
              //  if(tabsLayout.isShown()){
                    hideView(tabsLayout);
               // }
                // Create new fragment and transaction
                PresentationsTabActivity.PlaceholderFragment frg = PresentationsTabActivity.PlaceholderFragment.newInstance(0);
                transaction = manager.beginTransaction();

                // Add animation in transition
                //transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack if needed
                transaction.replace(R.id.container, frg, "PRESENTATIONS_TAG");
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();

            }else{
                // Create new fragment and transaction
                MainPromosActivity.PlaceholderFragment frg = MainPromosActivity.PlaceholderFragment.newInstance(0);
                transaction = manager.beginTransaction();

                // Add animation in transition
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right);
                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack if needed
                transaction.replace(R.id.container, frg, "PROMOS_TAG");
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();

                //               MainPromosActivity.PlaceholderFragment frg = MainPromosActivity.PlaceholderFragment.newInstance(0);
//                manager = getSupportFragmentManager();//create an instance of fragment manager
//                transaction = manager.beginTransaction();//create an instance of Fragment-transaction
//                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right);
//                transaction.replace(R.id.container, frg, "PROMOS_TAG");
//                transaction.commit();
            }




        }else{
            Log.v("isFirstRun?", String.valueOf(appState.isFirstRun()));

            ConnectivityManager manager = (ConnectivityManager) HomeActivity.this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnectedOrConnecting()){

                hasInternetConnection = true;
            }else {
                hasInternetConnection = false;
            }

            if(hasInternetConnection == true) {
                Log.v("sync pres?", "from homeactivity");
                final String realtorIdStr = agentData.getRealtor().getId();
                final PresentationDataSource ds = new PresentationDataSource(HomeActivity.this);
                // final boolean firstRun = (extras != null) ? extras.getBoolean("isFirstRun") : false;
                Thread syncThread = new Thread() {
                    public void run() {
//                        SyncPresentations.runCommand(getActivity(), realtorIdStr,  ds, null);
                        SyncPresentations.runCommand(HomeActivity.this, realtorIdStr, ds, new Runnable() {
                            @Override
                            public void run() {

                                Log.v("Last Item totalSize?", "after sync pres");
                            }
                        });
                    }
                };
                syncThread.start();

            }

            appState.setFirstRun(false);

        }


      /*  //replace with our main home page - Promos
        MainPromosActivity.PlaceholderFragment frg = MainPromosActivity.PlaceholderFragment.newInstance(0);
        transaction = manager.beginTransaction();

        // Add animation in transition
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right);
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack if needed
        transaction.replace(R.id.container, frg, "PROMOS_TAG");
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();*/

        ComponentName component=new ComponentName(this, NetworkChangeReceiver.class);
        getPackageManager()
                .setComponentEnabledSetting(component,
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);

        String status = NetworkUtil.getConnectivityStatusString(getApplicationContext());

        if (status.equals("Not connected to Internet")) {
            networkStatLayout.setVisibility(View.VISIBLE);
            networkStatus.setVisibility(View.VISIBLE);
            networkStatBG.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.noInternetConn));
            networkStatus.setText(status);
        }else if (status.equals("Waiting for Network")){
            networkStatLayout.setVisibility(View.VISIBLE);
            networkStatus.setVisibility(View.VISIBLE);
            networkStatBG.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.waitingForNetwork));
            networkStatus.setText(status);
        }else{
            //no need to show "Connected" status on activityResume if it is already connected to the Internet unless there is changes on the connectivity which is to be done by NetworkChangeReceiver
            networkStatLayout.setVisibility(View.GONE);
            networkStatus.setVisibility(View.GONE);
        }



    }

    public void onPause(){
        super.onPause();
        ComponentName component=new ComponentName(this, NetworkChangeReceiver.class);
        getPackageManager()
                .setComponentEnabledSetting(component,
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbindDrawables(findViewById(R.id.drawer_layout));
        System.gc();
    }

    private void unbindDrawables(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item1= menu.findItem(R.id.action_settings_default);
        item1.setVisible(false);

        MenuItem item2= menu.findItem(R.id.action_settings);
        item2.setVisible(false);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
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

         /*   case R.id.action_settings:
                Intent productIntent = new Intent(this, SecondActivity.class);
                startActivity(productIntent);
                return true;*/
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_edit_profile:
                drawer.closeDrawer(GravityCompat.START);
                Intent editProfileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(editProfileIntent);
                //   overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                return true;
            case R.id.action_listings:
                drawer.closeDrawer(GravityCompat.START);
                listingsTab.performClick();
                return true;
            case R.id.action_presentations:
                drawer.closeDrawer(GravityCompat.START);
                presentationsTab.performClick();
                return true;
            case R.id.action_customer_support:
                drawer.closeDrawer(GravityCompat.START);
                customerSupportTab.performClick();
                return true;
          /*  case R.id.action_shop:
                Intent productIntent = new Intent(getApplicationContext(), SecondActivity.class);
                startActivity(productIntent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                return true;
            case R.id.action_customer_support:
                Toast.makeText(MainActivity.this, "TODO " + item.getTitle() + "Page", Toast.LENGTH_SHORT).show();
                return true;*/

            case R.id.action_promo:

                drawer.closeDrawer(GravityCompat.START);
                appState.setFirstRun(false);
                Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                tabsLayout.startAnimation(slideUp);
                tabsLayout.setVisibility(View.VISIBLE);

                MainPromosActivity.PlaceholderFragment frg = MainPromosActivity.PlaceholderFragment.newInstance(0);
                manager = getSupportFragmentManager();//create an instance of fragment manager
                transaction = manager.beginTransaction();//create an instance of Fragment-transaction
                transaction.replace(R.id.container, frg, "PROMOS_TAG");
                transaction.commit();
                return true;

        /*    case R.id.action_rate_us:
                drawer.closeDrawer(GravityCompat.START);
                Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
                }
                return true;
        */
           // case R.id.action_app_settings:
           //     Toast.makeText(HomeActivity.this, "TODO " + item.getTitle() + "Page", Toast.LENGTH_SHORT).show();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void confirmLogout() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(HomeActivity.this);
        dialog.setTitle("Confirm");
        dialog.setMessage("Are you sure you want to log out?");
        dialog.setPositiveButton("Log out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handleLogoutMenu();
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.create().show();
    }

    private void handleLogoutMenu() {
        AgentData.getInstance().setLoggedIn(false);  //uncommented 080415
        AgentData.getInstance().setOfflineMode(false); //uncommented 080415

        Globals.USERNAME = null;
        Globals.PASSWORD = null;
        Globals.REMEMBERME = false;

//		appState.clearLoginSharedPreferences();

        // Editor object to make preferences changes
        SharedPreferences settings = getSharedPreferences(AccountActivity.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("username", "");
        editor.putString("password", "");
        editor.putBoolean("rememberMe", false);
        editor.putString("lastResponse", "");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            editor.apply();
        } else {
            editor.commit();
        }

        // Clear the database  080415
        PresentationDataSource dao = new PresentationDataSource(HomeActivity.this);
        dao.open(true);
        List<Presentation> presentations = dao.getAllPresentations();
        for (Presentation p : presentations) {
            dao.deletePresentation(p);
        }
        dao.close();

        Intent i = new Intent(HomeActivity.this, LoginActivity.class);
        startActivityForResult(i, 0);

        // Restore preferences
        SharedPreferences prefs = getSharedPreferences(AccountActivity.PREFS_NAME, 0);
        Log.d("LOGCAT ACCOUNTACTIVITY", "rememberMe is now: " + prefs.getBoolean("rememberMe", false));
    }

    private void setAgentDetails(){
        Log.v("agentImage: ", agentData.getRealtor().getImage());
        Log.v("agentName: ", agentData.getRealtor().getName());
        Log.v("agentEmail: ", agentData.getRealtor().getEmail());

//        if(!agentData.getRealtor().getImage().equals("")){
        if(!agentData.getAgentProfileInformation().getAgentImage().equals("")){
            Glide.with(getApplicationContext())
                    .load(agentData.getAgentProfileInformation().getAgentImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .placeholder(R.drawable.image_circle_gradient)
                    .error(R.drawable.broken_file_icon)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                   // .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                    .into(agentImage);
        }else{
            Glide.with(getApplicationContext()).load("").into(agentImage);
            progressBar.setVisibility(View.GONE);
        }

        if(!agentData.getAgentProfileInformation().getFullName().equals(null) && !agentData.getAgentProfileInformation().getFullName().isEmpty()){
            agentName.setText(agentData.getAgentProfileInformation().getFullName());
        }else{
            agentName.setText("");
        }

        if(!agentData.getAgentProfileInformation().getEmail().equals(null) && !agentData.getAgentProfileInformation().getEmail().isEmpty()){
            agentEmail.setText(agentData.getAgentProfileInformation().getEmail());
        }else{
            agentEmail.setText("");
        }

        //agentName.setText(agentData.getRealtor().getName());
        //agentEmail.setText(agentData.getRealtor().getEmail());
    }

    private void hideView(final View view){
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        //use this to make it longer:  animation.setDuration(1000);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }
        });

        view.startAnimation(animation);
    }

    private void showView(final View view){
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        //use this to make it longer:  animation.setDuration(1000);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }
        });

        view.startAnimation(animation);
    }


    protected class GetAgentProfileInformation extends AsyncTask<Context, Integer, String> {



        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(Context... params) {


            //    String BASE_URL = "http://keuahn.circlepix.dev/api/agentProfile.php?method=getAgentInfo&realtorId=%s";
            String BASE_URL = "http://stag-mobile.circlepix.com/api/agentProfile.php?method=getAgentInfo&realtorId=%s";
            String urlString = String.format(BASE_URL, agentData.getRealtor().getId());
            String responseString = null;
            String status="";
            String message="";

            //  AgentProfile agentProfile = new AgentProfile();

            // ArrayList<AgentProfile> profile = new ArrayList<>();

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

                }

                responseString = response.body().string();
                Log.i("Response code", response.code() + " ");
                Log.v("OkHTTP Results: ", responseString);

                try {

                    JSONObject Jobject = new JSONObject(responseString);
                    JSONObject data =  Jobject.getJSONObject("data");

                    status = Jobject.getString("status");
                    message = Jobject.getString("message");

                    AgentProfile agentProfileInformation = new AgentProfile();

                    //       JSONObject object = Jarray.getJSONObject(i);
                    JSONObject agentInfoObj =  data.getJSONObject("agentInfo");

                    if(!agentInfoObj.isNull("id")){
                        agentProfileInformation.setId(agentInfoObj.getString("id"));
                        Log.v("agent id get: ", String.valueOf(agentProfileInformation.getId()));
                    }else{
                        agentProfileInformation.setId("");
                    }
                    if(!agentInfoObj.isNull("firstName")){
                        agentProfileInformation.setFirstName(agentInfoObj.getString("firstName"));
                    }else{
                        agentProfileInformation.setFirstName("");
                    }
                    if(!agentInfoObj.isNull("lastName")){
                        agentProfileInformation.setLastName(agentInfoObj.getString("lastName"));
                    }else{
                        agentProfileInformation.setLastName("");
                    }
                    if(!agentInfoObj.isNull("agency")){
                        agentProfileInformation.setAgency(agentInfoObj.getString("agency"));
                    }else{
                        agentProfileInformation.setAgency("");
                    }
                    if(!agentInfoObj.isNull("phoneNumber")){
                        agentProfileInformation.setPhoneNumber(agentInfoObj.getString("phoneNumber"));
                    }else{
                        agentProfileInformation.setPhoneNumber("");
                    }
                    if(!agentInfoObj.isNull("cellNumber")){
                        agentProfileInformation.setCellNumber(agentInfoObj.getString("cellNumber"));
                    }else{
                        agentProfileInformation.setCellNumber("");
                    }
                    if(!agentInfoObj.isNull("mobileProvider")){
                        agentProfileInformation.setCellProvider(agentInfoObj.getString("mobileProvider"));
                    }else{
                        agentProfileInformation.setCellProvider("");
                    }
                    if(!agentInfoObj.isNull("textNotification")){
                        agentProfileInformation.setTextNotifications(agentInfoObj.getString("textNotification"));
                    }else{
                        agentProfileInformation.setTextNotifications("");
                    }
                    if(!agentInfoObj.isNull("faxNumber")) {
                        agentProfileInformation.setFaxNumber(agentInfoObj.getString("faxNumber"));
                    }else{
                        agentProfileInformation.setFaxNumber("");
                    }
                    if(!agentInfoObj.isNull("emailAddress")){
                        agentProfileInformation.setEmail(agentInfoObj.getString("emailAddress"));
                    }else{
                        agentProfileInformation.setEmail("");
                    }
                    if(!agentInfoObj.isNull("website")){
                        agentProfileInformation.setWebsite(agentInfoObj.getString("website"));
                    }else{
                        agentProfileInformation.setWebsite("");
                    }
                    if(!agentInfoObj.isNull("address")){
                        agentProfileInformation.setStreetAddress(agentInfoObj.getString("address"));
                    }else{
                        agentProfileInformation.setStreetAddress("");
                    }
                    if(!agentInfoObj.isNull("city")){
                        agentProfileInformation.setCity(agentInfoObj.getString("city"));
                    }else{
                        agentProfileInformation.setCity("");
                    }
                    if(!agentInfoObj.isNull("state")){
                        agentProfileInformation.setState(agentInfoObj.getString("state"));
                    }else{
                        agentProfileInformation.setState("");
                    }
                    if(!agentInfoObj.isNull("zipcode")){
                        agentProfileInformation.setZipcode(agentInfoObj.getString("zipcode"));
                    }else{
                        agentProfileInformation.setZipcode("");
                    }

                    if(!agentInfoObj.isNull("leadbeePin")){
                        agentProfileInformation.setLeadBeePin(agentInfoObj.getString("leadbeePin"));
                    }else{
                        agentProfileInformation.setLeadBeePin("");
                    }
                    if(!agentInfoObj.isNull("productNumber")){
                        agentProfileInformation.setProductNumber(agentInfoObj.getString("productNumber"));
                    }else{
                        agentProfileInformation.setProductNumber("");
                    }
                    if(!agentInfoObj.isNull("billingType")){
                        agentProfileInformation.setBillingType(agentInfoObj.getString("billingType"));
                    }else{
                        agentProfileInformation.setBillingType("");
                    }
                    if(!agentInfoObj.isNull("stateLicenseNumber")){
                        agentProfileInformation.setStateLicenseNumber(agentInfoObj.getString("stateLicenseNumber"));
                    }else{
                        agentProfileInformation.setStateLicenseNumber("");
                    }
                    if(!agentInfoObj.isNull("bio")){
                        agentProfileInformation.setBiography(agentInfoObj.getString("bio"));
                    }else{
                        agentProfileInformation.setBiography("");
                    }
                    if(!agentInfoObj.isNull("youtubeId")){
                        agentProfileInformation.setYoutubeId(agentInfoObj.getString("youtubeId"));
                    }else{
                        agentProfileInformation.setYoutubeId("");
                    }

                    Log.v("agentInfo Address()", agentProfileInformation.getStreetAddress());
                    agentProfileInformation.setFullAddress(agentProfileInformation.getStreetAddress() + ", " + agentProfileInformation.getCity() + " " + agentProfileInformation.getState());
                    Log.v("agentInfo FullAddress()", agentProfileInformation.getFullAddress());

                    agentProfileInformation.setFullName(agentProfileInformation.getFirstName() + " " + agentProfileInformation.getLastName());

                    if (!agentInfoObj.isNull("socialMediaLinks")) {

                        JSONArray socialMediaLinksArray = agentInfoObj.getJSONArray("socialMediaLinks");

                        if(socialMediaLinksArray.length() != 0){
                            agentProfileInformation.setFacebookURL("");
                            agentProfileInformation.setYoutubeURL("");
                            agentProfileInformation.setBlogURL("");
                            agentProfileInformation.setLinkedinURL("");
                            agentProfileInformation.setTwitterURL("");
                            agentProfileInformation.setPinterestURL("");

                            Log.v("ffff social lenght:" , String.valueOf(socialMediaLinksArray.length()));
                            for (int j = 0; j < socialMediaLinksArray.length(); j++) {
                                // Instance of checks that we have an object
                                // In the cases where nothing is returned (in the format []), this will prevent the following code from executing and crashing
                                if (socialMediaLinksArray.get(j) instanceof JSONObject) {

                                    JSONObject socialMediaObj = socialMediaLinksArray.getJSONObject(j);
                                    //  if (!socialMediaObj.getString("facebook").equals(null)) {
                                    if (!socialMediaObj.isNull("facebook")) {
                                        agentProfileInformation.setFacebookURL(socialMediaObj.getString("facebook"));
                                    } else {
                                        agentProfileInformation.setFacebookURL("");
                                    }
                                    Log.v("facebook Link: ", String.valueOf(agentProfileInformation.getFacebookURL()));

                                    //     if (!socialMediaObj.getString("youtube").equals(null)) {
                                    if (!socialMediaObj.isNull("youtube")) {
                                        agentProfileInformation.setYoutubeURL(socialMediaObj.getString("youtube"));
                                    } else {
                                        agentProfileInformation.setYoutubeURL("");
                                    }

                                    //   if (!socialMediaObj.getString("blog").equals(null)) {
                                    if (!socialMediaObj.isNull("blog")) {
                                        agentProfileInformation.setBlogURL(socialMediaObj.getString("blog"));
                                    } else {
                                        agentProfileInformation.setBlogURL("");
                                    }

//                                if(!socialMediaLinksObj.isNull("website")){
//                                    agentProfileInformation.setWebsiteURL(socialMediaLinksObj.getString("website"));
//                                }

                                    //  if (!socialMediaObj.getString("linkedin").equals(null)) {
                                    if (!socialMediaObj.isNull("linkedin")) {
                                        agentProfileInformation.setLinkedinURL(socialMediaObj.getString("linkedin"));
                                    } else {
                                        agentProfileInformation.setLinkedinURL("");
                                    }

                                    //  if (!socialMediaObj.getString("twitter").equals(null)) {
                                    if (!socialMediaObj.isNull("twitter")) {
                                        agentProfileInformation.setTwitterURL(socialMediaObj.getString("twitter"));
                                    } else {
                                        agentProfileInformation.setTwitterURL("");
                                    }

                                    // if (!socialMediaObj.getString("pinterest").equals(null)) {
                                    if (!socialMediaObj.isNull("pinterest")) {
                                        agentProfileInformation.setPinterestURL(socialMediaObj.getString("pinterest"));
                                    } else {
                                        agentProfileInformation.setPinterestURL("");
                                    }
                                } else {
                                    Log.v("nothing: ", status);
                                }
                            }
                        }


                    }else{
                        Log.v("status rrr: ", status);
                    }


                    //String hostURL = "keuahn.circlepix.dev";
                    String hostURL = "stag-mobile.circlepix.com";

                    if(!data.isNull("agentPic")){

                        if(data.getString("agentPic").toLowerCase().contains(hostURL.toLowerCase())){
                            agentProfileInformation.setAgentImage(data.getString("agentPic"));
                        }else{
                            agentProfileInformation.setAgentImage("http://" + hostURL + data.getString("agentPic"));
                        }
                    }else{
                        agentProfileInformation.setAgentImage("");
                    }

                    Log.v("agentPic: ", String.valueOf(agentProfileInformation.getAgentImage()));

                    if(!data.isNull("agentLogo")){

                        if(data.getString("agentLogo").toLowerCase().contains(hostURL.toLowerCase())){
                            agentProfileInformation.setAgentLogo(data.getString("agentLogo"));
                        }else{
                            agentProfileInformation.setAgentLogo("http://" + hostURL + data.getString("agentLogo"));
                        }
                    }else{
                        agentProfileInformation.setAgentLogo("");
                    }
                    Log.v("agentLogo: ", String.valueOf(agentProfileInformation.getAgentLogo()));

                    agentProfileInformation.setFullAddress(agentProfileInformation.getCity() + ", " + agentProfileInformation.getState() + " " + agentProfileInformation.getZipcode());

                    //    agentProfile.add(agentProfileInformation); // arraylist of # of agentProfileInformation
                    // }

                    agentData.setAgentProfileInformation(agentProfileInformation);

                    Log.v("status: ", status);
                    Log.v("message: ", message);



                }
                catch (final JSONException e) {
                    Log.v("Error: ", e.getLocalizedMessage());

                }


            } catch (final IOException e) {


            }

            return responseString;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (agentData.getAgentProfileInformation() != null) {
                setAgentDetails();
            }

        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected())
        {
            isAvailable = true;

        }
        return isAvailable;
    }


    public void onBackPressed(){

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            MainPromosActivity.PlaceholderFragment test = (MainPromosActivity.PlaceholderFragment) getSupportFragmentManager().findFragmentByTag("PROMOS_TAG");

            if (test != null && test.isVisible()) {
                //app can now be exited or moved to background
                moveTaskToBack(true);
            }
            else {
                Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                tabsLayout.startAnimation(slideUp);
                tabsLayout.setVisibility(View.VISIBLE);

                //replace with our main home page - Promos
                MainPromosActivity.PlaceholderFragment frg = MainPromosActivity.PlaceholderFragment.newInstance(0);
                transaction = manager.beginTransaction();

                // Add animation in transition
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right);
                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack if needed
                transaction.replace(R.id.container, frg, "PROMOS_TAG");
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }

        }
    }
}

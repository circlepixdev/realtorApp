package com.circlepix.android;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import com.circlepix.android.data.Presentation;
import com.circlepix.android.data.PresentationDataSource;
import com.circlepix.android.helpers.CropCircleTransformation;
import com.circlepix.android.helpers.Globals;
import com.circlepix.android.helpers.NetworkChangeReceiver;
import com.circlepix.android.helpers.NetworkUtil;

import java.util.List;

/**
 * Created by Keuahn on 8/27/2016.
 */
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
        networkStatLayout = (LinearLayout) findViewById(R.id.networkStatLayout);
        networkStatBG  = (LinearLayout) findViewById(R.id.networkStatBG);
        networkStatus = (TextView) findViewById(R.id.networkStatus);


        // Get global shared data
        agentData = AgentData.getInstance();

        // Setup application class
        appState = ((CirclePixAppState)getApplicationContext());
        appState.setContextForPreferences(this);

        appState.setFirstRun(true);

        // Get the realtor data for use later
        Bundle extras = getIntent().getExtras();
        String responseString = null;

        if (extras != null) {
            responseString = extras.getString("responseString");
        } else {
            SharedPreferences settings = getSharedPreferences(AccountActivity.PREFS_NAME, 0);
            responseString = settings.getString("lastResponse", "");
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

        if(responseString != null){
            agentData.parseResponseString(responseString);
        }

        setAgentDetails();
    }

    public void onResume(){
        super.onResume();

        // Get global shared data
        agentData = AgentData.getInstance();

        if(!appState.isFirstRun()){
            appState.setFirstRun(false);
            Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
            tabsLayout.startAnimation(slideUp);
            tabsLayout.setVisibility(View.VISIBLE);

            MainPromosActivity.PlaceholderFragment frg = MainPromosActivity.PlaceholderFragment.newInstance(0);

            manager = getSupportFragmentManager();//create an instance of fragment manager
            transaction = manager.beginTransaction();//create an instance of Fragment-transaction
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right);
            transaction.replace(R.id.container, frg, "PROMOS_TAG");
            transaction.commit();

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

            case R.id.action_rate_us:
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

            case R.id.action_app_settings:
                Toast.makeText(HomeActivity.this, "TODO " + item.getTitle() + "Page", Toast.LENGTH_SHORT).show();
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

        if(!agentData.getRealtor().getImage().equals("")){
            Glide.with(getApplicationContext())
                    .load(agentData.getRealtor().getImage())
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
                    .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                    .into(agentImage);
        }else{
            agentImage.setImageResource(R.drawable.avatar);
            progressBar.setVisibility(View.GONE);
        }

    agentName.setText(agentData.getRealtor().getName());
        agentEmail.setText(agentData.getRealtor().getEmail());
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

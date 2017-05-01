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
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.circlepix.android.ui.MainActivityAdapter;

import java.util.List;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private CirclePixAppState appState;

    private Toolbar toolbar;
    private CoordinatorLayout coordinator;
    private FloatingActionButton fab;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private View navHeaderLayout;
    private RelativeLayout drawerHeader;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Button logoutBtn;
    public static LinearLayout networkStatLayout;
    public static LinearLayout networkStatBG;
    public static TextView networkStatus;

    private AgentData agentData;
    private ImageView agentImage;
    private TextView agentEmail;
    private TextView agentName;

    private ProgressBar progressBar;

    //for 4 tabs
 /*   private int[] tabIcons = {
            R.drawable.listing_selected,
            R.drawable.calendar_selected,
            R.drawable.leads_selected,
            R.drawable.presentations_selected
    };*/

    //for 3 Tabs
    private int[] tabIcons = {
            R.drawable.listing_selected,
            R.drawable.presentations_selected,
            R.drawable.customer_support_selected
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        coordinator = (CoordinatorLayout) findViewById(R.id.coordinator);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navHeaderLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        drawerHeader = (RelativeLayout) navHeaderLayout.findViewById(R.id.drawer_header);

        agentName = (TextView) navHeaderLayout.findViewById(R.id.agent_name);
        agentImage = (ImageView) navHeaderLayout.findViewById(R.id.agent_img);
        agentEmail = (TextView) navHeaderLayout.findViewById(R.id.agent_email);
        progressBar = (ProgressBar) navHeaderLayout.findViewById(R.id.progressBar);
        logoutBtn = (Button)findViewById(R.id.logout_button);
        networkStatLayout = (LinearLayout) findViewById(R.id.networkStatLayout);
        networkStatBG  = (LinearLayout) findViewById(R.id.networkStatBG);
        networkStatus = (TextView) findViewById(R.id.networkStatus);

        //clear index and top value for PresentationsActivity
       // appState.clearPresentationsActivitySP();

        // Get global shared data
        agentData = AgentData.getInstance();

        // Setup application class
        appState = ((CirclePixAppState)getApplicationContext());
        appState.setContextForPreferences(this);

        // Get the realtor data for use later
        Bundle extras = getIntent().getExtras();
        String responseString = null;

        if (extras != null) {
            responseString = extras.getString("responseString");
        } else {
            SharedPreferences settings = getSharedPreferences(AccountActivity.PREFS_NAME, 0);
            responseString = settings.getString("lastResponse", "");
        }

   /*     String projectToken = "9e3874ec7f51d34585e95ed52e8dcb2b"; // e.g.: "1ef7e30d2a58d27f4b90c42e31d6d7ad"
        MixpanelAPI mixpanel = MixpanelAPI.getInstance(this, projectToken);

        try {
            JSONObject props = new JSONObject();
            props.put("Gender", "Female");
            props.put("Logged in", true);
            mixpanel.track("MainActivity - onCreate called", props);
        } catch (JSONException e) {
            Log.e("MYAPP", "Unable to add properties to JSONObject", e);
        }
*/
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpannableStringBuilder snackbarText = new SpannableStringBuilder();
                snackbarText.append("Add ");
                int boldStart = snackbarText.length();
                snackbarText.append("bold color");
                snackbarText.setSpan(new ForegroundColorSpan(0xFFFF0000), boldStart, snackbarText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                snackbarText.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), boldStart, snackbarText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                snackbarText.append(" to Snackbar text");

                Snackbar.make(view, "I'm a Snackbar", Snackbar.LENGTH_LONG).setAction("Action", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "Snackbar Action", Toast.LENGTH_LONG).show();
                    }
                }).show();
            }
        });


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        MainActivityAdapter adapter = new MainActivityAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                animateFab(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        navigationView.setNavigationItemSelectedListener(this);
        drawerHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(profileIntent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });


        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmLogout();
            }
        });

        if(responseString != null){
            agentData.parseResponseString(responseString);
        }

        setupTabIcons();
        setAgentDetails();

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
            MainActivity.networkStatLayout.setVisibility(View.GONE);
            MainActivity.networkStatus.setVisibility(View.GONE);
        }
    }

    public void onResume(){
        super.onResume();

        // Get global shared data
        agentData = AgentData.getInstance();

        ComponentName component=new ComponentName(this, NetworkChangeReceiver.class);
        getPackageManager()
                .setComponentEnabledSetting(component,
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);

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
        MenuItem item= menu.findItem(R.id.action_settings_default);
        item.setVisible(false);
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
                 viewPager.setCurrentItem(0);
                 return true;
            case R.id.action_presentations:
                drawer.closeDrawer(GravityCompat.START);
                viewPager.setCurrentItem(1);
                return true;
            case R.id.action_customer_support:
                drawer.closeDrawer(GravityCompat.START);
                viewPager.setCurrentItem(2);
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
                appState.setFirstTimeLaunch(true);
                Intent promoIntent = new Intent(getApplicationContext(), PromosActivity.class);
                startActivity(promoIntent);
                return true;

         /*   case R.id.action_rate_us:
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
           //     Toast.makeText(MainActivity.this, "TODO " + item.getTitle() + "Page", Toast.LENGTH_SHORT).show();
           //     return true;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public FloatingActionButton getFloatingActionButton() {
        return fab;
    }

//   int[] colorIntArray = {R.color.circlepix_yellowgreen,R.color.circlepix_brown,R.color.circlepix_yellowgreen, R.color.circlepix_yellowgreen};
//    int[] iconIntArray = {R.drawable.ic_action_add, R.drawable.ic_action_home, R.drawable.ic_add, R.drawable.ic_add};

    //animated FAB when swiping on viewpager
    protected void animateFab(final int position) {

       /* if((position == 0) || (position == 2)){
            fab.setVisibility(View.GONE);
        }else{
            fab.setVisibility(View.VISIBLE);*/

            fab.clearAnimation();

            // Scale down animation
            ScaleAnimation shrink = new ScaleAnimation(1f, 0.1f, 1f, 0.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            shrink.setDuration(100);     // animation duration in milliseconds
            shrink.setInterpolator(new AccelerateInterpolator());
            shrink.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // Change FAB color and icon
//                ColorStateList colorStateList = getApplicationContext().getResources()
//                        .getColorStateList( colorIntArray[position]);
//               fab.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), colorIntArray[position]));
//               fab.setBackgroundTintList(colorStateList);
//               fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), iconIntArray[position]));

                    // Rotate Animation
                    Animation rotate = new RotateAnimation(60.0f, 0.0f,
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                            0.5f);
                    rotate.setDuration(150);
                    rotate.setInterpolator(new DecelerateInterpolator());

                    // Scale up animation
                    ScaleAnimation expand = new ScaleAnimation(0.1f, 1f, 0.1f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    expand.setDuration(150);     // animation duration in milliseconds
                    expand.setInterpolator(new DecelerateInterpolator());

                    // Add both animations to animation state
                    AnimationSet s = new AnimationSet(false); //false means don't share interpolators
                    s.addAnimation(rotate);
                    s.addAnimation(expand);
                    fab.startAnimation(s);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            fab.startAnimation(shrink);
      //  }

    }

    private void confirmLogout() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
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
        PresentationDataSource dao = new PresentationDataSource(MainActivity.this);
        dao.open(true);
        List<Presentation> presentations = dao.getAllPresentations();
        for (Presentation p : presentations) {
            dao.deletePresentation(p);
        }
        dao.close();

        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(i, 0);

        // Restore preferences
        SharedPreferences prefs = getSharedPreferences(AccountActivity.PREFS_NAME, 0);
        Log.d("LOGCAT ACCOUNTACTIVITY", "rememberMe is now: " + prefs.getBoolean("rememberMe", false));
    }


    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
//        tabLayout.getTabAt(3).setIcon(tabIcons[3]);

       /* tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.listing_selected));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.calendar_selected));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.leads_selected));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.presentations_selected));*/

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) tab.setCustomView(R.layout.view_main_tab);
        }

        tabLayout.getTabAt(0).getCustomView().setSelected(true);
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
        }
        agentName.setText(agentData.getRealtor().getName());
        agentEmail.setText(agentData.getRealtor().getEmail());
    }

    public void onBackPressed(){

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            //when back button is pressed, switch tab to ListingsTab (Home) if viewPagerId is not 0
            int viewPagerId = viewPager.getCurrentItem();

            if(viewPagerId != 0){
                tabLayout.getTabAt(0).select();
            }else{
                //app can now be exited or moved to background
                moveTaskToBack(true);
            }
        }
    }
}

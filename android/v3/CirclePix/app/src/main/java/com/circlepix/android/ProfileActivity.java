package com.circlepix.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.circlepix.android.beans.ListingDescription;
import com.circlepix.android.beans.ListingInformation;
import com.circlepix.android.beans.Realtor;
import com.circlepix.android.helpers.CropCircleTransformation;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Keuahn on 8/19/2016.
// */
public class ProfileActivity extends AppCompatActivity implements  YouTubePlayer.OnInitializedListener  {


    private AgentData agentData;

    private ImageView agentImg;
    private ImageView agentLogo;
    private ProgressBar progressBar;
    private TextView agentName, agentUsername, agentPassword, agentIDNum, agentAgency, agentPhoneNum, agentCellNum,
                    agentCellProvider, agentTextNotifications, agentFaxNum, agentEmailAd, agentWebsite,
                    agentStreet, agentCity, agentZipCode, agentOffice, agentLeadBeePin, agentProductNum, agentBillingType, agentStateLicenseNum;
    private TextView agentYoutubeId, agentBio;
    private TextView agentFacebookUrl, agentTwitterUrl, agentYoutubeUrl, agentLinkedInUrl, agentPinterestUrl, agentBloggerUrl, agentGalleryLink;

    // Edits
    private ImageView  editPicture, editUsernamePassword, editAgentInfo, editBio, editSocialMediaLinks, editLogo;

    private GetAgentProfileInformation getAgentProfileInformation;

    private YouTubePlayerSupportFragment youtubePlayerFragment;
    private String agentYouTubeURL = "";

    private static final int RECOVERY_DIALOG_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Get global shared data
        agentData = AgentData.getInstance();



        youtubePlayerFragment = (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.youtube_fragment);


        agentImg = (ImageView) findViewById(R.id.agent_img);
        agentLogo = (ImageView) findViewById(R.id.agent_logo);
        agentName = (TextView) findViewById(R.id.agent_name);
        agentUsername = (TextView) findViewById(R.id.text_username);
 //       agentPassword = (TextView) findViewById(R.id.text_password);
        agentIDNum = (TextView) findViewById(R.id.text_agent_id);
        agentPhoneNum = (TextView) findViewById(R.id.text_phone_number);
        agentAgency = (TextView) findViewById(R.id.text_agency);
        agentCellNum = (TextView) findViewById(R.id.text_cell_number);
        agentCellProvider = (TextView) findViewById(R.id.text_cell_provider);
        agentTextNotifications = (TextView) findViewById(R.id.text_notifications);
        agentFaxNum = (TextView) findViewById(R.id.text_fax_number);
        agentEmailAd = (TextView) findViewById(R.id.text_email_ad);
        agentWebsite = (TextView) findViewById(R.id.text_website);
        agentStreet = (TextView) findViewById(R.id.text_street);
        agentCity = (TextView) findViewById(R.id.text_city);
        agentZipCode = (TextView) findViewById(R.id.text_zipcode);
//        agentOffice = (TextView) findViewById(R.id.text_office);
        agentLeadBeePin = (TextView) findViewById(R.id.text_leadbee_pin);
        agentProductNum = (TextView) findViewById(R.id.text_product_number);
        agentBillingType = (TextView) findViewById(R.id.text_billing_type);
        agentStateLicenseNum = (TextView) findViewById(R.id.text_state_license_number);

//        agentYoutubeId = (TextView) findViewById(R.id.text_youtube_id);
        agentBio = (TextView) findViewById(R.id.text_agent_bio);

        agentFacebookUrl = (TextView) findViewById(R.id.text_facebook_url);
        agentTwitterUrl = (TextView) findViewById(R.id.text_twitter_url);
        agentYoutubeUrl = (TextView) findViewById(R.id.text_youtube_url);
        agentLinkedInUrl = (TextView) findViewById(R.id.text_linkedin_url);
        agentPinterestUrl = (TextView) findViewById(R.id.text_pinterest_url);
        agentBloggerUrl = (TextView) findViewById(R.id.text_blogger_url);
        agentGalleryLink = (TextView) findViewById(R.id.text_agent_gallery_link);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Edit Profile
        editUsernamePassword = (ImageView) findViewById(R.id.edit_un_pword);
        editPicture = (ImageView) findViewById(R.id.edit_pic);
        editAgentInfo = (ImageView) findViewById(R.id.edit_agent_info);
        editBio = (ImageView) findViewById(R.id.edit_agent_bio);
        editSocialMediaLinks = (ImageView) findViewById(R.id.edit_social_media);
        editLogo = (ImageView) findViewById(R.id.edit_agent_logo);

        editPicture.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), EditAgentProfilePictureActivity.class);
                startActivity(intent);
            }
        });

        editUsernamePassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), EditAgentCredentialsActivity.class);
                startActivity(intent);
            }
        });

        editAgentInfo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), EditAgentInformationActivity.class);
                startActivity(intent);
            }
        });

        editBio.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), EditAgentBioActivity.class);
                startActivity(intent);
            }
        });

        editSocialMediaLinks.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), EditAgentSocialMediaLinks.class);
                startActivity(intent);
            }
        });

        editLogo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), EditAgentLogoActivity.class);
                startActivity(intent);
            }
        });
     //   setAgentInformation();
    }


    @Override
    public void onResume(){
        super.onResume();

      //  agentName.setText(agentData.getRealtor().getName());

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()){

            getAgentProfileInformation = new GetAgentProfileInformation();

            if(Build.VERSION.SDK_INT >= 11) {
                getAgentProfileInformation.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }else {
                getAgentProfileInformation.execute();
            }
        }else {
            showAlertDialog("No Internet Connection", "Please try again");
        }

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                        boolean wasRestored) {
        if (!wasRestored) {
            //I assume the below String value is your video id
            player.cueVideo(agentYouTubeURL);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format(getString(R.string.error_player), errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(CirclePixAppState.DEVELOPER_KEY, this);
        }
    }

    private YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.youtube_fragment);
    }

 /*   @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format(
                    getString(R.string.error_player), errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                        YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {

            // loadVideo() will auto play video
            // Use cueVideo() method, if you don't want to play it automatically
            player.cueVideo(CirclePixAppState.YOUTUBE_VIDEO_CODE);

            // Hiding player controls
            player.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(CirclePixAppState.DEVELOPER_KEY, this);
        }
    }

    private YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerSupportFragment) findViewById(R.id.youtube_fragment);
    }
*/

    private void setAgentInformation(){
        // Agent Profile Section


        if(!agentData.getAgentProfileInformation().getAgentImage().isEmpty()){

            Glide.with(getApplicationContext())
                    .load(agentData.getAgentProfileInformation().getAgentImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.circlepix_bg)
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
//                    .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                    .into(agentImg);
        }else{
            Glide.with(getApplicationContext()).load("").into(agentImg);
            progressBar.setVisibility(View.GONE);
        }

        if(!agentData.getAgentProfileInformation().getAgentLogo().isEmpty()){

            Glide.with(getApplicationContext())
                    .load(agentData.getAgentProfileInformation().getAgentLogo())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
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
                    .into(agentLogo);
        }else{
            Glide.with(getApplicationContext()).load("").into(agentLogo);
        }


        final SharedPreferences prefs = getSharedPreferences(AccountActivity.PREFS_NAME, 0);
        String restoredUsername = Uri.decode(prefs.getString("username", null));

        if(!restoredUsername.equals(null)){
            agentUsername.setText(restoredUsername );
        }else{
            agentUsername.setText("");
        }


        if(!agentData.getAgentProfileInformation().getFullName().equals(null) && !agentData.getAgentProfileInformation().getFullName().isEmpty()){
            agentName.setText(agentData.getAgentProfileInformation().getFullName());
        }else{
            agentName.setText("");
        }

        if(!agentData.getAgentProfileInformation().getId().equals(null) && !agentData.getAgentProfileInformation().getId().isEmpty()){
            agentIDNum.setText(agentData.getAgentProfileInformation().getId());
        }else{
            agentIDNum.setText("");
        }

        if(!agentData.getAgentProfileInformation().getAgency().equals(null)  && !agentData.getAgentProfileInformation().getAgency().isEmpty()){
            agentAgency.setText(agentData.getAgentProfileInformation().getAgency());
        }else{
            agentAgency.setText("");
        }

        if(!agentData.getAgentProfileInformation().getPhoneNumber().equals(null) && !agentData.getAgentProfileInformation().getPhoneNumber().isEmpty()){
            agentPhoneNum.setText(agentData.getAgentProfileInformation().getPhoneNumber());
        }else{
            agentPhoneNum.setText("");
        }

        if(!agentData.getAgentProfileInformation().getCellNumber().equals(null) && !agentData.getAgentProfileInformation().getCellNumber().isEmpty()){
            agentCellNum.setText(agentData.getAgentProfileInformation().getCellNumber());
        }else{
            agentCellNum.setText("");
        }

        if(!agentData.getAgentProfileInformation().getCellProvider().equals(null) && !agentData.getAgentProfileInformation().getCellProvider().isEmpty()){
            agentCellProvider.setText(agentData.getAgentProfileInformation().getCellProvider());
        }else{
            agentCellProvider.setText("");
        }

        if(!agentData.getAgentProfileInformation().getTextNotifications().equals(null) && !agentData.getAgentProfileInformation().getTextNotifications().isEmpty()){
            agentTextNotifications.setText(agentData.getAgentProfileInformation().getTextNotifications());
        }else{
            agentTextNotifications.setText("");
        }

        if(!agentData.getAgentProfileInformation().getFaxNumber().equals(null) && !agentData.getAgentProfileInformation().getFaxNumber().isEmpty()){
            agentFaxNum.setText(agentData.getAgentProfileInformation().getFaxNumber());
        }else{
            agentFaxNum.setText("");
        }

        if(!agentData.getAgentProfileInformation().getEmail().equals(null) && !agentData.getAgentProfileInformation().getEmail().isEmpty()){
            agentEmailAd.setText(agentData.getAgentProfileInformation().getEmail());
        }else{
            agentEmailAd.setText("");
        }

        if(!agentData.getAgentProfileInformation().getWebsite().equals(null) && !agentData.getAgentProfileInformation().getWebsite().isEmpty()){
            agentWebsite.setText(agentData.getAgentProfileInformation().getWebsite());
        }else{
            agentWebsite.setText("");
        }

        if(!agentData.getAgentProfileInformation().getStreetAddress().equals(null) && !agentData.getAgentProfileInformation().getStreetAddress().isEmpty()){
            agentStreet.setText(agentData.getAgentProfileInformation().getStreetAddress());

        }else{
            agentStreet.setText("");
        }

        if(!agentData.getAgentProfileInformation().getCity().equals(null) && !agentData.getAgentProfileInformation().getCity().isEmpty()){
            agentCity.setText(agentData.getAgentProfileInformation().getCity());

        }else{
            agentCity.setText("");
        }

        if(!agentData.getAgentProfileInformation().getZipcode().equals(null) && !agentData.getAgentProfileInformation().getZipcode().isEmpty()){
            agentZipCode.setText(agentData.getAgentProfileInformation().getZipcode());

        }else{
            agentZipCode.setText("");
        }

//        if(!agentData.getAgentProfileInformation().getOffice().equals(null) && !agentData.getAgentProfileInformation().getOffice().isEmpty()){
//            agentOffice.setText(agentData.getAgentProfileInformation().getOffice());
//
//        }else{
//            agentOffice.setText("");
//        }

//        Log.v("offiiice", agentData.getAgentProfileInformation().getOffice());
        if(!agentData.getAgentProfileInformation().getLeadBeePin().equals(null) && !agentData.getAgentProfileInformation().getLeadBeePin().isEmpty()){
            agentLeadBeePin.setText(agentData.getAgentProfileInformation().getLeadBeePin());

        }else{
            agentLeadBeePin.setText("");
        }

        if(!agentData.getAgentProfileInformation().getProductNumber().equals(null) && !agentData.getAgentProfileInformation().getProductNumber().isEmpty()){
            agentProductNum.setText(agentData.getAgentProfileInformation().getProductNumber());

        }else{
            agentProductNum.setText("");
        }

        if(!agentData.getAgentProfileInformation().getBillingType().equals(null) && !agentData.getAgentProfileInformation().getBillingType().isEmpty()){
            agentBillingType.setText(agentData.getAgentProfileInformation().getBillingType());
        }else{
            agentBillingType.setText("");
        }

        if(!agentData.getAgentProfileInformation().getStateLicenseNumber().equals(null) && !agentData.getAgentProfileInformation().getStateLicenseNumber().isEmpty()){
            agentStateLicenseNum.setText(agentData.getAgentProfileInformation().getStateLicenseNumber());

        }else{
            agentStateLicenseNum.setText("");
        }
        if(!agentData.getAgentProfileInformation().getYoutubeId().equals(null) && !agentData.getAgentProfileInformation().getYoutubeId().isEmpty()){
            agentYouTubeURL = agentData.getAgentProfileInformation().getYoutubeId();

            youtubePlayerFragment.initialize(CirclePixAppState.DEVELOPER_KEY, this);

        }else{
            agentYouTubeURL = "";
        }
        if(!agentData.getAgentProfileInformation().getBiography().equals(null) && !agentData.getAgentProfileInformation().getBiography().isEmpty()){
            agentBio.setText(agentData.getAgentProfileInformation().getBiography());

        }else{
            agentBio.setText("");
        }
//        if(!agentData.getAgentProfileInformation().getWebsite().equals(null)){
//            agentWebsite.setText(agentData.getAgentProfileInformation().getWebsite());
//        }

        // Social Media Links
        if(!agentData.getAgentProfileInformation().getFacebookURL().isEmpty()){
            agentFacebookUrl.setText(agentData.getAgentProfileInformation().getFacebookURL());
            agentFacebookUrl.setLinkTextColor(Color.BLUE);
        }else{
            agentFacebookUrl.setText("");
        }

        if(!agentData.getAgentProfileInformation().getYoutubeURL().isEmpty()){
            agentYoutubeUrl.setText(agentData.getAgentProfileInformation().getYoutubeURL());
            agentYoutubeUrl.setLinkTextColor(Color.BLUE);
        }else{
            agentYoutubeUrl.setText("");
        }

        if(!agentData.getAgentProfileInformation().getBlogURL().isEmpty()){
            agentBloggerUrl.setText(agentData.getAgentProfileInformation().getBlogURL());
            agentBloggerUrl.setLinkTextColor(Color.BLUE);
        }else{
            agentBloggerUrl.setText("");
        }

        if(!agentData.getAgentProfileInformation().getLinkedinURL().isEmpty()){
            agentLinkedInUrl.setText(agentData.getAgentProfileInformation().getLinkedinURL());
            agentLinkedInUrl.setLinkTextColor(Color.BLUE);
        }else{
            agentLinkedInUrl.setText("");
        }

        if(!agentData.getAgentProfileInformation().getPinterestURL().isEmpty()){
            agentPinterestUrl.setText(agentData.getAgentProfileInformation().getPinterestURL());
            agentPinterestUrl.setLinkTextColor(Color.BLUE);
        }else{
            agentPinterestUrl.setText("");
        }

        if(!agentData.getAgentProfileInformation().getTwitterURL().isEmpty()){
            agentTwitterUrl.setText(agentData.getAgentProfileInformation().getTwitterURL());
            agentTwitterUrl.setLinkTextColor(Color.BLUE);
        }else{
            agentTwitterUrl.setText("");
        }


       /* if(!agentData.getRealtor().getId().equals(null)){
            agentIDNum.setText(agentData.getRealtor().getId()); }

        if(!agentData.getRealtor().getAgency().equals(null)){
            agentAgency.setText(agentData.getRealtor().getAgency());}

        if(!agentData.getRealtor().getPhone().equals(null)){
            agentPhoneNum.setText(agentData.getRealtor().getPhone());}

        if(!agentData.getRealtor().getMobile().equals(null)){
            agentCellNum.setText(agentData.getRealtor().getMobile());}

        if(!agentData.getRealtor().getMobile().equals(null)){
            agentCellNum.setText(agentData.getRealtor().getMobile());}

        if(!agentData.getRealtor().getEmail().equals(null)){
            agentEmailAd.setText(agentData.getRealtor().getEmail());}

        // Agent Bio Section
        if(!agentData.getRealtor().getEmail().equals(null)){
            agentYoutubeId.setText(agentData.getRealtor().getVideo());}
**/
        // Agent Gallery Section
        String agentGalleryURL = "https://www.circlepix.com/tour/agent.htm?agentid=";
        if(!agentData.getRealtor().getId().equals(null) && !agentData.getRealtor().getId().isEmpty()){
             agentGalleryLink.setText(agentGalleryURL + agentData.getRealtor().getId());
             agentGalleryLink.setLinkTextColor(Color.BLUE);
        }else{
            agentGalleryLink.setText("");
        }


    }


    protected class GetAgentProfileInformation extends AsyncTask<Context, Integer, String> {

        ProgressDialog mProgressDialog;


        @Override
        protected void onPreExecute() {
            	mProgressDialog = ProgressDialog.show(ProfileActivity.this, "", "Loading...");

            Log.v("GetAgentProfileInformation", "true");
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

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //stuff that updates ui
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


//                    JSONArray Jarray = data.getJSONArray("agentInfo");
//                    if(Jarray.equals(null)){
//
//                        showAlertDialog("Error!", response.message());
//
//                    }else{
//                        for (int i = 0; i < Jarray.length(); i++) {


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
//                            if(!agentInfoObj.isNull("office")){
//                                agentProfileInformation.setOffice(agentInfoObj.getString("office"));
//                            }else{
//                                agentProfileInformation.setOffice("");
//                            }

//                            if (!agentInfoObj.getString("office").equals(null)) {
//                                agentProfileInformation.setOffice(agentInfoObj.getString("office"));
//                            }
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
                                // if(!listingObj.getString("listingDesc").equals(null)){

                               // JSONObject socialMediaLinksObj =  agentInfoObj.getJSONObject("socialMediaLinks");

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


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //stuff that updates ui
                            mProgressDialog.dismiss();
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
      //          Log.d("LOGCAT", "Failed!" + e.getLocalizedMessage().toString());

                mProgressDialog.dismiss();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showAlertDialog("Error",(e.getMessage() == null) ? e.getMessage() : e.toString());

                    }
                });

            }

            return responseString;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

        	mProgressDialog.dismiss();
            setAgentInformation();

//          if (agentData.getAgentProfileInformation() != null ) {
//              setAgentInformation();
//            }
        }


    }


    public void showAlertDialog(String title, String msg){
        final AlertDialog.Builder alert = new AlertDialog.Builder(ProfileActivity.this);
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
  /*  public static void startActivity(Context context) {
        context.startActivity(new Intent(context, ProfileActivity.class));
    }

    private Button mExpandButton;
    private ExpandableRelativeLayout mExpandLayout;
    private TextView mOverlayText;
    private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expandable_layout);

     //   getSupportActionBar().setTitle(ProfileActivity.class.getSimpleName());

        mExpandButton = (Button) findViewById(R.id.expandButton);
        mExpandLayout = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout);
        mOverlayText = (TextView) findViewById(R.id.overlayText);
        mExpandButton.setOnClickListener(this);

        mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mExpandLayout.move(mOverlayText.getHeight(), 0, null);

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    mOverlayText.getViewTreeObserver().removeGlobalOnLayoutListener(mGlobalLayoutListener);
                } else {
                    mOverlayText.getViewTreeObserver().removeOnGlobalLayoutListener(mGlobalLayoutListener);
                }
            }
        };
        mOverlayText.getViewTreeObserver().addOnGlobalLayoutListener(mGlobalLayoutListener);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.expandButton:
                mExpandLayout.expand();
           //     mExpandButton.setVisibility(View.GONE);
           //     mOverlayText.setVisibility(View.GONE);
                mExpandLayout.toggle();
                break;
        }
    }*/
}
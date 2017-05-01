package com.circlepix.android.presentations;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.circlepix.android.CirclePixAppState;
import com.circlepix.android.HomeActivity;
import com.circlepix.android.R;
import com.circlepix.android.beans.AgentData;
import com.circlepix.android.beans.Realtor;
import com.circlepix.android.data.Presentation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;

/**
 * Created by relly on 2/3/15.
 */
public class PresentationStart extends PresentationBase {

    private PresentationPageAudioPlayer player;
    private ProgressBar progressBar;
    private TextView realtorName;
    private TextView realtorPhone;
    private TextView realtorEmail;
    private ImageView realtorImage;
    private TextView companyName;

    //KBL
    private CirclePixAppState appState;
    private LinearLayout leftFrame;
    private String currentPos;
    private boolean appStopped;
    private AgentData agentData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.presentation_start);

        // Setup application class
        appState = ((CirclePixAppState)getApplicationContext());
        appState.setContextForPreferences(this);

        agentData = AgentData.getInstance();

        realtorName = (TextView) findViewById(R.id.realtorName);
        realtorPhone = (TextView) findViewById(R.id.realtorPhone);
        realtorEmail = (TextView) findViewById(R.id.realtorEmail);
        realtorImage = (ImageView) findViewById(R.id.agent_img);
        companyName = (TextView) findViewById(R.id.companyName);
        leftFrame = (LinearLayout) findViewById(R.id.leftFrame);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Realtor realtor = PresentationSequencingSet.getRealtorProfile(PresentationStart.this);
        Presentation p = PresentationSequencingSet.getPresentation();

        if (realtor != null) {

            //temporarily unavail
           /* if(realtor.getName().equalsIgnoreCase("Jeremy Durrant")){
                leftFrame.setBackgroundColor(getResources().getColor(R.color.circlepix_brown));
            }else if(realtor.getName().equalsIgnoreCase("Greg Gehring")){
                leftFrame.setBackgroundColor(getResources().getColor(R.color.circlepix_dtan));
            }else{
                leftFrame.setBackgroundColor(getResources().getColor(R.color.circlepix_blue));
            }*/

            ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            final Boolean is4g = (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null) ?
                    cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting() : false;
            final Boolean isWifi = (cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null) ?
                    cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting() : false;


            if (p.isDisplayAgentName()) {
                realtorName.setText(realtor.getName());
                realtorEmail.setText(realtor.getEmail());
                if(realtor.getPhone().equals("") || realtor.getPhone().equals(null)){
                    realtorPhone.setText(realtor.getMobile());
                }else{
                    realtorPhone.setText(realtor.getPhone());
                }
            } else {
                realtorName.setText("");
                realtorPhone.setText("");
                realtorEmail.setText("");
            }

            if (p.isDisplayAgentImage()) {

                if(agentData.getAgentProfileInformation().getAgentImage() != null && !agentData.getAgentProfileInformation().getAgentImage().isEmpty()){

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
                            .into(realtorImage);
                }else{
                    Glide.with(getApplicationContext()).load("").into(realtorImage);
                    progressBar.setVisibility(View.GONE);
                }

            } else {
               // realtorImage.setImageBitmap(null);
                Glide.with(getApplicationContext()).load("").into(realtorImage);
                progressBar.setVisibility(View.GONE);
            }

            if (p.isDisplayCompanyName()) {
                companyName.setText(realtor.getAgency());
            } else {
                companyName.setText("");
            }
        }

        appState.setAudioServiceCurrentPos(0);  //do not clear all sharedprefs so it will not set bgmusic to default

        player = new PresentationPageAudioPlayer(this);
        player.setPrevAndNextPage(null, PresentationStarMarketing.class);
        player.playAudio();

    }

    private Bitmap decodeFile(File f){
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);

            //The new size we want to scale to
            final int REQUIRED_SIZE=50; //70

            //Find the correct scale value. It should be the power of 2.
            int scale=1;
            while(o.outWidth/scale/4>=REQUIRED_SIZE && o.outHeight/scale/4>=REQUIRED_SIZE)
                scale*=2;

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                stopPresentation();

        }
        return (super.onOptionsItemSelected(menuItem));
    }


    @Override
    public void onResume() {
        super.onResume();

        CirclePixAppState myApp = (CirclePixAppState)this.getApplication();
        if (myApp.wasInBackground)
        {
//            appState.setActivityStopped(false);
            //Do specific came-here-from-background code
            Log.v("came-here-from-background code", "called");
            player.setPrevAndNextPage(null, PresentationStarMarketing.class);
            player.playAudio();
        }

        myApp.stopActivityTransitionTimer();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v("isActivityStopped", String.valueOf(appState.isActivityStopped()));
        if(!appState.isActivityStopped()){  //this is checking if false but if true, disregard checking if app went to background > clicking pause, forward, back, back button
            ((CirclePixAppState)this.getApplication()).startActivityTransitionTimer();  //checking if app went to background
            player.pauseAudio();

            appState.setActionBarStat(true);  //to show the actionbar to let the user know that presentation was paused when they pressed home or tas manager button
        }

    }

    //added 080815
    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbindDrawables(findViewById(R.id.rootView));
        System.gc();

        Log.v("onDestroy", "called");
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

   @Override
    public void pauseAnimation() {
    }

    @Override
    public void resumeAnimation() {
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        private ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            String urldisplay = params[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (MalformedURLException me) {
                Log.e("LazyAdapter", "Bad URL Error");
            }
            catch (Exception e) {
                Log.e("Bad URL Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            bmImage.setImageBitmap(bitmap);
        }
    }

    public void onBackPressed(){
        super.onBackPressed();

        stopPresentation();
    }

    public void stopPresentation(){
        player.stop();

        appState.setActivityStopped(true); //this wasn't included in clearSharedPref
        appState.clearSharedPreferences();
        stopService(new Intent(PresentationStart.this, BackgroundMusicService.class));

    }
}

package com.circlepix.android.presentations;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.circlepix.android.R;
import com.circlepix.android.beans.AgentData;
import com.circlepix.android.beans.Realtor;
import com.circlepix.android.data.Presentation;

import java.io.InputStream;
import java.net.MalformedURLException;

/**
 * Created by relly on 2/14/15.
 */
public class PresentationEnd extends PresentationBase {

    private PresentationPageAudioPlayer player;
    private TextView realtorName;
    private TextView realtorPhone;
    private TextView realtorEmail;
    private ImageView realtorImage;
    private TextView companyName;

    private AgentData agentData;
    private ProgressBar progressBar;

    //KBL
    private CirclePixAppState appState;
    private LinearLayout leftFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.presentation_end);

        // Setup application class
        appState = ((CirclePixAppState)getApplicationContext());
        appState.setContextForPreferences(this);

        agentData = AgentData.getInstance();

        realtorName = (TextView) findViewById(R.id.realtorName);
        realtorPhone = (TextView) findViewById(R.id.realtorPhone);
        realtorEmail = (TextView) findViewById(R.id.realtorEmail);
        realtorImage = (ImageView) findViewById(R.id.imgRealtor);
        companyName = (TextView) findViewById(R.id.companyName);
        leftFrame = (LinearLayout) findViewById(R.id.leftFrame);

        Realtor realtor = PresentationSequencingSet.getRealtorProfile(PresentationEnd.this);
        Presentation p = PresentationSequencingSet.getPresentation();

        if (realtor != null) {

            //temporarily unavailable - sample test to change theme based on the company for Jeremy and Greg's account
           /* if(realtor.getName().equalsIgnoreCase("Jeremy Durrant")){
                leftFrame.setBackgroundColor(getResources().getColor(R.color.circlepix_brown));
            }else if(realtor.getName().equalsIgnoreCase("Greg Gehring")){
                leftFrame.setBackgroundColor(getResources().getColor(R.color.circlepix_dtan));
            }else{
                leftFrame.setBackgroundColor(getResources().getColor(R.color.circlepix_blue));
            }*/

            if (p.isDisplayAgentName()) {
                realtorName.setText(realtor.getName());
                realtorPhone.setText(realtor.getPhone());
                realtorEmail.setText(realtor.getEmail());
            } else {
                realtorName.setText("");
                realtorPhone.setText("");
                realtorEmail.setText("");
            }

//            if (p.isDisplayAgentImage()) {
//                new DownloadImageTask(realtorImage).execute(realtor.getImage());
//            } else {
//                realtorImage.setImageBitmap(null);
//            }


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
                Glide.with(getApplicationContext()).load("").into(realtorImage);
                progressBar.setVisibility(View.GONE);
            }

            if (p.isDisplayCompanyName()) {
                companyName.setText(realtor.getAgency());
            } else {
                companyName.setText("");
            }
        }

        player = new PresentationPageAudioPlayer(this);

        player.setPrevAndNextPage(PresentationCommBatchTexting.class, null);
        player.playAudio();
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
            //   appState.setActivityStopped(false);
            //Do specific came-here-from-background code
            Log.v("came-here-from-background code", "called");
            player.setPrevAndNextPage(PresentationCommBatchTexting.class, null);
            player.playAudio();
        }

        myApp.stopActivityTransitionTimer();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v("isActivityStopped", String.valueOf(appState.isActivityStopped()));
        if(!appState.isActivityStopped()){
            ((CirclePixAppState)this.getApplication()).startActivityTransitionTimer();
            player.pauseAudio();

            appState.setActionBarStat(true);  //to show the actionbar to let the user know that presentation was paused when they pressed home or tas manager button
        }
    }



    @Override
    public void pauseAnimation() {

    }

    @Override
    public void resumeAnimation() {

    }


    public void onBackPressed(){
        super.onBackPressed();
        stopPresentation();
    }

    public void stopPresentation() {
        player.stop();

        appState.setActivityStopped(true);
        appState.clearSharedPreferences();
        stopService(new Intent(PresentationEnd.this, BackgroundMusicService.class));
    }
}

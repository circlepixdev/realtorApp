package com.circlepix.android.presentations;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.circlepix.android.CirclePixAppState;
import com.circlepix.android.R;

/**
 * Created by relly on 2/27/15.
 */
public class PresentationExposurePortals extends PresentationBase {

    private PresentationPageAudioPlayer player;
    private ImageView imgZillow;
    private ImageView imgTrulia;
    private ImageView imgRealtor;

    //KBL
    private CirclePixAppState appState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.presentation_exposure_portals);

        // Setup application class
        appState = ((CirclePixAppState)getApplicationContext());
        appState.setContextForPreferences(this);

        player = new PresentationPageAudioPlayer(this);
        player.setPrevAndNextPage(PresentationExposureIntro.class, PresentationExposurePersonalWebsite.class);
        player.playAudio();

     //   imgZillow = (ImageView) findViewById(R.id.imgZillow);
     //   imgTrulia = (ImageView) findViewById(R.id.imgTrulia);
        imgRealtor = (ImageView) findViewById(R.id.imgRealtor);

        //temporarily removed
    /*    Handler handler = new Handler();
        Runnable runnableRealtor = new Runnable() {
            @Override
            public void run() {
                zoomInRealtor();
            }
        };
        handler.postDelayed(runnableRealtor, 5000);

        Runnable runnableTrulia = new Runnable() {
            @Override
            public void run() {
                zoomInTrulia();
            }
        };
        handler.postDelayed(runnableTrulia, 6000);

        Runnable runnableZillow = new Runnable() {
            @Override
            public void run() {
                zoomInZillow();
            }
        };
        handler.postDelayed(runnableZillow, 7000);*/

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
            player.setPrevAndNextPage(PresentationExposureIntro.class, PresentationExposurePersonalWebsite.class);
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

        Log.v("going-to-background code", "called");
    }



    private void zoomInRealtor() {
        ScaleAnimation anim = new ScaleAnimation(0, 1f, 0, 1f, Animation.RELATIVE_TO_SELF, (float)0.5, Animation.RELATIVE_TO_SELF, (float)0.5);
        anim.setDuration(1000);
        anim.setFillAfter(true);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                imgRealtor.setVisibility(View.VISIBLE);
            }
        });
        imgRealtor.startAnimation(anim);
    }

    private void zoomInTrulia() {
        ScaleAnimation anim = new ScaleAnimation(0, 1f, 0, 1f, Animation.RELATIVE_TO_SELF, (float)0.5, Animation.RELATIVE_TO_SELF, (float)0.5);
        anim.setDuration(1000);
        anim.setFillAfter(true);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                imgTrulia.setVisibility(View.VISIBLE);
            }
        });
        imgTrulia.startAnimation(anim);
    }

    private void zoomInZillow() {
        ScaleAnimation anim = new ScaleAnimation(0, 1f, 0, 1f, Animation.RELATIVE_TO_SELF, (float)0.5, Animation.RELATIVE_TO_SELF, (float)0.5);
        anim.setDuration(1000);
        anim.setFillAfter(true);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                imgZillow.setVisibility(View.VISIBLE);
            }
        });
        imgZillow.startAnimation(anim);
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
        stopService(new Intent(PresentationExposurePortals.this, BackgroundMusicService.class));
    }
}

package com.circlepix.android.presentations;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.circlepix.android.CirclePixAppState;
import com.circlepix.android.R;

/**
 * Created by relly on 2/11/15.
 */
public class PresentationMediaMobilePlatform extends PresentationBase {

    private PresentationPageAudioPlayer player;
    private ImageView imgPhotoStack;

    //KBL
    private CirclePixAppState appState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.presentation_media_mobileplatform);

        // Setup application class
        appState = ((CirclePixAppState)getApplicationContext());
        appState.setContextForPreferences(this);

        // Setup application class
        appState = ((CirclePixAppState)getApplicationContext());
        appState.setContextForPreferences(this);

        player = new PresentationPageAudioPlayer(this);

        player.setPrevAndNextPage(PresentationMediaListingVideo.class, PresentationMediaQRCodes.class);
//        player.playAudio(R.raw.marketingmaterials_mobileplatform);

        imgPhotoStack = (ImageView) findViewById(R.id.imgPhotoStack);

        //temporarily removed
    /*    Handler handlerMedia = new Handler();
        Runnable runnableMedia = new Runnable() {
            @Override
            public void run() {
                animateMobile();
            }
        };
        handlerMedia.postDelayed(runnableMedia, 1000);*/

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
            player.setPrevAndNextPage(PresentationMediaListingVideo.class, PresentationMediaQRCodes.class);
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

    private void animateMobile() {
        TranslateAnimation tAnimation = new TranslateAnimation(0, 0, 800, 0);
        tAnimation.setDuration(1000);
        tAnimation.setRepeatCount(0);
        tAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        tAnimation.setFillAfter(true);
        tAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                imgPhotoStack.setVisibility(View.VISIBLE);
            }
        });

        imgPhotoStack.startAnimation(tAnimation);
    }

    @Override
    public void pauseAnimation() {

    }

    @Override
    public void resumeAnimation() {

    }

    public void onBackPressed(){
        super.onBackPressed();
        player.stop();

        appState.setActivityStopped(true);
        appState.clearSharedPreferences();
        stopService(new Intent(PresentationMediaMobilePlatform.this, BackgroundMusicService.class));
    }
}

package com.circlepix.android.presentations;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.circlepix.android.CirclePixAppState;
import com.circlepix.android.R;

/**
 * Created by relly on 2/12/15.
 */
public class PresentationMediaFlyers extends PresentationBase {

    private PresentationPageAudioPlayer player;
    private ImageView imgPhotoStack;

    //KBL
    private CirclePixAppState appState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.presentation_media_flyers);

        // Setup application class
        appState = ((CirclePixAppState)getApplicationContext());
        appState.setContextForPreferences(this);

        player = new PresentationPageAudioPlayer(this);
        player.setPrevAndNextPage(PresentationMediaShortcode.class, PresentationMediaDVDs.class);
        player.playAudio();


      //  imgPhotoStack = (ImageView) findViewById(R.id.imgPhotoStack);


        //temporarily removed
      /*  Handler handlerMedia = new Handler();
        Runnable runnableSlideUp = new Runnable() {
            @Override
            public void run() {
                bounceAnimate();
            }
        };
        handlerMedia.postDelayed(runnableSlideUp, 2000);*/

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
            player.setPrevAndNextPage(PresentationMediaShortcode.class, PresentationMediaDVDs.class);
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

/*

    private void bounceAnimate() {

        Animation bounce = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1,
                Animation.RELATIVE_TO_SELF, 0);
        bounce.setDuration(1000);
        bounce.setInterpolator(new BounceInterpolator());
        bounce.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imgPhotoStack.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imgPhotoStack.startAnimation(bounce);
    }
*/


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
        stopService(new Intent(PresentationMediaFlyers.this, BackgroundMusicService.class));

    }
}

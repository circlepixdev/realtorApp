package com.circlepix.android.presentations;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.circlepix.android.CirclePixAppState;
import com.circlepix.android.R;

/**
 * Created by relly on 2/25/15.
 */
public class PresentationCommBatchTexting extends PresentationBase {

    private PresentationPageAudioPlayer player;
    private ImageView imgHand;
    private ImageView imgArrow;
    private ImageView imgText;

    //KBL
    private CirclePixAppState appState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.presentation_comm_batchtexting);

        // Setup application class
        appState = ((CirclePixAppState)getApplicationContext());
        appState.setContextForPreferences(this);

        player = new PresentationPageAudioPlayer(this);
        player.setPrevAndNextPage(PresentationCommEMarketing.class, PresentationEnd.class);
        player.playAudio();

    //    imgHand = (ImageView) findViewById(R.id.imgBatchTextHand);
    //    imgArrow = (ImageView) findViewById(R.id.imgBatchTextArrow);
    //    imgText = (ImageView) findViewById(R.id.imgBatchTextText);

    /*    Handler handler = new Handler();
        Runnable runnableHand = new Runnable() {
            @Override
            public void run() {
                animateHand();
            }
        };
        handler.postDelayed(runnableHand, 2000);
*/
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
            player.setPrevAndNextPage(PresentationCommEMarketing.class, PresentationEnd.class);
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

    private void animateHand() {
        Animation fadein = new AlphaAnimation(0, 1);
        fadein.setInterpolator(new AccelerateDecelerateInterpolator());
        fadein.setDuration(1500);
        fadein.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imgHand.setVisibility(View.VISIBLE);
                animateArrow();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imgHand.startAnimation(fadein);
    }

    private void animateArrow() {
        Animation fadein = new AlphaAnimation(0, 1);
        fadein.setInterpolator(new AccelerateDecelerateInterpolator());
        fadein.setDuration(1500);
        fadein.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imgArrow.setVisibility(View.VISIBLE);
                animateTexts();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imgArrow.startAnimation(fadein);
    }

    private void animateTexts() {
        Animation fadein = new AlphaAnimation(0, 1);
        fadein.setInterpolator(new AccelerateDecelerateInterpolator());
        fadein.setDuration(1500);
        fadein.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imgText.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imgText.startAnimation(fadein);
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
        stopService(new Intent(PresentationCommBatchTexting.this, BackgroundMusicService.class));
    }

}

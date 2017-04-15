package com.circlepix.android.presentations;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.circlepix.android.CirclePixAppState;
import com.circlepix.android.R;

/**
 * Created by relly on 2/12/15.
 */
public class PresentationMediaDVDs extends PresentationBase {

    private PresentationPageAudioPlayer player;
    private ImageView imgDVD;

    //KBL
    private CirclePixAppState appState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.presentation_media_dvds);

        // Setup application class
        appState = ((CirclePixAppState)getApplicationContext());
        appState.setContextForPreferences(this);

        player = new PresentationPageAudioPlayer(this);

        player.setPrevAndNextPage(PresentationMediaFlyers.class, PresentationExposureIntro.class);
        player.playAudio();

//        imgDVD = (ImageView) findViewById(R.id.imgDVD);
//        imgDVD = (ImageView) findViewById(R.id.imgPhotoStack);
 //       imgDVD.setImageResource(R.drawable.presentation_media_dvd);


        //temporarily removed
     /*   Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                rollIn();
            }
        };
        handler.postDelayed(runnable, 3000);*/

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
            player.setPrevAndNextPage(PresentationMediaFlyers.class, PresentationExposureIntro.class);
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


    //added 080815
    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbindDrawables(findViewById(R.id.rootView));
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


  /*  private void rollIn() {
        Animation moving = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1.5f,
                                                    Animation.RELATIVE_TO_SELF, 0,
                                                    Animation.RELATIVE_TO_SELF, 0,
                                                    Animation.RELATIVE_TO_SELF, 0);
        moving.setDuration(1000);
        moving.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imgDVD.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        Animation rotating = new RotateAnimation(180, 360,
                                                    Animation.RELATIVE_TO_SELF, 0.5f,
                                                    Animation.RELATIVE_TO_SELF, 0.5f); // 0.5f
        rotating.setDuration(1000);

        Animation fadein = new AlphaAnimation(0, 1);
        fadein.setDuration(1500);

        AnimationSet rollingIn = new AnimationSet(true);
        rollingIn.addAnimation(rotating);
        rollingIn.addAnimation(moving);
        rollingIn.addAnimation(fadein);
        imgDVD.startAnimation(rollingIn);

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
        stopService(new Intent(PresentationMediaDVDs.this, BackgroundMusicService.class));

    }
}

package com.circlepix.android.presentations;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.circlepix.android.CirclePixAppState;
import com.circlepix.android.R;

/**
 * Created by relly on 2/3/15.
 */
public class PresentationStarMarketing extends PresentationBase {


    private PresentationPageAudioPlayer player;
    private CirclePixAppState appState;

   // ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.presentation_starmarketing);

        // Setup application class
        appState = ((CirclePixAppState)getApplicationContext());
        appState.setContextForPreferences(this);

      /*  image = (ImageView) findViewById(R.id.imageView8);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.presentation_starmarketing);
        image.setImageBitmap(bitmap);
*/

        player = new PresentationPageAudioPlayer(this);
        player.setPrevAndNextPage(PresentationStart.class, PresentationMarketingIntro.class);
        player.playAudio();
        Log.v("playAudio PresStarMtng", "called");
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
            player.setPrevAndNextPage(PresentationStart.class, PresentationMarketingIntro.class);
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

            appState.setActionBarStat(true);  //to show the actionbar to let the user know that presentation was paused when they pressed home or task manager button
        }

        Log.v("going-to-background code", "called");
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

    public void onBackPressed(){
        super.onBackPressed();

        player.stop();

        appState.setActivityStopped(true);
        appState.clearSharedPreferences();
        stopService(new Intent(PresentationStarMarketing.this, BackgroundMusicService.class));
    }
}

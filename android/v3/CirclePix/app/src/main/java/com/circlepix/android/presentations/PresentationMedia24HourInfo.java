package com.circlepix.android.presentations;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.circlepix.android.CirclePixAppState;
import com.circlepix.android.R;

/**
 * Created by relly on 2/11/15.
 */
public class PresentationMedia24HourInfo extends PresentationBase {

    private PresentationPageAudioPlayer player;

    //KBL
    private CirclePixAppState appState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.presentation_media_24hourinfo);

        // Setup application class
        appState = ((CirclePixAppState)getApplicationContext());
        appState.setContextForPreferences(this);

        player = new PresentationPageAudioPlayer(this);

        player.setPrevAndNextPage(PresentationMediaQRCodes.class, PresentationMediaShortcode.class);
        player.playAudio();

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
            player.setPrevAndNextPage(PresentationMediaQRCodes.class, PresentationMediaShortcode.class);
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
        stopService(new Intent(PresentationMedia24HourInfo.this, BackgroundMusicService.class));

    }
}

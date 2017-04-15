package com.circlepix.android.presentations;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.circlepix.android.CirclePixAppState;
import com.circlepix.android.R;

/**
 * Created by relly on 2/14/15.
 */
public class PresentationLeadGenPropertySite extends PresentationBase {

    private PresentationPageAudioPlayer player;
    private TextView leadList;

    //KBL
    private CirclePixAppState appState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.presentation_leadgen_propertysite);

        // Setup application class
        appState = ((CirclePixAppState)getApplicationContext());
        appState.setContextForPreferences(this);

        leadList = (TextView) findViewById(R.id.leadList);

        leadList.setText("\u2022 Driving Directions\n" +
                "\u2022 Schedule Showing\n" +
                "\u2022 Email Brochure\n" +
                "\u2022 Send to a Friend\n" +
                "\u2022 Mortgage Calculator\n" +
                "\u2022 Schools\n" +
                "\u2022 Download Tour\n" +
                "\u2022 Preferred Lender\n" +
                "\u2022 Open House Announcer\n");

        player = new PresentationPageAudioPlayer(this);

        player.setPrevAndNextPage(PresentationLeadGenIntro.class, PresentationLeadGen24hourinfo.class);
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
            player.setPrevAndNextPage(PresentationLeadGenIntro.class, PresentationLeadGen24hourinfo.class);
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
        stopService(new Intent(PresentationLeadGenPropertySite.this, BackgroundMusicService.class));
    }
}

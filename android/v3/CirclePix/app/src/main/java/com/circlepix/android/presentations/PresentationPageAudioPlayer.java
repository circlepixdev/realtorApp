package com.circlepix.android.presentations;


import android.annotation.TargetApi;
import android.support.v7.app.ActionBar;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.circlepix.android.CirclePixAppState;
import com.circlepix.android.HomeActivity;
import com.circlepix.android.MainActivity;
import com.circlepix.android.PresentationsTabActivity;
import com.circlepix.android.R;
import com.circlepix.android.beans.PresentationSequencingPage;
import com.circlepix.android.helpers.OnSwipeTouchListener;

import java.util.Map;


/**
 * Created by relly on 2/4/15.
 */

/**
 * Edited by keuahn on June 23, 2015
 */
public class PresentationPageAudioPlayer extends AppCompatActivity {

    private AppCompatActivity activity;
    private Class nextPage;
    private Class prevPage;
//    private View actionBarLayout;
    private ActionBar actionBar;
    private MediaPlayer mediaPlayer;
    private AudioManager am;
    private boolean isMute;
    private ImageButton muteButtonPresentation;
    private ImageButton unmuteButtonPresentation;
    private ImageButton pauseButtonPresentation;
    private ImageButton playButtonPresentation;
    private ImageButton forwardButtonPresentation;
    private ImageButton backButtonPresentation;
    private LinearLayout linearBackActionBar;
    private Map<Integer, PresentationSequencingPage> map;
    private PresentationSequencingPage page;

    Toolbar toolbar;
    //July 8, 2015: KBL
    private CirclePixAppState appState;

 // public static int audioServiceCurrentPos;
 // private int audioCurrentPosForPlay;
 // private int audioPreviousPos; // when audio is on pause then forward or back is pressed
 // private boolean paused;
 // private boolean forwarded;

    public PresentationPageAudioPlayer(AppCompatActivity activity) {
        this.activity = activity;

        //July 9, 2015: KBL

        // Setup application class
        appState = ((CirclePixAppState)this.activity.getApplicationContext());
        appState.setContextForPreferences(this.activity);


        activity.getSupportActionBar().setDisplayShowHomeEnabled(true); //false
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
      //  activity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        LayoutInflater mInflater = LayoutInflater.from(activity);

        View mCustomView = mInflater.inflate(R.layout.presentation_actionbar_new, null);
        activity.getSupportActionBar().setCustomView(mCustomView);
        activity.getSupportActionBar().setDisplayShowCustomEnabled(true);
        Toolbar parent =(Toolbar) mCustomView.getParent();//first get parent toolbar of current action bar
        parent.setContentInsetsAbsolute(0,0);// set padding programmatically to 0dp

//        parent.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//
//            }
//        });

        View v = activity.getSupportActionBar().getCustomView();
        ViewGroup.LayoutParams lp = v.getLayoutParams();
        lp.width = android.support.v7.app.ActionBar.LayoutParams.MATCH_PARENT;
        v.setLayoutParams(lp);

        muteButtonPresentation = (ImageButton) v.findViewById(R.id.muteButtonPresentation);
        muteButtonPresentation.setOnClickListener(muteButtonClickListener);
        unmuteButtonPresentation = (ImageButton) v.findViewById(R.id.unmuteButtonPresentation);
        unmuteButtonPresentation.setOnClickListener(unmuteButtonClickListener);
        pauseButtonPresentation = (ImageButton) v.findViewById(R.id.pauseButtonPresentation);
        pauseButtonPresentation.setOnClickListener(pauseButtonClickListener);
        playButtonPresentation = (ImageButton) v.findViewById(R.id.playButtonPresentation);
        playButtonPresentation.setOnClickListener(playButtonClickListener);
        forwardButtonPresentation = (ImageButton) v.findViewById(R.id.forwardButtonPresentation);
        forwardButtonPresentation.setOnClickListener(forwardButtonClickListener);
        backButtonPresentation = (ImageButton) v.findViewById(R.id.backButtonPresentation);
        backButtonPresentation.setOnClickListener(backButtonClickListener);
 //       linearBackActionBar = (LinearLayout) v.findViewById(R.id.linearBackActionBar);
//        linearBackActionBar.setOnClickListener(buttonBackActionBarClickListener);

      //  toolbar = (Toolbar) findViewById(R.id.toolbar);
      //  activity.setSupportActionBar(toolbar);
      //  activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

  /*      actionBarLayout = activity.getLayoutInflater().inflate(R.layout.presentation_actionbar, null);
        actionBar = activity.getActionBar();

        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionBarLayout);

        if(appState.actionBarIsHidden().equals(false)){
            actionBar.hide();

        }else{
            actionBar.show();
        }
        Log.v(" GET actionBarStat", String.valueOf(appState.actionBarIsHidden()));

        muteButtonPresentation = (ImageButton) actionBarLayout.findViewById(R.id.muteButtonPresentation);
        muteButtonPresentation.setOnClickListener(muteButtonClickListener);
        unmuteButtonPresentation = (ImageButton) actionBarLayout.findViewById(R.id.unmuteButtonPresentation);
        unmuteButtonPresentation.setOnClickListener(unmuteButtonClickListener);
        pauseButtonPresentation = (ImageButton) actionBarLayout.findViewById(R.id.pauseButtonPresentation);
        pauseButtonPresentation.setOnClickListener(pauseButtonClickListener);
        playButtonPresentation = (ImageButton) actionBarLayout.findViewById(R.id.playButtonPresentation);
        playButtonPresentation.setOnClickListener(playButtonClickListener);
        forwardButtonPresentation = (ImageButton) actionBarLayout.findViewById(R.id.forwardButtonPresentation);
        forwardButtonPresentation.setOnClickListener(forwardButtonClickListener);
        backButtonPresentation = (ImageButton) actionBarLayout.findViewById(R.id.backButtonPresentation);
        backButtonPresentation.setOnClickListener(backButtonClickListener);
        linearBackActionBar = (LinearLayout) actionBarLayout.findViewById(R.id.linearBackActionBar);
        linearBackActionBar.setOnClickListener(buttonBackActionBarClickListener);

*/
        activity.getWindow().getDecorView().findViewById(R.id.rootView).setOnTouchListener(onSwipeTouchListener);

        map = PresentationSequencingSet.getSelectedPresentations();

    }

   /* public void onBackPressed(){

        Intent intentBack = new Intent(activity, PresentationsActivity.class);
        activity.startActivity(intentBack);
        activity.finish();
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:

                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }

//                Intent homeIntent = new Intent(this, HomeActivity.class);
//                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(homeIntent);

                finish();
                //appState.setAudioServiceCurrentPos(0);
                appState.clearSharedPreferences();
                activity.stopService(new Intent(activity, BackgroundMusicService.class));

                //added by KBL 080615
                appState.setActivityStopped(true);
                appState.setPresPlayerPaused(false);




        }
        return (super.onOptionsItemSelected(menuItem));
    }



    public void playAudio() {


        page = map.get(PresentationSequencingSet.currentPageNum);

        //added by KBL 080615: restored this when activity calls playAudio
        appState.setActivityStopped(false);
        if(appState.actionBarIsHidden().equals(false)){
          //  actionBar.hide();
            activity.getSupportActionBar().hide();
        }else{
         //   actionBar.show();
            activity.getSupportActionBar().show();
        }
        //ends here
        if (page.getNarrationType().equals("male")) {
            activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = MediaPlayer.create(activity, page.getMaleAudioFile());
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            am = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onCompletion(MediaPlayer mp) {

                    PresentationSequencingSet.isPause = false;

                    if (PresentationSequencingSet.currentPageNum < map.size()) {

                        if (mediaPlayer != null) {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            mediaPlayer = null;
                        }

                        if (isMute)
                            unmute();

                        page = map.get(++PresentationSequencingSet.currentPageNum);
                        nextPage = page.getClassFile();
                        Bundle bundle = ActivityOptions.makeCustomAnimation(activity.getApplicationContext(),
                                R.anim.slide_in_left,
                                R.anim.slide_out_left).toBundle();
                        Intent intentNextPage = new Intent(activity, nextPage);
                        activity.startActivity(intentNextPage, bundle);
                        //added 081215
                        activity.finish();

                        appState.setActivityStopped(true);
                       // appState.setPresentationEnds(false); //default
                    }/*else if (PresentationSequencingSet.currentPageNum == map.size()) {
                        Log.v("Pres current page number", String.valueOf(PresentationSequencingSet.currentPageNum));
                        Log.v("Pres map.size()", String.valueOf(map.size()));
                        appState.setActivityStopped(false);  //when presentation reaches the end, we should observe when user presses home button too. so bgm will be stopped
                        appState.setPresentationEnds(true);
                    }*/

                }
            });

            if (PresentationSequencingSet.isPause) {
                pause();
            } else {
                play();
            }
            unmuteButtonOpacity();

        } else if (page.getNarrationType().equals("female")) {
            activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = MediaPlayer.create(activity, page.getFemaleAudioFile());
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            am = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onCompletion(MediaPlayer mp) {

                    PresentationSequencingSet.isPause = false;

                    if (PresentationSequencingSet.currentPageNum < map.size()) {

                        if (mediaPlayer != null) {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            mediaPlayer = null;
                        }

                        if (isMute)
                            unmute();

                        page = map.get(++PresentationSequencingSet.currentPageNum);
                        nextPage = page.getClassFile();
                        Bundle bundle = ActivityOptions.makeCustomAnimation(activity.getApplicationContext(),
                                R.anim.slide_in_left,
                                R.anim.slide_out_left).toBundle();
                        Intent intentNextPage = new Intent(activity, nextPage);
                        activity.startActivity(intentNextPage, bundle);
                        //added 081215
                        activity.finish();

                        appState.setActivityStopped(true);
                      //  appState.setPresentationEnds(false); //default
                    }
                    /*else if (PresentationSequencingSet.currentPageNum == map.size()) {
                        Log.v("Pres current page number", String.valueOf(PresentationSequencingSet.currentPageNum));
                        Log.v("Pres map.size()", String.valueOf(map.size()));
                        appState.setActivityStopped(false);
                        appState.setPresentationEnds(true);
                    }*/



                }
            });

            if (PresentationSequencingSet.isPause) {
                pause();
            } else {
                play();
            }
            unmuteButtonOpacity();

        } else {

            manualMode();

        }
    }

    private View.OnClickListener muteButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mute();
        }
    };
    private View.OnClickListener unmuteButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            unmute();
        }
    };
    private View.OnClickListener pauseButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mediaPlayer.isPlaying()) {
                pause();
                ((PresentationBase)activity).pauseAnimation();
                //added by KBL 080615
               // appState.setActivityStopped(false);

                //added by KBL 081315
              //  appState.setPresentationEnds(false);
            }
        }
    };
    private View.OnClickListener playButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!mediaPlayer.isPlaying()) {
                play();
                ((PresentationBase)activity).resumeAnimation();
            }
        }
    };
    private View.OnClickListener forwardButtonClickListener = new View.OnClickListener() {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onClick(View v) {
            if (PresentationSequencingSet.currentPageNum < map.size()) {

                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }

                if (isMute)
                    unmute();

                page = map.get(++PresentationSequencingSet.currentPageNum);
                nextPage = page.getClassFile();
                Bundle bundle = ActivityOptions.makeCustomAnimation(activity.getApplicationContext(),
                        R.anim.slide_in_left,
                        R.anim.slide_out_left).toBundle();
                Intent intentNextPage = new Intent(activity, nextPage);
                activity.startActivity(intentNextPage, bundle);
                //added 081215
                activity.finish();

                //July 9, 2015: KBL
                if(appState.isPaused()){
                    appState.setAudioPreviousPos(appState.getAudioCurrentPosForPlay());
                    appState.setForwarded(true);
                }
                //added by KBL 080615
                appState.setActivityStopped(true);
                appState.setPresPlayerPaused(false); //default 0 pos for audio

            /*  //  if(paused){
                   // audioServiceCurrentPos = BackgroundMusicService.player.getCurrentPosition();
                    audioPreviousPos = audioCurrentPosForPlay;
                    forwarded = true;
                    Log.v("FORWARD POS", String.valueOf(audioPreviousPos));
              //  }*/

                //added by KBL 081315
              //  appState.setPresentationEnds(false);

            }
        }
    };

    private View.OnClickListener backButtonClickListener = new View.OnClickListener() {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onClick(View v) {
            if (PresentationSequencingSet.currentPageNum > 1) {

                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }

                if (isMute)
                    unmute();

                page = map.get(--PresentationSequencingSet.currentPageNum);
                prevPage = page.getClassFile();
                Bundle bundle = ActivityOptions.makeCustomAnimation(activity.getApplicationContext(),
                        R.anim.slide_in_right,
                        R.anim.slide_out_right).toBundle();
                Intent intentBack = new Intent(activity, prevPage);
                activity.startActivity(intentBack, bundle);
                //added 081215
                activity.finish();

                //July 9, 2015: KBL
                if(appState.isPaused()) {
                    appState.setAudioPreviousPos(appState.getAudioCurrentPosForPlay());
                    appState.setBacked(true);
                }

                //added by KBL 080615
                appState.setActivityStopped(true);
                appState.setPresPlayerPaused(false);

                //added by KBL 081315
               // appState.setPresentationEnds(false);

            }

        }
    };
    private View.OnClickListener buttonBackActionBarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }

       //     Intent intentBack = new Intent(activity, PresentationsTabActivity.class);
            Intent intentBack = new Intent(activity, HomeActivity.class);
            activity.startActivity(intentBack);
        //    activity.finish();


            //appState.setAudioServiceCurrentPos(0);
            appState.clearSharedPreferences();
            activity.stopService(new Intent(activity, BackgroundMusicService.class));

            //added by KBL 080615
            appState.setActivityStopped(true);
            appState.setPresPlayerPaused(false);
        }
    };

    //KBL
    public void stop(){
        if (mediaPlayer != null) {
            mediaPlayer.reset();
          //  mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        appState.clearSharedPreferences();
        activity.stopService(new Intent(activity, BackgroundMusicService.class));

//        Intent intentBack = new Intent(activity, PresentationsTabActivity.class);
//        Intent intentBack = new Intent(activity, HomeActivity.class);
//        activity.startActivity(intentBack);
//        activity.finish();




    }

    public void pauseAudio(){ //pause audio when home button is pressed

/*

        if(appState.isPresentationEnds().equals(true)) {

            appState.setActivityStopped(false);
            appState.setPresentationEnds(true);

            appState.setPaused(true);
            appState.setAudioServiceCurrentPos(BackgroundMusicService.player.getCurrentPosition());
          //  appState.setAudioCurrentPosForPlay(appState.getAudioServiceCurrentPos());

            //   paused = true;
            //   audioServiceCurrentPos = BackgroundMusicService.player.getCurrentPosition();
            //   audioCurrentPosForPlay = audioServiceCurrentPos;
            Log.v("PAUSE audioCurrentPos", String.valueOf(appState.getAudioServiceCurrentPos()));
            this.activity.stopService(new Intent(this.activity, BackgroundMusicService.class));

            Log.v("appGetMusic pause", appState.getBgMusic());
        }else{
*/

            //for when user pressed  home button
            appState.setPresPlayerCurrentPos(mediaPlayer.getCurrentPosition());
            appState.setPresPlayerPaused(true);
            //ends here

            pause();
    //    }


    }

    public void resumeAudio(){ //resume audio to the last position(after home button was pressed)
        appState.setAudioServiceCurrentPos(appState.getAudioCurrentPosOnHome());
        this.activity.startService(new Intent(this.activity, BackgroundMusicService.class));
    }

    public void getAudioCurrentPos(String currentPos){ //check from time to time so we know the current pos when Home button is pressed
        if(mediaPlayer != null || mediaPlayer.isPlaying()){
        //    appState.setAudioCurrentPosOnHome(BackgroundMusicService.player.getCurrentPosition());
        //    appState.setPresPlayerCurrentPosOnHome(mediaPlayer.getCurrentPosition());

            appState.setAudioServiceCurrentPos(BackgroundMusicService.player.getCurrentPosition());
            appState.setAudioCurrentPosForPlay(appState.getAudioServiceCurrentPos());
        }

    }

    private void play() {

        if(appState.isPaused().equals(true)) {
            appState.setPaused(false);
            appState.setAudioServiceCurrentPos(appState.getAudioCurrentPosForPlay());
        //    appState.setPresPlayerPaused(false); //set to default

        }else if(appState.isForwarded().equals(true)) {
            appState.setForwarded(false);
            appState.setAudioServiceCurrentPos(appState.getAudioPreviousPos());
        //    appState.setPresPlayerPaused(false);

        }else if(appState.isBacked().equals(true)){
            appState.setBacked(false);
            appState.setAudioServiceCurrentPos(appState.getAudioPreviousPos());
        //    appState.setPresPlayerPaused(false);

        }else{
            appState.setAudioServiceCurrentPos(BackgroundMusicService.player.getCurrentPosition()); //continuous playing og bgm on normal swiping or clicking next or previous

        }

        Log.v("PLAY audioCurrentPos", String.valueOf(appState.getAudioServiceCurrentPos()));
        Log.v("appGetMusic play", appState.getBgMusic());

        this.activity.startService(new Intent(this.activity, BackgroundMusicService.class));

        PresentationSequencingSet.isPause = false;

        Log.v("isPresPlayerPaused", String.valueOf(appState.isPresPlayerPaused()));
        if(appState.isPresPlayerPaused().equals(true)) {
            mediaPlayer.seekTo(appState.getPresPlayerCurrentPos());
            appState.setPresPlayerPaused(false);
            mediaPlayer.start();
        }
      /*  else if(appState.isPresentationEnds() == true ){
            //   appState.setPresentationEnds(false);
        }else {*/
            mediaPlayer.start();
      //  }

      //  Log.v("isPres Ends", String.valueOf(appState.isPresentationEnds()));

        playButtonPresentation.setEnabled(false);
        pauseButtonPresentation.setEnabled(true);
        playButtonPresentation.setAlpha(0.2f);
        pauseButtonPresentation.setAlpha(1.0f);

    }


    private void pause() {

        PresentationSequencingSet.isPause = true;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();

            appState.setPaused(true);
            appState.setAudioServiceCurrentPos(BackgroundMusicService.player.getCurrentPosition());
            appState.setAudioCurrentPosForPlay(appState.getAudioServiceCurrentPos());

            //   paused = true;
            //   audioServiceCurrentPos = BackgroundMusicService.player.getCurrentPosition();
            //   audioCurrentPosForPlay = audioServiceCurrentPos;
            Log.v("PAUSE audioCurrentPos", String.valueOf(appState.getAudioServiceCurrentPos()));
            this.activity.stopService(new Intent(this.activity, BackgroundMusicService.class));

            Log.v("appGetMusic pause", appState.getBgMusic());
        }

        playButtonPresentation.setEnabled(true);
        pauseButtonPresentation.setEnabled(false);
        playButtonPresentation.setAlpha(1.0f);
        pauseButtonPresentation.setAlpha(0.2f);

    }

    private void mute() {
        am.setStreamMute(AudioManager.STREAM_MUSIC, true);
        muteButtonPresentation.setEnabled(false);
        unmuteButtonPresentation.setEnabled(true);
        muteButtonPresentation.setAlpha(0.2f);
        unmuteButtonPresentation.setAlpha(1.0f);
        isMute = true;
    }

    private void unmute() {
        int maxvolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        am.setStreamMute(AudioManager.STREAM_MUSIC, false);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, maxvolume, AudioManager.FLAG_PLAY_SOUND);
        muteButtonPresentation.setEnabled(true);
        unmuteButtonPresentation.setEnabled(false);
        muteButtonPresentation.setAlpha(1.0f);
        unmuteButtonPresentation.setAlpha(0.2f);
        isMute = false;
    }

    private void unmuteButtonOpacity() {
        muteButtonPresentation.setEnabled(true);
        unmuteButtonPresentation.setEnabled(false);
        muteButtonPresentation.setAlpha(1.0f);
        unmuteButtonPresentation.setAlpha(0.2f);
        isMute = false;
    }

    private void manualMode() {
        playButtonPresentation.setEnabled(false);
        pauseButtonPresentation.setEnabled(false);
        playButtonPresentation.setAlpha(0.2f);
        pauseButtonPresentation.setAlpha(0.2f);
    }

    public void setPrevAndNextPage(Class prevPage, Class nextPage) {
        this.prevPage = prevPage;
        this.nextPage = nextPage;
    }

    public void unbindDrawables(View view) {
        if (view.getBackground() != null)
            view.getBackground().setCallback(null);

        if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;
            imageView.setImageBitmap(null);
        } else if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++)
                unbindDrawables(viewGroup.getChildAt(i));

            if (!(view instanceof AdapterView))
                viewGroup.removeAllViews();
        }
    }

    private OnSwipeTouchListener onSwipeTouchListener = new OnSwipeTouchListener(activity) {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onSwipeRight() {
            if (PresentationSequencingSet.currentPageNum > 1) {

                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }

                if (isMute)
                    unmute();

                page = map.get(--PresentationSequencingSet.currentPageNum);
                prevPage = page.getClassFile();
                Bundle bundle = ActivityOptions.makeCustomAnimation(activity.getApplicationContext(),
                        R.anim.slide_in_right,
                        R.anim.slide_out_right).toBundle();
                Intent intentBack = new Intent(activity, prevPage);
                activity.startActivity(intentBack, bundle);
                //added 081215
                activity.finish();


                //July 9, 2015: KBL
                if(appState.isPaused()) {
                    appState.setAudioPreviousPos(appState.getAudioCurrentPosForPlay());
                    appState.setBacked(true);
                 }


                //added by KBL 080615
                appState.setActivityStopped(true);
                appState.setPresPlayerPaused(false);

                //added by KBL 081315
               // appState.setPresentationEnds(false);

            }
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onSwipeLeft() {
            if (PresentationSequencingSet.currentPageNum < map.size()) {

                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }

                if (isMute)
                    unmute();

                page = map.get(++PresentationSequencingSet.currentPageNum);
                nextPage = page.getClassFile();
                Bundle bundle = ActivityOptions.makeCustomAnimation(activity.getApplicationContext(),
                        R.anim.slide_in_left,
                        R.anim.slide_out_left).toBundle();
                Intent intentNextPage = new Intent(activity, nextPage);
                activity.startActivity(intentNextPage, bundle);

                //added 081215
                activity.finish();

                //July 9, 2015: KBL
                if(appState.isPaused()){
                    appState.setAudioPreviousPos(appState.getAudioCurrentPosForPlay());
                    appState.setForwarded(true);
                }

                //added by KBL 080615
                appState.setActivityStopped(true);
                appState.setPresPlayerPaused(false);

                //added by KBL 081315
               // appState.setPresentationEnds(false);
            }

        }

        @Override
        public void onClick() {

            if (activity.getSupportActionBar().isShowing()) {
                activity.getSupportActionBar().hide();
                appState.setActionBarStat(false);
            } else {
                activity.getSupportActionBar().show();
                appState.setActionBarStat(true);
            }


//            if (actionBar.isShowing()) {
//                actionBar.hide();
//                appState.setActionBarStat(false);
//            } else {
//                actionBar.show();
//                appState.setActionBarStat(true);
//            }

            Log.v("SET actionBarStat", String.valueOf(appState.actionBarIsHidden()));
        }
    };



}

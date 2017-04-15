package com.circlepix.android;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.circlepix.android.beans.ListingInformation;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by user on 7/9/2015.
 */
public class CirclePixAppState  extends Application {

    //test 080615
    private Timer mActivityTransitionTimer;
    private TimerTask mActivityTransitionTimerTask;
    public boolean wasInBackground;
    private final long MAX_ACTIVITY_TRANSITION_TIME_MS = 2000;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    //To avoid Exception java.lang.NoClassDefFoundError: com.google.firebase.FirebaseOptions
    public void startActivityTransitionTimer() {
        this.mActivityTransitionTimer = new Timer();
        this.mActivityTransitionTimerTask = new TimerTask() {
            public void run() {
                CirclePixAppState.this.wasInBackground = true;
            }
        };

        this.mActivityTransitionTimer.schedule(mActivityTransitionTimerTask,
                MAX_ACTIVITY_TRANSITION_TIME_MS);
    }

    public void stopActivityTransitionTimer() {
        if (this.mActivityTransitionTimerTask != null) {
            this.mActivityTransitionTimerTask.cancel();
        }

        if (this.mActivityTransitionTimer != null) {
            this.mActivityTransitionTimer.cancel();
        }

        this.wasInBackground = false;
    }
    //ends here

    // Prefs
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    // Private with getters/setters. Encapsulation baby!
    private Integer audioServiceCurrentPos = 0; //audio current position of bgm
    private Integer audioCurrentPos = 0;
    private Integer audioPreviousPos = 0;
    private Integer audioCurrentPosForPlay = 0;
    private Integer audioCurrentPosOnHome = 0;  // when home button is pressed(for bgm)

    private Integer presPlayerCurrentPos = 0; //constantly check audio pos(for pres mediaPlayer)
    private boolean presPlayerPaused = false; //to not confuse when user pressed pause in presentation and when presentation was paused only due to user pressed home button
                                              //to check for mediaPlayer.seekto

    private boolean paused = false;
    private boolean forwarded = false;
    private boolean backed = false;
    private boolean activityStopped = false; //when user presses deveice's backbutton, actionbarBack, forward, backward, swipeLeft, swipeRight - so startActivityTransitionTimer will not be started when onPause of each actvity

    private boolean actionBarStat = false;
    private String bgMusic = "";

    private Integer serverPresTotalSize = 0;
    private boolean downloadDone = false;
  //  private boolean presentationEnds = false; //oncompletion of presentationEnd


    private ArrayList<String> newPresIds = new ArrayList<String>();
    private Integer index = 0; //for swipeListView current index to be used in PresentationsActivity
    private Integer top = 0;
    private boolean addNewPres = false;
    private String networkStatus = "";
    private boolean neverAskAgain = false;

    private boolean isFirstTimeLaunch = false;
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    private boolean isFirstRun = false;
    private boolean isLoginActivityVisible = false;
    private boolean isHomeActivityVisible = false;

    //ListingInformation
    private ArrayList<ListingInformation> listings = new ArrayList<ListingInformation>();

    //for Login rememberMe
//    private boolean rememberMe = false;

    // Delete all shared prefs
    public void clearSharedPreferences() {

        // Clear out all local values
        this.audioServiceCurrentPos = 0;
        this.audioCurrentPos = 0;
        this.audioPreviousPos = 0;
        this.audioCurrentPosForPlay = 0;
        this.audioCurrentPosOnHome = 0;
        this.presPlayerCurrentPos = 0;
        this.presPlayerPaused = false;
        this.paused = false;
        this.forwarded = false;
        this.backed = false;
     //   this.activityStopped = false;
      //  this.presentationEnds = false;

        this.actionBarStat = false;
        this.bgMusic = "";
        this.serverPresTotalSize = 0;

        // Clear shared prefs
        editor.clear();
        editor.commit();
    }


    public void clearPresentationsActivitySP() { //clear sharedPrefs

        this.index = 0;
        this.top = 0;
        this.addNewPres = false;

        // Clear shared prefs
        editor.clear();
        editor.commit();
    }

/*
    public void clearLoginSharedPreferences() {

        this.rememberMe = false;

        editor.clear();
        editor.commit();
    }*/

    // *************************
    // Getters and Setters
    // *************************

    public void setContextForPreferences(Context activity) {
        // Setup preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        editor = preferences.edit();
    }


    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return preferences.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

//--------------------------------------------------------------------------------------------------

 /*   public Boolean isFirstTimeLaunch() {

        Boolean prefIsFirstTimeLaunch= preferences.getBoolean("prefIsFirstTimeLaunch", false);
        if (prefIsFirstTimeLaunch != false) {
            Log.d("LOGCAT", "retrieved " + prefIsFirstTimeLaunch);
            return prefIsFirstTimeLaunch;
        }

        return isFirstTimeLaunch;
    }

    public void setFirstTimeLaunch(Boolean isFirstTimeLaunch) {
        this.isFirstTimeLaunch = isFirstTimeLaunch;
        editor.putBoolean("isFirstTimeLaunch", isFirstTimeLaunch);
        editor.commit();
    }*/

//--------------------------------------------------------------------------------------------------

    public Integer getAudioServiceCurrentPos() {

        Integer prefAudioServiceCurrentPos = preferences.getInt("audioServiceCurrentPos", 0);
        if (prefAudioServiceCurrentPos != 0) {
            Log.d("LOGCAT", "retrieved " + prefAudioServiceCurrentPos);
            return prefAudioServiceCurrentPos;
        }

        return audioServiceCurrentPos;
    }

    public void setAudioServiceCurrentPos(Integer audioServiceCurrentPos) {
        this.audioServiceCurrentPos = audioServiceCurrentPos;
        editor.putInt("audioServiceCurrentPos", audioServiceCurrentPos);
        editor.commit();
    }
//--------------------------------------------------------------------------------------------------

    public Integer getAudioCurrentPos() {

        Integer prefAudioCurrentPos = preferences.getInt("audioCurrentPos", 0);
        if (prefAudioCurrentPos != 0) {
            Log.d("LOGCAT", "retrieved " + prefAudioCurrentPos);
            return prefAudioCurrentPos;
        }

        return audioCurrentPos;
    }

    public void setAudioCurrentPos(Integer audioCurrentPos) {
        this.audioCurrentPos = audioCurrentPos;
        editor.putInt("audioCurrentPos", audioCurrentPos);
        editor.commit();
    }
//--------------------------------------------------------------------------------------------------

    public Integer getAudioPreviousPos() {

        Integer prefAudioPreviousPos = preferences.getInt("audioPreviousPos", 0);
        if (prefAudioPreviousPos != 0) {
            Log.d("LOGCAT", "retrieved " + prefAudioPreviousPos);
            return prefAudioPreviousPos;
        }

        return audioPreviousPos;
    }

    public void setAudioPreviousPos(Integer audioPreviousPos) {
        this.audioPreviousPos = audioPreviousPos;
        editor.putInt("audioPreviousPos", audioPreviousPos);
        editor.commit();
    }
//--------------------------------------------------------------------------------------------------

    public Integer getAudioCurrentPosForPlay() {

        Integer prefAudioCurrentPosForPlay = preferences.getInt("audioCurrentPosForPlay", 0);
        if (prefAudioCurrentPosForPlay != 0) {
            Log.d("LOGCAT", "retrieved " + prefAudioCurrentPosForPlay);
            return prefAudioCurrentPosForPlay;
        }

        return audioCurrentPosForPlay;
    }

    public void setAudioCurrentPosForPlay(Integer audioCurrentPosForPlay) {
        this.audioCurrentPosForPlay = audioCurrentPosForPlay;
        editor.putInt("audioCurrentPosForPlay", audioCurrentPosForPlay);
        editor.commit();
    }
//--------------------------------------------------------------------------------------------------

    public Integer getAudioCurrentPosOnHome() {

        Integer prefAudioCurrentPosOnHome = preferences.getInt("audioCurrentPosOnHome", 0);
        if (prefAudioCurrentPosOnHome != 0) {
            Log.d("LOGCAT", "retrieved " + prefAudioCurrentPosOnHome);
            return prefAudioCurrentPosOnHome;
        }

        return audioCurrentPosOnHome;
    }

    public void setAudioCurrentPosOnHome(Integer audioCurrentPosOnHome) {
        this.audioCurrentPosOnHome = audioCurrentPosOnHome;
        editor.putInt("audioCurrentPosOnHome", audioCurrentPosOnHome);
        editor.commit();
    }
//--------------------------------------------------------------------------------------------------

    public Integer getPresPlayerCurrentPos() {

        Integer prefpresPlayerCurrentPos = preferences.getInt("presPlayerCurrentPos", 0);
        if (prefpresPlayerCurrentPos != 0) {
            Log.d("LOGCAT", "retrieved " + prefpresPlayerCurrentPos);
            return prefpresPlayerCurrentPos;
        }

        return presPlayerCurrentPos;
    }

    public void setPresPlayerCurrentPos(Integer presPlayerCurrentPos) {
        this.presPlayerCurrentPos = presPlayerCurrentPos;
        editor.putInt("presPlayerCurrentPos", presPlayerCurrentPos);
        editor.commit();
    }
//--------------------------------------------------------------------------------------------------

    public Boolean isPresPlayerPaused() {

        Boolean prefPresPlayerPaused= preferences.getBoolean("presPlayerPaused", false);
        if (prefPresPlayerPaused != false) {
            Log.d("LOGCAT", "retrieved " + prefPresPlayerPaused);
            return prefPresPlayerPaused;
        }

        return presPlayerPaused;
    }

    public void setPresPlayerPaused(Boolean presPlayerPaused) {
        this.presPlayerPaused = presPlayerPaused;
        editor.putBoolean("presPlayerPaused", presPlayerPaused);
        editor.commit();
    }
//--------------------------------------------------------------------------------------------------

    public Boolean isPaused() {

        Boolean prefPaused = preferences.getBoolean("paused", false);
        if (prefPaused != false) {
            Log.d("LOGCAT", "retrieved " + prefPaused);
            return prefPaused;
        }

        return paused;
    }

    public void setPaused(Boolean paused) {
        this.paused = paused;
        editor.putBoolean("paused", paused);
        editor.commit();
    }
//--------------------------------------------------------------------------------------------------

    public Boolean isForwarded() {

        Boolean prefForwarded = preferences.getBoolean("forwarded", false);
        if (prefForwarded != false) {
            Log.d("LOGCAT", "retrieved " + prefForwarded);
            return prefForwarded;
        }

        return forwarded;
    }

    public void setForwarded(Boolean forwarded) {
        this.forwarded = forwarded;
        editor.putBoolean("forwarded", forwarded);
        editor.commit();
    }
//--------------------------------------------------------------------------------------------------

    public Boolean isBacked() {

        Boolean prefBacked = preferences.getBoolean("backed", false);
        if (prefBacked != false) {
            Log.d("LOGCAT", "retrieved " + prefBacked);
            return prefBacked;
        }

        return backed;
    }

    public void setBacked(Boolean backed) {
        this.backed = backed;
        editor.putBoolean("backed", backed);
        editor.commit();
    }
//--------------------------------------------------------------------------------------------------

    public Boolean actionBarIsHidden() {

        Boolean prefActionBarStat = preferences.getBoolean("actionBarStat", false);
        if (prefActionBarStat != false) {
            Log.d("LOGCAT", "retrieved " + prefActionBarStat);
            return prefActionBarStat;
        }

        return actionBarStat;
    }

    public void setActionBarStat(Boolean actionBarStat) {
        this.actionBarStat = actionBarStat;
        editor.putBoolean("actionBarStat", actionBarStat);
        editor.commit();
    }
//--------------------------------------------------------------------------------------------------

    public Boolean isActivityStopped() {

        Boolean prefActivityStopped = preferences.getBoolean("activityStopped", false);
        if (prefActivityStopped != false) {
            Log.d("LOGCAT", "retrieved " + prefActivityStopped);
            return prefActivityStopped;
        }

        return activityStopped;
    }

    public void setActivityStopped(Boolean activityStopped) {
        this.activityStopped = activityStopped;
        editor.putBoolean("activityStopped", activityStopped);
        editor.commit();
    }
//--------------------------------------------------------------------------------------------------

    public String getBgMusic() {

        String prefBgMusic = preferences.getString("bgMusic", null);
        if (prefBgMusic != null) {
            Log.d("LOGCAT", "retrieved " + prefBgMusic);
            return prefBgMusic;
        }

        return bgMusic;
    }

    public void setBgMusic(String bgMusic) {
        this.bgMusic = bgMusic;
        editor.putString("bgMusic", bgMusic);
        editor.commit();
    }


//--------------------------------------------------------------------------------------------------

    public Integer getServerPresTotalSize() {

        Integer prefServerPresTotalSize = preferences.getInt("serverPresTotalSize", 0);
        if (prefServerPresTotalSize != 0) {
            Log.d("LOGCAT", "retrieved " + prefServerPresTotalSize);
            return prefServerPresTotalSize;
        }

        return serverPresTotalSize;
    }

    public void setServerPresTotalSize(Integer serverPresTotalSize) {
        this.serverPresTotalSize = serverPresTotalSize;
        editor.putInt("serverPresTotalSize", serverPresTotalSize);
        editor.commit();
    }

//--------------------------------------------------------------------------------------------------

    public Boolean isDownloadDone() {

        Boolean prefDownloadDone = preferences.getBoolean("downloadDone", false);
        if (prefDownloadDone != false) {
            Log.d("LOGCAT", "retrieved " + prefDownloadDone);
            return prefDownloadDone;
        }

        return downloadDone;
    }

    public void setDownloadDone(Boolean downloadDone) {
        this.downloadDone = downloadDone;
        editor.putBoolean("downloadDone", downloadDone);
        editor.commit();
    }

//--------------------------------------------------------------------------------------------------
/*
    public Boolean isPresentationEnds() {

        Boolean prefPresentationEnds = preferences.getBoolean("presentationEnds", false);
        if (prefPresentationEnds != false) {
            Log.d("LOGCAT", "retrieved " + prefPresentationEnds);
            return prefPresentationEnds;
        }

        return presentationEnds;
    }

    public void setPresentationEnds(Boolean presentationEnds) {
        this.presentationEnds = presentationEnds;
        editor.putBoolean("presentationEnds", presentationEnds);
        editor.commit();
    }*/

//--------------------------------------------------------------------------------------------------

    public ArrayList<String> getNewPresIds(){
        return newPresIds;
    }


    public void removeNewPresIds(ArrayList<String> newPresIds)
    {

        int size = newPresIds.size();

        for (int i = 0; i < size; i++) {
            editor.remove("list_"+i);
        }
        editor.commit();
    }


    public void addNewPresIds(ArrayList<String> newPresIds)
    {

        int size = newPresIds.size();
        editor.putInt("list_size", size);

        for (int i = 0; i < size; i++) {
            editor.putString("list_" + i, newPresIds.get(i));
        }
        editor.commit();
    }

//--------------------------------------------------------------------------------------------------

    public Integer getIndex() {

        Integer prefIndex = preferences.getInt("index", 0);
        if (prefIndex != 0) {
            Log.d("LOGCAT", "retrieved " + prefIndex);
            return prefIndex;
        }

        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
        editor.putInt("index", index);
        editor.commit();
    }

//--------------------------------------------------------------------------------------------------

    public Integer getTop() {

        Integer prefTop = preferences.getInt("top", 0);
        if (prefTop != 0) {
            Log.d("LOGCAT", "retrieved " + prefTop);
            return prefTop;
        }

        return top;
    }

    public void setTop(Integer top) {
        this.top = top;
        editor.putInt("top", top);
        editor.commit();
    }
//--------------------------------------------------------------------------------------------------

/*    public Boolean rememberMe() {

        Boolean prefRememberMe = preferences.getBoolean("rememberMeLogin", false);
        if (prefRememberMe != false) {
            Log.d("LOGCAT", "retrieved " + prefRememberMe);
            return prefRememberMe;
        }

        return rememberMe;
    }

    public void setRememberMe(Boolean rememberMe) {
        this.rememberMe = rememberMe;
        editor.putBoolean("rememberMeLogin", rememberMe);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            editor.apply();
        } else {
            editor.commit();
        }
    }*/

//--------------------------------------------------------------------------------------------------

    public Boolean isNeverAskAgain() {

        Boolean prefNeverAskAgain = preferences.getBoolean("neverAskAgain", false);
        if (prefNeverAskAgain != false) {
            Log.d("LOGCAT", "retrieved " + prefNeverAskAgain);
            return prefNeverAskAgain;
        }

        return paused;
    }

    public void setNeverAskAgain(Boolean neverAskAgain) {
        this.neverAskAgain = neverAskAgain;
        editor.putBoolean("neverAskAgain", neverAskAgain);
        editor.commit();
    }

//--------------------------------------------------------------------------------------------------

    public Boolean isFirstRun() {

        Boolean prefFirstRun = preferences.getBoolean("isFirstRun", false);
        if (prefFirstRun != false) {
            Log.d("LOGCAT", "retrieved " + prefFirstRun);
            return prefFirstRun;
        }

        return paused;
    }

    public void setFirstRun(Boolean isFirstRun) {
        this.isFirstRun = isFirstRun;
        editor.putBoolean("isFirstRun", isFirstRun);
        editor.commit();
    }

//--------------------------------------------------------------------------------------------------

    public Boolean isHomeActivityVisible() {

        Boolean prefHomeActivityVisible = preferences.getBoolean("isHomeActivityVisible", false);
        if (prefHomeActivityVisible != false) {
            Log.d("LOGCAT", "retrieved " + prefHomeActivityVisible);
            return prefHomeActivityVisible;
        }

        return paused;
    }

    public void setHomeActivityVisible(Boolean isHomeActivityVisible) {
        this.isHomeActivityVisible = isHomeActivityVisible;
        editor.putBoolean("isHomeActivityVisible", isHomeActivityVisible);
        editor.commit();
    }

//--------------------------------------------------------------------------------------------------

    public Boolean isLoginActivityVisible() {

        Boolean prefLoginActivityVisible = preferences.getBoolean("isLoginActivityVisible", false);
        if (prefLoginActivityVisible != false) {
            Log.d("LOGCAT", "retrieved " + prefLoginActivityVisible);
            return prefLoginActivityVisible;
        }

        return paused;
    }

    public void setLoginActivityVisible(Boolean isLoginActivityVisible) {
        this.isLoginActivityVisible = isLoginActivityVisible;
        editor.putBoolean("isLoginActivityVisible", isLoginActivityVisible);
        editor.commit();
    }



//--------------------------------------------------------------------------------------------------

    public ArrayList<ListingInformation> getListings() {
        return listings;
    }

    public void setListings(ArrayList<ListingInformation> listings) {
        this.listings = listings;
    }


}
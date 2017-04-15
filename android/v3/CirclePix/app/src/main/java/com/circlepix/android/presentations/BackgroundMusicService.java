package com.circlepix.android.presentations;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.circlepix.android.CirclePixAppState;
import com.circlepix.android.R;

/**
 * Created by keuahn on 7/8/2015.
 */
public class BackgroundMusicService extends Service {
    private static final String TAG = null;
    public static MediaPlayer player;
    private int maxVolume = 100;
    private CirclePixAppState appState;
    private int resID;
    private float volume;

    public IBinder onBind(Intent arg0) {

        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();

        // Setup application class
        appState = ((CirclePixAppState)getApplicationContext());
        appState.setContextForPreferences(this);

        if(appState.getBgMusic().equalsIgnoreCase("Song 1")){
            resID = R.raw.bgmusic_1;
            volume = (float) (1 - (Math.log(maxVolume - 70) / Math.log(maxVolume)));
        }else if(appState.getBgMusic().equalsIgnoreCase("Song 2")){
            resID = R.raw.bgmusic_2;
            volume = (float) (1 - (Math.log(maxVolume - 70) / Math.log(maxVolume)));
        }else if(appState.getBgMusic().equalsIgnoreCase("Song 3")){
            resID = R.raw.bgmusic_3;
            volume = (float) (1 - (Math.log(maxVolume - 30) / Math.log(maxVolume)));
        }else{
            //for "None". We will set volume to 0 as if there is no bgmusic. So we don't have to edit PresentationPageAudioPlayer anymore.
            resID = R.raw.bgmusic_1;  //to avoid nullpointerexception
            volume = 0;
           // volume = (float) (1 - (Math.log(maxVolume - 70) / Math.log(maxVolume)));
        }



        player = MediaPlayer.create(this, resID);
        player.setLooping(true); // Set looping
        player.setVolume(volume, volume);
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
//        player.seekTo(PresentationPageAudioPlayer.audioServiceCurrentPos);
        Log.v("appState.getPos", String.valueOf(appState.getAudioServiceCurrentPos()));
        player.seekTo(appState.getAudioServiceCurrentPos());
        player.start();
        return 1;
    }

    public void onStart(Intent intent, int startId) {
        // TODO



    }
    public IBinder onUnBind(Intent arg0) {
        // TODO Auto-generated method stub

        return null;
    }


    public void onStop() {

    }

    public void onPause() {
        if(player.isPlaying()){
            player.pause();
        }

    }
    @Override
    public void onDestroy() {

        player.stop();
        player.release();
      /*  player.reset();
        stopSelf();*/
//        currentAudioPos = PresentationPageAudioPlayer.audioServiceCurrentPos;
//        PresentationPageAudioPlayer.audioServiceCurrentPos = currentAudioPos;
       // PresentationPageAudioPlayer.audioServiceCurrentPos = 0;
    }

    @Override
    public void onLowMemory() {

    }
}
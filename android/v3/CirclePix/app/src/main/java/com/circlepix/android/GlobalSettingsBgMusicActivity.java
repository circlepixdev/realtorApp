package com.circlepix.android;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.circlepix.android.beans.ApplicationSettings;
import com.circlepix.android.helpers.BaseActionBar;
import com.circlepix.android.helpers.Globals;
import com.circlepix.android.helpers.RadioGroupHelper;
import com.circlepix.android.interfaces.IBaseActionBarCallback;
import com.circlepix.android.types.BackgroundMusicType;
import com.google.gson.Gson;

/**
 * Created by keuahn on 7/9/2015.
 */
public class GlobalSettingsBgMusicActivity extends Activity {

    private RadioButton rbBgmNone;
    private RadioButton rbBgm1;
    private RadioButton rbBgm2;
    private RadioButton rbBgm3;
    private ImageView audioBgm1;
    private ImageView audioBgm2;
    private ImageView audioBgm3;
    private MediaPlayer mediaPlayer;
    private int maxVolume = 100;
    private float volume;


    private final int[] bgm_resID = { R.raw.bgmusic_1, R.raw.bgmusic_2, R.raw.bgmusic_3 };

    private final int[] btnID = { R.id.AudioBgm1, R.id.AudioBgm2, R.id.AudioBgm3 };

    private int currentPos;
    private int previousPos = 3; //put dummy value for previousPos just to compare later
    private CirclePixAppState appState;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_settings_bgmusic);


        // Setup application class
        appState = ((CirclePixAppState)getApplicationContext());
        appState.setContextForPreferences(this);


        // Show custom actionbar
        BaseActionBar actionBar = new BaseActionBar(GlobalSettingsBgMusicActivity.this);
        actionBar.setConfig(GlobalSettingsActivity.class,
                "Save",
                false,
                false,
                new IBaseActionBarCallback.Null() {
                    @Override
                    public void back() {
                        appState.setActivityStopped(true);
                        saveChanges();
                        setResult(RESULT_OK);
                        finish();
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                    }
                });
        actionBar.show();


        rbBgmNone = (RadioButton) findViewById(R.id.rbBgmNone);
        rbBgm1 = (RadioButton) findViewById(R.id.rbBgm1);
        rbBgm2 = (RadioButton) findViewById(R.id.rbBgm2);
        rbBgm3 = (RadioButton) findViewById(R.id.rbBgm3);

        audioBgm1 = (ImageView) findViewById(R.id.AudioBgm1);
        audioBgm2 = (ImageView) findViewById(R.id.AudioBgm2);
        audioBgm3 = (ImageView) findViewById(R.id.AudioBgm3);

        mediaPlayer = new MediaPlayer();

        SharedPreferences sharedPreferences = getSharedPreferences(Globals.PREFS_APP_SETTINGS, 0);
        Gson gson = new Gson();
        String fromJson = sharedPreferences.getString(Globals.PREFS_APP_SETTINGS, "");
        ApplicationSettings appSettings = gson.fromJson(fromJson, ApplicationSettings.class);

        if(appSettings.getMusic() == BackgroundMusicType.none){
            rbBgmNone.setChecked(true);
        }else if (appSettings.getMusic() == BackgroundMusicType.song1) {
            rbBgm1.setChecked(true);
        } else if (appSettings.getMusic() == BackgroundMusicType.song2) {
            rbBgm2.setChecked(true);
        } else if (appSettings.getMusic() == BackgroundMusicType.song3) {
            rbBgm3.setChecked(true);
        }


        // Required for the custom radio button to work
        ViewGroup parent = (ViewGroup) findViewById(R.id.linearLayoutRadioGroup);
        RadioGroupHelper.setRadioExclusiveClick(parent);

        addListenersOnButton();

    }



    @Override
    public void onResume() {
        super.onResume();

        CirclePixAppState myApp = (CirclePixAppState)this.getApplication();
        if (myApp.wasInBackground)
        {
            //do nothing
        }
        myApp.stopActivityTransitionTimer();
        ((CirclePixAppState)this.getApplication()).setActivityStopped(false);
    }

    @Override
    public void onPause() {
        super.onPause();

        Boolean j = ((CirclePixAppState)this.getApplication()).isActivityStopped();
        Log.v("isActivityStopped", String.valueOf(j));
        if(!((CirclePixAppState)this.getApplication()).isActivityStopped()){
            ((CirclePixAppState)this.getApplication()).startActivityTransitionTimer();  //checking if app went to background

            if(mediaPlayer.isPlaying()){
                stopAudio(); //stop audio when Home button is pressed
            }
        }
    }



    private void saveChanges() {

        SharedPreferences sharedPreferences = getSharedPreferences(Globals.PREFS_APP_SETTINGS, 0);
        Gson gson = new Gson();
        String fromJson = sharedPreferences.getString(Globals.PREFS_APP_SETTINGS, "");
        ApplicationSettings appSettings = gson.fromJson(fromJson, ApplicationSettings.class);

        if (rbBgmNone.isChecked()) {
            appSettings.setMusic(BackgroundMusicType.none);
        }else if (rbBgm1.isChecked()) {
            appSettings.setMusic(BackgroundMusicType.song1);
        } else if (rbBgm2.isChecked()) {
            appSettings.setMusic(BackgroundMusicType.song2);
        } else if (rbBgm3.isChecked()) {
            appSettings.setMusic(BackgroundMusicType.song3);
        }


        String toJsonObject = gson.toJson(appSettings);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Globals.PREFS_APP_SETTINGS, toJsonObject);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            editor.apply();
        } else {
            editor.commit();
        }

        Log.v("GLOBAL setMusic", appSettings.getMusic().toString());
    }

    public void addListenersOnButton(){

        audioBgm1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPos = 0;
                if(currentPos == previousPos){
                    stopAudio();
                }else{
                    playAudio();
                }
            }
        });

        audioBgm2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPos = 1;
                if (currentPos == previousPos) {
                    stopAudio();
                } else {
                    playAudio();
                }
            }
        });

        audioBgm3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPos = 2;
                if (currentPos == previousPos) {
                    stopAudio();
                } else {
                    playAudio();
                }
            }
        });

    }



    public void playAudio() {

        final int index = currentPos;

        if(previousPos != 3){ //check if
            // not dummy pos, change the image of previously played audio
            ImageView ivPrev = (ImageView)findViewById(btnID[previousPos]);
            ivPrev.setImageResource(R.drawable.audio_play_button);
        }

        ImageView ivCurrent = (ImageView)findViewById(btnID[index]);
        ivCurrent.setImageResource(R.drawable.audio_stop_button);


        if(index == 0){ //reduce volume if it is audioBGM_3
            volume = (float) (1 - (Math.log(maxVolume - 70) / Math.log(maxVolume)));
        }else if(index == 1){ //reduce volume if it is audioBGM_3
            volume = (float) (1 - (Math.log(maxVolume - 70) / Math.log(maxVolume)));
        }else if(index == 2){ //reduce volume if it is audioBGM_3
            volume = (float) (1 - (Math.log(maxVolume - 30) / Math.log(maxVolume)));
        }

        // Play audio
        mediaPlayer.reset();// stops any current playing audio
        mediaPlayer = MediaPlayer.create(this.getApplicationContext(), bgm_resID[index]);
        mediaPlayer.setVolume(volume, volume);
        // create's
        // new
        // mediaplayer
        // with
        // song.
        // if you don't know what is getApplicationContext() here better use
        // MainActivity.this or yourclassname.this
        mediaPlayer.start(); // starting mediaplayer

        // setting up what to do if current song completes.
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override public void onCompletion(MediaPlayer mp) { // TODO
                // Auto-generated method stub
                ImageView ivCurrentDone = (ImageView)findViewById(btnID[index]);
                ivCurrentDone.setImageResource(R.drawable.audio_play_button);
            } });

        previousPos = index;

    }


    public void stopAudio(){
        ImageView iv = (ImageView)findViewById(btnID[previousPos]);
        iv.setImageResource(R.drawable.audio_play_button);

        // Stop audio
        mediaPlayer.reset();// stops any current playing audio
        previousPos = 3; // dummy pos
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }


    //KBL 071515
    @Override
    public void onBackPressed() {
     //   Intent intent = new Intent(GlobalSettingsBgMusicActivity.this, GlobalSettingsActivity.class);
     //   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
     //   startActivity(intent);

        appState.setActivityStopped(true);
        saveChanges();
        setResult(RESULT_OK);
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}

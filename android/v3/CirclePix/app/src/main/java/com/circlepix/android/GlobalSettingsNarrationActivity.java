package com.circlepix.android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.circlepix.android.beans.ApplicationSettings;
import com.circlepix.android.helpers.Globals;
import com.circlepix.android.helpers.RadioGroupHelper;
import com.circlepix.android.types.NarrationType;
import com.google.gson.Gson;

/**
 * Created by relly on 4/29/15.
 */
public class GlobalSettingsNarrationActivity extends AppCompatActivity {

    public LinearLayout maleLayout;
    public LinearLayout femaleLayout;

    private RadioButton rbMale;
    private RadioButton rbFemale;
    private ImageView audioMale;
    private ImageView audioFemale;
    private MediaPlayer mediaPlayer;

    private final int[] bgm_resID = { R.raw.m_presentationintro, R.raw.f_presentationintro };

    private final int[] btnID = { R.id.AudioMale, R.id.AudioFemale };

    private int currentPos;
    private int previousPos = 2; //put dummy value for previousPos just to compare later
    private CirclePixAppState appState;
    private  TextView toolBarSave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_settings_narration);

        // Setup application class
        appState = ((CirclePixAppState)getApplicationContext());
        appState.setContextForPreferences(this);

        // Show custom actionbar
     /*   BaseActionBar actionBar = new BaseActionBar(GlobalSettingsNarrationActivity.this);
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
*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolBarSave = (TextView) findViewById(R.id.toolbar_save);
        toolBarSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Toast.makeText(getApplicationContext(), "Note: API is not done yet", Toast.LENGTH_SHORT).show();

                appState.setActivityStopped(true);
                saveChanges();


            }
        });

        maleLayout = (LinearLayout) findViewById(R.id.linearLayout5);
        femaleLayout = (LinearLayout) findViewById(R.id.linearLayout6);


        rbMale = (RadioButton) findViewById(R.id.rbMale);
        rbFemale = (RadioButton) findViewById(R.id.rbFemale);
        audioMale = (ImageView) findViewById(R.id.AudioMale);
        audioFemale = (ImageView) findViewById(R.id.AudioFemale);

        mediaPlayer = new MediaPlayer();


        SharedPreferences sharedPreferences = getSharedPreferences(Globals.PREFS_APP_SETTINGS, 0);
        Gson gson = new Gson();
        String fromJson = sharedPreferences.getString(Globals.PREFS_APP_SETTINGS, "");
        ApplicationSettings appSettings = gson.fromJson(fromJson, ApplicationSettings.class);
        if (appSettings.getNarration() == NarrationType.male) {
            rbMale.setChecked(true);
        } else if (appSettings.getNarration() == NarrationType.female) {
            rbFemale.setChecked(true);
        }

        maleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbMale.setChecked(true);
                rbFemale.setChecked(false);
            }
        });

        femaleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbFemale.setChecked(true);
                rbMale.setChecked(false);
            }
        });

        // Required for the custom radio button to work
        ViewGroup parent = (ViewGroup) findViewById(R.id.linearLayoutRadioGroup);
        RadioGroupHelper.setRadioExclusiveClick(parent);

        addListenersOnButton();
    }


   /* @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                appState.setActivityStopped(true);
                saveChanges();
                //setResult(RESULT_OK);
                Intent returnIntent = new Intent();
                setResult(AppCompatActivity.RESULT_OK, returnIntent);
//                finish();

                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

        }
        return (super.onOptionsItemSelected(menuItem));
    }
*/
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

        if (rbMale.isChecked()) {
            appSettings.setNarration(NarrationType.male);
        } else if (rbFemale.isChecked()) {
            appSettings.setNarration(NarrationType.female);
        }

        String toJsonObject = gson.toJson(appSettings);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Globals.PREFS_APP_SETTINGS, toJsonObject);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            editor.apply();
        } else {
            editor.commit();
        }

        Intent returnIntent = new Intent();
        setResult(AppCompatActivity.RESULT_OK, returnIntent);
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

        Log.v("GLOBAL setNarration", appSettings.getNarration().toString());
    }


    public void addListenersOnButton(){

        audioMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPos = 0;
                if (currentPos == previousPos) {
                    stopAudio();
                } else {
                    playAudio();
                }
            }
        });

        audioFemale.setOnClickListener(new View.OnClickListener() {
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
    }


    public void playAudio() {

        final int index = currentPos;

        if(previousPos != 2){ //check if
            // not dummy pos, change the image of previously played audio
            ImageView ivPrev = (ImageView)findViewById(btnID[previousPos]);
            ivPrev.setImageResource(R.drawable.audio_play_button);
        }

        ImageView ivCurrent = (ImageView)findViewById(btnID[index]);
        ivCurrent.setImageResource(R.drawable.audio_stop_button);

        // Play audio
        mediaPlayer.reset();// stops any current playing audio
        mediaPlayer = MediaPlayer.create(this.getApplicationContext(), bgm_resID[index]);// create's
        // new
        // mediaplayer
        // with
        // song.
        // if you don't know what is getApplicationContext() here better use
        // MainActivity.this or yourclassname.this
        mediaPlayer.start(); // starting mediaplayer

        // setting up what to do if current song completes.
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) { // TODO
                // Auto-generated method stub
                ImageView ivCurrentDone = (ImageView)findViewById(btnID[index]);
                ivCurrentDone.setImageResource(R.drawable.audio_play_button);
                // previousPos = 2;
            } });

        previousPos = index;

    }


    public void stopAudio(){
        ImageView iv = (ImageView)findViewById(btnID[previousPos]);
        iv.setImageResource(R.drawable.audio_play_button);

        // Stop audio
        mediaPlayer.reset();// stops any current playing audio
        previousPos = 2; // dummy pos
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }


    //KBL 071515
    @Override
    public void onBackPressed() {
      //  Intent intent = new Intent(GlobalSettingsNarrationActivity.this, GlobalSettingsActivity.class);
      //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      //  startActivity(intent);
        appState.setActivityStopped(true);
        Intent returnIntent = new Intent();
        setResult(AppCompatActivity.RESULT_OK, returnIntent);
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
      //  saveChanges();
       // setResult(RESULT_OK);
      //  finish();

     //   overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}

package com.circlepix.android;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.circlepix.android.beans.ApplicationSettings;
import com.circlepix.android.data.Presentation;
import com.circlepix.android.data.PresentationDataSource;
import com.circlepix.android.helpers.BaseActionBar;
import com.circlepix.android.helpers.Globals;
import com.circlepix.android.helpers.RadioGroupHelper;
import com.circlepix.android.interfaces.IBaseActionBarCallback;
import com.circlepix.android.types.BackgroundMusicType;
import com.google.gson.Gson;


/**
 * Created by user on 7/9/2015.
 */
public class WizardSettingsBGMusicActivity extends Activity {


    private Long presentationId = null;
    private Presentation p;
    private PlaceholderFragment frag;
    private boolean appSettingsEditMode;

    //KBL 071415
  //  private boolean appBGMusicEditMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizard_settings_bgmusic);
        setTitle("");


        final CirclePixAppState myApp = (CirclePixAppState)this.getApplication();

        // Show custom actionbar
        BaseActionBar actionBar = new BaseActionBar(WizardSettingsBGMusicActivity.this);
        actionBar.setConfig(WizardSettingsActivity.class,
                "Save",
                false,
                false,
                new IBaseActionBarCallback.Null() {
                    @Override
                    public void back() {

                        try {
                             myApp.setActivityStopped(true);
                             saveChanges();
                        } catch (Exception e) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(WizardSettingsBGMusicActivity.this);
                            alert.setTitle("Database Error");
                            alert.setMessage("There was a database error. If this problem persists then please report it.");
                            alert.setPositiveButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int whichButton) {
                                            // Do nothing
                                        }
                                    });
                            alert.show();
                        }
                        setResult(RESULT_OK);
                        finish();
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                    }
                });
        actionBar.show();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            presentationId = extras.getLong("presentationId");
            appSettingsEditMode = extras.getBoolean("appSettingsEditMode");
//            appBGMusicEditMode = extras.getBoolean("appBGMusicEditMode");

            PresentationDataSource dao = new PresentationDataSource(this);
            dao.open(false);
            p = dao.fetch(presentationId);
            dao.close();
        }

        if (savedInstanceState == null) {
            frag = new PlaceholderFragment();
            frag.setContainer(p);
//            frag.setAppSettingsMode(appBGMusicEditMode);//appSettingsEditMode
            frag.setAppSettingsMode(appSettingsEditMode);
            getFragmentManager().beginTransaction()
                    .add(R.id.container, frag).commit();
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // ignore orientation/keyboard change
        super.onConfigurationChanged(newConfig);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        try {
            ((CirclePixAppState)this.getApplication()).setActivityStopped(true);
            saveChanges();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setResult(RESULT_OK);
        super.onBackPressed();
    }



    private void saveChanges() throws Exception{
        ((CirclePixAppState)this.getApplication()).setActivityStopped(true);
        BackgroundMusicType bgMusic = null;
        if (frag.rbBgmNone.isChecked()) {
            bgMusic = BackgroundMusicType.none;
        }else if (frag.rbBgm1.isChecked()) {
            bgMusic = BackgroundMusicType.song1;
        } else if (frag.rbBgm2.isChecked()) {
            bgMusic = BackgroundMusicType.song2;
        } else if (frag.rbBgm3.isChecked()) {
            bgMusic = BackgroundMusicType.song3;
        }

        p.setMusic(bgMusic);

        PresentationDataSource dao = new PresentationDataSource(this);
        dao.open(true);
        dao.updatePresentation(p);
        dao.close();
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private Presentation p;
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
        private View rootView;

        private final int[] bgm_resID = { R.raw.bgmusic_1, R.raw.bgmusic_2, R.raw.bgmusic_3 };

        private final int[] btnID = { R.id.AudioBgm1, R.id.AudioBgm2, R.id.AudioBgm3 };

        private int currentPos;
        private int previousPos = 3; //put dummy value for previousPos just to compare later

        private boolean appSettingsEditMode;
       // private boolean appBGMusicEditMode;

        public PlaceholderFragment() {
        }

        public void setContainer(Presentation p) {
            this.p = p;
        }

        public void setAppSettingsMode(boolean appSettingsEditMode) {
            this.appSettingsEditMode = appSettingsEditMode;
        }

      /*  public void setAppSettingsMode(boolean appBGMusicEditMode) {
            this.appBGMusicEditMode = appBGMusicEditMode;
        }
*/
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_wizard_settings_bgmusic, container, false);

            rbBgmNone = (RadioButton) rootView.findViewById(R.id.rbBgmNone);
            rbBgm1 = (RadioButton) rootView.findViewById(R.id.rbBgm1);
            rbBgm2 = (RadioButton) rootView.findViewById(R.id.rbBgm2);
            rbBgm3 = (RadioButton) rootView.findViewById(R.id.rbBgm3);
            audioBgm1 = (ImageView) rootView.findViewById(R.id.AudioBgm1);
            audioBgm2 = (ImageView) rootView.findViewById(R.id.AudioBgm2);
            audioBgm3 = (ImageView) rootView.findViewById(R.id.AudioBgm3);

            mediaPlayer = new MediaPlayer();

            BackgroundMusicType bgMusic = null;

            if (p != null) {

                if (appSettingsEditMode) {
                    // Edit mode
                    bgMusic = p.getMusic();
                    if (bgMusic == BackgroundMusicType.none) {
                        rbBgmNone.setChecked(true);
                    }else if (bgMusic == BackgroundMusicType.song1) {
                        rbBgm1.setChecked(true);
                    } else if (bgMusic == BackgroundMusicType.song2) {
                        rbBgm2.setChecked(true);
                    } else if (bgMusic == BackgroundMusicType.song3) {
                        rbBgm3.setChecked(true);
                    }

                    Log.v("BGMUSIC", bgMusic.toString());
                }else{
                   // PresentationsActivity.isNewPres = false;

                    // Load default global settings for new presentation
                    SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(Globals.PREFS_APP_SETTINGS, 0);
                    Gson gson = new Gson();
                    String fromJson = sharedPreferences.getString(Globals.PREFS_APP_SETTINGS, "");
                    ApplicationSettings appSettings = gson.fromJson(fromJson, ApplicationSettings.class);
                    bgMusic = appSettings.getMusic();

                    if (bgMusic == BackgroundMusicType.none) {
                        rbBgmNone.setChecked(true);
                    }else if (bgMusic == BackgroundMusicType.song1) {
                        rbBgm1.setChecked(true);
                    } else if (bgMusic == BackgroundMusicType.song2) {
                        rbBgm2.setChecked(true);
                    } else if (bgMusic == BackgroundMusicType.song3) {
                        rbBgm3.setChecked(true);
                    }
                }

                Log.v("WZ BGM appSettingsEditMode", String.valueOf(appSettingsEditMode));
            }

            audioBgm1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentPos = 0;
                    if(currentPos == previousPos){
                        stopAudio(rootView);
                    }else{
                        playAudio(rootView);
                    }
                }
            });

            audioBgm2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentPos = 1;
                    if (currentPos == previousPos) {
                        stopAudio(rootView);
                    } else {
                        playAudio(rootView);
                    }
                }
            });

            audioBgm3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentPos = 2;
                    if (currentPos == previousPos) {
                        stopAudio(rootView);
                    } else {
                        playAudio(rootView);
                    }
                }
            });

            // Required for the custom radio button to work
            final ViewGroup parent = (ViewGroup) rootView.findViewById(R.id.linearLayoutRadioGroup);
            RadioGroupHelper.setRadioExclusiveClick(parent);


            return rootView;
        }


        public void playAudio(final View v) {

            final int index = currentPos;


            if(previousPos != 3){ //check if
                // not dummy pos, change the image of previously played audio
                ImageView ivPrev = (ImageView)v.findViewById(btnID[previousPos]);
                ivPrev.setImageResource(R.drawable.audio_play_button);
            }

            ImageView ivCurrent = (ImageView)v.findViewById(btnID[index]);
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
            mediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), bgm_resID[index]);
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
                    ImageView ivCurrentDone = (ImageView)v.findViewById(btnID[index]);
                    ivCurrentDone.setImageResource(R.drawable.audio_play_button);
                } });

            previousPos = index;

        }


        public void stopAudio(final View v){
            ImageView iv = (ImageView)v.findViewById(btnID[previousPos]);
            iv.setImageResource(R.drawable.audio_play_button);

            // Stop audio=
            mediaPlayer.reset();// stops any current playing audio
            previousPos = 3; // dummy pos
        }


        @Override
        public void onDestroy() {
            super.onDestroy();
            mediaPlayer.release();
        }


        @Override
        public void onResume() {
            super.onResume();

            CirclePixAppState myApp = (CirclePixAppState)getActivity().getApplication();
            if (myApp.wasInBackground)
            {
                //do nothing
            }
            myApp.stopActivityTransitionTimer();
            ((CirclePixAppState)getActivity().getApplication()).setActivityStopped(false);
        }

        @Override
        public void onPause() {
            super.onPause();

            Boolean j = ((CirclePixAppState)getActivity().getApplication()).isActivityStopped();
            Log.v("isActivityStopped", String.valueOf(j));
            if(!((CirclePixAppState)getActivity().getApplication()).isActivityStopped()){
                ((CirclePixAppState)getActivity().getApplication()).startActivityTransitionTimer();  //checking if app went to background

                if(mediaPlayer.isPlaying()){
                    stopAudio(rootView); //stop audio when Home button is pressed
                }
            }
        }
    }

}
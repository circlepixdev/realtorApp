package com.circlepix.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.circlepix.android.beans.ApplicationSettings;
import com.circlepix.android.data.Presentation;
import com.circlepix.android.data.PresentationDataSource;
import com.circlepix.android.helpers.BaseActionBar;
import com.circlepix.android.helpers.Globals;
import com.circlepix.android.helpers.RadioGroupHelper;
import com.circlepix.android.interfaces.IBaseActionBarCallback;
import com.circlepix.android.types.NarrationType;
import com.google.gson.Gson;

/**
 * Created by relly on 3/5/15.
 */
public class WizardSettingsNarrationActivity extends AppCompatActivity {


    private Long presentationId = null;
    private Presentation p;
    private PlaceholderFragment frag;
    private boolean appSettingsEditMode;
    private TextView toolBarSave;
    //KBL 071415
   // private boolean appNarrationEditMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizard_settings_narration);
        setTitle("");

        final CirclePixAppState myApp = (CirclePixAppState)this.getApplication();

        // Show custom actionbar
      /*  BaseActionBar actionBar = new BaseActionBar(WizardSettingsNarrationActivity.this);
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
                            AlertDialog.Builder alert = new AlertDialog.Builder(WizardSettingsNarrationActivity.this);
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
*/

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_wizard_settings_narration);

        toolBarSave = (TextView) findViewById(R.id.toolbar_save);
        toolBarSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    myApp.setActivityStopped(true);
                    saveChanges();
                } catch (Exception e) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(WizardSettingsNarrationActivity.this);
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

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            presentationId = extras.getLong("presentationId");
            appSettingsEditMode = extras.getBoolean("appSettingsEditMode");
         //   appNarrationEditMode = extras.getBoolean("appNarrationEditMode");
            PresentationDataSource dao = new PresentationDataSource(this);
            dao.open(false);
            p = dao.fetch(presentationId);
            dao.close();
        }

        if (savedInstanceState == null) {
            frag = new PlaceholderFragment();
            frag.setContainer(p);

//            frag.setAppSettingsMode(appNarrationEditMode); //appSettingsEditMode
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
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            // The user pressed the UP button (on actionbar)
            ((CirclePixAppState)this.getApplication()).setActivityStopped(true);

            setResult(RESULT_OK);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {

//        try {
//            ((CirclePixAppState)this.getApplication()).setActivityStopped(true);
//            saveChanges();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        ((CirclePixAppState)this.getApplication()).setActivityStopped(true);

        setResult(RESULT_OK);
        super.onBackPressed();
    }

    private void saveChanges() throws Exception {

        NarrationType narration = null;
        if (frag.rbMale.isChecked()) {
            narration = NarrationType.male;
        } else if (frag.rbFemale.isChecked()) {
            narration = NarrationType.female;
        }

        p.setNarration(narration);

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
        public LinearLayout maleLayout;
        public LinearLayout femaleLayout;

        private RadioButton rbMale;
        private RadioButton rbFemale;
        private ImageView audioMale;
        private ImageView audioFemale;
        private MediaPlayer mediaPlayer;
        private boolean appSettingsEditMode;
        private View rootView;
        private CirclePixAppState appState;

        //KBL 071415
      //  private boolean appNarrationEditMode;

        private final int[] bgm_resID = { R.raw.m_presentationintro, R.raw.f_presentationintro };

        private final int[] btnID = { R.id.AudioMale, R.id.AudioFemale };

        private int currentPos;
        private int previousPos = 2; //put dummy value for previousPos just to compare later

        public PlaceholderFragment() {
        }

        public void setContainer(Presentation p) {
            this.p = p;
        }


        public void setAppSettingsMode(boolean appSettingsEditMode) {
            this.appSettingsEditMode = appSettingsEditMode;
        }

     /*   public void setAppSettingsMode(boolean appNarrationEditMode) {
            this.appNarrationEditMode = appNarrationEditMode;
        }*/

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_wizard_settings_narration, container, false);


            // Setup application class
            appState = ((CirclePixAppState)getActivity().getApplicationContext());
            appState.setContextForPreferences(getActivity().getApplicationContext());

            maleLayout = (LinearLayout) rootView.findViewById(R.id.linearLayout1);
            femaleLayout = (LinearLayout) rootView.findViewById(R.id.linearLayout2);

            rbMale = (RadioButton) rootView.findViewById(R.id.rbMale);
            rbFemale = (RadioButton) rootView.findViewById(R.id.rbFemale);
            audioMale = (ImageView) rootView.findViewById(R.id.AudioMale);
            audioFemale = (ImageView) rootView.findViewById(R.id.AudioFemale);

            mediaPlayer = new MediaPlayer();

            NarrationType narration = null;

            if (p != null) {
                if (appSettingsEditMode) {
                    // Edit mode
                    narration = p.getNarration();
                    if (narration == NarrationType.male) {
                        rbMale.setChecked(true);
                    } else if (narration == NarrationType.female) {
                        rbFemale.setChecked(true);
                    }
                }else {
                    // Load default global settings for new presentation

                    SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(Globals.PREFS_APP_SETTINGS, 0);
                    Gson gson = new Gson();
                    String fromJson = sharedPreferences.getString(Globals.PREFS_APP_SETTINGS, "");
                    ApplicationSettings appSettings = gson.fromJson(fromJson, ApplicationSettings.class);
                    narration = appSettings.getNarration();
                    if (narration == NarrationType.male) {
                        rbMale.setChecked(true);
                    } else if (narration == NarrationType.female) {
                        rbFemale.setChecked(true);
                    }
                }

                Log.v("WZ NARR appSettingsEditMode", String.valueOf(appSettingsEditMode));
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

            audioMale.setOnClickListener(new View.OnClickListener() {
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

            audioFemale.setOnClickListener(new View.OnClickListener() {
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

            // Required for the custom radio button to work
            final ViewGroup parent = (ViewGroup) rootView.findViewById(R.id.linearLayoutRadioGroup);
            RadioGroupHelper.setRadioExclusiveClick(parent);

            return rootView;
        }



        public void playAudio(final View v) {

            final int index = currentPos;

            if(previousPos != 2){ //check if
                // not dummy pos, change the image of previously played audio
                ImageView ivPrev = (ImageView)v.findViewById(btnID[previousPos]);
                ivPrev.setImageResource(R.drawable.audio_play_button);
            }

            ImageView ivCurrent = (ImageView)v.findViewById(btnID[index]);
            ivCurrent.setImageResource(R.drawable.audio_stop_button);

            // Play audio
            mediaPlayer.reset();// stops any current playing audio
            mediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), bgm_resID[index]);// create's
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
                    ImageView ivCurrentDone = (ImageView)v.findViewById(btnID[index]);
                    ivCurrentDone.setImageResource(R.drawable.audio_play_button);

                } });

            previousPos = index;

        }


        public void stopAudio(final View v){
            ImageView iv = (ImageView)v.findViewById(btnID[previousPos]);
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

package com.circlepix.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
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

import com.circlepix.android.data.Presentation;
import com.circlepix.android.data.PresentationDataSource;
import com.circlepix.android.helpers.BaseActionBar;
import com.circlepix.android.helpers.RadioGroupHelper;
import com.circlepix.android.interfaces.IBaseActionBarCallback;
import com.circlepix.android.types.NarrationType;
import com.circlepix.android.types.PhotographyType;

/**
 * Created by relly on 3/5/15.
 */
public class WizardSettingsPhotographyActivity extends AppCompatActivity {

    private Long presentationId = null;
    private Presentation p;
    private PlaceholderFragment frag;
    Context context = this;
    private TextView toolBarSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizard_settings_photography);
        setTitle("");


        final CirclePixAppState myApp = (CirclePixAppState)this.getApplication();

        // Show custom actionbar
        /*BaseActionBar actionBar = new BaseActionBar(WizardSettingsPhotographyActivity.this);
        actionBar.setConfig(WizardSettingsActivity.class,
                "Save",
                false,
                false,
                new IBaseActionBarCallback.Null() {
                    @Override
                    public void back() {
                        try {
                            saveChanges();
                        } catch (Exception e) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(WizardSettingsPhotographyActivity.this);
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
        actionBar.show();*/

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_wizard_settings_photography);

        toolBarSave = (TextView) findViewById(R.id.toolbar_save);
        toolBarSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    saveChanges();
                } catch (Exception e) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(WizardSettingsPhotographyActivity.this);
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
            PresentationDataSource dao = new PresentationDataSource(this);
            dao.open(false);
            p = dao.fetch(presentationId);
            dao.close();
        }

        if (savedInstanceState == null) {
            frag = new PlaceholderFragment();
            frag.setContainer(p);
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
        ((CirclePixAppState)this.getApplication()).setActivityStopped(true);
        PhotographyType photoType = null;
        if (frag.rbPro.isChecked()) {
            photoType = PhotographyType.professional;
        } else {
            photoType = PhotographyType.agent;
        }

        p.setPhotographyType(photoType);

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
        public LinearLayout agentLayout;
        public LinearLayout professionalLayout;

        private ImageView audioProfessional;
        private ImageView audioAgent;
        private MediaPlayer mediaPlayer;
        private RadioButton rbPro;
        private RadioButton rbAgent;
        private View rootView;

        private final int[] M_resID = { R.raw.m_marketingmaterials_professionalphoto, R.raw.m_marketingmaterials_agentphoto };
        private final int[] F_resID = { R.raw.f_marketingmaterials_professionalphoto, R.raw.f_marketingmaterials_agentphoto };

        private final int[] btnID = { R.id.AudioProfessional, R.id.AudioAgent };

        private int currentPos;
        private int previousPos = 2; //put dummy value for previousPos just to compare later

        public PlaceholderFragment() {
        }

        public void setContainer(Presentation p) {
            this.p = p;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_wizard_settings_photography, container, false);

            professionalLayout = (LinearLayout) rootView.findViewById(R.id.linearLayout3);
            agentLayout = (LinearLayout) rootView.findViewById(R.id.linearLayout4);

            rbPro = (RadioButton) rootView.findViewById(R.id.rbProfessional);
            rbAgent = (RadioButton) rootView.findViewById(R.id.rbAgent);
            audioProfessional = (ImageView) rootView.findViewById(R.id.AudioProfessional);
            audioAgent = (ImageView) rootView.findViewById(R.id.AudioAgent);

            mediaPlayer = new MediaPlayer();

            if (p != null) {
                PhotographyType photoType = p.getPhotographyType();
                if (photoType.name().equalsIgnoreCase("Professional")) {
                    rbPro.setChecked(true);
                } else if (photoType.name().equalsIgnoreCase("Agent")) {
                    rbAgent.setChecked(true);
                }
            }

            agentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    rbAgent.setChecked(true);
                    rbPro.setChecked(false);
                }
            });

            professionalLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rbPro.setChecked(true);
                    rbAgent.setChecked(false);

                }
            });

            audioProfessional.setOnClickListener(new View.OnClickListener() {
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

            audioAgent.setOnClickListener(new View.OnClickListener() {
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

            ViewGroup parent = (ViewGroup) rootView.findViewById(R.id.linearLayoutRadioGroup);
            RadioGroupHelper.setRadioExclusiveClick(parent);

            return rootView;
        }


        public void playAudio(final View v) {

            final int index = currentPos;
            int genderNarration[];

            NarrationType narration = null;
            narration = p.getNarration();
            if (narration == NarrationType.male) {
                genderNarration = M_resID;
            }else{
                genderNarration = F_resID;
            }

            if(previousPos != 2){ //check if
                // not dummy pos, change the image of previously played audio
                ImageView ivPrev = (ImageView)v.findViewById(btnID[previousPos]);
                ivPrev.setImageResource(R.drawable.audio_play_button);
            }

            ImageView ivCurrent = (ImageView)v.findViewById(btnID[index]);
            ivCurrent.setImageResource(R.drawable.audio_stop_button);

            // Play audio
            mediaPlayer.reset();// stops any current playing audio
            mediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), genderNarration[index]);// create's
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
                    ImageView ivCurrentDone = (ImageView) v.findViewById(btnID[index]);
                    ivCurrentDone.setImageResource(R.drawable.audio_play_button);
                }
            });

            previousPos = index;

        }


        public void stopAudio(View v){
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

            Boolean j = ((CirclePixAppState) getActivity().getApplication()).isActivityStopped();
            Log.v("isActivityStopped", String.valueOf(j));
            if (!((CirclePixAppState) getActivity().getApplication()).isActivityStopped()) {
                ((CirclePixAppState) getActivity().getApplication()).startActivityTransitionTimer();  //checking if app went to background

                if (mediaPlayer.isPlaying()) {
                    stopAudio(rootView); //stop audio when Home button is pressed
                }
            }
        }

    }

}

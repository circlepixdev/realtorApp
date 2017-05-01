package com.circlepix.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
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
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.circlepix.android.data.Presentation;
import com.circlepix.android.data.PresentationDataSource;
import com.circlepix.android.helpers.BaseActionBar;
import com.circlepix.android.interfaces.IBaseActionBarCallback;
import com.circlepix.android.types.NarrationType;

public class WizardCommActivity extends AppCompatActivity {

	private Long presentationId = null;
	private Presentation p;
	private PlaceholderFragment frag;
	private TextView toolBarSave;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wizard_comm);
		setTitle("");

		final CirclePixAppState myApp = (CirclePixAppState)this.getApplication();

        // Show custom actionbar
     /*   BaseActionBar actionBar = new BaseActionBar(WizardCommActivity.this);
        actionBar.setConfig(WizardMainActivity.class,
                "Save",
                false,
                false,
                new IBaseActionBarCallback.Null() {
                    @Override
                    public void back() {
						*//*saveChanges();
                        setResult(RESULT_OK);
                        finish();
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);*//*

						myApp.setActivityStopped(true);

						if (!prepareToNavigate()) {
							return;
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
		getSupportActionBar().setTitle(R.string.title_activity_wizard_comm);

		toolBarSave = (TextView) findViewById(R.id.toolbar_save);
		toolBarSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				myApp.setActivityStopped(true);

				if (!prepareToNavigate()) {
					return;
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
	public void onBackPressed() {
	/*	saveChanges();
	    setResult(RESULT_OK);
	    super.onBackPressed();*/

		// The user pressed the hardware back button
//		if (!prepareToNavigate()) {
//			// Stop the navigation
//			return;
//		}

		((CirclePixAppState)this.getApplication()).setActivityStopped(true);

		setResult(RESULT_OK);
		super.onBackPressed();
	}

	private boolean prepareToNavigate() {

		if (!validateForm()) {
			// Stop the navigation
			return false;
		}

		// Save the presentation data
		try {
			saveChanges();
		} catch (Exception e) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
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

			return false;
		}
		
		return true;
	}

	private void saveChanges() {

		try {
			p.setCommStats(frag.stats.isChecked());
			p.setCommEmail(frag.email.isChecked());
			p.setCommBatchText(frag.batchText.isChecked());
			PresentationDataSource dao = new PresentationDataSource(this);
			dao.open(true);
			dao.updatePresentation(p);
			dao.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private boolean validateForm() {
		
		if (!frag.stats.isChecked()) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Communications Errors");
			alert.setMessage("The Stats option must be selected.");
			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Do nothing
				}
			});
			alert.show();

			return false;
		}
		
		return true;
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public RelativeLayout emailLayout;
		public RelativeLayout batchTextingLayout;

		public Switch stats;
		public Switch email;
		public Switch batchText;

		public ImageView audioStats;
		public ImageView audioEmail;
		public ImageView audioBatchText;
		private MediaPlayer mediaPlayer;
		private View rootView;

		private final int[] F_resID = { R.raw.f_comm_stats, R.raw.f_comm_email, R.raw.f_comm_text };

		private final int[] M_resID = { R.raw.m_comm_stats, R.raw.m_comm_email, R.raw.m_comm_text };

		private final int[] btnID = { R.id.AudioStats, R.id.AudioEmail, R.id.AudioBatchText};

		private int currentPos;
		private int previousPos = 3; //put dummy value for previousPos just to compare later

		private Presentation p;

		public PlaceholderFragment() {
		}

		public void setContainer(Presentation p) {
			this.p = p;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
			rootView = inflater.inflate(R.layout.fragment_wizard_comm,
					container, false);

			emailLayout = (RelativeLayout) rootView.findViewById(R.id.relativeLayout20);
			batchTextingLayout = (RelativeLayout) rootView.findViewById(R.id.relativeLayout21);

			// Get controls
			stats = (Switch) rootView.findViewById(R.id.CommStats);
			email = (Switch) rootView.findViewById(R.id.CommEmail);
			batchText = (Switch) rootView.findViewById(R.id.CommBatchText);

			audioStats = (ImageView) rootView.findViewById(R.id.AudioStats);
			audioEmail = (ImageView) rootView.findViewById(R.id.AudioEmail);
			audioBatchText = (ImageView) rootView.findViewById(R.id.AudioBatchText);
			mediaPlayer = new MediaPlayer();

			stats.setAlpha(0.6f);

			if (p!= null) {
				stats.setChecked(true);
				//stats.setChecked(p.isCommStats());
				email.setChecked(p.isCommEmail());
				batchText.setChecked(p.isCommBatchText());
			}

			emailLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(email.isChecked()){
						email.setChecked(false);
					}else{
						email.setChecked(true);
					}
				}
			});

			batchTextingLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(batchText.isChecked()){
						batchText.setChecked(false);
					}else{
						batchText.setChecked(true);
					}
				}
			});

			audioStats.setOnClickListener(new View.OnClickListener() {
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

			audioEmail.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					currentPos = 1;
					if(currentPos == previousPos){
						stopAudio(rootView);
					}else{
						playAudio(rootView);
					}
				}
			});

			audioBatchText.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					currentPos = 2;
					if(currentPos == previousPos){
						stopAudio(rootView);
					}else{
						playAudio(rootView);
					}
				}
			});

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

			if(previousPos != 3){ //check if
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

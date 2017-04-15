package com.circlepix.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;

import com.circlepix.android.data.Presentation;
import com.circlepix.android.data.PresentationDataSource;
import com.circlepix.android.helpers.BaseActionBar;
import com.circlepix.android.interfaces.IBaseActionBarCallback;
import com.circlepix.android.types.NarrationType;

/**
 * Edited by Keuahn on June 20, 2015
 */
public class WizardMediaActivity extends Activity {

	private Long presentationId = null;
	private Presentation p;
	private PlaceholderFragment frag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wizard_media);
		setTitle("");

		final CirclePixAppState myApp = (CirclePixAppState)this.getApplication();

        // Show custom actionbar
        BaseActionBar actionBar = new BaseActionBar(WizardMediaActivity.this);
        actionBar.setConfig(WizardMainActivity.class,
                "Save",
                false,
                false,
                new IBaseActionBarCallback.Null() {
                    @Override
                    public void back() {
						myApp.setActivityStopped(true);
                        if (!prepareToNavigate()) {
                            return;
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
	public void onBackPressed() {
		// The user pressed the hardware back button
		if (!prepareToNavigate()) {
			// Stop the navigation
			return;
		}


		((CirclePixAppState)this.getApplication()).setActivityStopped(true);
	    setResult(RESULT_OK);
	    super.onBackPressed();
	}

//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//
//		int id = item.getItemId();
//		if (id == android.R.id.home) {
//			// The user pressed the UP button (on actionbar)
//			if (!prepareToNavigate()) {
//				// Stop the navigation
//				return true;
//			}
//
//		    setResult(RESULT_OK);
//		    finish();
//		}
//
//		return super.onOptionsItemSelected(item);
//	}
	
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
	
	private void saveChanges() throws Exception {
		
		p.setMediaPropertySite(true);
		p.setMediaListingVideo(true);
		p.setMediaQRCodes(frag.qrCodes.isChecked());
		p.setMedia24HourInfo(frag.info24Hour.isChecked());
		p.setMediaShortCode(frag.shortCode.isChecked());
		p.setMediaDvds(frag.dvds.isChecked());
		p.setMediaFlyers(frag.flyers.isChecked());
		PresentationDataSource dao = new PresentationDataSource(this);
		dao.open(true);
		dao.updatePresentation(p);
		dao.close();
	}

	private boolean validateForm() {
		
		if (!frag.propSite.isChecked()) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Media Errors");
			alert.setMessage("Property site must be selected.");
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
		
		public Switch propSite;
		public Switch listingVideo;
		public Switch qrCodes;
		public Switch info24Hour;
		public Switch shortCode;
		public Switch flyers;
		public Switch dvds;

		public ImageView audioPropSite;
		public ImageView audioListingVideo;
		public ImageView audioQrCodes;
		public ImageView audioInfo24Hour;
		public ImageView audioShortCode;
		public ImageView audioFlyers;
		public ImageView audioDvds;
		private MediaPlayer mediaPlayer;
		private View rootView;

		private final int[] F_resID = { R.raw.f_marketingmaterials_propertysite, R.raw.f_marketingmaterials_listingvideo, R.raw.f_marketingmaterials_qr,
				R.raw.f_marketingmaterials_24hour, R.raw.f_marketingmaterials_shortcode, R.raw.f_marketingmaterials_flyers, R.raw.f_marketingmaterials_dvd };

		private final int[] M_resID = { R.raw.m_marketingmaterials_propertysite, R.raw.m_marketingmaterials_listingvideo, R.raw.m_marketingmaterials_qr,
				R.raw.m_marketingmaterials_24hour, R.raw.m_marketingmaterials_shortcode, R.raw.m_marketingmaterials_flyers, R.raw.m_marketingmaterials_dvd };

		private final int[] btnID = { R.id.AudioPropSite, R.id.AudioListingVideo, R.id.AudioQRCodes, R.id.Audio24HourInfo, R.id.AudioShortcode,  R.id.AudioFlyers,
				R.id.AudioDvds};

		private int currentPos;
		private int previousPos = 7; //put dummy value for previousPos just to compare later

		private Presentation p;
		public PlaceholderFragment() {
		}

		public void setContainer(Presentation p) {
			this.p = p;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			rootView = inflater.inflate(R.layout.fragment_wizard_media, container, false);

            propSite = (Switch) rootView.findViewById(R.id.MediaPropSite);
			listingVideo = (Switch) rootView.findViewById(R.id.MediaListingVideo);
			qrCodes = (Switch) rootView.findViewById(R.id.MediaQRCodes);
			info24Hour = (Switch) rootView.findViewById(R.id.Media24HourInfo);
			shortCode = (Switch) rootView.findViewById(R.id.MediaShortcode);
			flyers = (Switch) rootView.findViewById(R.id.MediaFlyers);
			dvds = (Switch) rootView.findViewById(R.id.MediaDvds);

			audioPropSite = (ImageView) rootView.findViewById(R.id.AudioPropSite);
			audioListingVideo = (ImageView) rootView.findViewById(R.id.AudioListingVideo);
			audioQrCodes = (ImageView) rootView.findViewById(R.id.AudioQRCodes);
			audioInfo24Hour = (ImageView) rootView.findViewById(R.id.Audio24HourInfo);
			audioShortCode = (ImageView) rootView.findViewById(R.id.AudioShortcode);
			audioFlyers = (ImageView) rootView.findViewById(R.id.AudioFlyers);
			audioDvds = (ImageView) rootView.findViewById(R.id.AudioDvds);

			mediaPlayer = new MediaPlayer();
			propSite.setAlpha(0.6f);
			listingVideo.setAlpha(0.6f);

			// Set values
			if (p != null) {
				propSite.setChecked(true);
                listingVideo.setChecked(true);
				qrCodes.setChecked(p.isMediaQRCodes());
				info24Hour.setChecked(p.isMedia24HourInfo());
				shortCode.setChecked(p.isMediaShortCode());
                flyers.setChecked(p.isMediaFlyers());
				dvds.setChecked(p.isMediaDvds());


			}

			audioPropSite.setOnClickListener(new View.OnClickListener() {
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

			audioListingVideo.setOnClickListener(new View.OnClickListener() {
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

			audioQrCodes.setOnClickListener(new View.OnClickListener() {
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

			audioInfo24Hour.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					currentPos = 3;
					if(currentPos == previousPos){
						stopAudio(rootView);
					}else{
						playAudio(rootView);
					}
				}
			});

			audioShortCode.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					currentPos = 4;
					if (currentPos == previousPos) {
						stopAudio(rootView);
					} else {
						playAudio(rootView);
					}
				}
			});

			audioFlyers.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					currentPos = 5;
					if (currentPos == previousPos) {
						stopAudio(rootView);
					} else {
						playAudio(rootView);
					}
				}
			});

			audioDvds.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v){
					currentPos = 6;

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

			if(previousPos != 7){ //check if
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
			previousPos = 7; // dummy pos
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

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
import android.widget.Toast;

import com.circlepix.android.data.Presentation;
import com.circlepix.android.data.PresentationDataSource;
import com.circlepix.android.helpers.BaseActionBar;
import com.circlepix.android.interfaces.IBaseActionBarCallback;
import com.circlepix.android.types.NarrationType;

public class WizardExpActivity extends AppCompatActivity {

	private Long presentationId = null;
	private Presentation p;
	private PlaceholderFragment frag;
	private TextView toolBarSave;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wizard_exp);
		setTitle("");

		final CirclePixAppState myApp = (CirclePixAppState)this.getApplication();

        // Show custom actionbar
 /*       BaseActionBar actionBar = new BaseActionBar(WizardExpActivity.this);
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
*/
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setTitle(R.string.title_activity_wizard_exp);

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
	
	private void saveChanges() throws Exception {
		
		p.setExpRealPortals(frag.portals.isChecked());
		p.setExpPersonalSite(frag.personalSite.isChecked());
		p.setExpCompanySite(frag.companySite.isChecked());
		p.setExpBlogger(frag.blogger.isChecked());
		p.setExpYouTube(true);
		p.setExpFacebook(frag.facebook.isChecked());
		p.setExpTwitter(frag.twitter.isChecked());
		p.setExpCraigslist(frag.craigslist.isChecked());
		p.setExpLinkedin(frag.linkedin.isChecked());
		p.setExpPinterest(frag.pinterest.isChecked());
		p.setExpSeoBoost(frag.seoBoost.isChecked());
		PresentationDataSource dao = new PresentationDataSource(this);
		dao.open(true);
		dao.updatePresentation(p);
		dao.close();
	}

	private boolean validateForm() {
		
		if (!frag.youTube.isChecked()) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Exposure Errors");
			alert.setMessage("YouTube option must be selected.");
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

		public RelativeLayout portalsLayout;
		public RelativeLayout personalSiteLayout;
		public RelativeLayout companySiteLayout;
		public RelativeLayout bloggerLayout;
		public RelativeLayout facebookLayout;
		public RelativeLayout twitterLayout;
		public RelativeLayout craigslistLayout;
		public RelativeLayout linkedinLayout;
		public RelativeLayout pinterestLayout;
		public RelativeLayout seoBoostLayout;

		public Switch portals;
		public Switch personalSite;
		public Switch companySite;
		public Switch blogger;
		public Switch youTube;
		public Switch facebook;
		public Switch twitter;
		public Switch craigslist;
		public Switch linkedin;
		public Switch pinterest;
		public Switch seoBoost;
		private Presentation p;

		public ImageView audioPortals;
		public ImageView audioPersonalSite;
		public ImageView audioCompanySite;
		public ImageView audioBlogger;
		public ImageView audioYouTube;
		public ImageView audioFacebook;
		public ImageView audioTwitter;
		public ImageView audioCraigslist;
		public ImageView audioLinkedIn;
		public ImageView audioPinterest;
		public ImageView audioSeoBoost;
		private MediaPlayer mediaPlayer;
		private View rootView;

		private final int[] F_resID = { R.raw.f_exposure_portals, R.raw.f_exposure_personal, R.raw.f_exposure_personalcompany,
				R.raw.f_exposure_blog, R.raw.f_exposure_youtube, R.raw.f_exposure_facebook, R.raw.f_exposure_twitter,
				R.raw.f_exposure_craigslist, R.raw.f_exposure_linkedin, R.raw.f_exposure_pinterest, R.raw.f_exposure_seo };

		private final int[] M_resID = { R.raw.m_exposure_portals, R.raw.m_exposure_personal, R.raw.m_exposure_personalcompany,
				R.raw.m_exposure_blog, R.raw.m_exposure_youtube, R.raw.m_exposure_facebook, R.raw.m_exposure_twitter,
				R.raw.m_exposure_craigslist, R.raw.m_exposure_linkedin, R.raw.m_exposure_pinterest, R.raw.m_exposure_seo };

		private final int[] btnID = { R.id.AudioPortals, R.id.AudioPersonalSite, R.id.AudioCompanySite, R.id.AudioBlogger, R.id.AudioYouTube,  R.id.AudioFacebook,
				R.id.AudioTwitter, R.id.AudioCraigslist, R.id.AudioLinkedIn, R.id.AudioPinterest,  R.id.AudioSeoBoost};

		private int currentPos;
		private int previousPos = 11; //put dummy value for previousPos just to compare later

		public PlaceholderFragment() {
		}

		public void setContainer(Presentation p) {
			this.p = p;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
			rootView = inflater.inflate(R.layout.fragment_wizard_exp,
					container, false);

			portalsLayout = (RelativeLayout) rootView.findViewById(R.id.relativeLayout8);
			personalSiteLayout = (RelativeLayout) rootView.findViewById(R.id.relativeLayout9);
			companySiteLayout = (RelativeLayout) rootView.findViewById(R.id.relativeLayout10);
			facebookLayout = (RelativeLayout) rootView.findViewById(R.id.relativeLayout11);
			twitterLayout = (RelativeLayout) rootView.findViewById(R.id.relativeLayout13);
			bloggerLayout = (RelativeLayout) rootView.findViewById(R.id.relativeLayout14);
			craigslistLayout = (RelativeLayout) rootView.findViewById(R.id.relativeLayout15);
			linkedinLayout = (RelativeLayout) rootView.findViewById(R.id.relativeLayout16);
			pinterestLayout = (RelativeLayout) rootView.findViewById(R.id.relativeLayout17);
			seoBoostLayout = (RelativeLayout) rootView.findViewById(R.id.relativeLayout18);

			// Get controls
			portals = (Switch)rootView.findViewById(R.id.ExpPortals);
			personalSite = (Switch)rootView.findViewById(R.id.ExpPersonalSite);
			companySite = (Switch)rootView.findViewById(R.id.ExpCompanySite);
			blogger = (Switch)rootView.findViewById(R.id.ExpBlogger);
			youTube = (Switch)rootView.findViewById(R.id.ExpYouTube);
			facebook = (Switch)rootView.findViewById(R.id.ExpFacebook);
			twitter = (Switch)rootView.findViewById(R.id.ExpTwitter);
			craigslist = (Switch)rootView.findViewById(R.id.ExpCraigslist);
			linkedin = (Switch)rootView.findViewById(R.id.ExpLinkedIn);
			pinterest = (Switch)rootView.findViewById(R.id.ExpPinterest);
			seoBoost = (Switch)rootView.findViewById(R.id.ExpSeoBoost);

			audioPortals = (ImageView) rootView.findViewById(R.id.AudioPortals);
			audioPersonalSite = (ImageView) rootView.findViewById(R.id.AudioPersonalSite);
			audioCompanySite = (ImageView) rootView.findViewById(R.id.AudioCompanySite);
			audioBlogger = (ImageView) rootView.findViewById(R.id.AudioBlogger);
			audioYouTube = (ImageView) rootView.findViewById(R.id.AudioYouTube);
			audioFacebook = (ImageView) rootView.findViewById(R.id.AudioFacebook);
			audioTwitter = (ImageView) rootView.findViewById(R.id.AudioTwitter);
			audioCraigslist = (ImageView) rootView.findViewById(R.id.AudioCraigslist);
			audioLinkedIn = (ImageView) rootView.findViewById(R.id.AudioLinkedIn);
			audioPinterest = (ImageView) rootView.findViewById(R.id.AudioPinterest);
			audioSeoBoost = (ImageView) rootView.findViewById(R.id.AudioSeoBoost);

			mediaPlayer = new MediaPlayer();

			youTube.setAlpha(0.6f);

			if (p != null) {
				portals.setChecked(p.isExpRealPortals());
				personalSite.setChecked(p.isExpPersonalSite());
				companySite.setChecked(p.isExpCompanySite());
				blogger.setChecked(p.isExpBlogger());
				youTube.setChecked(true);
				facebook.setChecked(p.isExpFacebook());
				twitter.setChecked(p.isExpTwitter());
				craigslist.setChecked(p.isExpCraigslist());
				linkedin.setChecked(p.isExpLinkedin());
				pinterest.setChecked(p.isExpPinterest());
				seoBoost.setChecked(p.isExpSeoBoost());
			}

			portalsLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(portals.isChecked()){
						portals.setChecked(false);
					}else{
						portals.setChecked(true);
					}
				}
			});

			personalSiteLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(personalSite.isChecked()){
						personalSite.setChecked(false);
					}else{
						personalSite.setChecked(true);
					}
				}
			});

			companySiteLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(companySite.isChecked()){
						companySite.setChecked(false);
					}else{
						companySite.setChecked(true);
					}
				}
			});

			bloggerLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(blogger.isChecked()){
						blogger.setChecked(false);
					}else{
						blogger.setChecked(true);
					}
				}
			});

			facebookLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(facebook.isChecked()){
						facebook.setChecked(false);
					}else{
						facebook.setChecked(true);
					}
				}
			});

			twitterLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(twitter.isChecked()){
						twitter.setChecked(false);
					}else{
						twitter.setChecked(true);
					}
				}
			});

			craigslistLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(craigslist.isChecked()){
						craigslist.setChecked(false);
					}else{
						craigslist.setChecked(true);
					}
				}
			});

			linkedinLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(linkedin.isChecked()){
						linkedin.setChecked(false);
					}else{
						linkedin.setChecked(true);
					}
				}
			});

			pinterestLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(pinterest.isChecked()){
						pinterest.setChecked(false);
					}else{
						pinterest.setChecked(true);
					}
				}
			});

			seoBoostLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(seoBoost.isChecked()){
						seoBoost.setChecked(false);
					}else{
						seoBoost.setChecked(true);
					}
				}
			});

			audioPortals.setOnClickListener(new View.OnClickListener() {
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

			audioPersonalSite.setOnClickListener(new View.OnClickListener() {
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

			audioCompanySite.setOnClickListener(new View.OnClickListener() {
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

			audioBlogger.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					currentPos = 3;
					if (currentPos == previousPos) {
						stopAudio(rootView);
					} else {
						playAudio(rootView);
					}
				}
			});

			audioYouTube.setOnClickListener(new View.OnClickListener() {
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

			audioFacebook.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					currentPos = 5;
					if(currentPos == previousPos){
						stopAudio(rootView);
					}else{
						playAudio(rootView);
					}
				}
			});

			audioTwitter.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					currentPos = 6;

					if (currentPos == previousPos) {
						stopAudio(rootView);
					} else {
						playAudio(rootView);
					}

				}

			});

			audioCraigslist.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					currentPos = 7;
					if(currentPos == previousPos){
						stopAudio(rootView);
					}else{
						playAudio(rootView);
					}
				}
			});

			audioLinkedIn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					currentPos = 8;
					if (currentPos == previousPos) {
						stopAudio(rootView);
					} else {
						playAudio(rootView);
					}
				}
			});

			audioPinterest.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					currentPos = 9;
					if(currentPos == previousPos){
						stopAudio(rootView);
					}else{
						playAudio(rootView);
					}
				}
			});

			audioSeoBoost.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					currentPos = 10;

					if (currentPos == previousPos) {
						stopAudio(rootView);
					} else {
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

			if(previousPos != 11){ //check if
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
					ImageView ivCurrentDone = (ImageView)v.findViewById(btnID[index]);
					ivCurrentDone.setImageResource(R.drawable.audio_play_button);
				} });

			previousPos = index;

		}


		public void stopAudio(View v){
			ImageView iv = (ImageView)v.findViewById(btnID[previousPos]);
			iv.setImageResource(R.drawable.audio_play_button);

			// Stop audio
			mediaPlayer.reset();// stops any current playing audio
			previousPos = 11; // dummy pos
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

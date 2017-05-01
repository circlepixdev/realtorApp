package com.circlepix.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.circlepix.android.beans.ApplicationSettings;
import com.circlepix.android.data.Presentation;
import com.circlepix.android.data.PresentationDataSource;
import com.circlepix.android.helpers.BaseActionBar;
import com.circlepix.android.helpers.Globals;
import com.circlepix.android.interfaces.IBaseActionBarCallback;
import com.google.gson.Gson;

public class WizardSettingsActivity extends AppCompatActivity {

    private static final int WIZARD_SETTINGS_NARRATION = 1;
    private static final int WIZARD_SETTINGS_PHOTOGRAPHY = 2;
    private static final int WIZARD_SETTINGS_BGMUSIC = 3;
  //  private static final int WIZARD_SETTINGS_PROPERTY = 4;

	private Long presentationId = null;
	private Presentation p;
	private PlaceholderFragment frag;
    private boolean appSettingsEditMode;

    //KBL 071415
    private boolean appNarrationEditMode;
    private boolean appBGMusicEditMode;
    private boolean appPhotographyEditMode;
    private boolean appPropertyEditMode;
    private boolean clickedSave;
    private TextView toolBarSave;



    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wizard_settings);
		setTitle("");

        // Show custom actionbar
      /*  BaseActionBar actionBar = new BaseActionBar(WizardSettingsActivity.this);
        actionBar.setConfig(WizardMainActivity.class,
                "Save",
                false,
                false,
                new IBaseActionBarCallback.Null() {
                    @Override
                    public void back() {
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
        getSupportActionBar().setTitle(R.string.title_activity_wizard_settings);

        toolBarSave = (TextView) findViewById(R.id.toolbar_save);
        toolBarSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

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
            appSettingsEditMode = extras.getBoolean("appSettingsEditMode");
            appNarrationEditMode = appSettingsEditMode;
            appBGMusicEditMode = appSettingsEditMode;
            appPhotographyEditMode = appSettingsEditMode;
            appPropertyEditMode = appSettingsEditMode;
            Log.v("appSettingsEditMode", String.valueOf(appSettingsEditMode));
            refreshPresentation();
		}

		if (savedInstanceState == null) {
			frag = new PlaceholderFragment();
			frag.setContainer(p);
            frag.setAppSettingsEditMode(appSettingsEditMode);
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
//		if (!prepareToNavigate()) {
//			// Stop the navigation
//			return;
//		}
		
//	    setResult(RESULT_OK);
        checkSave();
//	    super.onBackPressed();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            checkSave();
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }

//
//		int id = item.getItemId();
//		if (id == android.R.id.home) {
//            checkSave();
//			// The user pressed the UP button (on actionbar)
////			if (!prepareToNavigate()) {
////				// Stop the navigation
////				return true;
////			}
//
////		    setResult(RESULT_OK);
////		    finish();
//		}
//
//		return super.onOptionsItemSelected(item);
	}

    @Override
    protected void onStart() {

        frag.narrationSettingsRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                appSettingStat = appNarrationEditMode;
//                appSettingsStr = "appNarrationEditMode";

                appSettingsEditMode = appNarrationEditMode;

                navToChildView(WizardSettingsNarrationActivity.class, WIZARD_SETTINGS_NARRATION );
            }
        });
        frag.photographySettingsRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                appSettingStat = appPhotographyEditMode;
//                appSettingsStr = "appPhotographyEditMode";
                appSettingsEditMode = appPhotographyEditMode;

                navToChildView(WizardSettingsPhotographyActivity.class, WIZARD_SETTINGS_PHOTOGRAPHY );
            }
        });
        frag.bgMusicSettingsRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                appSettingStat = appBGMusicEditMode;
//                appSettingsStr = "appBGMusicEditMode";

                appSettingsEditMode = appBGMusicEditMode;

                navToChildView(WizardSettingsBGMusicActivity.class, WIZARD_SETTINGS_BGMUSIC );
            }
        });

        //temporarily unavail 081515
/*        frag.propertySettingsRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                appSettingStat = appBGMusicEditMode;
//                appSettingsStr = "appBGMusicEditMode";

                appSettingsEditMode = appPropertyEditMode;

                navToChildView(WizardSettingsPropertyActivity.class, WIZARD_SETTINGS_PROPERTY );
            }
        });*/

        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Returned from a child activity. Fetch the presentation object
        if (requestCode == WIZARD_SETTINGS_NARRATION) {
            if (resultCode == RESULT_OK || resultCode == RESULT_CANCELED) {
                // re-fetch the presentation
                PresentationDataSource dao = new PresentationDataSource(this);
                dao.open(false);
                p = dao.fetch(presentationId);
                dao.close();
                appNarrationEditMode = true;
                Log.v("appNarrationEditMode onresult", String.valueOf(appNarrationEditMode));
                return;
            }
        }else if (requestCode == WIZARD_SETTINGS_PHOTOGRAPHY) {
            if (resultCode == RESULT_OK || resultCode == RESULT_CANCELED) {
                // re-fetch the presentation
                PresentationDataSource dao = new PresentationDataSource(this);
                dao.open(false);
                p = dao.fetch(presentationId);
                dao.close();
                appPhotographyEditMode = true;
                Log.v("appPhotographyEditMode onresult", String.valueOf(appPhotographyEditMode));
                return;
            }
        }else if (requestCode == WIZARD_SETTINGS_BGMUSIC) {
            if (resultCode == RESULT_OK || resultCode == RESULT_CANCELED) {
                // re-fetch the presentation
                PresentationDataSource dao = new PresentationDataSource(this);
                dao.open(false);
                p = dao.fetch(presentationId);
                dao.close();
                appBGMusicEditMode = true;
                Log.v("appBGMusicEditMode onresult", String.valueOf(appBGMusicEditMode));
                return;
            }
        }

        //temporarily unavail
        /*else if (requestCode == WIZARD_SETTINGS_PROPERTY) {
            if (resultCode == RESULT_OK || resultCode == RESULT_CANCELED) {
                // re-fetch the presentation
                PresentationDataSource dao = new PresentationDataSource(this);
                dao.open(false);
                p = dao.fetch(presentationId);
                dao.close();
                appPropertyEditMode = true;
                Log.v("appBGMusicEditMode onresult", String.valueOf(appPropertyEditMode));
                return;
            }
        }*/
        super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressLint("NewApi")
    private void navToChildView(Class cls, int id) {
        //KBL 071415
        //to set which edit mode it came from :narration, photography, background music


        // Nav to next wizard page
        Bundle bundle = ActivityOptions.makeCustomAnimation(WizardSettingsActivity.this,
                R.anim.slide_in_left,
                R.anim.slide_out_left).toBundle();
        bundle.putLong("presentationId", presentationId);
//        bundle.putBoolean(appSettingsStr, appSettingStat); // appSettingsEditMode
        bundle.putBoolean("appSettingsEditMode", appSettingsEditMode);

        Intent wizardNextActivity = new Intent(WizardSettingsActivity.this, cls);
        wizardNextActivity.putExtras(bundle);
        startActivityForResult(wizardNextActivity, id, bundle);

        Log.v("appSettingStat to be sent", String.valueOf(appSettingsEditMode));
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

    private void checkSave(){


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(WizardSettingsActivity.this);
        alertDialogBuilder.setTitle("Save Settings");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder
                .setMessage("Do you want to save this presentation settings?")
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                dialog.dismiss();
                                try {
                                    clickedSave = true;
                                    saveChanges();

                                } catch (Exception e) {
                                    AlertDialog.Builder alert = new AlertDialog.Builder(WizardSettingsActivity.this);
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
//                                    finish();
//                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {

                                dialog.cancel();

                            }
                        })
                .setNeutralButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                dialog.cancel();
                                finish();
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }


	private void saveChanges() throws Exception {
		
//		BackgroundMusicType bgMusic = (BackgroundMusicType) frag.bgMusicSpin.getSelectedItem();
//		NarrationType narration = (NarrationType) frag.narrationSpin.getSelectedItem();
//		ThemeType theme = (ThemeType) frag.themeSpin.getSelectedItem();
		
//		p.setBackgroundMusic(bgMusic);
//		p.setNarration(narration);
//		p.setTheme(theme);
        refreshPresentation();

		p.setDisplayCompanyLogo(frag.displayCompanyLogo.isChecked());
		p.setDisplayCompanyName(frag.displayCompanyName.isChecked());
		p.setDisplayAgentImage(frag.displayAgentImage.isChecked());
		p.setDisplayAgentName(frag.displayAgentName.isChecked());
		
		PresentationDataSource dao = new PresentationDataSource(this);
		dao.open(true);
		dao.updatePresentation(p);
		dao.close();

        if(clickedSave == true){ //checked applyToExisting
            clickedSave = false;

            setResult(RESULT_OK);
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        }
      //  PresentationsActivity.isNewPres = false;
	}
	
	private boolean validateForm() {
		
		// TODO: Add validation
//		if (!frag.stats.isChecked()) {
//			AlertDialog.Builder alert = new AlertDialog.Builder(this);
//			alert.setTitle("Communications Errors");
//			alert.setMessage("The Stats option must be selected.");
//			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int whichButton) {
//					// Do nothing
//				}
//			});
//			alert.show();
//
//			return false;
//		}
		
		return true;
	}

    private void refreshPresentation() {
        PresentationDataSource dao = new PresentationDataSource(this);
        dao.open(false);
        p = dao.fetch(presentationId);
        dao.close();
    }

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		private Presentation p;
        private boolean appSettingsEditMode;
        private RelativeLayout displayCompanyLogoLayout;
        private RelativeLayout displayCompanyNameLayout;
        private RelativeLayout displayAgentImageLayout;
        private RelativeLayout displayAgentNameLayout;
		private Switch displayCompanyLogo;
		private Switch displayCompanyName;
		private Switch displayAgentImage;
		private Switch displayAgentName;
        private LinearLayout narrationSettingsRow;
        private LinearLayout photographySettingsRow;
        private LinearLayout bgMusicSettingsRow;
    //    private LinearLayout propertySettingsRow;

		public PlaceholderFragment() {
		}

		public void setContainer(Presentation p) {
			this.p = p;
		}

        public void setAppSettingsEditMode(boolean appSettingsEditMode) {
            this.appSettingsEditMode = appSettingsEditMode;
        }

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_wizard_settings,
					container, false);

            displayCompanyLogoLayout = (RelativeLayout) rootView.findViewById(R.id.relativeLayout22);
            displayCompanyNameLayout = (RelativeLayout) rootView.findViewById(R.id.relativeLayout23);
            displayAgentImageLayout = (RelativeLayout) rootView.findViewById(R.id.relativeLayout24);
            displayAgentNameLayout = (RelativeLayout) rootView.findViewById(R.id.relativeLayout25);

            // More controls
			displayCompanyLogo = (Switch) rootView.findViewById(R.id.cbCompanyLogo);
			displayCompanyName = (Switch) rootView.findViewById(R.id.cbCompanyName);
			displayAgentImage = (Switch) rootView.findViewById(R.id.cbAgentImage);
			displayAgentName = (Switch) rootView.findViewById(R.id.cbAgentName);
            narrationSettingsRow = (LinearLayout) rootView.findViewById(R.id.narrationSettingsRow);
            photographySettingsRow = (LinearLayout) rootView.findViewById(R.id.photographySettingsRow);
            bgMusicSettingsRow = (LinearLayout) rootView.findViewById(R.id.bgMusicSettingsRow);
      //      propertySettingsRow = (LinearLayout) rootView.findViewById(R.id.propertySettingsRow);

            if (p != null) {
                if (appSettingsEditMode) {
                    // Edit mode
                    displayCompanyLogo.setChecked(p.isDisplayCompanyLogo());
                    displayCompanyName.setChecked(p.isDisplayCompanyName());
                    displayAgentImage.setChecked(p.isDisplayAgentImage());
                    displayAgentName.setChecked(p.isDisplayAgentName());
                } else {
                    // Load default global settings for new presentation
                    SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(Globals.PREFS_APP_SETTINGS, 0);
                    Gson gson = new Gson();
                    String fromJson = sharedPreferences.getString(Globals.PREFS_APP_SETTINGS, "");
                    ApplicationSettings appSettings = gson.fromJson(fromJson, ApplicationSettings.class);
                    displayCompanyLogo.setChecked(appSettings.isDisplayCompanyLogo());
                    displayCompanyName.setChecked(appSettings.isDisplayCompanyName());
                    displayAgentImage.setChecked(appSettings.isDisplayAgentImage());
                    displayAgentName.setChecked(appSettings.isDisplayAgentName());
                }
			}


            displayCompanyLogoLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(displayCompanyLogo.isChecked()){
                        displayCompanyLogo.setChecked(false);
                    }else{
                        displayCompanyLogo.setChecked(true);
                    }
                }
            });

            displayCompanyNameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(displayCompanyName.isChecked()){
                        displayCompanyName.setChecked(false);
                    }else{
                        displayCompanyName.setChecked(true);
                    }
                }
            });

            displayAgentImageLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(displayAgentImage.isChecked()){
                        displayAgentImage.setChecked(false);
                    }else{
                        displayAgentImage.setChecked(true);
                    }
                }
            });

            displayAgentNameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(displayAgentName.isChecked()){
                        displayAgentName.setChecked(false);
                    }else{
                        displayAgentName.setChecked(true);
                    }
                }
            });

			return rootView;
		}
	}

}

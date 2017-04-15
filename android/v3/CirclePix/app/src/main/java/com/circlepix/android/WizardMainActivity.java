package com.circlepix.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;

import com.circlepix.android.beans.AgentData;
import com.circlepix.android.beans.ApplicationSettings;
import com.circlepix.android.data.Presentation;
import com.circlepix.android.data.PresentationDataSource;
import com.circlepix.android.helpers.BaseActionBar;
import com.circlepix.android.helpers.CountingMultipartEntity;
import com.circlepix.android.helpers.Globals;
import com.circlepix.android.interfaces.IBaseActionBarCallback;
import com.circlepix.android.sync.commands.UpdatePresentation;
import com.circlepix.android.types.BackgroundMusicType;
import com.circlepix.android.types.NarrationType;
import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;


public class WizardMainActivity extends Activity {
	
	private static final int WIZARD_MEDIA_VIEW = 1;
	private static final int WIZARD_EXP_VIEW = 2;
	private static final int WIZARD_COMM_VIEW = 3;
	private static final int WIZARD_SETTINGS_VIEW = 4;
	private static final int WIZARD_STAR_SETTINGS_VIEW = 5;

	private Long presentationId = null;
	private Presentation p;
	private PlaceholderFragment frag;
	private boolean editMode = false;
    private boolean appSettingsEditMode;

	//test for Updating presentation
	protected UploadPresentationsTask updatePresentationTask;
	private boolean cancelled = false;
	long totalSize;
	private ProgressDialog progressBar;

	private boolean cameFromSetting;

	private CirclePixAppState appState;

	private  ArrayList<String> newPresentationIds = new ArrayList<String>();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wizard_main);
		setTitle("");

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		// Setup application class
		appState = ((CirclePixAppState)getApplicationContext());
		appState.setContextForPreferences(this);

        // Show custom actionbar
        BaseActionBar actionBar = new BaseActionBar(WizardMainActivity.this);
        actionBar.setConfig(PresentationsActivity.class,
                            "Done",
                            false,
                            false,
                            new IBaseActionBarCallback.Null() {
                                @Override
                                public void back() {
									savePresentation();
                                }
                            });
        actionBar.show();

        Bundle extras = getIntent().getExtras();
		if (extras != null) {
			presentationId = extras.getLong("presentationId");
			Log.v("PresId from WizMain", String.valueOf(presentationId));
			editMode = true;
            appSettingsEditMode = true;
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

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.wizard_main, menu);
//		return super.onCreateOptionsMenu(menu);
//	}

	@Override
	protected void onStart() {

        frag.mediaRow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                navToChildView(WizardMediaActivity.class, WIZARD_MEDIA_VIEW);
            }
        });
        frag.expRow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                navToChildView(WizardExpActivity.class, WIZARD_EXP_VIEW);
            }
        });
        frag.commRow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                navToChildView(WizardCommActivity.class, WIZARD_COMM_VIEW);
            }
        });
        frag.settingsRow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
				navToChildView(WizardSettingsActivity.class, WIZARD_SETTINGS_VIEW);
            }
        });

		super.onStart();
	}

	@Override
	public void onBackPressed() {
		savePresentation();
		Intent intent = new Intent(WizardMainActivity.this, PresentationsActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
		super.onBackPressed();
	}

    @SuppressLint("NewApi")
    private void navToChildView(Class cls, int id) {
		// Save the presentation data
		savePresentation();
		
		// Nav to next wizard page
        Bundle bundle = ActivityOptions.makeCustomAnimation(WizardMainActivity.this,
                R.anim.slide_in_left,
                R.anim.slide_out_left).toBundle();
        bundle.putLong("presentationId", presentationId);
        bundle.putBoolean("appSettingsEditMode", appSettingsEditMode);

		Intent wizardNextActivity = new Intent(WizardMainActivity.this, cls);
        wizardNextActivity.putExtras(bundle);
        startActivityForResult(wizardNextActivity, id, bundle);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Returned from a child activity. Fetch the presentation object
	    if (requestCode == WIZARD_MEDIA_VIEW || requestCode == WIZARD_EXP_VIEW ||
	    		requestCode == WIZARD_STAR_SETTINGS_VIEW || requestCode == WIZARD_COMM_VIEW) {
	        if (resultCode == RESULT_OK || resultCode == RESULT_CANCELED) {
	    		// re-fetch the presentation
				PresentationDataSource dao = new PresentationDataSource(this);
				dao.open(false);
	    		p = dao.fetch(presentationId);
	    		dao.close();
	    		frag.nameText.setText(p.getName());
	    		return;
	        }
	    } else if (requestCode == WIZARD_SETTINGS_VIEW) {
            if (resultCode == RESULT_OK || resultCode == RESULT_CANCELED) {
                // re-fetch the presentation
                PresentationDataSource dao = new PresentationDataSource(this);
                dao.open(false);
                p = dao.fetch(presentationId);
                dao.close();
                frag.nameText.setText(p.getName());
                appSettingsEditMode = true;
				cameFromSetting = true;
                return;
            }
        }

		super.onActivityResult(requestCode, resultCode, data);
	}

//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//
//		int id = item.getItemId();
//		if (id == android.R.id.home) {
//			savePresentation();
//		}
//		return super.onOptionsItemSelected(item);
//	}

	private void savePresentation() {
		
		if (!editMode) {
			p = new Presentation();
			// Get form data
			if (!frag.nameText.getText().toString().isEmpty()) {
				p.setName(frag.nameText.getText().toString());
			}


			//added by KBL 081315: if user did not clicked settings, get values from global settings

			if(!cameFromSetting){
				NarrationType narration = null;
				BackgroundMusicType bgMusic = null;

				SharedPreferences sharedPreferences = WizardMainActivity.this.getSharedPreferences(Globals.PREFS_APP_SETTINGS, 0);
				Gson gson = new Gson();
				String fromJson = sharedPreferences.getString(Globals.PREFS_APP_SETTINGS, "");
				ApplicationSettings appSettings = gson.fromJson(fromJson, ApplicationSettings.class);

				p.setDisplayCompanyLogo(appSettings.isDisplayCompanyLogo());
				p.setDisplayCompanyName(appSettings.isDisplayCompanyName());
				p.setDisplayAgentImage(appSettings.isDisplayAgentImage());
				p.setDisplayAgentName(appSettings.isDisplayAgentName());

				narration = appSettings.getNarration();
				if (narration == NarrationType.male) {
					p.setNarration(NarrationType.male);
				} else if (narration == NarrationType.female) {
					p.setNarration(NarrationType.female);
				}

				bgMusic = appSettings.getMusic();
				if (bgMusic == BackgroundMusicType.none) {
					p.setMusic(BackgroundMusicType.none);
				}else if (bgMusic == BackgroundMusicType.song1) {
					p.setMusic(BackgroundMusicType.song1);
				} else if (bgMusic == BackgroundMusicType.song2) {
					p.setMusic(BackgroundMusicType.song2);
				} else if (bgMusic == BackgroundMusicType.song3) {
					p.setMusic(BackgroundMusicType.song3);
				}

				cameFromSetting = false;
			}

		//	p.setCreated(new Date());

		} else {
			if (!frag.nameText.getText().toString().isEmpty()) {
				p.setName(frag.nameText.getText().toString());
			}

		}


	//	p.setModified(new Date());
		
		// Save the new data
		PresentationDataSource dao = null;
		try {
			dao = new PresentationDataSource(this);
			dao.open(true);

			Log.v("editMode", String.valueOf(editMode));
			if (!editMode) {
//				p.setAction("create"); //added by KBL 081515

				p = dao.createPresentation(p);
				p.setCreated(new Date());
				p.setModified(new Date());
				presentationId = p.getId();
				editMode = true;

				Log.v("created date", p.getCreated().toString());

			//	int size = PresentationsActivity.newlyAddedPresentations.size();
			//	PresentationsActivity.newlyAddedPresentations.add(size, p.getGuid());

			//	int size = appState.getNewPresIds().size();
				ArrayList<String> presId = new ArrayList<String>();
				presId.add(p.getGuid());

				appState.addNewPresIds(presId);
				Log.v("newPres size", String.valueOf(appState.getNewPresIds().size()));


			} else {
//				p.setAction("update"); //added by KBL 081515
				p.setModified(new Date());
				dao.updatePresentation(p);
			}


			// Sync with server
//			UpdatePresentation.runCommand(AgentData.getInstance().getRealtor().getId(), dao, p, null);
			UpdatePresentation.runCommand(AgentData.getInstance().getRealtor().getId(), p, null);
			Log.v("sync with server", "from WizardMainActivity");
		} catch (Exception e) {
			e.printStackTrace();
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Presentation Data Error");
			alert.setMessage("Could not save data. Critical database error" + e.toString());
			alert.show();
		} finally {
			if (dao != null) {
				dao.close();
			}
		}

	//	updatePresentationTask = new UploadPresentationsTask();
	//	updatePresentationTask.execute(WizardMainActivity.this);

	/*	Realtor realtor = new Realtor();

		final String realtorIdStr = AgentData.getInstance().getRealtor().getId(); //realtor.getId();
		final PresentationDataSource ds = new PresentationDataSource(this);
	//	final boolean firstRun = (extras != null) ? extras.getBoolean("isFirstRun") : false;
		Thread syncThread = new Thread() {
			public void run() {
				SyncPresentations.runCommand(realtorIdStr, false, ds, null);
			}
		};
		syncThread.start();
		Log.v("update the server", "ok");*/

	}

	private boolean validateForm() {

		StringBuffer sb = new StringBuffer();
		String name = ((EditText) findViewById(R.id.presentationName)).getText().toString();
		
		if (name == null || name.isEmpty()) {
			sb.append("The Name field cannot be blank.\n");
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Presentation Errors");
			alert.setMessage(sb);
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

		private Presentation p;
		public EditText nameText;
        public TextView headerTitle;
        public TableRow mediaRow;
        public TableRow expRow;
        public TableRow commRow;
        public TableRow settingsRow;

		public PlaceholderFragment() {
		}

		public void setContainer(Presentation p) {
			this.p = p;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_wizard_main, container, false);
			
			nameText = (EditText) rootView.findViewById(R.id.presentationName);
			nameText.getBackground().setColorFilter(getResources().getColor(R.color.circlepix_darkgrey), PorterDuff.Mode.SRC_ATOP);
            headerTitle = (TextView) rootView.findViewById(R.id.header_title);
            mediaRow = (TableRow) rootView.findViewById(R.id.mediaRow);
            expRow = (TableRow) rootView.findViewById(R.id.expRow);
            commRow = (TableRow) rootView.findViewById(R.id.commRow);
            settingsRow = (TableRow) rootView.findViewById(R.id.settingsRow);

            if (p != null) {
				nameText.setText(p.getName());
                headerTitle.setText("Edit Listing Presentation");
			} else {
                headerTitle.setText("New Listing Presentation");
            }
			
			return rootView;
		}
	}




	protected class UploadPresentationsTask extends AsyncTask<Context, Integer, String> {

		int progressInt = 0;

		// -- notice that the datatype of the first param in the class definition matches the param passed to this method
		// -- and that the datatype of the last param in the class definition matches the return type of this mehtod
		@Override
		protected String doInBackground(Context... params) {

			String responseString = "";

			String json = "{\\\"expSeoBoost\\\":\\\"true\\\",\n" +
					"\t\t\\\"displayAgentName\\\":\\\"true\\\",\\\"displayPropAddress\\\":\\\"false\\\",\\\"mediaQRCodes\\\":\\\"true\\\",\\\"mediaShortCode\\\":\\\"true\\\",\\\"companyLogo\\\":\\\"\\\",\\\"commEmail\\\":\\\"true\\\",\\\"leadOpenHouseAnnce\\\":\\\"true\\\",\\\"autoplay\\\":\\\"true\\\",\\\"leadFacebook\\\":\\\"true\\\",\\\"mediaMobile\\\":\\\"true\\\",\\\"expBlogger\\\":\\\"true\\\",\\\"media24HourInfo\\\":\\\"true\\\",\\\"displayAgentImage\\\":\\\"true\\\",\\\"displayPropImage\\\":\\\"false\\\",\\\"theme\\\":\\\"CirclePix\\\",\\\"expTwitter\\\":\\\"true\\\",\\\"expPinterest\\\":\\\"true\\\",\\\"displayCompanyName\\\":\\\"true\\\",\\\"commBatchText\\\":\\\"true\\\",\\\"expRealPortals\\\":\\\"true\\\",\\\"expPersonalSite\\\":\\\"true\\\",\\\"companyName\\\":\\\"\\\",\\\"description\\\":\\\"\\\",\\\"expCompanySite\\\":\\\"true\\\",\\\"expFacebook\\\":\\\"true\\\",\\\"mediaListingVideo\\\":\\\"true\\\",\\\"name\\\":\\\"Bear Lake Commercial Property \\\",\\\"music\\\":\\\"None\\\",\\\"narration\\\":\\\"Male\\\",\\\"photographyType\\\":\\\"Agent\\\",\\\"leadMobile\\\":\\\"true\\\",\\\"expCraigslist\\\":\\\"true\\\",\\\"mediaPropertySite\\\":\\\"true\\\",\\\"mediaFlyers\\\":\\\"true\\\",\\\"propertyAddress\\\":\\\"\\\",\\\"mediaDvds\\\":\\\"true\\\",\\\"leadLeadBee\\\":\\\"true\\\",\\\"expYouTube\\\":\\\"true\\\",\\\"leadPropertySite\\\":\\\"true\\\",\\\"propertyImage\\\":\\\"\\\",\\\"expLinkedin\\\":\\\"true\\\",\\\"agentPhoneNum\\\":\\\"\\\",\\\"guid\\\":\\\"C688AC21-4EA6-49F5-8C94-5ACA6FC0D5C2\\\",\\\"displayCompanyLogo\\\":\\\"true\\\",\\\"commStats\\\":\\\"true\\\",\\\"listingType\\\":\\\"Residential\\\",\\\"agentPhoto\\\":\\\"\\\"}";

			while(!cancelled) {

				try {



					Log.v("GUID",String.valueOf(p.getId()));
					Log.v("realtorId",AgentData.getInstance().getRealtor().getId());
					Log.v("name",p.getName());
					Log.v("jsonString",json);

					String baseUrl = "https://www.circlepix.com/api/updatePresentation.php";
					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(baseUrl);

					StringBody GUID = new StringBody(String.valueOf(p.getId()));
					StringBody realtorId = new StringBody(AgentData.getInstance().getRealtor().getId());
					StringBody name = new StringBody(p.getName());
					StringBody jsonString = new StringBody(json);
					StringBody updated = new StringBody("updated=2015-07-01%2015:02:53");


					CountingMultipartEntity reqEntity = new CountingMultipartEntity(new CountingMultipartEntity.ProgressListener()
					{
						@Override
						public void transferred(long num)
						{
							publishProgress((int) ((num / (float) totalSize) * 100));
							progressInt = (int) ((num / (float) totalSize) * 100);
						}
					});

					/*// Add multiple images
					for (String imageFilePath : imageUrls) {

						FileBody filebody = new FileBody(new File(imageFilePath), "image/jpeg");
						reqEntity.addPart("mediaFile[]", filebody);
					}


					StringBody createNewSlideShow = new StringBody("1");
*/


						reqEntity.addPart("GUID", GUID);
						reqEntity.addPart("realtorId", realtorId);
						reqEntity.addPart("name", name);
						reqEntity.addPart("json", jsonString);
						reqEntity.addPart("updated", updated);
						httppost.setEntity(reqEntity);



					// File size
					totalSize = reqEntity.getContentLength();
					Log.d("LOGCAT", "Total upload size: " + totalSize);

					HttpResponse response = httpclient.execute( httppost );
					HttpEntity resEntity = response.getEntity( );

					Log.d("LOGCAT", "Response status: " + response.getStatusLine());

					if (resEntity != null) {
						responseString = "" + EntityUtils.toString(resEntity);
						Log.d("LOGCAT", "Response string: " + responseString);
					}

					if (resEntity != null) {
						resEntity.consumeContent( );
					}

					httpclient.getConnectionManager( ).shutdown( );

				} catch (ParseException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				finally {
					// Guaranteed progressbar is dismissed
					progressBar.dismiss();
				}

				//-- runs a while loop that causes the thread to sleep for 50 milliseconds
				//-- and increments the counter variable i by one
				//-- attempting to wait for the progress to complete
				while(progressInt < 100)
				{
					Log.d("LOGCAT", "progress is " + progressInt);

					if (cancelled)  break;

					try{
						Thread.sleep( 50 );
					} catch( Exception e ){
						Log.i("makemachine", e.getMessage());
					}
				}
				// Done, return responseString to onPostExecute
				return responseString;
			}
			return "cancelled";
		}

		// -- gets called just before thread begins
		@Override
		protected void onPreExecute() {

			cancelled = false;

			progressBar = new ProgressDialog(WizardMainActivity.this);
			progressBar.setMessage("Updating Presentation...");
			progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressBar.setProgress(0);
			progressBar.setMax(100);
			progressBar.setCancelable(false);
			progressBar.setOnCancelListener(new DialogInterface.OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {

					cancel(true);
				}
			});

			progressBar.show();

			Log.i( "makemachine", "onPreExecute()" );
			super.onPreExecute();
		}

		// -- called from the publish progress
		// -- notice that the datatype of the second param gets passed to this method
		@Override
		protected void onProgressUpdate(Integer... values)  {

			progressBar.setProgress((int) (values[0]));

			super.onProgressUpdate(values);
			Log.i( "makemachine", "onProgressUpdate(): " +  String.valueOf( values[0] ) );

		}

		// -- called if the cancel button is pressed
		@Override
		protected void onCancelled() {

			super.onCancelled();
			updatePresentationTask.cancel(true);
			finish();
			cancelled = true;
			Log.i( "makemachine", "onCancelled()" );
		}

		// -- notice that the third param gets passed to this method
		@Override
		protected void onPostExecute( String result ) {

			super.onPostExecute(result);
			progressBar.dismiss();

			Log.i( "makemachine", "onPostExecute(): " + result );

			if (result != null && !result.isEmpty()) {

				Log.d("LOGCAT", "media upload result: " + result);

				try {
					JSONObject rootObject = (JSONObject) new JSONTokener(result).nextValue();
					String status = rootObject.getString("status");
					String message = rootObject.getString("message");

					if (status.equals("1")) {


						// Alert user and finish activity
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(WizardMainActivity.this);


						alertDialogBuilder.setTitle("Update Successful");
						// Set message
						alertDialogBuilder
						.setMessage("Hurray")
						.setNegativeButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								// Close dialog and finish activity
								dialog.cancel();
								setResult(100);	// return a request code 100 to tell the previous activity to finish
								finish();
							}
						});

						// create alert dialog
						AlertDialog alertDialog = alertDialogBuilder.create();

						// show it
						alertDialog.show();

					}
					else {
						showAlertDialog("Media Upload Failed", message);
					}

				} catch (JSONException e) {
					e.printStackTrace();
					showAlertDialog("Media Upload Failed", "Server error.");
				}
			}
			else {
				showAlertDialog("Media Upload Failed", "Unable to establish connection with server.");
			}
		}
	}

	public void showAlertDialog(String title, String message) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(WizardMainActivity.this);

		// set title
		alertDialogBuilder.setTitle(title);

		// set dialog message
		alertDialogBuilder
				.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}
}

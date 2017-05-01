package com.circlepix.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.circlepix.android.data.Presentation;
import com.circlepix.android.data.PresentationDataSource;

public class WizardLeadsActivity extends Activity {

	private Long presentationId = null;
	private Presentation p;
	private PlaceholderFragment frag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wizard_leads);
		setTitle("");

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
	public void onBackPressed() {
		// The user pressed the hardware back button
		if (!prepareToNavigate()) {
			// Stop the navigation
			return;
		}
		
	    setResult(RESULT_OK);
	    super.onBackPressed();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		if (id == android.R.id.home) {
			// The user pressed the UP button (on actionbar)
			if (!prepareToNavigate()) {
				// Stop the navigation
				return true;
			}
			
		    setResult(RESULT_OK);
		    finish();
		}

		return super.onOptionsItemSelected(item);
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

		p.setLeadPropertySite(frag.propSite.isChecked());
		p.setLeadLeadBee(frag.leadbee.isChecked());
		p.setLeadFacebook(frag.facebook.isChecked());
		p.setLeadMobile(frag.mobile.isChecked());
		p.setLeadOpenHouseAnnce(frag.openHouse.isChecked());
		PresentationDataSource dao = new PresentationDataSource(this);
		dao.open(true);
		dao.updatePresentation(p);
		dao.close();
	}

	private boolean validateForm() {
		
		if (!frag.propSite.isChecked()) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Leads Errors");
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

		public CheckBox propSite;
		public CheckBox leadbee;
		public CheckBox facebook;
		public CheckBox mobile;
		public CheckBox openHouse;
		private Presentation p;

		public PlaceholderFragment() {
		}

		public void setContainer(Presentation p) {
			this.p = p;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_wizard_leads,
					container, false);
			
			// Get controls
			propSite = (CheckBox) rootView.findViewById(R.id.LeadPropSite);
			leadbee = (CheckBox) rootView.findViewById(R.id.LeadLeadbee);
			facebook = (CheckBox) rootView.findViewById(R.id.LeadFacebook);
			mobile = (CheckBox) rootView.findViewById(R.id.LeadMobile);
			openHouse = (CheckBox) rootView.findViewById(R.id.LeadOpenHousee);
			
			if (p != null) {
				propSite.setChecked(p.isLeadPropertySite());
				leadbee.setChecked(p.isLeadLeadBee());
				facebook.setChecked(p.isLeadFacebook());
				mobile.setChecked(p.isLeadMobile());
				openHouse.setChecked(p.isLeadOpenHouseAnnce());
			}

			return rootView;
		}
	}

}

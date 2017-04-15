package com.circlepix.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.circlepix.android.data.Presentation;
import com.circlepix.android.data.PresentationDataSource;
import com.circlepix.android.helpers.ImageHelper;

public class WizardPropertyActivity extends Activity {

	private static int RESULT_LOAD_PROP_IMAGE = 1;
	private Long presentationId = null;
	private Presentation p;
	private PlaceholderFragment frag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wizard_property);
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
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.wizard_property, menu);
		return true;
	}

	@Override
	protected void onStart() {
		
		frag.propertyImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Launch to pick image
				Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, RESULT_LOAD_PROP_IMAGE);
			}
		});
		
		super.onStart();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == RESULT_LOAD_PROP_IMAGE)
        		&& resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
        	p.setPropertyImage(picturePath);
            frag.propertyImage.setImageBitmap(ImageHelper.getScaledBitmap(picturePath, 200, 200));
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
		
	//	p.setDisplayPropAddress(frag.displayPropAddress.isChecked());
	//	if (frag.displayPropAddress.isChecked()) {
	//		p.setPropertyAddress(frag.propertyAddress.getText().toString());
	//	} else {
			p.setPropertyAddress(null);
	//	}
	//	p.setDisplayPropertyImage(frag.displayPropImage.isChecked());
		if (!frag.displayPropImage.isChecked()) {
			p.setPropertyImage(null);
		}
		
		PresentationDataSource dao = new PresentationDataSource(this);
		dao.open(true);
		dao.updatePresentation(p);
		dao.close();
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		private Presentation p;
//		private CheckBox displayPropAddress;
		private CheckBox displayPropImage;
//		private EditText propertyAddress;
		private ImageView propertyImage;

		public PlaceholderFragment() {
		}

		public void setContainer(Presentation p) {
			this.p = p;
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_wizard_property,
					container, false);
			
	//		displayPropAddress = (CheckBox) rootView.findViewById(R.id.cbPropertyAddress);
	//		displayPropImage = (CheckBox) rootView.findViewById(R.id.cbPropertyImage);
	//		propertyAddress = (EditText) rootView.findViewById(R.id.propertyAddress);
			propertyImage = (ImageView) rootView.findViewById(R.id.propertyImage);

			if (p != null) {
			//	displayPropAddress.setChecked(p.isDisplayPropAddress());
				displayPropImage.setChecked(p.isDisplayPropAddress());
			//	propertyAddress.setText(p.getPropertyAddress());
				if (p.getPropertyImage() != null && !p.getPropertyImage().isEmpty()) {
					// Load the property image
	            	propertyImage.setImageBitmap(ImageHelper.getScaledBitmap(p.getPropertyImage(), 200, 200));
				}
			}
			
			return rootView;
		}
	}

}

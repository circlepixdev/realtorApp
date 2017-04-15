package com.circlepix.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.circlepix.android.beans.AgentData;
import com.circlepix.android.helpers.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by keuahnlumanog on 02/12/2016.
 */

public class EditListingActivity extends AppCompatActivity {

    private AgentData agentData;

    private Spinner spinnerState, spinnerPropertyType, spinnerListingType;
    private String[]stateNames;
    private String[] splitStateNames;
    private List<String> splittedStateName;

    private EditText editAddress, editZipCode, editCity, editCountry, editPrice, editSqFt, editBedrooms;
    private EditText editFullBaths, editThreeQtBaths, editHalfBaths, editQuarterBath;
    private EditText editMlsNum, editAltMlsNum;
    private EditText editListingDesc, editListingDesc2, editListingDesc3, editComments;

    private LinearLayout addCommentContainer, addCommentLayout;
    private ImageView addComment;
    private CheckBox chkSocMediaSites, chkRealEstateSites;
    private Button btnCancel, btnSave;
    private LinearLayout addLayout;

    private String stateCode, propertyType, listingType;
    private String address, zipcode, city, county, price, sqft, bedrooms, fullbaths,
            threeQtbaths, halfbaths, quarterbaths, mlsnum, altmlsnum, socMediaSites, realEstateSites, listingDesc, listingDesc2, listingDesc3, additionalComments;

    private List<String> listingDescriptions = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_api_test);
        setContentView(R.layout.activity_edit_listing);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get global shared data
        agentData = AgentData.getInstance();

        editAddress = (EditText) findViewById(R.id.editText_address);
        editZipCode = (EditText) findViewById(R.id.editText_zip_code);
        editCity = (EditText) findViewById(R.id.editText_city);
        editCountry = (EditText) findViewById(R.id.editText_county);
        editPrice = (EditText) findViewById(R.id.editText_price);
        editSqFt = (EditText) findViewById(R.id.editText_sq_footage);
        editBedrooms = (EditText) findViewById(R.id.editText_bedrooms);
        editFullBaths = (EditText) findViewById(R.id.editText_full_bathrooms);
        editThreeQtBaths = (EditText) findViewById(R.id.editText_three_quarters_bathrooms);
        editHalfBaths = (EditText) findViewById(R.id.editText_half_bathrooms);
        editQuarterBath = (EditText) findViewById(R.id.editText_quarter_bathrooms);
        editMlsNum = (EditText) findViewById(R.id.editText_mls_num);
        editAltMlsNum = (EditText) findViewById(R.id.editText_alt_mls_num);
        editListingDesc = (EditText) findViewById(R.id.editText_listing_desc);
        editListingDesc2 = (EditText) findViewById(R.id.editText_listing_desc2);
        editListingDesc3 = (EditText) findViewById(R.id.editText_listing_desc3);
        editComments = (EditText) findViewById(R.id.editText_comments);

        addCommentContainer = (LinearLayout) findViewById(R.id.add_comment_container);
        //      addCommentLayout = (LinearLayout) findViewById(R.id.add_comment_layout);
        addComment = (ImageView) findViewById(R.id.add_edittext_comment);
        chkSocMediaSites = (CheckBox) findViewById(R.id.checkBoxSocialMediaSites);
        chkRealEstateSites = (CheckBox) findViewById(R.id.checkBoxRealEstateSites);

        btnSave = (Button) findViewById(R.id.btn_save);
        btnCancel = (Button) findViewById(R.id.btn_cancel);

        addLayout = new LinearLayout(EditListingActivity.this);

//        onClickAddCommentLayout();
        listingDescriptions.clear();
        addBtnOnCLickListener();

//        spinner = (Spinner)findViewById(R.id.spinner_state);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateListingActivity.this,
//        android.R.layout.simple_spinner_item, paths);

        stateNames = getResources().getStringArray(R.array.state_array);
        splittedStateName = new ArrayList<String>();
        for (int i = 0; i < stateNames.length; i++) {
            splitStateNames = stateNames[i].toString().split(",");
            splittedStateName.add(splitStateNames[0]);
        }

        ArrayAdapter<String> adapterState = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, splittedStateName);
        adapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerState = (MaterialSpinner) findViewById(R.id.spinner_state_province);
        spinnerState.setAdapter(adapterState);
        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (position != -1) {
                    //  String stateCode = stateNames[position];
                    String splittedStateCode[] = stateNames[position].toString().split(",");
                    String splittedCode = splittedStateCode[1];
                    Log.v("State Name: ", splittedStateName.get(position).toString() + "\t State Code: " + splittedCode);
                    //    Toast.makeText(getApplicationContext(), "State Name: " + splittedStateName.get(position).toString() + "\t State Code: " + splittedCode, Toast.LENGTH_SHORT).show();

                    stateCode = splittedCode;
                } else {
                    //  Toast.makeText(getApplicationContext(), "State Name: send null or blank string", Toast.LENGTH_SHORT).show();
                    stateCode = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });


        ArrayAdapter<String> adapterPropertyType = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.property_type_array));
        adapterPropertyType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPropertyType = (MaterialSpinner) findViewById(R.id.spinner_property_type);
        spinnerPropertyType.setAdapter(adapterPropertyType);
        spinnerPropertyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                if (position != -1) {

                    propertyType = parent.getItemAtPosition(position).toString();
                    Log.v("Property Type: ", propertyType);
                } else {
                    propertyType = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        ArrayAdapter<String> adapterListingType = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.listingy_type_array));
        adapterListingType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerListingType = (MaterialSpinner) findViewById(R.id.spinner_listing_type);
        spinnerListingType.setAdapter(adapterListingType);
        spinnerListingType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (position != -1) {
                    Log.v("Listing Type: ", (String) parent.getItemAtPosition(position));
                    listingType = (String) parent.getItemAtPosition(position);
                } else {
                    listingType = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void addBtnOnCLickListener() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // addListingInfo();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  addListingInfo();
            }
        });
    }
}

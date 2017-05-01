package com.circlepix.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.circlepix.android.beans.AgentData;
import com.circlepix.android.beans.ListingDescription;
import com.circlepix.android.beans.ListingInformation;
import com.circlepix.android.helpers.MaterialSpinner;
import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Keuahn on 9/20/2016.
 */
public class CreateListingActivity extends AppCompatActivity {

    private AgentData agentData;
    private CirclePixAppState appState;
    private  TextView toolBarSave;
    private ProgressDialog mProgressDialog;
    private Spinner spinnerState, spinnerPropertyType, spinnerListingType;
    private String[]stateNames;
    private String[] splitStateNames;
    private List<String> splittedStateName;

    private String[]propertyTypeNames;
    private String[] splitPropertyTypeNames;
    private List<String> splittedPropertyTypeName;

    private String[]listingTypeNames;
    private String[] splitListingTypeNames;
    private List<String> splittedListingTypeName;

    private String stateValue, propertyTypeValue, listingTypeValue;
    ArrayAdapter<String> adapterState, adapterPropertyType, adapterListingType;


    private EditText editAddress, editZipCode, editCity, editCountry, editPrice, editSqFt, editBedrooms;
    private EditText editFullBaths, editThreeQtBaths, editHalfBaths, editQuarterBath;
    private EditText editMlsNum, editAltMlsNum;
    private EditText editListingDesc, editListingDesc2, editListingDesc3, editListingDesc4, editListingDesc5, editListingDesc6,editListingDesc7, editListingDesc8, editListingDesc9,editListingDesc10,editComments;

    private final int[] btnID = { R.id.editText_listing_desc, R.id.editText_listing_desc2, R.id.editText_listing_desc3, R.id.editText_listing_desc4, R.id.editText_listing_desc5,  R.id.editText_listing_desc6,
            R.id.editText_listing_desc7, R.id.editText_listing_desc8, R.id.editText_listing_desc9, R.id.editText_listing_desc10};
    private LinearLayout addCommentContainer, addCommentLayout;
    private ImageView addComment;
    private CheckBox chkSocMediaSites, chkRealEstateSites;
    private Button btnCancel, btnNext;
    private LinearLayout addLayout;

    private String stateCode = "";
    private String propertyType = "";
    private String listingType = "";
    private String homeId;
//    private String homeId, address, zipcode, city, county, price, sqft, bedrooms, fullbaths,
//            threeQtbaths, halfbaths, quarterbaths, mlsnum, altmlsnum, socMediaSites, realEstateSites, listingDesc, listingDesc2, listingDesc3, additionalComments;

    private List<String> listingDescriptions = new ArrayList<String>();
    private int selectedListingPosition;
    private ListingInformation selectedListing;
    private boolean editMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_api_test);
        setContentView(R.layout.activity_create_new_listing);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        toolBarSave = (TextView) findViewById(R.id.toolbar_save);
        toolBarSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Toast.makeText(getApplicationContext(), "Note: API is not done yet", Toast.LENGTH_SHORT).show();

                addListingInfo();

            }
        });

        // Get global shared data
        agentData = AgentData.getInstance();

        appState = ((CirclePixAppState)getApplicationContext());
        appState.setContextForPreferences(this);

        appState.setActiveClassName("ListingsTabActivity");

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
        editListingDesc4 = (EditText) findViewById(R.id.editText_listing_desc4);
        editListingDesc5 = (EditText) findViewById(R.id.editText_listing_desc5);
        editListingDesc6 = (EditText) findViewById(R.id.editText_listing_desc6);
        editListingDesc7 = (EditText) findViewById(R.id.editText_listing_desc7);
        editListingDesc8 = (EditText) findViewById(R.id.editText_listing_desc8);
        editListingDesc9 = (EditText) findViewById(R.id.editText_listing_desc9);
        editListingDesc10 = (EditText) findViewById(R.id.editText_listing_desc10);
        editComments = (EditText) findViewById(R.id.editText_comments);

        addCommentContainer = (LinearLayout) findViewById(R.id.add_comment_container);
  //      addCommentLayout = (LinearLayout) findViewById(R.id.add_comment_layout);
        addComment = (ImageView) findViewById(R.id.add_edittext_comment);
        chkSocMediaSites = (CheckBox) findViewById(R.id.checkBoxSocialMediaSites);
        chkRealEstateSites = (CheckBox) findViewById(R.id.checkBoxRealEstateSites);

//        btnNext = (Button) findViewById(R.id.btn_next);
//        btnCancel = (Button) findViewById(R.id.btn_cancel);

        addLayout = new LinearLayout(CreateListingActivity.this);

        // State/Province
        stateNames = getResources().getStringArray(R.array.state_array);
        splittedStateName = new ArrayList<String>();
        for(int i = 0; i < stateNames.length  ; i++){
            splitStateNames = stateNames[i].toString().split(",");
            splittedStateName.add(splitStateNames[0]);
        }

        adapterState = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, splittedStateName);
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

                    stateCode= splittedCode;
                }else{
                    //  Toast.makeText(getApplicationContext(), "State Name: send null or blank string", Toast.LENGTH_SHORT).show();
                    stateCode= "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });


      /*  spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (position != -1) {
                    String splittedStateNamesValues[] = stateNames[position].toString().split(",");
                    String splittedStateValue = splittedStateNamesValues[1];
                    Log.v("State Name: ", splittedStateName.get(position).toString() + "\t splittedProviderValue: " + splittedBillingValue);
                    //    Toast.makeText(getApplicationContext(), "State Name: " + splittedStateName.get(position).toString() + "\t State Code: " + splittedCode, Toast.LENGTH_SHORT).show();

                    stateValue = splittedStateValue;
                } else {
                    //  Toast.makeText(getApplicationContext(), "State Name: send null or blank string", Toast.LENGTH_SHORT).show();
                    stateValue = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
*/

        // Property Types
        propertyTypeNames = getResources().getStringArray(R.array.property_type_array);
        splittedPropertyTypeName = new ArrayList<String>();
        for(int i = 0; i < propertyTypeNames.length  ; i++){
            splitPropertyTypeNames = propertyTypeNames[i].toString().split(",");
            splittedPropertyTypeName.add(splitPropertyTypeNames[0]);
        }


        adapterPropertyType = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, splittedPropertyTypeName);
        adapterPropertyType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPropertyType = (MaterialSpinner) findViewById(R.id.spinner_property_type);
        spinnerPropertyType.setAdapter(adapterPropertyType);
        spinnerPropertyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                if (position != -1) {
                    //  String stateCode = stateNames[position];
                    String splittedStateCode[] = propertyTypeNames[position].toString().split(",");
                    String splittedCode = splittedStateCode[1];
                    Log.v("Property Type: ", splittedPropertyTypeName.get(position).toString() + "\t Property Type Code: " + splittedCode);
                    //    Toast.makeText(getApplicationContext(), "State Name: " + splittedStateName.get(position).toString() + "\t State Code: " + splittedCode, Toast.LENGTH_SHORT).show();

                    propertyType= splittedCode;
                }else{
                    //  Toast.makeText(getApplicationContext(), "State Name: send null or blank string", Toast.LENGTH_SHORT).show();
                    propertyType= "";
                }


//                if (position != -1) {
//
//                    propertyType= parent.getItemAtPosition(position).toString();
//                    Log.v("Property Type: ", propertyType);
//                }else{
//                    propertyType="";
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });


        // Listing Types
        listingTypeNames = getResources().getStringArray(R.array.listingy_type_array);
        splittedListingTypeName = new ArrayList<String>();
        for(int i = 0; i < listingTypeNames.length  ; i++){
            splitListingTypeNames = listingTypeNames[i].toString().split(",");
            splittedListingTypeName.add(splitListingTypeNames[0]);
        }


        adapterListingType = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, splittedListingTypeName);
        adapterListingType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerListingType = (MaterialSpinner) findViewById(R.id.spinner_listing_type);
        spinnerListingType.setAdapter(adapterListingType);
        spinnerListingType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                if (position != -1) {
                    //  String stateCode = stateNames[position];
                    String splittedStateCode[] = listingTypeNames[position].toString().split(",");
                    String splittedCode = splittedStateCode[1];
                    Log.v("Listing Type: ", splittedListingTypeName.get(position).toString() + "\t Listing Type Code: " + splittedCode);
                    //    Toast.makeText(getApplicationContext(), "State Name: " + splittedStateName.get(position).toString() + "\t State Code: " + splittedCode, Toast.LENGTH_SHORT).show();

                    listingType= splittedCode;
                }else{
                    //  Toast.makeText(getApplicationContext(), "State Name: send null or blank string", Toast.LENGTH_SHORT).show();
                    listingType= "";
                }

//
//                if (position != -1) {
//                    Log.v("Listing Type: ", (String) parent.getItemAtPosition(position));
//                    listingType= (String) parent.getItemAtPosition(position);
//                }else{
//                    listingType="";
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });



     /*   ArrayAdapter<String> adapterPropertyType = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.property_type_array));
        adapterPropertyType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPropertyType = (MaterialSpinner) findViewById(R.id.spinner_property_type);
        spinnerPropertyType.setAdapter(adapterPropertyType);


        ArrayAdapter<String> adapterState = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, splittedStateName);
        adapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerState = (MaterialSpinner) findViewById(R.id.spinner_state_province);
        spinnerState.setAdapter(adapterState);

        ArrayAdapter<String> adapterListingType = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.listingy_type_array));
        adapterListingType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerListingType = (MaterialSpinner) findViewById(R.id.spinner_listing_type);
        spinnerListingType.setAdapter(adapterListingType);
*/
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            editMode = true;
            selectedListingPosition = extras.getInt("selectedListingPosition");
            Log.v("selectedListingPos: ", String.valueOf(selectedListingPosition));

            selectedListing = agentData.getListingInformation().get(selectedListingPosition);
            Log.v("selectedListing: ", String.valueOf(selectedListing));


            homeId = selectedListing.getId();
            Log.v("homeId in edit: ", homeId);
            if(!selectedListing.getAddress().equals(null)){
                editAddress.setText(selectedListing.getAddress());
            }
            if(!selectedListing.getZipcode().equals(null)){
                editZipCode.setText(selectedListing.getZipcode());
            }
            if(!selectedListing.getCity().equals(null)){
                editCity.setText(selectedListing.getCity());
            }
            if(!selectedListing.getCounty().equals(null)){
                editCountry.setText(selectedListing.getCounty());
            }



            //if(!agentData.getAgentProfileInformation().getBillingType().equals(null) && !agentData.getAgentProfileInformation().getBillingType().isEmpty()){
            if(!selectedListing.getState().equals(null) && !selectedListing.getState().isEmpty()){
                int spinnerPosition = 0;
                for(int i=0; i < stateNames.length ; i++) {
                    if (stateNames[i].contains(selectedListing.getState())) {
                        spinnerPosition = i + 1; // Add 1 because item 0 is : "-- Select --"
                    }
                }

                spinnerState.setSelection(spinnerPosition);
            }else{
                spinnerState.setSelection(-1);
            }


            if(!selectedListing.getPropertyType().equals(null)){
                int spinnerPosition = 0;
                for(int i=0; i < propertyTypeNames.length ; i++) {
                    if (propertyTypeNames[i].contains(selectedListing.getPropertyType())) {
                        spinnerPosition = i;
                    }
                }

                spinnerPropertyType.setSelection(spinnerPosition);
            }

            if(!selectedListing.getListingType().equals(null)){
                int spinnerPosition = 0;
                for(int i=0; i < listingTypeNames.length ; i++) {
                    if (listingTypeNames[i].contains(selectedListing.getListingType())) {
                        spinnerPosition = i;
                    }
                }

                spinnerListingType.setSelection(spinnerPosition);
            }

         /*   if(!selectedListing.getState().equals(null)){

                int spinnerPosition = adapterState.getPosition(selectedListing.getState());
                spinnerState.setSelection(spinnerPosition);

            }

            if(!selectedListing.getPropertyType().equals(null)){

                int spinnerPosition = adapterState.getPosition(selectedListing.getPropertyType());
                spinnerPropertyType.setSelection(spinnerPosition);

            }

            if(!selectedListing.getListingType().equals(null)){

                int spinnerPosition = adapterState.getPosition(selectedListing.getListingType());
                spinnerListingType.setSelection(spinnerPosition);

            }
        */

            if(!selectedListing.getPrice().equals(null)){
                editPrice.setText(selectedListing.getPrice());
            }

            if(!selectedListing.getSquareFootage().equals(null)){
                editSqFt.setText(selectedListing.getSquareFootage());
            }
            if(!selectedListing.getBedrooms().equals(null)){
                editBedrooms.setText(selectedListing.getBedrooms());
            }
            if(!selectedListing.getFullBaths().equals(null)){
                editFullBaths.setText(selectedListing.getFullBaths());
            }
            if(!selectedListing.getThreeQuaterBaths().equals(null)){
                editThreeQtBaths.setText(selectedListing.getThreeQuaterBaths());
            }
            if(!selectedListing.getHalfBaths().isEmpty()){
                editHalfBaths.setText(selectedListing.getHalfBaths());
                Log.v("getHalfBaths()", selectedListing.getHalfBaths());
            }

            if(!selectedListing.getQuarterBaths().equals(null)){
                editQuarterBath.setText(selectedListing.getQuarterBaths());
            }
            if(!selectedListing.getMlsNum().equals(null)){
                editMlsNum.setText(selectedListing.getMlsNum());
            }
            if(!selectedListing.getAltmlsNum().equals(null)){
                editAltMlsNum.setText(selectedListing.getAltmlsNum());
            }

            if(selectedListing.getSocialMediaSites().equalsIgnoreCase("true")){
                chkSocMediaSites.setChecked(true);
            }else{
                chkSocMediaSites.setChecked(false);
            }

            if(selectedListing.getRealEstateSites().equalsIgnoreCase("true")){
                chkRealEstateSites.setChecked(true);
            }else{
                chkRealEstateSites.setChecked(false);
            }

            if(!selectedListing.getComments().equals(null)){
                editComments.setText(selectedListing.getComments());
            }

            EditText[] editListingDescriptions = {editListingDesc, editListingDesc2, editListingDesc3, editListingDesc4, editListingDesc5, editListingDesc6, editListingDesc7, editListingDesc8, editListingDesc9, editListingDesc10};

            ArrayList<ListingDescription> listingDescriptions = new ArrayList<>();
            ArrayList<ListingDescription> tempListingDescriptions = new ArrayList<>();

            tempListingDescriptions.addAll(selectedListing.getListingDesc());

            listingDescriptions.clear();
            listingDescriptions =  selectedListing.getListingDesc(); // tempListingDescriptions;

       //     Log.v("listingDescriptions: ", String.valueOf(listingDescriptions.size()));

            for(int i = 0; i<listingDescriptions.size(); i++){
                if(i<10){
                  //  Log.v("listingdesc: ", listingDescriptions.get(i).getTDSCvalue());
                    editListingDescriptions[i].setText(listingDescriptions.get(i).getTDSCvalue());
                }

            }

            toolBarSave.setText("Save");
        }else{
            homeId = "";
            editMode = false;
            toolBarSave.setText("Next");
        }

    //    btnNext.setEnabled(false);

        if(editAddress.getText().toString().isEmpty()){
            toolBarSave.setEnabled(false);
        }else{
            toolBarSave.setEnabled(true);
        }


        editAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length()==0){
                   // btnNext.setEnabled(false);
                  //  btnNext.setTextColor(getResources().getColor(R.color.disabled_color));
                    toolBarSave.setEnabled(false);
                } else {
                    toolBarSave.setEnabled(true);
                //    btnNext.setEnabled(true);
                //    btnNext.setTextColor(getResources().getColor(R.color.text_title));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

//        onClickAddCommentLayout();
        listingDescriptions.clear();
        addBtnOnCLickListener();

//        spinner = (Spinner)findViewById(R.id.spinner_state);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateListingActivity.this,
//        android.R.layout.simple_spinner_item, paths);



       /* ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateListingActivity.this,
                android.R.layout.simple_spinner_item, splittedName);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);*/

     /*   spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                if(position !=0){
                    String stateCode =  stateNames[position];
                    String splittedStateCode[] = stateNames[position].toString().split(",");
                    String splittedCode= splittedStateCode[1];
                    Log.v("State Name: ",  splittedName.get(position).toString() + "\t State Code: " + stateCode);
                    Toast.makeText(getApplicationContext(), "State Name: " + splittedName.get(position).toString() + "\t State Code: " + splittedCode, Toast.LENGTH_SHORT ).show();

                }



               *//* switch (position) {
                    case 0:
                        // Whatever you want to happen when the first item gets selected
                        Log.v("item", (String) parent.getItemAtPosition(position));
                        break;
                    case 1:
                        // Whatever you want to happen when the second item gets selected
                        Log.v("item", (String) parent.getItemAtPosition(position));
                        break;
                    case 2:
                        // Whatever you want to happen when the thrid item gets selected
                        Log.v("item", (String) parent.getItemAtPosition(position));
                        break;
                    case 3:
                        // Whatever you want to happen when the thrid item gets selected
                        Log.v("item", (String) parent.getItemAtPosition(position));
                        break;

                }*//*
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });*/

    }

/*    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
//                Intent homeIntent = new Intent(this, HomeActivity.class);
//                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(homeIntent);
//                Toast.makeText(getApplicationContext(), "Back Button Pressed!", Toast.LENGTH_SHORT).show();
        }
        return (super.onOptionsItemSelected(menuItem));
    }*/


    private void addBtnOnCLickListener() {
      /*  addComment.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                LayoutInflater ltInflater = getLayoutInflater();
                LinearLayout subLayoutFieldsForBtnAdd = (LinearLayout) findViewById(R.id.add_comment_container);
                View view1 = ltInflater.inflate(R.layout.listing_add_comment, subLayoutFieldsForBtnAdd, true);



              *//*  LinearLayout li=new LinearLayout(this);
                EditText et=new EditText(this);
                Button b=new Button(this);

                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub

                        int pos=(Integer) v.getTag();
                        mainLayout.removeViewAt(pos);

                    }
                });

                b.setTag((mainLayout.getChildCount()+1));*//*
            }

        });*/

       /* btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addListingInfo();
            }
        });*/
    }

    public void onClickAddCommentLayout() {

        LayoutInflater ltInflater = getLayoutInflater();
        LinearLayout subLayoutFieldsForBtnAdd = (LinearLayout) findViewById(R.id.add_comment_container);
        View view1 = ltInflater.inflate(R.layout.listing_add_comment, subLayoutFieldsForBtnAdd, true);


        EditText edittext = (EditText) view1.findViewById(R.id.editText_listing_desc);
        Log.v("edit text", edittext.getText().toString());

        final Button btnAddComment = (Button) view1.findViewById(R.id.add_edittext_comment);

        btnAddComment.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onClickAddCommentLayout();
            }
        });

    }

    public void onClickRemoveCommentLayout(View v) {
        View v1 = (View) v.getParent();
        LinearLayout subLayoutFieldsForBtnRemove = (LinearLayout) findViewById(R.id.add_comment_container);
        subLayoutFieldsForBtnRemove.removeView(v1);
    }


    public void addListingInfo(){

        Thread networkThread = new Thread() {

                public void run() {

                String strStateCode = "";
                String strPropertyType = "";
                String strListingType = "";

                String homeIdTemp = "";
                String address = "";
                String zipcode = "";
                String city = "";
                String county = "";
                String price = "";
                String sqft = "";
                String bedrooms = "";
                String fullbaths = "";
                String threeQtbaths = "";
                String halfbaths  = "";
                String quarterbaths = "";
                String mlsnum = "";
                String altmlsnum = "";
                String socMediaSites = "";
                String realEstateSites = "";
                String additionalComments = "";

                if(!homeId.isEmpty()){
                    homeIdTemp = homeId;
                }else{
                    homeIdTemp = "";

                }

                if(!editAddress.getText().toString().isEmpty()){
                    address = editAddress.getText().toString();
                }else{
                    address = "";
                }
                if(!editZipCode.getText().toString().isEmpty()){
                    zipcode = editZipCode.getText().toString();
                }else{
                    zipcode = "";
                }
                if(!editCity.getText().toString().isEmpty()){
                    city = editCity.getText().toString();
                }else{
                    city = "";
                }

                if(!editCountry.getText().toString().isEmpty()){
                    county = editCountry.getText().toString();
                }else{
                    county = "";
                }

                if(!stateCode.isEmpty()){
                    strStateCode = stateCode;
                }else{
                    strStateCode = "";
                }

                if(!propertyType.isEmpty()){
                    strPropertyType = propertyType;
                }else{
                    strPropertyType = "";
                }

                if(!listingType.isEmpty()){
                    strListingType = listingType;
                }else{
                    strListingType = "";
                }

                Log.v("listingType", listingType + " ; " + strListingType);
                Log.v("strStateCode", stateCode + " ; " + strStateCode);

                if(!editPrice.getText().toString().isEmpty()){
                    price = editPrice.getText().toString();
                }else {
                    price = "";
                }
                if(!editSqFt.getText().toString().isEmpty()){
                    sqft =editSqFt.getText().toString();
                }else{
                    sqft = "";
                }
                if(!editBedrooms.getText().toString().isEmpty()){
                    bedrooms = editBedrooms.getText().toString();
                }else{
                    bedrooms = "";
                }
                if(!editFullBaths.getText().toString().isEmpty()){
                    fullbaths =editFullBaths.getText().toString();
                }else{
                    fullbaths = "";
                }
                if(!editThreeQtBaths.getText().toString().isEmpty()){
                    threeQtbaths = editThreeQtBaths.getText().toString();
                }else{
                    threeQtbaths = "";
                }
                if(!editHalfBaths.getText().toString().isEmpty()){
                    halfbaths = editHalfBaths.getText().toString();
                }else{
                    halfbaths = "";
                }
                if(!editQuarterBath.getText().toString().isEmpty()){
                    quarterbaths = editQuarterBath.getText().toString();
                }else{
                    quarterbaths = "";
                }
                if(!editMlsNum.getText().toString().isEmpty()){
                    mlsnum = editMlsNum.getText().toString();
                }else{
                    mlsnum = "";
                }
                if(!editAltMlsNum.getText().toString().isEmpty()){
                    altmlsnum = editAltMlsNum.getText().toString();
                }else{
                    altmlsnum = "";
                }
                if(chkSocMediaSites.isChecked()){
                    socMediaSites = "true";
                }else{
                    socMediaSites = "false";
                }

                Log.v("socMediaSites", socMediaSites);
                if(chkRealEstateSites.isChecked()){
                    realEstateSites = "true";
                }else{
                    realEstateSites = "false";
                }
                Log.v("realEstateSites", realEstateSites);
                listingDescriptions.clear();

                if(!editListingDesc.getText().toString().isEmpty()){
                    listingDescriptions.add(editListingDesc.getText().toString());
                }
                if(!editListingDesc2.getText().toString().isEmpty()){
                    listingDescriptions.add(editListingDesc2.getText().toString());
                }
                if(!editListingDesc3.getText().toString().isEmpty()){
                    listingDescriptions.add(editListingDesc3.getText().toString());
                }
                if(!editListingDesc4.getText().toString().isEmpty()){
                    listingDescriptions.add(editListingDesc4.getText().toString());
                }
                if(!editListingDesc5.getText().toString().isEmpty()){
                    listingDescriptions.add(editListingDesc5.getText().toString());
                }
                if(!editListingDesc6.getText().toString().isEmpty()){
                    listingDescriptions.add(editListingDesc6.getText().toString());
                }
                if(!editListingDesc7.getText().toString().isEmpty()){
                    listingDescriptions.add(editListingDesc7.getText().toString());
                }
                if(!editListingDesc8.getText().toString().isEmpty()){
                    listingDescriptions.add(editListingDesc8.getText().toString());
                }
                if(!editListingDesc9.getText().toString().isEmpty()){
                    listingDescriptions.add(editListingDesc9.getText().toString());
                }
                if(!editListingDesc10.getText().toString().isEmpty()){
                    listingDescriptions.add(editListingDesc10.getText().toString());
                }
                if(!editComments.getText().toString().isEmpty()){
                    additionalComments = editComments.getText().toString();
                }else{
                    additionalComments = "";
                }


                try {
                   /* JSONArray jArry=new JSONArray();
                    for(int i=0; i<listingDescriptions.size(); i++) {
                        jArry.put(listingDescriptions.get(i));
                    }

                    Log.v("jArry listingDescriptions:", String.valueOf(jArry.toString()));
                    */


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog = ProgressDialog.show(CreateListingActivity.this, "", "Saving...");
                        }
                    });

                    String BASE_URL = "http://stag-mobile.circlepix.com/api/listing.php";
                //    String BASE_URL = "http://keuahn.circlepix.dev/api/listing.php";
                    MultipartBody.Builder buildernew = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                               //Here you can add the fix number of data.
                            .addFormDataPart("method", "addListingInfo")
                            .addFormDataPart("realtorId", agentData.getRealtor().getId())
                            .addFormDataPart("homeId", homeIdTemp)
                            .addFormDataPart("address", address)
                            .addFormDataPart("city", city)
                            .addFormDataPart("zipCode", zipcode)
                            .addFormDataPart("county", county)
                            .addFormDataPart("state", strStateCode)
                            .addFormDataPart("propertyType", strPropertyType)
                            .addFormDataPart("price", price)
                            .addFormDataPart("listingType", strListingType)
                            .addFormDataPart("squareFootage", sqft)
                            .addFormDataPart("bedrooms", bedrooms)
                            .addFormDataPart("fullBaths", fullbaths)
                            .addFormDataPart("threeQuaterBaths", threeQtbaths)
                            .addFormDataPart("halfBaths", halfbaths)
                            .addFormDataPart("quarterBaths", quarterbaths)
                            .addFormDataPart("mlsNum", mlsnum)
                            .addFormDataPart("altmlsNum", altmlsnum)
                            .addFormDataPart("socialMediaSites", socMediaSites)
                            .addFormDataPart("realEstateSites", realEstateSites)
                           // .addFormDataPart("listingDescription[0]", jArry.toString())
                            .addFormDataPart("comments", additionalComments);

                       for (int i = 0; i < listingDescriptions.size(); i++) {
                            buildernew.addFormDataPart("listingDescription[" + i + "]", listingDescriptions.get(i));
                            Log.v("listingDescription[" + i + "]", listingDescriptions.get(i).toString());
                        }

                    MultipartBody requestBody = buildernew.build();

                   /* RequestBody requestBody1 = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("method", "addListingInfo")
                            .addFormDataPart("address", address)
                            .addFormDataPart("city", city)
                            .addFormDataPart("county", county)
                            .addFormDataPart("state", state)
                            .addFormDataPart("propertyType", property_type)
                            .addFormDataPart("price", price)
                            .addFormDataPart("listingType", listing_type)
                            .addFormDataPart("squareFootage", sqft)
                            .addFormDataPart("bedrooms", bedrooms)
                            .addFormDataPart("fullBaths", fullbaths)
                            .addFormDataPart("threeQuaterBaths", threeQtbaths)
                            .addFormDataPart("halfBaths", halfbaths)
                            .addFormDataPart("quarterBaths", quarterbaths)
                            .addFormDataPart("mlsNum", mlsnum)
                            .addFormDataPart("altmlsNum", altmlsnum)
                            .addFormDataPart("socialMediaSites", socMediaSites)
                            .addFormDataPart("RealEstateSites", realEstateSites)
                            .addFormDataPart("comments", additionalComments);
                            for (String imageFilePath : newSelectedImagesUrl) {

                                FileBody filebody = new FileBody(new File(imageFilePath), "image/jpeg");
                                requestBody1.addFormDataPart("mediaFile[]", filebody);
                            }

                            .build();*/

                    Request request = new Request.Builder()
                            .url(BASE_URL)
                            .post(requestBody)
                            .build();

                    OkHttpClient client = new OkHttpClient();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, final IOException e) {
//                            Log.i("Failed: " ,  e.getMessage());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //stuff that updates ui
                                    mProgressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Failed: "+ e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, final Response response) throws IOException {

                            final String responseString;
                            String status;
                            String message;

                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    responseString = response.body().string();
                                    Log.i("Done", responseString);
                                    response.body().close();

                                    try {

                                        JSONObject Jobject = new JSONObject(responseString);
                                        JSONObject returnedHomeId =  Jobject.getJSONObject("data");
                                        homeId = returnedHomeId.getString("homeId"); //store this value to sharedpreference so you don't need
                                                                                    // to check it again next time (maybe only on opening the listing page only)
//                                        selectedListing.setId(homeId);
                                        status = Jobject.getString("status");
                                        message = Jobject.getString("message");

                                        Log.v("status: ", status);
                                        Log.v("message: ", message);
                                        Log.v("hasSelfServe: ",homeId);

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //stuff that updates ui
                                                mProgressDialog.dismiss();
                                              //  finish();
                                            }
                                        });

                                        if(editMode == true){
                                            finish();
                                        }else{
                                            Intent intent = new Intent(getApplicationContext() , AlbumSelectActivity.class);
                                            //set limit on number of images that can be selected, default is 10
                                            intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 10);
                                            startActivityForResult(intent, Constants.REQUEST_CODE);
                                            appState.setActiveClassName("ListingsTabActivity");
                                        }

                                    }
                                    catch (JSONException e) {
                                        Log.v("Error: ", e.getLocalizedMessage());
                                    }


                                }
                            } else {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //stuff that updates ui
                                        mProgressDialog.dismiss();
                                      //  finish();
                                        Toast.makeText(getApplicationContext(), "Error: "+ response.message(), Toast.LENGTH_SHORT).show();
                                    }
                                });
//                                Log.i("Error", "unsuccessful");
//                                Intent intent = new Intent(getApplicationContext(), AddListingImagesActivity.class);
//                                intent.putExtra("responseBody","Error: "+ "Response Message: " + response.message() + ",  Response Code: "  + response.code() );
//                                startActivity(intent);
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        networkThread.start();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if  (requestCode == Constants.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            //The array list has the image paths of the selected images
            ArrayList<Image> images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);


            ArrayList<String> imagePaths = new ArrayList<String>();

            for (int i = 0; i < images.size(); i++) {
                imagePaths.add(images.get(i).path);
                Log.v("images " + i  , String.valueOf(imagePaths.get(i).toString()));

            }

            Intent intent = new Intent(getApplicationContext(), AddListingImagesActivity.class);
            Log.v("homeId to send"  , homeId);
            intent.putExtra("homeId", homeId);
            intent.putExtra("images", imagePaths);
            startActivity(intent);
            appState.setActiveClassName("ListingsTabActivity");
            finish();
        }else if (resultCode == RESULT_CANCELED) {
            finish();
        }
    }

    public String getToString(String[] arrayData) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0 ; i < arrayData.length; i++) {
            stringBuilder.append(arrayData[i]);
            if (i < arrayData.length - 1) {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }
}

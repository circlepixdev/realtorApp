package com.circlepix.android;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.circlepix.android.beans.AgentData;
import com.circlepix.android.helpers.MaterialSpinner;

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
 * Created by keuahnlumanog on 11/12/2016.
 */

public class EditAgentInformationActivity extends AppCompatActivity {

    private AgentData agentData;
    private TextView toolBarSave, agentIdNumber;

    private EditText agentFirstName, agentLastName,  agentAgency, agentPhoneNum, agentCellNum,
            agentFaxNum, agentEmailAd, agentWebsite,
            agentStreetAddress, agentCity, agentState, agentZipCode, agentLeadBeePin, agentProductNum,  agentStateLicenseNum;

    private CheckBox recreateListingVideo;
    private ProgressDialog mProgressDialog;
    private Spinner spinnerCellProvider, spinnerTextNotifications, spinnerBillingType;

    private String[] splitBillingTypeNames;
    private List<String> splittedBillingTypeNames;
    private String cellProviderValue, textNotificationValue, billingTypeValue;
    ArrayAdapter<String> adapterCellProvider, adapterTextNotifications, adapterBillingType;

    // String array from xml
    private String[] cellProviderArray, textNotificationsArray, billingTypeArray;

    private List<String> checkEmptyFields;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_agent_information);

        // Get global shared data
        agentData = AgentData.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        toolBarSave = (TextView) findViewById(R.id.toolbar_save);
        toolBarSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                checkEmptyFields = new ArrayList<String>();
                checkEmptyFields.clear();
                if(agentProductNum.getText().toString().isEmpty() || agentEmailAd.getText().toString().isEmpty()){
                 //   StringBuilder strBuilder = new StringBuilder();

                    if((agentProductNum.getText().toString().isEmpty()) && (agentEmailAd.getText().toString().isEmpty())){

                        Toast.makeText(getApplicationContext(), "Product # and Email Address cannot be empty", Toast.LENGTH_SHORT).show();
                    }else if (agentEmailAd.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(), "Email Address cannot be empty", Toast.LENGTH_SHORT).show();

                    }else if (agentProductNum.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(), "Product #  cannot be empty", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    editAgentInformation();
                }

            }
        });

        agentIdNumber = (TextView)findViewById(R.id.text_agent_id_number);
        agentFirstName = (EditText) findViewById(R.id.editText_first_name);
        agentLastName = (EditText) findViewById(R.id.editText_last_name);

        agentPhoneNum = (EditText) findViewById(R.id.editText_phone_number);
        agentAgency = (EditText) findViewById(R.id.editText_agency);
        agentCellNum = (EditText) findViewById(R.id.editText_cell_number);

        agentFaxNum = (EditText) findViewById(R.id.editText_fax_number);
        agentEmailAd = (EditText) findViewById(R.id.editText_email_address);
        agentWebsite = (EditText) findViewById(R.id.editText_website);
        agentStreetAddress = (EditText) findViewById(R.id.editText_street_address);
        agentCity = (EditText) findViewById(R.id.editText_city);
        agentState = (EditText) findViewById(R.id.editText_state);
        agentZipCode = (EditText) findViewById(R.id.editText_zip_code);
        agentLeadBeePin = (EditText) findViewById(R.id.editText_leadbee_pin);
        agentProductNum = (EditText) findViewById(R.id.editText_product_number);
        agentStateLicenseNum = (EditText) findViewById(R.id.editText_state_license_number);

        recreateListingVideo = (CheckBox) findViewById(R.id.checkbox_recreate_listing_videos);

        cellProviderArray = getResources().getStringArray(R.array.cell_provider_array);
        textNotificationsArray = getResources().getStringArray(R.array.text_notifications_array);
        billingTypeArray = getResources().getStringArray(R.array.billing_type_array);

        splittedBillingTypeNames = new ArrayList<String>();
        for (int i = 0; i < billingTypeArray.length; i++) {
            splitBillingTypeNames = billingTypeArray[i].toString().split(",");
            splittedBillingTypeNames.add(splitBillingTypeNames[0]);
        }

        adapterBillingType = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, splittedBillingTypeNames);
        adapterBillingType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBillingType = (MaterialSpinner) findViewById(R.id.spinner_billing_type);
        spinnerBillingType.setAdapter(adapterBillingType);
        spinnerBillingType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (position != -1) {
                    String splittedBillingTypeValue[] = billingTypeArray[position].toString().split(",");
                    String splittedBillingValue = splittedBillingTypeValue[1];
                    Log.v("Billing Type: ", splittedBillingTypeNames.get(position).toString() + "\t splittedProviderValue: " + splittedBillingValue);
                    //    Toast.makeText(getApplicationContext(), "State Name: " + splittedStateName.get(position).toString() + "\t State Code: " + splittedCode, Toast.LENGTH_SHORT).show();

                    billingTypeValue = splittedBillingValue;
                } else {
                    //  Toast.makeText(getApplicationContext(), "State Name: send null or blank string", Toast.LENGTH_SHORT).show();
                    billingTypeValue = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        adapterTextNotifications = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.text_notifications_array));
        adapterTextNotifications.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTextNotifications = (MaterialSpinner) findViewById(R.id.spinner_text_notifications);
        spinnerTextNotifications.setAdapter(adapterTextNotifications);
        spinnerTextNotifications.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (position != -1) {
                    textNotificationValue= parent.getItemAtPosition(position).toString();
                    Log.v("textNotificationValue: ", textNotificationValue);

                } else {
                    //  Toast.makeText(getApplicationContext(), "State Name: send null or blank string", Toast.LENGTH_SHORT).show();
                    textNotificationValue = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });


        adapterCellProvider = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.cell_provider_array));
        adapterCellProvider.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCellProvider = (MaterialSpinner) findViewById(R.id.spinner_cell_provider);
        spinnerCellProvider.setAdapter(adapterCellProvider);
        spinnerCellProvider.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (position != -1) {
                    cellProviderValue= parent.getItemAtPosition(position).toString();
                    Log.v("cellProviderValue: ", cellProviderValue);
                } else {
                    //  Toast.makeText(getApplicationContext(), "State Name: send null or blank string", Toast.LENGTH_SHORT).show();
                    cellProviderValue = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });


        setAgentInformation();
    }


    private void setAgentInformation(){
        // Agent Profile Section

        if(!agentData.getAgentProfileInformation().getId().equals(null) && !agentData.getAgentProfileInformation().getId().isEmpty()){
            agentIdNumber.setText(agentData.getAgentProfileInformation().getId());
        }else{
            agentIdNumber.setText("");
        }

        if(!agentData.getAgentProfileInformation().getFirstName().equals(null) && !agentData.getAgentProfileInformation().getFirstName().isEmpty()){
            agentFirstName.setText(agentData.getAgentProfileInformation().getFirstName());
            agentFirstName.setSelection(agentFirstName.getText().length());
        }else{
            agentFirstName.setText("");
        }

        if(!agentData.getAgentProfileInformation().getLastName().equals(null) && !agentData.getAgentProfileInformation().getLastName().isEmpty()){
            agentLastName.setText(agentData.getAgentProfileInformation().getLastName());
            agentLastName.setSelection(agentLastName.getText().length());
        }else{
            agentLastName.setText("");
        }

        if(!agentData.getAgentProfileInformation().getAgency().equals(null)  && !agentData.getAgentProfileInformation().getAgency().isEmpty()){
            agentAgency.setText(agentData.getAgentProfileInformation().getAgency());
            agentAgency.setSelection(agentAgency.getText().length());
        }else{
            agentAgency.setText("");
        }

        if(!agentData.getAgentProfileInformation().getPhoneNumber().equals(null) && !agentData.getAgentProfileInformation().getPhoneNumber().isEmpty()){
            agentPhoneNum.setText(agentData.getAgentProfileInformation().getPhoneNumber());
            agentPhoneNum.setSelection(agentPhoneNum.getText().length());
        }else{
            agentPhoneNum.setText("");
        }

        if(!agentData.getAgentProfileInformation().getCellNumber().equals(null) && !agentData.getAgentProfileInformation().getCellNumber().isEmpty()){
            agentCellNum.setText(agentData.getAgentProfileInformation().getCellNumber());
            agentCellNum.setSelection(agentCellNum.getText().length());
        }else{
            agentCellNum.setText("");
        }

//        if(!agentData.getAgentProfileInformation().getCellProvider().equals(null) && !agentData.getAgentProfileInformation().getCellProvider().isEmpty()){
//            agentCellProvider.setText(agentData.getAgentProfileInformation().getCellProvider());
//        }else{
//            agentCellProvider.setText("");
//        }

        if(!agentData.getAgentProfileInformation().getCellProvider().equals(null) && !agentData.getAgentProfileInformation().getCellProvider().isEmpty()){

            int spinnerPosition = 0;
            for(int i=0; i < cellProviderArray.length ; i++) {
                if (cellProviderArray[i].contains(agentData.getAgentProfileInformation().getCellProvider())) {
                    spinnerPosition = i;
                }
            }
            spinnerCellProvider.setSelection(spinnerPosition);
        }

        if(!agentData.getAgentProfileInformation().getTextNotifications().equals(null) && !agentData.getAgentProfileInformation().getTextNotifications().isEmpty()){

            int spinnerPosition = 0;
            for(int i=0; i < textNotificationsArray.length ; i++) {
                if (textNotificationsArray[i].contains(agentData.getAgentProfileInformation().getTextNotifications())) {
                    spinnerPosition = i;
                }
            }
            spinnerTextNotifications.setSelection(spinnerPosition);
        }

        if(!agentData.getAgentProfileInformation().getBillingType().equals(null) && !agentData.getAgentProfileInformation().getBillingType().isEmpty()){

            int spinnerPosition = 0;
            for(int i=0; i < billingTypeArray.length ; i++) {
                if (billingTypeArray[i].contains(agentData.getAgentProfileInformation().getBillingType())) {
                    spinnerPosition = i;
                }
            }
            spinnerBillingType.setSelection(spinnerPosition);
        }

        if(!agentData.getAgentProfileInformation().getFaxNumber().equals(null) && !agentData.getAgentProfileInformation().getFaxNumber().isEmpty()){
            agentFaxNum.setText(agentData.getAgentProfileInformation().getFaxNumber());
            agentFaxNum.setSelection(agentFaxNum.getText().length());
        }else{
            agentFaxNum.setText("");
        }

        if(!agentData.getAgentProfileInformation().getEmail().equals(null) && !agentData.getAgentProfileInformation().getEmail().isEmpty()){
            agentEmailAd.setText(agentData.getAgentProfileInformation().getEmail());
            agentEmailAd.setSelection(agentEmailAd.getText().length());
        }else{
            agentEmailAd.setText("");
        }

        if(!agentData.getAgentProfileInformation().getWebsite().equals(null) && !agentData.getAgentProfileInformation().getWebsite().isEmpty()){
            agentWebsite.setText(agentData.getAgentProfileInformation().getWebsite());
            agentWebsite.setSelection(agentWebsite.getText().length());
        }else{
            agentWebsite.setText("");
        }

        if(!agentData.getAgentProfileInformation().getStreetAddress().equals(null) && !agentData.getAgentProfileInformation().getStreetAddress().isEmpty()){
            agentStreetAddress.setText(agentData.getAgentProfileInformation().getStreetAddress());
            agentStreetAddress.setSelection(agentStreetAddress.getText().length());

        }else{
            agentStreetAddress.setText("");
        }

        if(!agentData.getAgentProfileInformation().getCity().equals(null) && !agentData.getAgentProfileInformation().getCity().isEmpty()){
            agentCity.setText(agentData.getAgentProfileInformation().getCity());
            agentCity.setSelection(agentCity.getText().length());

        }else{
            agentCity.setText("");
        }

        if(!agentData.getAgentProfileInformation().getState().equals(null) && !agentData.getAgentProfileInformation().getState().isEmpty()){
            agentState.setText(agentData.getAgentProfileInformation().getState());
            agentState.setSelection(agentState.getText().length());

        }else{
            agentState.setText("");
        }

        if(!agentData.getAgentProfileInformation().getZipcode().equals(null) && !agentData.getAgentProfileInformation().getZipcode().isEmpty()){
            agentZipCode.setText(agentData.getAgentProfileInformation().getZipcode());
            agentZipCode.setSelection(agentZipCode.getText().length());

        }else{
            agentZipCode.setText("");
        }


        if(!agentData.getAgentProfileInformation().getLeadBeePin().equals(null) && !agentData.getAgentProfileInformation().getLeadBeePin().isEmpty()){
            agentLeadBeePin.setText(agentData.getAgentProfileInformation().getLeadBeePin());
            agentLeadBeePin.setSelection(agentLeadBeePin.getText().length());

        }else{
            agentLeadBeePin.setText("");
        }

        if(!agentData.getAgentProfileInformation().getProductNumber().equals(null) && !agentData.getAgentProfileInformation().getProductNumber().isEmpty()){
            agentProductNum.setText(agentData.getAgentProfileInformation().getProductNumber());
            agentProductNum.setSelection(agentProductNum.getText().length());

        }else{
            agentProductNum.setText("");
        }

        if(!agentData.getAgentProfileInformation().getStateLicenseNumber().equals(null) && !agentData.getAgentProfileInformation().getStateLicenseNumber().isEmpty()){
            agentStateLicenseNum.setText(agentData.getAgentProfileInformation().getStateLicenseNumber());
            agentStateLicenseNum.setSelection(agentStateLicenseNum.getText().length());

        }else{
            agentStateLicenseNum.setText("");
        }
    }


    public void editAgentInformation(){

        Thread networkThread = new Thread() {

            public void run() {


                String strAgentFirstName = "";
                String strAgentLastName = "";
                String strAgency = "";
                String strPhoneNumber = "";
                String strCellNumber = "";
                String strCellProvider = "";
                String strTextNotifications = "";
                String strFaxNumber = "";
                String strEmailAddress = "";
                String strWebsite = "";
                String strStreet = "";
                String strCity = "";
                String strState = "";
                String strZip = "";
                String strLeadBeePin = "";
                String strProductNumber = "";
                String strBillingType = "";
                String strStateLicenseNumber = "";
                String strRecreateListingVideo = "false";

                if(!agentFirstName.getText().toString().isEmpty()){
                    strAgentFirstName = agentFirstName.getText().toString();
                }else{
                    strAgentFirstName = "";
                }

                if(!agentLastName.getText().toString().isEmpty()){
                    strAgentLastName = agentLastName.getText().toString();
                }else{
                    strAgentLastName = "";
                }

                if(!agentAgency.getText().toString().isEmpty()){
                    strAgency = agentAgency.getText().toString();
                }else{
                    strAgency = "";
                }

                if(!agentPhoneNum.getText().toString().isEmpty()){
                    strPhoneNumber = agentPhoneNum.getText().toString();
                }else{
                    strPhoneNumber = "";
                }

                if(!cellProviderValue.isEmpty()){
                    strCellProvider = cellProviderValue;
                }else{
                    strCellProvider = "";
                }

                if(!textNotificationValue.isEmpty()){
                    strTextNotifications = textNotificationValue;
                }else{
                    strTextNotifications = "";
                }

                if(!agentCellNum.getText().toString().isEmpty()){
                    strCellNumber = agentCellNum.getText().toString();
                }else{
                    strCellNumber = "";
                }

                if(!agentFaxNum.getText().toString().isEmpty()){
                    strFaxNumber = agentFaxNum.getText().toString();
                }else{
                    strFaxNumber = "";
                }

                if(!agentEmailAd.getText().toString().isEmpty()){
                    strEmailAddress = agentEmailAd.getText().toString();
                }else{
                    strEmailAddress = "";
                }

                if(!agentWebsite.getText().toString().isEmpty()){
                    strWebsite = agentWebsite.getText().toString();
                }else{
                    strWebsite = "";
                }

                if(!agentStreetAddress.getText().toString().isEmpty()){
                    strStreet = agentStreetAddress.getText().toString();
                }else{
                    strStreet = "";
                }

                if(!agentCity.getText().toString().isEmpty()){
                    strCity = agentCity.getText().toString();
                }else{
                    strCity = "";
                }

                if(!agentState.getText().toString().isEmpty()){
                    strState = agentState.getText().toString();
                }else{
                    strState = "";
                }

                if(!agentZipCode.getText().toString().isEmpty()){
                    strZip = agentZipCode.getText().toString();
                }else{
                    strZip = "";
                }

                if(!agentLeadBeePin.getText().toString().isEmpty()){
                    strLeadBeePin = agentLeadBeePin.getText().toString();
                }else{
                    strLeadBeePin = "";
                }

                if(!agentProductNum.getText().toString().isEmpty()){
                    strProductNumber = agentProductNum.getText().toString();
                }else{
                    strProductNumber = "";
                }

                if(!billingTypeValue.isEmpty()){
                    strBillingType = billingTypeValue;
                }else{
                    strBillingType = "";
                }

                if(!agentStateLicenseNum.getText().toString().isEmpty()){
                    strStateLicenseNumber = agentStateLicenseNum.getText().toString();
                }else{
                    strStateLicenseNumber = "";
                }

                if(recreateListingVideo.isChecked()){
                    strRecreateListingVideo = "true";
                }else{
                    strRecreateListingVideo = "false";
                }
                Log.v("strAgentYoutubeId", strAgentFirstName);
                Log.v("strAgentBio", strAgentLastName);

                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog = ProgressDialog.show(EditAgentInformationActivity.this, "", "Saving...");
                        }
                    });

                    String BASE_URL = "http://stag-mobile.circlepix.com/api/agentProfile.php";
                    //String BASE_URL = "http://keuahn.circlepix.dev/api/agentProfile.php";
                    MultipartBody.Builder buildernew = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("method", "updateAgentInfo")
                            .addFormDataPart("realtorId", agentData.getRealtor().getId())
                            .addFormDataPart("form", "agentProfileInfo")
                            .addFormDataPart("fname", strAgentFirstName)
                            .addFormDataPart("lname", strAgentLastName)
                            .addFormDataPart("agency", strAgency)
                            .addFormDataPart("phoneNumber", strPhoneNumber)
                            .addFormDataPart("cellNumber", strCellNumber)
                            .addFormDataPart("cellProvider", strCellProvider)
                            .addFormDataPart("textNotification", strTextNotifications)
                            .addFormDataPart("faxNumber", strFaxNumber)
                            .addFormDataPart("emailAddress", strEmailAddress)
                            .addFormDataPart("website", strWebsite)
                            .addFormDataPart("streetAddress", strStreet)
                            .addFormDataPart("city", strCity)
                            .addFormDataPart("state", strState)
                            .addFormDataPart("zip", strZip)
                            .addFormDataPart("leadBeePin", strLeadBeePin)
                            .addFormDataPart("productNumber", strProductNumber)
                            .addFormDataPart("billingType", strBillingType)
                            .addFormDataPart("stateLicenseNumber", strStateLicenseNumber)
                            .addFormDataPart("recreateListingVideo", strRecreateListingVideo);


                    MultipartBody requestBody = buildernew.build();

                    Request request = new Request.Builder()
                            .url(BASE_URL)
                            .post(requestBody)
                            .build();

                    Log.v("update credentials: ", String.valueOf(requestBody));
                    OkHttpClient client = new OkHttpClient();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, final IOException e) {
                            Log.i("Failed: " ,  e.getMessage());
//                            Intent intent = new Intent(getApplicationContext(), AddListingImagesActivity.class);
//                            intent.putExtra("responseBody","Failed: " + e.getLocalizedMessage() );
//                            startActivity(intent);
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

                                        status = Jobject.getString("status");
                                        message = Jobject.getString("message");

                                        Log.v("status: ", status);
                                        Log.v("message: ", message);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //stuff that updates ui
                                                mProgressDialog.dismiss();
                                            }
                                        });

                                    }
                                    catch (JSONException e) {
                                        Log.v("Error: ", e.getLocalizedMessage());
                                    }
                                    finish();

                                }
                            } else {
                                Log.i("Error", "unsuccessful");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mProgressDialog.dismiss();
                                    }
                                });

                                finish();
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
}

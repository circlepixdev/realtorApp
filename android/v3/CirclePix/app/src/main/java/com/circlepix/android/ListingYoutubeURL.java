package com.circlepix.android;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.circlepix.android.beans.AgentData;
import com.circlepix.android.beans.ListingInformation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by keuahnlumanog on 02/04/2017.
 */

public class ListingYoutubeURL extends AppCompatActivity {

    private AgentData agentData;

    private TextView toolBarSave;
    private ProgressDialog mProgressDialog;

    private EditText editYoutubeURL, editVideoTitle;
    private CheckBox chkAllowOnMLS;

    private int selectedListingPosition;
    private ListingInformation selectedListing;
    private String homeId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_api_test);
        setContentView(R.layout.activity_listing_youtube_url);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        toolBarSave = (TextView) findViewById(R.id.toolbar_save);
        toolBarSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                addYoutubeLink();

            }
        });

        // Get global shared data
        agentData = AgentData.getInstance();
        editYoutubeURL = (EditText) findViewById(R.id.editText_youtube_video_id);
        editVideoTitle = (EditText) findViewById(R.id.editText_youtube_video_title);
        chkAllowOnMLS = (CheckBox) findViewById(R.id.checkBox_allow_on_mls);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            selectedListingPosition = extras.getInt("selectedListingPosition");
            Log.v("selectedListingPos: ", String.valueOf(selectedListingPosition));

            selectedListing = agentData.getListingInformation().get(selectedListingPosition);

            homeId = selectedListing.getId();
        }else{
            homeId = "";
        }

    }

    public void addYoutubeLink(){

        Thread networkThread = new Thread() {

            public void run() {

                String strYoutubeId = "";
                String strVideoTitle = "";
                String strAllowOnMLS = "";

                if(!editYoutubeURL.getText().toString().isEmpty()){
                    strYoutubeId = editYoutubeURL.getText().toString();
                }

                if(!editVideoTitle.getText().toString().isEmpty()){
                    strVideoTitle = editVideoTitle.getText().toString();
                }

                if(chkAllowOnMLS.isChecked()){
                    strAllowOnMLS = "on";
                }

                Log.v("homeId: ", String.valueOf(homeId));
                Log.v("strYoutubeId: ", strYoutubeId);
                Log.v("strVideoTitle: ", String.valueOf(strVideoTitle));
                Log.v("strAllowOnMLS: ", String.valueOf(strAllowOnMLS));
                Log.v("realtorId: ", String.valueOf(agentData.getRealtor().getId()));

                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog = ProgressDialog.show(ListingYoutubeURL.this, "", "Saving...");
                        }
                    });

                    String BASE_URL = "http://stag-mobile.circlepix.com/api/listing.php";
                  // String BASE_URL = "http://keuahn.circlepix.dev/api/listing.php";
                    MultipartBody.Builder buildernew = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("method", "addYoutubeURL")
                            .addFormDataPart("realtorId", agentData.getRealtor().getId())
                            .addFormDataPart("tourID", homeId)
                            .addFormDataPart("youtubeLink", strYoutubeId)
                            .addFormDataPart("title", strVideoTitle)
                            .addFormDataPart("mlsCompliant", strAllowOnMLS);


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
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //stuff that updates ui
                                                mProgressDialog.dismiss();
                                            }
                                        });
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

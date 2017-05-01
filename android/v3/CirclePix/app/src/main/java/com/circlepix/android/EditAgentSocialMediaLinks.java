package com.circlepix.android;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.circlepix.android.beans.AgentData;

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
 * Created by keuahnlumanog on 11/12/2016.
 */

public class EditAgentSocialMediaLinks extends AppCompatActivity {

    private AgentData agentData;

    private TextView toolBarSave;
    private EditText agentFacebookUrl, agentTwitterUrl, agentYoutubeUrl, agentLinkedInUrl, agentPinterestUrl, agentBloggerUrl, agentGalleryLink;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_agent_social_media_links);

        // Get global shared data
        agentData = AgentData.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolBarSave = (TextView) findViewById(R.id.toolbar_save);
        toolBarSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
             //   Toast.makeText(getApplicationContext(), "Note: API is not done yet", Toast.LENGTH_SHORT).show();
                editSocialMediaLinks();
            }
        });

        agentFacebookUrl = (EditText) findViewById(R.id.editText_facebook);
        agentTwitterUrl = (EditText) findViewById(R.id.editText_twitter);
        agentYoutubeUrl = (EditText) findViewById(R.id.editText_youtube);
        agentLinkedInUrl = (EditText) findViewById(R.id.editText_linkedin);
        agentPinterestUrl = (EditText) findViewById(R.id.editText_pinterest);
        agentBloggerUrl = (EditText) findViewById(R.id.editText_blog);

        // Social Media Links
        if(!agentData.getAgentProfileInformation().getFacebookURL().equals(null) || !agentData.getAgentProfileInformation().getFacebookURL().isEmpty()){
            agentFacebookUrl.setText(agentData.getAgentProfileInformation().getFacebookURL());
            agentFacebookUrl.setSelection(agentFacebookUrl.getText().length());
        }else{
            agentFacebookUrl.setText("");
        }

        if(!agentData.getAgentProfileInformation().getYoutubeURL().equals(null) || !agentData.getAgentProfileInformation().getYoutubeURL().isEmpty()){
            agentYoutubeUrl.setText(agentData.getAgentProfileInformation().getYoutubeURL());
            agentYoutubeUrl.setSelection(agentYoutubeUrl.getText().length());
        }else{
            agentYoutubeUrl.setText("");
        }

        if(!agentData.getAgentProfileInformation().getBlogURL().equals(null) || !agentData.getAgentProfileInformation().getBlogURL().isEmpty()){
            agentBloggerUrl.setText(agentData.getAgentProfileInformation().getBlogURL());
            agentBloggerUrl.setSelection(agentBloggerUrl.getText().length());
        }else{
            agentBloggerUrl.setText("");
        }

        if(!agentData.getAgentProfileInformation().getLinkedinURL().equals(null)||  !agentData.getAgentProfileInformation().getLinkedinURL().isEmpty()){
            agentLinkedInUrl.setText(agentData.getAgentProfileInformation().getLinkedinURL());
            agentLinkedInUrl.setSelection(agentLinkedInUrl.getText().length());
        }else{
            agentLinkedInUrl.setText("");
        }

        if(!agentData.getAgentProfileInformation().getPinterestURL().equals(null) || !agentData.getAgentProfileInformation().getPinterestURL().isEmpty()){
            agentPinterestUrl.setText(agentData.getAgentProfileInformation().getPinterestURL());
            agentPinterestUrl.setSelection(agentPinterestUrl.getText().length());
        }else{
            agentPinterestUrl.setText("");
        }

        if(!agentData.getAgentProfileInformation().getTwitterURL().equals(null) || !agentData.getAgentProfileInformation().getTwitterURL().isEmpty()){
            agentTwitterUrl.setText(agentData.getAgentProfileInformation().getTwitterURL());
            agentTwitterUrl.setSelection(agentTwitterUrl.getText().length());
        }else{
            agentTwitterUrl.setText("");
        }
    }

    public void editSocialMediaLinks(){

        Thread networkThread = new Thread() {

            public void run() {


                String strFacebookURL = "";
                String strTwitterURL = "";
                String strYoutubeURL = "";
                String strBloggerURL = "";
                String strLinkedInURL = "";
                String strPinterest = "";

                if(!agentFacebookUrl.getText().toString().isEmpty()){
                    strFacebookURL = agentFacebookUrl.getText().toString();
                }else{
                    strFacebookURL = "";
                }

                if(!agentTwitterUrl.getText().toString().isEmpty()){
                    strTwitterURL = agentTwitterUrl.getText().toString();
                }else{
                    strTwitterURL = "";
                }

                if(!agentYoutubeUrl.getText().toString().isEmpty()){
                    strYoutubeURL = agentYoutubeUrl.getText().toString();
                }else{
                    strYoutubeURL = "";
                }

                if(!agentBloggerUrl.getText().toString().isEmpty()){
                    strBloggerURL = agentBloggerUrl.getText().toString();
                }else{
                    strBloggerURL = "";
                }

                if(!agentLinkedInUrl.getText().toString().isEmpty()){
                    strLinkedInURL = agentLinkedInUrl.getText().toString();
                }else{
                    strLinkedInURL = "";
                }

                if(!agentPinterestUrl.getText().toString().isEmpty()){
                    strPinterest = agentPinterestUrl.getText().toString();
                }else{
                    strPinterest = "";
                }


                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog = ProgressDialog.show(EditAgentSocialMediaLinks.this, "", "Saving...");
                        }
                    });

                    String BASE_URL = "http://stag-mobile.circlepix.com/api/agentProfile.php";
                    //String BASE_URL = "http://keuahn.circlepix.dev/api/agentProfile.php";
                    MultipartBody.Builder buildernew = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("method", "updateAgentInfo")
                            .addFormDataPart("realtorId", agentData.getRealtor().getId())
                            .addFormDataPart("form", "socialMediaLinks")
                            .addFormDataPart("facebookURL", strFacebookURL)
                            .addFormDataPart("twitterURL", strTwitterURL)
                            .addFormDataPart("youtubeURL", strYoutubeURL)
                            .addFormDataPart("blogURL", strBloggerURL)
                            .addFormDataPart("linkedinURL", strLinkedInURL)
                            .addFormDataPart("pinterestURL", strPinterest);


                    MultipartBody requestBody = buildernew.build();

                    Request request = new Request.Builder()
                            .url(BASE_URL)
                            .post(requestBody)
                            .build();

                    Log.v("update social media links: ", String.valueOf(requestBody));
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

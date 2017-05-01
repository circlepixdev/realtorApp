package com.circlepix.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.circlepix.android.beans.AgentData;
import com.circlepix.android.helpers.Globals;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.R.attr.name;

/**
 * Created by keuahnlumanog on 11/12/2016.
 */

public class EditAgentCredentialsActivity extends AppCompatActivity {

    private AgentData agentData;

    private TextView toolBarSave, currentUsername;
    private EditText ediTextNewUsername, editTextNewPassword, editTextConfirmPassword;
    private String newUsername, newPassword, confirmPassword;
    private ProgressDialog mProgressDialog;
    private String tempUserName = "";
    private Button saveUserNameBtn, savePasswordBtn;
    private String updateCredentialStr = "";
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_agent_credentials);

        // Get global shared data
        agentData = AgentData.getInstance();
        prefs = getSharedPreferences(AccountActivity.PREFS_NAME, 0);

        currentUsername = (TextView) findViewById(R.id.current_username_textview);
        ediTextNewUsername = (EditText) findViewById(R.id.editText_new_username);
        editTextNewPassword = (EditText) findViewById(R.id.editText_new_password);
        editTextConfirmPassword = (EditText) findViewById(R.id.editText_confirm_password);
        saveUserNameBtn = (Button) findViewById(R.id.save_username_button);
        savePasswordBtn = (Button) findViewById(R.id.save_password_button);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

      /*  toolBarSave = (TextView) findViewById(R.id.toolbar_save);
        toolBarSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(editTextNewPassword.getText().toString().equals(editTextConfirmPassword.getText().toString())){
                 //   Toast.makeText(getApplicationContext(), editTextNewPassword.getText() + " " + editTextConfirmPassword.getText(), Toast.LENGTH_SHORT).show();
                    editAgentCredentials();
                }else{
                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            }
        });*/


        String restoredUsername = Uri.decode(prefs.getString("username", null));
        tempUserName = restoredUsername;

        // a SpannableStringBuilder containing text to display
        SpannableStringBuilder sb = new SpannableStringBuilder("Your current username for CirclePix is " + tempUserName + ". You may change the username below. Note that usernames must be more than four characters long.");

        // create a bold StyleSpan to be used on the SpannableStringBuilder
        StyleSpan b = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold

        // set only the name part of the SpannableStringBuilder to be bold --> 16, 16 + name.length()
        sb.setSpan(b, 39, 39 + restoredUsername.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        currentUsername.setText(sb); // set the TextView to be the SpannableStringBuilder


        savePasswordBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (editTextNewPassword.getText().toString().length() >= 4){
                    if(editTextNewPassword.getText().toString().equals(editTextConfirmPassword.getText().toString())){
                        updateCredentialStr = "updatePassword";
                        editAgentCredentials();
                    }else{
                        updateCredentialStr = "";
                        showAlertDialog("Change Password", "Passwords do not match.");
                    }
                }else{
                    updateCredentialStr = "";
                    showAlertDialog("Change Password", "Password should be at least 4 characters.");
                }

            }
        });

        saveUserNameBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (ediTextNewUsername.getText().toString().length() >= 4){
                    if(ediTextNewUsername.getText().toString().equals(tempUserName)){
                        updateCredentialStr = "";
                        showAlertDialog("Change Username", "New username should not be the same with current username.");
                    }else{
                        updateCredentialStr = "updateUserName";
                        editAgentCredentials();
                    }
                }else{
                    updateCredentialStr = "";
                    showAlertDialog("Change Username", "Username should be at least 4 characters.");
                }

            }
        });

    }

    public void editAgentCredentials(){

        Thread networkThread = new Thread() {

            public void run() {


                if(!ediTextNewUsername.getText().toString().isEmpty()){
                    newUsername = ediTextNewUsername.getText().toString();
                }else{
                    newUsername = "";
                }
                if(!editTextNewPassword.getText().toString().isEmpty()){
                    newPassword = editTextNewPassword.getText().toString();
                }else{
                    newPassword = "";
                }

                if(!editTextConfirmPassword.getText().toString().isEmpty()){
                    confirmPassword = editTextConfirmPassword.getText().toString();
                }else{
                    confirmPassword = "";
                }

                Log.v("newUsername", newUsername);
                Log.v("newPassword", newPassword);
                Log.v("confirmPassword",confirmPassword);


                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog = ProgressDialog.show(EditAgentCredentialsActivity.this, "", "Saving...");
                        }
                    });

                    String BASE_URL = "http://stag-mobile.circlepix.com/api/agentProfile.php";
                    //    String BASE_URL = "http://keuahn.circlepix.dev/api/agentProfile.php";

//
//                    MultipartBody.Builder buildernew;
//
//                    if(!updateCredentialStr.isEmpty() && updateCredentialStr.equals("updateUserName")){
//                        buildernew = new MultipartBody.Builder()
//                            .setType(MultipartBody.FORM)
//                            .addFormDataPart("method", "updateAgentCredentials")
//                            .addFormDataPart("realtorId", agentData.getRealtor().getId())
//                            .addFormDataPart("username", newUsername);
//
//                    }else if(!updateCredentialStr.isEmpty() && updateCredentialStr.equals("updatePassword")){
//
//                        buildernew = new MultipartBody.Builder()
//                            .setType(MultipartBody.FORM)
//                            .addFormDataPart("method", "updateAgentCredentials")
//                            .addFormDataPart("realtorId", agentData.getRealtor().getId())
//                            .addFormDataPart("password", confirmPassword);
//                    }
//
//
//                    MultipartBody requestBody = buildernew.build();

                    MultipartBody.Builder buildernew;


                    if(!updateCredentialStr.isEmpty() && updateCredentialStr.equals("updateUserName")){
                        buildernew = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("method", "updateAgentCredentials")
                            .addFormDataPart("realtorId", agentData.getRealtor().getId())
                            .addFormDataPart("username", newUsername);

                    }else {

                        buildernew = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("method", "updateAgentCredentials")
                            .addFormDataPart("realtorId", agentData.getRealtor().getId())
                            .addFormDataPart("password", confirmPassword);

                    }


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
                                    updateCredentialStr = "";
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
                                    //    JSONObject returnedHomeId =  Jobject.getJSONObject("data");

                                        status = Jobject.getString("status");
                                        message = Jobject.getString("message");

                                        Log.v("status: ", status);
                                        Log.v("message: ", message);

                                        if(!updateCredentialStr.isEmpty() && updateCredentialStr.equals("updateUserName")){
                                            SharedPreferences.Editor editor = prefs.edit();
                                            editor.putString("username", newUsername);
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                                                editor.apply();
                                            } else {
                                                editor.commit();
                                            }
                                            Log.v("username oi: ", Uri.decode(prefs.getString("username", null)));
                                            updateCredentialStr = "";

                                        }else{
                                            SharedPreferences.Editor editor = prefs.edit();
                                            editor.putString("password", confirmPassword);
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                                                editor.apply();
                                            } else {
                                                editor.commit();
                                            }
                                            Log.v("password oi: ",Uri.decode(prefs.getString("password", null)));
                                            updateCredentialStr = "";
                                        }


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
                                        //stuff that updates ui
                                        mProgressDialog.dismiss();
                                        updateCredentialStr = "";
                                    }
                                });
                                finish();
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    updateCredentialStr = "";
                }
            }
        };
        networkThread.start();
    }


    private void showAlertDialog(String title, String msg){
        final AlertDialog.Builder alert = new AlertDialog.Builder(EditAgentCredentialsActivity.this);
        alert.setTitle(title);
        alert.setMessage(msg);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Do nothing
                dialog.dismiss();
            }
        });
        alert.show();
    }
}

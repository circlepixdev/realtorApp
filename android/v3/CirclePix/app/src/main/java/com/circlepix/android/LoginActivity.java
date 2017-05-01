package com.circlepix.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.circlepix.android.beans.AgentData;
import com.circlepix.android.beans.AgentProfile;
import com.circlepix.android.beans.ApplicationSettings;
import com.circlepix.android.helpers.Globals;
import com.circlepix.android.helpers.LoginHelper;
import com.circlepix.android.types.ApiResultType;
import com.circlepix.android.types.NarrationType;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LoginActivity extends Activity{

    private AgentData agentData;
    private CirclePixAppState appState;
    private Button mLoginButton;
    private EditText mUsername;
    private EditText mPassword;
    private TextView mForgotPassword;
 //   private TextView mNoAccount;
    private ProgressDialog dialog;
    private LinearLayout rootLayout;
    private LoginHandler handler;
    protected Activity activity;
 //   public static boolean loginActivityVisible;



    protected InitTask initTask;

    private boolean rememberMe;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        agentData = AgentData.getInstance();

        // Setup application class
        appState = ((CirclePixAppState)getApplicationContext());
        appState.setContextForPreferences(this);
        appState.setLoginActivityVisible(true);

        handler = new LoginHandler(this);
        activity = this;

        // Restore preferences
        final SharedPreferences prefs = getSharedPreferences(AccountActivity.PREFS_NAME, 0);
        String restoredUsername = prefs.getString("username", null);
        String restoredPassword = prefs.getString("password", null);
        rememberMe = prefs.getBoolean("rememberMe", false);
        long userToken = prefs.getLong("userToken", 0);

        mLoginButton = (Button) findViewById(R.id.login_button);
        mForgotPassword = (TextView) findViewById(R.id.forgot_password);
    //    mNoAccount = (TextView) findViewById(R.id.no_account);
        mUsername = (EditText) findViewById(R.id.login_username);
        mUsername.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);

        mPassword = (EditText) findViewById(R.id.login_password);
        mPassword.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        mPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // do your stuff here
                    mLoginButton.performClick();
                    mLoginButton.setPressed(true);
                    mLoginButton.invalidate();
                }
                return false;
            }
        });

        rootLayout = (LinearLayout) findViewById(R.id.login_root);

        // Setup Listeners
       // rootLayout.setOnTouchListener(this);

        mLoginButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if(isNetworkAvailable()){
                    // Form validation
                    if (mUsername.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(),
                                "Please Enter a Username", Toast.LENGTH_SHORT)
                                .show();
                        return;
                    } else if (mPassword.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(),
                                "Please Enter a Password", Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }

                    initTask = new InitTask();
                    initTask.execute(LoginActivity.this);
                    dialog = ProgressDialog.show(LoginActivity.this, "", "Loading...");
                    Thread networkThread = new Thread() {

                        public void run() {

                            String un = Uri.encode(mUsername.getText().toString());
                            String pw = Uri.encode(mPassword.getText().toString());

                            LoginHelper loginHelper = new LoginHelper();
                            boolean result = loginHelper.silentAuth(un, pw, prefs);

                            Log.v("Login result", String.valueOf(result));

                           // LoginHelper loginHelper = new LoginHelper();
                           // loginHelper.testLocalAPI();

                            loadApplicationSettings();
                            dialog.dismiss();
                            initTask.cancel(true);
                            if (result) {
                                //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                appState.setFirstRun(true);
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                intent.putExtra("isStartup", true);
                                startActivity(intent);
                                finish();
                                appState.setLoginActivityVisible(false);


                            } else {
                                if (Globals.LAST_API_RESULT == ApiResultType.DOWN) {
                                    sendToast("The CirclePix server is currently down for maintenance, please try again later.");
                                } else if (Globals.LAST_API_RESULT == ApiResultType.FAILED) {
                                    sendToast("Invalid Login");
                                } else {
                                    sendToast("Unknown error. Please try again later.");
                                }
                            }
                            LoginActivity.this.handler.sendEmptyMessage(0);
                        }
                    };
                    networkThread.start();

                }else{
                    sendToast("Please check your internet connection.");
                }
            }
        });

        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the user email address
                LayoutInflater li = LayoutInflater.from(LoginActivity.this);
                View promptsView = li.inflate(R.layout.forgot_password_dialog, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        LoginActivity.this);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.email_address);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Submit",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        // Post the email address to the forgot_password page
                                        postToForgotPassword(userInput.getText().toString());
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });

       /* mNoAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebPage("https://www.circlepix.com");
            }
        });*/


        // Check to see if account credentials are remembered
        // NEW: If the user is offline we could use a time token and display stored data


        if (rememberMe == true) {
            // NEW: If the user is offline we could use a time token and display stored data
            if (restoredUsername != null)
            {
                mUsername.setText(restoredUsername);
                Globals.USERNAME = restoredUsername;
            }
            if (restoredPassword != null)
            {
                mPassword.setText(restoredPassword);
                Globals.PASSWORD = restoredPassword;
            }


            //we will just direct it to MainActivity - it will be the one to handle internet connection issues
            //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//            loadApplicationSettings();
//
//            appState.setLoginActivityVisible(false);
//            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//            startActivity(intent);


            if (isNetworkAvailable()) {

                loadApplicationSettings();

                appState.setLoginActivityVisible(false);
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);

            }else{
                Date now = new Date();
                boolean isTokenExpired = now.getTime() > (userToken + Globals.TOKEN_EXPIRY);

                if (isTokenExpired) {
                    // Alert the user that they must get a connection
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle("No Connection");
                    alert.setMessage("Currently there is no network connection and it has been too long since you last logged in. Please connect to a network and log in.");
                    alert.setCancelable(false);
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Do nothing
                        }
                    });
                    alert.show();
                } else {
                    // Allow offline mode
                    final String oldResponseString = prefs.getString("lastResponse",
                            "<response><status>1</status><realtor></realtor><listings></listings></response>");
                    AgentData ad = AgentData.getInstance();
                    ad.setOfflineMode(true);
                    ad.parseResponseString(oldResponseString);

                    // Alert the user that they will have limited functionaliity
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle("No Connection");
                    alert.setMessage("Currently there is no network connection. You can use the app but some functionality will be disabled.");
                    alert.setCancelable(false);
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            loadApplicationSettings();

                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.putExtra("isStartup", true);
                            startActivity(intent);

                            appState.setLoginActivityVisible(false);

                            finish();
                        }
                    });
                    alert.show();
                }
            }


          /*  if (isNetworkAvailable()) {
                // Simulate a button click
              //  mLoginButton.performClick();


                loadApplicationSettings();
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
            } else {
                // Handle the offline case
                Date now = new Date();
                boolean isTokenExpired = now.getTime() > (userToken + Globals.TOKEN_EXPIRY);

                if (isTokenExpired) {
                    // Alert the user that they must get a connection
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle("No Connection");
                    alert.setMessage("Currently there is no network connection and it has been too long since you last logged in. Please connect to a network and log in.");
                    alert.setCancelable(false);
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Do nothing
                        }
                    });
                    alert.show();
                } else {
                    // Allow offline mode
                    final String oldResponseString = prefs.getString("lastResponse",
                            "<response><status>1</status><realtor></realtor><listings></listings></response>");
                    AgentData ad = AgentData.getInstance();
                    ad.setOfflineMode(true);
                    ad.parseResponseString(oldResponseString);

                    // Alert the user that they will have limited functionaliity
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle("No Connection");
                    alert.setMessage("Currently there is no network connection. You can use the app but some functionality will be disabled.");
                    alert.setCancelable(false);
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.putExtra("isStartup", true);
                            startActivity(intent);
                            finish();
                        }
                    });
                    alert.show();
                }
            }*/

        }

        Log.d("LOGIN ACTIVITY", "restored rememberme is " + rememberMe);
        Log.d("LOGIN ACTIVITY", "restored username is " + restoredUsername);
        Log.d("LOGIN ACTIVITY", "restored password is " + restoredPassword);
    }

    private void loadApplicationSettings() {

        SharedPreferences sharedPreferences = getSharedPreferences(Globals.PREFS_APP_SETTINGS, 0);
        Gson gson = new Gson();
        String fromJson = sharedPreferences.getString(Globals.PREFS_APP_SETTINGS, "");
        ApplicationSettings settings = gson.fromJson(fromJson, ApplicationSettings.class);

        if (settings == null) {
            ApplicationSettings appSettings = new ApplicationSettings();
            appSettings.setDisplayCompanyLogo(true);
            appSettings.setDisplayCompanyName(true);
            appSettings.setDisplayAgentImage(true);
            appSettings.setDisplayAgentName(true);
            appSettings.setNarration(NarrationType.female);

            String toJsonObject = gson.toJson(appSettings);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Globals.PREFS_APP_SETTINGS, toJsonObject);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                editor.apply();
            } else {
                editor.commit();
            }
        }

    }
    /**
     * Toasting method
     */
    public void sendToast(final String msg) {

        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(activity, "" + msg, Toast.LENGTH_LONG).show();
            }
        });

    }

    /**
     * Login handler
     */
    class LoginHandler extends Handler {

        private LoginActivity parent;

        public LoginHandler(LoginActivity parent) {
            this.parent = parent;
        }

        public void handleMessage(Message msg) {
            parent.handleMessage(msg);
        }
    }

    public void handleMessage(Message msg) {
        switch (msg.what) {
            case 0:
                // Update UI here
                break;
        }
    }

    /**
     * Async task subclass used for the loading screen
     * sub-class of AsyncTask
     */
    @SuppressLint("NewApi")
    protected class InitTask extends AsyncTask<Context, Integer, String>
    {
        // -- run intensive processes here
        // -- notice that the datatype of the first param in the class definition matches the param passed to this method
        // -- and that the datatype of the last param in the class definition matches the return type of this mehtod
        @Override
        protected String doInBackground( Context... params )
        {

            //-- on every iteration
            //-- runs a while loop that causes the thread to sleep for 50 milliseconds
            //-- publishes the progress - calls the onProgressUpdate handler defined below
            //-- and increments the counter variable i by one
            int i = 0;
            while( i <= 50 )
            {
                try{
                    Thread.sleep( 50 );
                    publishProgress( i );
                    i++;
                } catch( Exception e ){
                    Log.i("makemachine", (e.getMessage() == null) ? e.getMessage() : e.toString());
                }
            }
            return "COMPLETE!";
        }

        // -- gets called just before thread begins
        @Override
        protected void onPreExecute()
        {
            Log.i( "makemachine", "onPreExecute()" );
            super.onPreExecute();

        }

        // -- called from the publish progress
        // -- notice that the datatype of the second param gets passed to this method
        @Override
        protected void onProgressUpdate(Integer... values)
        {
            super.onProgressUpdate(values);
            Log.i( "makemachine", "onProgressUpdate(): " +  String.valueOf( values[0] ) );
        }

        // -- called if the cancel button is pressed
        @Override
        protected void onCancelled()
        {
            super.onCancelled();
            Log.i( "makemachine", "onCancelled()" );
        }

        // -- called as soon as doInBackground method completes
        // -- notice that the third param gets passed to this method
        @Override
        protected void onPostExecute( String result )
        {
            super.onPostExecute(result);
            Log.i( "makemachine", "onPostExecute(): " + result );
            //_cancelButton.setVisibility( View.INVISIBLE );
        }
    }

    public static final int getColor(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            return ContextCompat.getColor(context, id);
        } else {
            return context.getResources().getColor(id);
        }
    }
/*    *//**
     * Used to close keyboard when user clicks away from edittext
     *//*
    public boolean onTouch(View v, MotionEvent event) {

        if (v ==  rootLayout) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mUsername.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(mPassword.getWindowToken(), 0);
            return true;
        }
        return false;
    }*/
    /**
     * Used to close keyboard when user clicks away from edittext
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        View v = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (v instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            Log.d("Activity", "Touch event "+event.getRawX()+","+event.getRawY()+" "+x+","+y+" rect "+w.getLeft()+","+w.getTop()+","+w.getRight()+","+w.getBottom()+" coords "+scrcoords[0]+","+scrcoords[1]);
            if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom()) ) {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }


    protected void onStop() {
        setResult(2);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        setResult(2);
        super.onDestroy();
    }

    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void postToForgotPassword(final String email) {
        // Post it to circlepix
        Log.d("LoginActivity", "Posting forgot_password for " + email);

        Thread networkThread = new Thread() {

            public void run() {

                //String em = Uri.encode(email);

                String url = "https://www.circlepix.com/forgot_password.php";

                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("?email", email)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    Log.d("Response string", "" + response);
                    // Do something with the response.
                } catch (IOException e) {
                    Log.d("LoginActivity", "Error posting to forgot_password.php", e);
                }

              /*  HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url + "?email=" + email);

                HttpResponse response;
                try {
                    // DEBUG
                    System.out.println( "executing request " + httppost.getRequestLine());
                    response = httpclient.execute(httppost);
                    // DEBUG
                    Log.d("Response string", "" + response.getStatusLine());
                } catch (Exception e) {

                    Log.d("LoginActivity", "Error posting to forgot_password.php", e);
                } finally {
                    httpclient.getConnectionManager( ).shutdown( );
                }*/
            }
        };
        networkThread.start();

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected())
        {
            isAvailable = true;

        }
        return isAvailable;
    }


    //moveTaskToBack(true) leaves your backstack as it is, just puts all Activities to background (same as if user pressed Home button).
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
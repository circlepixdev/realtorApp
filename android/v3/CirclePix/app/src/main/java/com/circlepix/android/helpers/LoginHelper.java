package com.circlepix.android.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.circlepix.android.beans.AgentData;
import com.circlepix.android.beans.AuthData;
import com.circlepix.android.data.Presentation;
import com.circlepix.android.data.PresentationDataSource;
import com.circlepix.android.types.ApiResultType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginHelper {

    //public static final String BASE_URL = "http://videoupload.circlepix.com/thePearl/cpixVideoApp.xml?method=login&username=%s&password=%s";
    public static final String BASE_URL = "http://stag-mobile.circlepix.com/thePearl/cpixVideoApp.xml?method=login&username=%s&password=%s";

    /**
     * Perform a synchronous authorization without any ui interaction. This
     * method loads the results into global objects and returns a boolean
     * representing the overall result.
     *
     * @param username
     * @param password
     * @param prefs
     * @return true if authorized, otherwise false.
     */
    public boolean silentAuth(String username, String password, SharedPreferences prefs) {

        String responseString = callApi(username, password);

        if (responseString == null) {
            Globals.LAST_API_RESULT = ApiResultType.DOWN;
        }
        else if (responseString.contains("<status>1</status>")) {

            Globals.LAST_API_RESULT = ApiResultType.SUCCESS;
            Globals.USERNAME = username;
            Globals.PASSWORD = password;
            Globals.REMEMBERME = true;

            // Make changes to preferences
            if (prefs != null) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("username", username);
                editor.putString("password", password);
                editor.putBoolean("rememberMe", true);
                editor.putString("lastResponse", responseString);
                Date now = new Date();
                editor.putLong("userToken", now.getTime());
                // Commit the edits!
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    editor.apply();
                } else {
                    editor.commit();
                }
            }

            AgentData ad = AgentData.getInstance();
            ad.setLoggedIn(true);
            ad.setOfflineMode(false);
            ad.parseResponseString(responseString);

            return true;
        } else if (responseString.contains("<status>-3</status>")) {
            Globals.LAST_API_RESULT = ApiResultType.FAILED;
            AgentData.getInstance().setLoggedIn(false);
        }
        else {
            Globals.LAST_API_RESULT = ApiResultType.UNKNOWN;
            AgentData.getInstance().setLoggedIn(false);
        }

        return false;
    }

    /**
     * Method to forget the user.
     *
     * @param prefs
     */
    public static void handleLogout(SharedPreferences prefs, Context context) {
        // Clear app globals
        AgentData.getInstance().setLoggedIn(false);
        AgentData.getInstance().setOfflineMode(false);
        Globals.PASSWORD = "";
        Globals.USERNAME = "";
        Globals.RESPONSE_STRING = "";

        // Clear the preferences
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("username");
        editor.remove("password");
        editor.remove("lastResponse");
        editor.remove("userToken");
        editor.remove("first_present_date");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            editor.apply();
        } else {
            editor.commit();
        }

        // Clear the database
        PresentationDataSource dao = new PresentationDataSource(context);
        dao.open(true);
        List<Presentation> presentations = dao.getAllPresentations();
        for (Presentation p : presentations) {
            dao.deletePresentation(p);
        }
        dao.close();

    }

    /**
     * Read the response object and return the response in the form of a string.
     *
     * @param response
     * @return
     */
    public static String request(Response response) {
        String result = null;

        try {
            InputStream in = response.body().byteStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder str = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                str.append(line + "\n");
            }
            in.close();
            result = str.toString();
        } catch (Exception ex) {
            result = "Error";
        }
        return result;
    }


    /**
     * Simple method to call the API and refresh the listing and realtor data.
     *
     * @param prefs
     */
    public void asyncListingData(SharedPreferences prefs) {
        // Use a simple Async class to refresh the listing data
        AuthData data = new AuthData();
        data.username = prefs.getString("username", null);
        data.password = prefs.getString("password", null);
        new ListingUpdater().execute(data);
    }

    private class ListingUpdater extends AsyncTask<AuthData, Void, Void> {

        @Override
        protected Void doInBackground(AuthData... params) {
            AuthData data = params[0];
            String responseString = callApi(data.username, data.password);

            if (responseString == null) {
                Globals.LAST_API_RESULT = ApiResultType.DOWN;
            }
            else if (responseString.contains("<status>1</status>")) {

                AgentData ad = AgentData.getInstance();
                ad.setLoggedIn(true);
                ad.setOfflineMode(false);
                ad.parseResponseString(responseString);
            }

            return null;
        }
    }

    /**
     * This basic helper method calls the API to authenticate and get user data
     * in the response.
     *
     * @param username
     * @param password
     * @return The response or null
     */
    private String callApi(String username, String password) {

        String urlstring = String.format(BASE_URL, username, password);
        String responseString = null;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlstring)
                .build();

        try {

            Call call = client.newCall(request);
            Response response = call.execute();

            if (!response.isSuccessful()) {
                Log.i("Response code", " " + response.code());
            }

            responseString = LoginHelper.request(response);

            Log.i("Response code", response.code() + " ");
            Log.v("OkHTTP Results: ", responseString);

        } catch (IOException e) {
            Log.d("LOGCAT", "Login Failed!");
            responseString = "Unknown error. Please try again later.";

//            Log.e("LOGCAT", (e.getMessage() == null) ? e.getMessage() : e.toString());
        }

        return responseString;

    }

    //LocalHost test
    public void testLocalAPI() {

        String urlString = "http://10.6.0.114:80/fileUploadExample/test.php";
        String responseString = null;


        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlString)
                .build();

        try {

            Call call = client.newCall(request);
            Response response = call.execute();

            if (!response.isSuccessful()) {
                Log.i("Response code", " " + response.code());
            }

            responseString = LoginHelper.request(response);

            Log.i("Response code", response.code() + " ");
            Log.v("OkHTTP Results: ", responseString);

        } catch (IOException e) {
            Log.d("LOGCAT", "Login Failed!");
            Log.e("LOGCAT", (e.getMessage() == null) ? e.getMessage() : e.toString());
        }

      //  return responseString;

    }
}
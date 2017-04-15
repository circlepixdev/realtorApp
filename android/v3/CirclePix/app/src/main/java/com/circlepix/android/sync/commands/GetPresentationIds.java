package com.circlepix.android.sync.commands;

import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Keuahn on 9/1/2016.
 */
public class GetPresentationIds {

    public static void runCommand(String realtorId){
        String BASE_URL = "http://www.circlepix.com/api/getPresentationIDs.php?realtorId=%s";
        Log.v("realtorId =  ", realtorId);
        String urlstring = String.format(BASE_URL, realtorId);
        Log.v("GetPresentationIds url ", urlstring);
    //    String responseString = null;


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

     //       responseString = LoginHelper.request(response);

            Log.i("Response code", response.code() + " ");
     //       Log.v("OkHTTP Results: ", responseString);

        } catch (IOException e) {
            Log.d("LOGCAT", "Login Failed!");
            Log.e("error: ", e.getLocalizedMessage());
      //      responseString = "Unknown error. Please try again later.";

      //      Log.e("LOGCAT", (e.getMessage() == null) ? e.getMessage() : e.toString());
        }

     //   Log.v("response string: ", responseString);

    }

}

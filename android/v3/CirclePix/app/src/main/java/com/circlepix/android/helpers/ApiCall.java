package com.circlepix.android.helpers;

import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;;
/**
 * Created by Keuahn on 7/7/2016.
 */
public class ApiCall {
    public static String responseString  = null;

    //GET network request
    public static String GET(OkHttpClient client, String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    //POST network request                         //HttpUrl
    public static String POST(OkHttpClient client, String url, RequestBody body) throws IOException {

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
      //  Response response = client.newCall(request).execute();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("Video Upload", "Failed");
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        responseString = response.body().string();
                        Log.i("Video Upload", responseString);
                        response.body().close();

                    }
                } else {

                    switch (response.code()){
                        case 401:
                            responseString="HTTP_UNAUTHORIZED";
                            break;
                    }
                }
            }
        });
        return responseString;
    }
}
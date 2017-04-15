package com.circlepix.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by Keuahn on 9/17/2016.
 */
public class ApiTest extends AppCompatActivity {

    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_test);


     /*   result = (TextView) findViewById(R.id.result);


        try {
            String BASE_URL = "http://ly.circlepix.dev/api/listing.php";
            final String urlString = String.format(BASE_URL);

            //    final OkHttpClient client = new OkHttpClient();


            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("method", "addListing")
                    .addFormDataPart("address", "F.Cabahug St. Cebu City, Cebu")
                    .build();

            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .post(requestBody)
                    .build();

            OkHttpClient client = new OkHttpClient();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    Log.i("Failed: " ,  e.getLocalizedMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //stuff that updates ui
                            result.setPadding(25,25,25,25);
                            result.setText("Failed: "+ e.getLocalizedMessage());
                        }
                    });
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {

                    final String respBody;
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            respBody = response.body().string();
                            Log.i("Done", respBody);
                            response.body().close();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //stuff that updates ui
                                    result.setPadding(25,25,25,25);
                                    result.setText("Success: "+ respBody);
                                }
                            });
                        }
                    } else {
                        Log.i("Error", "unsuccessful");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //stuff that updates ui
                                result.setPadding(25,25,25,25);
                                result.setText("Error: "+ "unsuccessful");
                            }
                        });
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
*/
    }
}

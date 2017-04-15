package com.circlepix.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

/**
 * Created by Keuahn on 9/22/2016.
 */

public class AddListingImagesActivity extends AppCompatActivity{

    private TextView responseBody;
    private  String responseString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_api_test);
        setContentView(R.layout.activity_add_listing_images);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            responseString = extras.getString("responseBody");
        }

        responseBody = (TextView) findViewById(R.id.response_text);
        responseBody.setText(responseString);

    }

}

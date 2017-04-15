package com.circlepix.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


/**
 * Created by keuahnlumanog1 on 4/21/16.
 */
public class LeadSelectedActivity extends AppCompatActivity {

    private ImageView call_btn;
    private ImageView sms_btn;
    private ImageView email_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leads_selected);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        call_btn = (ImageView) findViewById(R.id.action_call);
        sms_btn = (ImageView) findViewById(R.id.action_sms);
        email_btn = (ImageView) findViewById(R.id.action_email);

        addOnButtonClickListener();

    }


    public void addOnButtonClickListener(){
        call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(), "Call pressed", Toast.LENGTH_SHORT).show();
            }
        });

        sms_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(), "SMS pressed", Toast.LENGTH_SHORT).show();
            }
        });

        email_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(), "Email pressed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

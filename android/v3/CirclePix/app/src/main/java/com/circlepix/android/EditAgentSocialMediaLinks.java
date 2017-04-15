package com.circlepix.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by keuahnlumanog on 11/12/2016.
 */

public class EditAgentSocialMediaLinks extends AppCompatActivity {

    private TextView toolBarSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_agent_social_media_links);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolBarSave = (TextView) findViewById(R.id.toolbar_save);
        toolBarSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Note: API is not done yet", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

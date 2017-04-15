package com.circlepix.android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by relly on 3/5/15.
 */
public class GlobalSettingsThemeActivity extends Activity {


    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_settings_theme);
        setTitle("");

    }
}

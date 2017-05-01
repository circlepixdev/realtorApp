package com.circlepix.android.presentations;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by relly on 3/12/15.
 */
public abstract class PresentationBase extends AppCompatActivity {

    public abstract void pauseAnimation();
    public abstract void resumeAnimation();

}

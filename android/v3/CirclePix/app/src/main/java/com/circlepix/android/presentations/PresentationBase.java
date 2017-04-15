package com.circlepix.android.presentations;

import android.app.Activity;

import java.io.Serializable;

/**
 * Created by relly on 3/12/15.
 */
public abstract class PresentationBase extends Activity {

    public abstract void pauseAnimation();
    public abstract void resumeAnimation();

}

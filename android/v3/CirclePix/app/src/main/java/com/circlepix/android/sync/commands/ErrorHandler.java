package com.circlepix.android.sync.commands;

import android.util.Log;

import retrofit.RetrofitError;

/**
 * Created by sburns.
 */
public class ErrorHandler {

    private ErrorHandler() {
        /* intentionally blank - no instance needed. */
    }

    public static void log(final String subject, final String error) {
        Log.e(subject, error);
    }

    public static void log(RetrofitError error) {
        log(String.format("There was an error calling endpoint (%s)", error.getUrl()), error.toString());
    }
}

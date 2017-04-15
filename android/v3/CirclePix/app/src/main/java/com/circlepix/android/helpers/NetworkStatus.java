package com.circlepix.android.helpers;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Keuahn on 6/16/2016.
 */
public class NetworkStatus {

    public static String getNetworkStatus(Activity activity) {
        String networkStatus;
        ConnectivityManager manager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()){
            networkStatus = "Connected";
        }else if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            networkStatus = "Connecting";
        }else{
            networkStatus = "No Internet Connection";
        }

        return networkStatus;
    }
}

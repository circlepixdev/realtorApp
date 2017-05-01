package com.circlepix.android.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Keuahn on 6/16/2016.
 */
public class NetworkUtil {

    public static int TYPE_CONNECTED = 1;
    public static int TYPE_WAITING_FOR_NETWORK = 2;
//    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    public static int getConnectivityStatus(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()){
            return TYPE_CONNECTED;
        }else if (networkInfo != null && networkInfo.isAvailable()) {
            return TYPE_WAITING_FOR_NETWORK;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static String getConnectivityStatusString(Context context) {
        int conn = NetworkUtil.getConnectivityStatus(context);
        String status = null;
        if (conn == NetworkUtil.TYPE_CONNECTED) {
            status = "Connected";
        } else if (conn == NetworkUtil.TYPE_WAITING_FOR_NETWORK) {
            status = "Waiting for Network";
        } else if (conn == NetworkUtil.TYPE_NOT_CONNECTED) {
            status = "Not connected to Internet";
        } return status;
    }
}
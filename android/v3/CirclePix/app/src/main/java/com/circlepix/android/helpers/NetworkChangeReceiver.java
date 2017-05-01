package com.circlepix.android.helpers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.circlepix.android.CirclePixAppState;
import com.circlepix.android.HomeActivity;
import com.circlepix.android.R;

/**
 * Created by Keuahn on 6/16/2016.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    private CirclePixAppState appState;
    private Activity activity;
    @Override
    public void onReceive(Context context, Intent intent) {
        // Setup application class
        appState = ((CirclePixAppState)context.getApplicationContext());
        appState.setContextForPreferences(context);

        String status = NetworkUtil.getConnectivityStatusString(context);

        Log.e("Receiver ", "" + status);
        Log.e("LoginActivity visible ", String.valueOf(appState.isLoginActivityVisible()));

        if (appState.isLoginActivityVisible() == false){
            if ((!HomeActivity.networkStatLayout.isShown()) ||(!HomeActivity.networkStatus.isShown())){
                HomeActivity.networkStatLayout.setVisibility(View.VISIBLE);
                HomeActivity.networkStatus.setVisibility(View.VISIBLE);
            }

            if (status.equals("Not connected to Internet")) {
                HomeActivity.networkStatBG.setBackgroundColor(ContextCompat.getColor(context,R.color.noInternetConn));
                HomeActivity.networkStatus.setText(status);
            }else if (status.equals("Waiting for Network")){
                HomeActivity.networkStatBG.setBackgroundColor(ContextCompat.getColor(context,R.color.waitingForNetwork));
                HomeActivity.networkStatus.setText(status);
            } else {
                HomeActivity.networkStatBG.setBackgroundColor(ContextCompat.getColor(context,R.color.connected));
                HomeActivity.networkStatus.setText(status);
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        //done
                        HomeActivity.networkStatLayout.setVisibility(View.GONE);
                        HomeActivity.networkStatus.setVisibility(View.GONE);
                    }

                }, 3500);

            }
        }
    }
}
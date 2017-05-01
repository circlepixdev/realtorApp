package com.circlepix.android.helpers;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.circlepix.android.R;
import com.circlepix.android.SettingsActivity;
import com.circlepix.android.interfaces.IBaseActionBarCallback;

/**
 * Created by relly on 4/16/15.
 */
public class BaseActionBar {

    private Context context;
    private View actionBarLayout;
    private ActionBar actionBar;
    private LinearLayout actionbar_back_linear;
    private LinearLayout actionbar_logout_linear;
    private LinearLayout actionbar_add_linear;
    private TextView actionbar_back_text;

    private Class className;

    public BaseActionBar(Context context) {
        this.context = context;

        className = context.getClass();

        actionBarLayout = ((AppCompatActivity)context).getLayoutInflater().inflate(R.layout.base_actionbar, null);
        actionBar = ((AppCompatActivity)context).getActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        actionbar_back_text = (TextView) actionBarLayout.findViewById(R.id.actionbar_back_text);
        actionbar_back_linear = (LinearLayout) actionBarLayout.findViewById(R.id.actionbar_back_linear);

        actionbar_logout_linear = (LinearLayout) actionBarLayout.findViewById(R.id.actionbar_logout_linear);
        actionbar_add_linear = (LinearLayout) actionBarLayout.findViewById(R.id.actionbar_add_linear);
    }


    public void setConfig(final Class backToClass, String backText, boolean showLogoutButton, boolean showAddButton, final IBaseActionBarCallback callback) {

        if (backToClass != null) {
            actionbar_back_linear.setVisibility(View.VISIBLE);
            actionbar_back_text.setText(backText);

            actionbar_back_linear.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("NewApi")
                @Override
                public void onClick(View v) {
                    callback.back();


                    if(!backToClass.equals(SettingsActivity.class)){

                   /*     if(className.equals(WizardMainActivity.class)){
                            Bundle bundle = ActivityOptions.makeCustomAnimation(context,
                                    R.anim.slide_in_right,
                                    R.anim.slide_out_right).toBundle();
                            bundle.putBoolean("cameFromWizardMainActivity", true);

                            Intent backIntent = new Intent(context, backToClass);
                            backIntent.putExtras(bundle);
                            context.startActivity(backIntent, bundle);
                        }else{*/
                            Bundle bundle = ActivityOptions.makeCustomAnimation(context,
                                    R.anim.slide_in_right,
                                    R.anim.slide_out_right).toBundle();

                            Intent backIntent = new Intent(context, backToClass);
                            context.startActivity(backIntent, bundle);

                   //    }

                    }

                }
            });
        } else {
            actionbar_back_linear.setVisibility(View.GONE);
        }



        if (showLogoutButton && !showAddButton) {
            actionbar_add_linear.setVisibility(View.GONE);
            actionbar_logout_linear.setVisibility(View.VISIBLE);
            actionbar_logout_linear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.logout();
                }
            });
        } else if (!showLogoutButton && showAddButton) {
            actionbar_logout_linear.setVisibility(View.GONE);
            actionbar_add_linear.setVisibility(View.VISIBLE);
            actionbar_add_linear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.add();
                }
            });
        } else if (!showLogoutButton && !showAddButton) {
            actionbar_logout_linear.setVisibility(View.GONE);
            actionbar_add_linear.setVisibility(View.GONE);
        }
    }


    public void show() {
        actionBar.setCustomView(actionBarLayout);
    }

}

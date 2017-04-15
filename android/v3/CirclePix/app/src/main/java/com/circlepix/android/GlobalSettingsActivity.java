package com.circlepix.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.circlepix.android.beans.AgentData;
import com.circlepix.android.beans.ApplicationSettings;
import com.circlepix.android.data.Presentation;
import com.circlepix.android.data.PresentationDataSource;
import com.circlepix.android.helpers.BaseActionBar;
import com.circlepix.android.helpers.Globals;
import com.circlepix.android.interfaces.IBaseActionBarCallback;
import com.circlepix.android.sync.commands.ErrorHandler;
import com.circlepix.android.sync.commands.UpdatePresentation;
import com.google.gson.Gson;

import java.util.Date;
import java.util.List;

/**
 * Created by relly on 4/29/15.
 */
public class GlobalSettingsActivity extends Activity {

    private LinearLayout narrationSettings;
    private Switch swCompanyLogo;
    private Switch swCompanyName;
    private Switch swAgentImage;
    private Switch swAgentInfo;

    //July 9, 2015: KBL
    private LinearLayout bgMusicSettings;
    private Switch swApplyToExistingPres;
    private List<Presentation> presentations;
    private int ACTIVITY_SETTINGS_NARRATION = 1;
    private int ACTIVITY_SETTINGS_BGMUSIC = 2;
    private boolean clickedSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_settings);

        // Show custom actionbar
        BaseActionBar actionBar = new BaseActionBar(GlobalSettingsActivity.this);
        actionBar.setConfig(SettingsActivity.class,
                "Save",
                false, false, new IBaseActionBarCallback.Null() {
                    @Override
                    public void back() {

                        checkSave();

                    }
                });
        actionBar.show();

        swApplyToExistingPres = (Switch) findViewById(R.id.swApplyToExistingPres);
        swCompanyLogo = (Switch) findViewById(R.id.swCompanyLogo);
        swCompanyName = (Switch) findViewById(R.id.swCompanyName);
        swAgentImage = (Switch) findViewById(R.id.swAgentImage);
        swAgentInfo = (Switch) findViewById(R.id.swAgentInfo);
        narrationSettings = (LinearLayout) findViewById(R.id.narrationSettingsRow);
        narrationSettings.setOnClickListener(narrationOnClick);
        bgMusicSettings = (LinearLayout) findViewById(R.id.bgmusicSettingsRow);
        bgMusicSettings.setOnClickListener(bgMusicOnClick);

        SharedPreferences sharedPreferences = getSharedPreferences(Globals.PREFS_APP_SETTINGS, 0);
        Gson gson = new Gson();
        String fromJson = sharedPreferences.getString(Globals.PREFS_APP_SETTINGS, "");
        ApplicationSettings appSettings = gson.fromJson(fromJson, ApplicationSettings.class);
        swApplyToExistingPres.setChecked(appSettings.isApplyToExistingPres());
        swCompanyLogo.setChecked(appSettings.isDisplayCompanyLogo());
        swCompanyName.setChecked(appSettings.isDisplayCompanyName());
        swAgentImage.setChecked(appSettings.isDisplayAgentImage());
        swAgentInfo.setChecked(appSettings.isDisplayAgentName());
    }

    @Override
    protected void onStop() {
        super.onStop();

       /* Log.v("clickedSave", String.valueOf(clickedSave));

        if(clickedSave == true){
            saveChanges();
            clickedSave = false;
        }

        Log.v("went onStop", "yeah");*/

    }

    private View.OnClickListener narrationOnClick = new View.OnClickListener() {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onClick(View v) {
            Bundle bundle = ActivityOptions.makeCustomAnimation(GlobalSettingsActivity.this,
                    R.anim.slide_in_left,
                    R.anim.slide_out_left).toBundle();

          //  Intent intent = new Intent(GlobalSettingsActivity.this, GlobalSettingsNarrationActivity.class);
          //  startActivity(intent, bundle);

            Intent wizardNextActivity = new Intent(GlobalSettingsActivity.this, GlobalSettingsNarrationActivity.class);
            wizardNextActivity.putExtras(bundle);
            startActivityForResult(wizardNextActivity, ACTIVITY_SETTINGS_NARRATION, bundle);

        }
    };

    private View.OnClickListener bgMusicOnClick = new View.OnClickListener() {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onClick(View v) {
            Bundle bundle = ActivityOptions.makeCustomAnimation(GlobalSettingsActivity.this,
                    R.anim.slide_in_left,
                    R.anim.slide_out_left).toBundle();

            //  Intent intent = new Intent(GlobalSettingsActivity.this, GlobalSettingsBgMusicActivity.class);
            //  startActivity(intent, bundle);

            Intent wizardNextActivity = new Intent(GlobalSettingsActivity.this, GlobalSettingsBgMusicActivity.class);
            wizardNextActivity.putExtras(bundle);
            startActivityForResult(wizardNextActivity, ACTIVITY_SETTINGS_BGMUSIC, bundle);



        }
    };

    //KBL 071315
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Returned from a child activity. Fetch the presentation object
        if (requestCode == ACTIVITY_SETTINGS_NARRATION || requestCode == ACTIVITY_SETTINGS_BGMUSIC) {
            if (resultCode == RESULT_OK || resultCode == RESULT_CANCELED) {

                saveChanges();
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void checkSave(){
        SharedPreferences sharedPreferences = getSharedPreferences(Globals.PREFS_APP_SETTINGS, 0);
        Gson gson = new Gson();
        String fromJson = sharedPreferences.getString(Globals.PREFS_APP_SETTINGS, "");
        ApplicationSettings appSettings = gson.fromJson(fromJson, ApplicationSettings.class);

        Boolean prevSettings = appSettings.isApplyToExistingPres();
        Boolean newSettings;

        if(swApplyToExistingPres.isChecked()){
            newSettings = true;
        }else{
            newSettings = false;
        }
        Log.v("newSettings", String.valueOf(newSettings));
        Log.v("prevSettings", String.valueOf(prevSettings));

    //    if(!newSettings.equals(prevSettings)){
        if(appSettings.isApplyToExistingPres() || swApplyToExistingPres.isChecked()){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GlobalSettingsActivity.this);
            alertDialogBuilder.setTitle("Apply to existing presentations");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder
                    .setMessage("Do you want to save and apply current Global Settings to your existing and new presentations?")
                    .setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {

                                    clickedSave = true;

                                    saveChanges();
                                    finish();
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {

                                    dialog.cancel();

                                }
                            })
                    .setNeutralButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    finish();
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                                }
                            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();

        }
        else{
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GlobalSettingsActivity.this);
            alertDialogBuilder.setTitle("Warning");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder
                    .setMessage("\"Apply to Existing Presentations\" is currently disabled. Current Global Settings will not be saved.")
                    .setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {

                                    dialog.cancel();
                                    finish();
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {

                                    dialog.cancel();

                                }
                            });
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }
    }

    private void saveChanges() {
        SharedPreferences sharedPreferences = getSharedPreferences(Globals.PREFS_APP_SETTINGS, 0);
        Gson gson = new Gson();
        String fromJson = sharedPreferences.getString(Globals.PREFS_APP_SETTINGS, "");
        ApplicationSettings appSettings = gson.fromJson(fromJson, ApplicationSettings.class);

        appSettings.setApplyToExistingPres(swApplyToExistingPres.isChecked());
        appSettings.setDisplayCompanyLogo(swCompanyLogo.isChecked());
        appSettings.setDisplayCompanyName(swCompanyName.isChecked());
        appSettings.setDisplayAgentImage(swAgentImage.isChecked());
        appSettings.setDisplayAgentName(swAgentInfo.isChecked());

        String toJsonObject = gson.toJson(appSettings);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Globals.PREFS_APP_SETTINGS, toJsonObject);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            editor.apply();
        } else {
            editor.commit();
        }


        if((clickedSave == true) &&  (appSettings.isApplyToExistingPres())){ //checked applyToExisting
             ApplyToExistingPres();
             clickedSave = false;
             Log.v("APPLY TO EXISTING", "True");
         }

    }

    //update presentations before coming back to mainactivity so it will be updated both server and app Db data
    public void ApplyToExistingPres() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(Globals.PREFS_APP_SETTINGS, 0);
        Gson gson = new Gson();
        String fromJson = sharedPreferences.getString(Globals.PREFS_APP_SETTINGS, "");
        ApplicationSettings appSettings = gson.fromJson(fromJson, ApplicationSettings.class);

        try{
            PresentationDataSource dao = new PresentationDataSource(this);
            dao.open(false);
            presentations = dao.getAllPresentations();
            dao.close();

            Presentation p;

            for (Presentation getPresentation : presentations) {
                dao.open(false);
                p = dao.fetch(getPresentation.getId());
                dao.close();

                p.setDisplayCompanyLogo(appSettings.isDisplayCompanyLogo());
                p.setDisplayCompanyName(appSettings.isDisplayCompanyName());
                p.setDisplayAgentImage(appSettings.isDisplayAgentImage());
                p.setDisplayAgentName(appSettings.isDisplayAgentName());
                p.setNarration(appSettings.getNarration());
                p.setMusic(appSettings.getMusic());

                p.setModified(new Date());

                dao.open(true);
                dao.updatePresentation(p);
                dao.close();
                Log.v("pres name", p.getName());

                UpdatePresentation.runCommand(AgentData.getInstance().getRealtor().getId(), p, null);
                Log.v("sync with server", "from GLobalSettingsActivity");
            }

        } catch(Exception e) {
            ErrorHandler.log(" SQL exception: ", e.toString());
        }

    }

    //KBL 071515
    @Override
    public void onBackPressed() {
       // Intent intent = new Intent(GlobalSettingsActivity.this, SettingsActivity.class);
       // startActivity(intent);


      //  saveChanges();  //do not save settings when back button is pressed because if Apply to existing Pres is checked, it will affect the existing presentation settings


        checkSave();
      //  finish();
      //  overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}

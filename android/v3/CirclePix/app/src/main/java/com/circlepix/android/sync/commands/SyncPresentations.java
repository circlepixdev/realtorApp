package com.circlepix.android.sync.commands;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.util.Pair;

import com.circlepix.android.CirclePixAppState;
import com.circlepix.android.data.Presentation;
import com.circlepix.android.data.PresentationDataSource;
import com.circlepix.android.sync.data.ServerPresentation;
import com.circlepix.android.sync.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sburns.
 * Syncs presentations between the App's database and the circlepix server
 */
public class SyncPresentations {

    private static long MILLISECOND_GAP = 300;

    //added by KBL 081115
    static ProgressDialog mProgressDialog;
   // static int size = 0;
    static CirclePixAppState appState;



//    public static void runCommand(final Activity a, final String realtorId, final boolean isFirstRun, final PresentationDataSource ds, final Runnable postAction) {
    public static void runCommand(final Activity a, final String realtorId, final PresentationDataSource ds, final Runnable postAction) {

   // Setup application class
        appState = ((CirclePixAppState)a.getApplicationContext());
        appState.setContextForPreferences(a.getApplicationContext());

         a.runOnUiThread(new Runnable() {
            public void run() {
                mProgressDialog = new ProgressDialog(a);
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setMessage("Loading...");
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }
        });

      //  Log.v("isFirstRun Sync Command", String.valueOf(isFirstRun));
        // Get the list of presentations from the server along with their modified datestamp.
        final List<ServerPresentation> serverPresentations = new ArrayList<ServerPresentation>();

        GetPresentationIds.runCommand(realtorId, mProgressDialog, serverPresentations, new Runnable() {
            @Override
            public void run() {

                // Get the list of presentations from the App's database also with modified datestamp.
            	ds.open(false);

                List<Presentation> presentations = ds.getAllPresentations();

                ds.close();
                appState.setServerPresTotalSize(serverPresentations.size());


                Log.v("serverPres size", String.valueOf(serverPresentations.size()));
                if(serverPresentations.size() == 0){
                    if(mProgressDialog.isShowing()){
                        mProgressDialog.dismiss();
                    }
                }

                if ((appState.getServerPresTotalSize() == 0) && (mProgressDialog.isShowing())){
                    mProgressDialog.dismiss();
                    appState.setServerPresTotalSize(0);
                }


//                Log.v("isFirstRun from Sync Pres", String.valueOf(isFirstRun));
//                if (isFirstRun && presentations.size() == 0) {
                if (presentations.size() == 0) {
                    // This is the first time the app has been run
                    if (serverPresentations.size() > 0) {
                        // There are records on the server and this is the first run
                        // so insert the server records into the local database. This
                        // will only happen if the user got a new device or deleted the
                        // app and reinstalled.


                        for (ServerPresentation serverPresentation : serverPresentations) {
                          //  size ++;
                             GetPresentation.runCommand(a, mProgressDialog, realtorId, serverPresentation.guid, ds, null);

                         /*   Log.v("size", String.valueOf(size));

                           if ((size == serverPresentations.size()) && (mProgressDialog.isShowing())){
                                mProgressDialog.dismiss();
                                 size = 0;
                            }*/
                        }

                    }

                } else {
                    //keuahn 070715
                    //(if using multiple devices) check if there are presentations that needed to be updated and added to the app
                    for(ServerPresentation getServerPresentation : serverPresentations) {
                      //  size ++;
                        boolean found = false;
                        for (Presentation getPresentation : presentations) {

                            if (getServerPresentation.guid.equals(getPresentation.getGuid())) {
                                found = true;
                                //update presentations
                                UpdatePresentationsMultipleDevice.runCommand(a, mProgressDialog, realtorId, getServerPresentation.guid, getPresentation, ds, null);

                            }
                        }

                        if (!found) {//check if there are new presentations from server that needed to update the app
                            GetPresentation.runCommand(a,  mProgressDialog,realtorId, getServerPresentation.guid, ds, null);
                        }

                     /*   Log.v("size", String.valueOf(size));

                        if ((size == serverPresentations.size()) && (mProgressDialog.isShowing())){
                            mProgressDialog.dismiss();
                            size = 0;
                        }*/
                    }


                	// The app has been run before. See what we need to sync up
                	// to the server (we are the master). Update the server with 
                	// anything it doesn't already have the latest version of.
                    final Pair<List<Presentation>, List<String>> lists = getSyncLists(presentations, serverPresentations , realtorId , ds);

                    for(Presentation p : lists.first) {
                        // upload to server...
                         UpdatePresentation.runCommand(realtorId, p, null);
                         Log.v("update bgm", p.getMusic().toString());
                    }

                    //temporary deletion to the server, we will only filter presentations here
                    for(String guid : lists.second) {
                        // delete from server...
                        DeletePresentation.runCommand(realtorId, guid, null);
                    }

                }

                if(postAction != null) {
                    postAction.run();
                }
            }
        });
    }

    private static Pair<List<Presentation>, List<String>> getSyncLists(List<Presentation> appPresentations, List<ServerPresentation> serverPresentations , final String realtorId, final PresentationDataSource ds) {

        List<Presentation> uploadToServer = new ArrayList<Presentation>();
        List<String> deleteFromServer = new ArrayList<String>();

        // see if there are any presentations on the App that should be uploaded to the server...
        for(Presentation appPresentation : appPresentations) {

            boolean found = false;
            for(ServerPresentation serverPresentation : serverPresentations) {
                if(appPresentation.getGuid().equals(serverPresentation.guid)) {
                    found = true;


                    // okay, it's already on the server but should be updated?
                    long timeDiff = DateUtils.getMilliSecondsDifference(
                    		 serverPresentation.modified,
                    		 DateUtils.formatDateForRestAPI(appPresentation.getModified()));

                    if(timeDiff >= MILLISECOND_GAP || timeDiff == DateUtils.DATEDIFF_ERROR_RESULT) {
                        // add it to the upload list because the server version need to be updated
                        uploadToServer.add(appPresentation);
                    }

                    //This is for multiple devices.
                    //When presentations are created in offline mode.
                    //check if year in unsync presentations = 1970, 1969 or 0000. If yes, then replace it with current date
                    Log.v("server date", serverPresentation.modified);
                    String date= serverPresentation.modified;
                    String[] items1 = date.split("-");
                    String year=items1[0];
                    String month=items1[1];
                    String day=items1[2];

                    Log.v("year", year);

                    if(year.equals("0000") || year.equals("1970") || year.equals("1969")) {
                        appPresentation.setModified(new Date());
                        uploadToServer.add(appPresentation);
                    }
                }
            }

            if(!found) {
                // it's not yet on the server so add it to upload list
                appPresentation.setCreated(new Date());   //set current date as created and modified date  - this is for presentations created in offline mode(0000:00:00)
                appPresentation.setModified(new Date());
                Log.v("pres created at Sync", appPresentation.getCreated().toString());
                Log.v("pres modified at Sync", appPresentation.getModified().toString());

                uploadToServer.add(appPresentation);
            }
        }

        // see if there are any on the server that need to be deleted
        for(ServerPresentation serverPresentation : serverPresentations) {
            boolean found = false;
            for(final Presentation appPresentation : appPresentations) {
                if(serverPresentation.guid.equals(appPresentation.getGuid())) {
                    found = true;

                }

            }
            if(!found) {
                // It's not in the App, add it to the download list
                // WARNING: this could be a problem if the agent uses two different devices for their
                //   presentations and decides to delete a presentation from one of the devices.
                //   The presentation would be deleted from that device and from the server, but when the
                //   other device syncs it will upload its version of the deleted presentation and the
                //   then the first device will re-download it the next time it syncs.
                //   One way to avoid this is the keep deleted presentation guids around in the App's database.
            	deleteFromServer.add(serverPresentation.guid);

            }
        }

        return new Pair<List<Presentation>, List<String>>(uploadToServer, deleteFromServer);
    }

    public static boolean containsYear(String dateStr){
        return  dateStr.split("-").length == 3;

    }
}

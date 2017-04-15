package com.circlepix.android.sync.commands;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import com.circlepix.android.CirclePixAppState;
import com.circlepix.android.data.Presentation;
import com.circlepix.android.data.PresentationDataSource;
import com.circlepix.android.sync.utils.DateUtils;

import org.json.JSONException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by sburns. Get the Presentation for a given RealtorId and GUID from the circlepix server.
 */
public class GetPresentation {

    /* Define the POJO structures that Gson will deserialize the server's JSON into */
    // Be aware that the field names in these classes must exactly match the names used in the JSON or ....
    //   alternatively you can use the @SerializedName annotation if you want to give them a different name.
    static class RealtorPresentationData {
        String JsonString;
        String RealtorPresentationID;
        String RealtorPresentationGUID;
        String RealtorID;
        String Name;
        String PropertyImage;
        String CreatedAt;
        String UpdatedAt;
    }
    static class Data {
        RealtorPresentationData RealtorPresentation;
    }
    static class PresentationResponse {
        String status;
        String message;
        Data data;
    }

    static int totalSize;

    static CirclePixAppState appState;

    /* END OF deserialize POJO classes */

    interface GetPresentationClient {
        @GET("/getPresentation.php")
        void presentation(
                @Query("realtorId") String realtorId,
                @Query("GUID") String GUID,
                Callback<PresentationResponse> callback
        );
    }

    public static void runCommand(Activity a,  final ProgressDialog mProgressDialog, String realtorId, String guid, final PresentationDataSource ds, final Runnable postAction) {
    //    totalSize = serverPresSize;
        appState = ((CirclePixAppState)a.getApplicationContext());
        appState.setContextForPreferences(a.getApplicationContext());

        Log.v("serverPresSize getpres", String.valueOf((appState.getServerPresTotalSize())));

        // Create REST adapter which points the getPresentationIds end point.
        GetPresentationClient client = ServiceGenerator.createService(GetPresentationClient.class, Constants.CIRCLEPIX_ENDPOINT);

        Callback<PresentationResponse> callback = new Callback<PresentationResponse>() {
            @Override
            public void success(PresentationResponse getPresentation, Response response) {
                Log.d("GetPresentation", "Status: " + getPresentation.status + " (Message: " + getPresentation.message + ")");

                if(getPresentation.data != null && getPresentation.data.RealtorPresentation != null) {

                    RealtorPresentationData rpd = getPresentation.data.RealtorPresentation;
                    Log.v("jsonString", rpd.JsonString);
                    try {
                        // Convert to actual Presentation object
                        Presentation p = new Presentation();
                        p.setJsonData(rpd.JsonString);
                        p.setGuid(rpd.RealtorPresentationGUID);
                        p.setName(rpd.Name);
                        p.setModified(DateUtils.parseAPIFormattedDateString(rpd.UpdatedAt));

                        Log.v("DATE", String.valueOf(rpd.UpdatedAt));
                        ds.open(false);
                        ds.createPresentation(p);
                        ds.close();


                      /*  Log.v("getAction", p.getAction());
                        if(p.getAction().equalsIgnoreCase("delete")) {
                            //do nothing
                        }else   if(p.getAction().equalsIgnoreCase("update")) {
                            //do nothing
                        }else if(p.getAction().equalsIgnoreCase("create")){
                            ds.open(true);
                            ds.createPresentation(p);
                            ds.close();
                        }*/

                        if (appState.getServerPresTotalSize() != 0) {
                            totalSize = appState.getServerPresTotalSize() - 1;
                            appState.setServerPresTotalSize(totalSize);
                        }


                    } catch(Exception e) {
                        ErrorHandler.log(" SQL exception: ", e.toString());
                        if (mProgressDialog.isShowing()){
                            mProgressDialog.dismiss();
                        }
                    }

                    Log.v("size getpres", String.valueOf(totalSize));

                    if ((appState.getServerPresTotalSize() == 0) && (mProgressDialog.isShowing())){
                        mProgressDialog.dismiss();
                        appState.setServerPresTotalSize(0);
                    }
                }
                if(postAction != null) {
                    postAction.run();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ErrorHandler.log(error);
                if (mProgressDialog.isShowing()){
                    mProgressDialog.dismiss();
                }
            }
        };

        // Fetch and process a presentation
        client.presentation(realtorId, guid, callback);
    }
}

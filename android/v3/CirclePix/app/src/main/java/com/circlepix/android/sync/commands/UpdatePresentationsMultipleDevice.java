package com.circlepix.android.sync.commands;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import com.circlepix.android.CirclePixAppState;
import com.circlepix.android.data.Presentation;
import com.circlepix.android.data.PresentationDataSource;
import com.circlepix.android.sync.utils.DateUtils;


import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;


/**
 * Created by keuahn. Get the Presentation for a given RealtorId and GUID from the circlepix server and update the app.
 */
public class UpdatePresentationsMultipleDevice {

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
        @GET("getPresentation.php")
        Call<PresentationResponse> presentation(
                @Query("realtorId") String realtorId,
                @Query("GUID") String GUID
        );

    }

    public static void runCommand(Activity a, final ProgressDialog mProgressDialog, String realtorId, String guid, final Presentation b, final PresentationDataSource ds, final Runnable postAction) {
        // Setup application class
        appState = ((CirclePixAppState) a.getApplicationContext());
        appState.setContextForPreferences(a.getApplicationContext());

        Log.v("serverPresSize update", String.valueOf((appState.getServerPresTotalSize())));

        // Create REST adapter which points the getPresentationIds end point.
        GetPresentationClient client = ServiceGenerator.createService(GetPresentationClient.class, Constants.CIRCLEPIX_ENDPOINT);

        Call<PresentationResponse> call = client.presentation(realtorId, guid);

        call.enqueue(new Callback<PresentationResponse>() {
            @Override
            public void onResponse(Call<PresentationResponse> call, Response<PresentationResponse> response) {
                if (response.isSuccessful()) {

                    if (response.body().data != null && response.body().data.RealtorPresentation != null) {

                        RealtorPresentationData rpd = response.body().data.RealtorPresentation;

                        try {
                            // Convert to actual Presentation object
                            Presentation p;
                            ds.open(false);
                            p = ds.fetch(b.getId());

                            ds.close();
                            p.setJsonData(rpd.JsonString);
                            p.setGuid(rpd.RealtorPresentationGUID);
                            p.setName(rpd.Name);
                            p.setModified(DateUtils.parseAPIFormattedDateString(rpd.UpdatedAt));

                            Log.v("DATE", String.valueOf(rpd.UpdatedAt));
                            ds.open(true);
                            ds.updatePresentation(p);
                            ds.close();


                      /*  Log.v("getAction", p.getAction());
                        if(p.getAction().equalsIgnoreCase("delete")){
                            ds.open(false);
                            ds.deletePresentation(p);
                            ds.close();
                        }else    if(p.getAction().equalsIgnoreCase("update")){
                            ds.open(true);
                            ds.updatePresentation(p);
                            ds.close();
                        }*/

                            if (appState.getServerPresTotalSize() != 0) {
                                totalSize = appState.getServerPresTotalSize() - 1;
                                appState.setServerPresTotalSize(totalSize);
                            }


                        } catch (Exception e) {
                            ErrorHandler.log(" SQL exception: ", e.toString());
                            if (mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                            }
                        }

                        Log.v("size update", String.valueOf(totalSize));

                        if ((appState.getServerPresTotalSize() == 0) && (mProgressDialog.isShowing())) {
                            mProgressDialog.dismiss();
                            appState.setServerPresTotalSize(0);
                        }

                    }
                    if (postAction != null) {
                        postAction.run();
                    }

                } else {

                }
            }

            @Override
            public void onFailure(Call<PresentationResponse> call, Throwable throwable) {
                if (throwable instanceof IOException){
                    //Add your code for displaying no network connection error

               //     RetrofitException error = (RetrofitException) throwable;
               //     ErrorHandler.log(error);
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                }
            }

        });
    }
}


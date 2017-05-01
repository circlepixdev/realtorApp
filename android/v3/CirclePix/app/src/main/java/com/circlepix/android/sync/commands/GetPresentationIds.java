package com.circlepix.android.sync.commands;

import android.app.ProgressDialog;
import android.util.Log;

import com.circlepix.android.sync.data.ServerPresentation;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.http.GET;
import retrofit2.Response;
import retrofit2.http.Query;


/**
 * Created by sburns. Get the Presentation IDs for a given realtor
 */
public class GetPresentationIds {

    /* Define the POJO structures that Gson will deserialize the server's JSON into */
    // Be aware that the field names in these classes must exactly match the names used in the JSON or ....
    //   alternatively you can use the @SerializedName annotation if you want to give them a different name.
    static class RealtorPresentation {
        String GUID;
        String updated;
    }
    static class Data {
       List<RealtorPresentation> RealtorPresentations;
    }
    static class PresentationIdsResponse {
        String status;
        String message;
        Data data;
    }


    /* END OF deserialize POJO classes */

    interface GetPresentationsClient {
        @GET("getPresentationIDs.php")
        Call<PresentationIdsResponse> presentations(
                @Query("realtorId") String realtorId
        );

        //  @GET("/getPresentationIDs.php")
//        void presentations(
//                @Query("realtorId") String realtorId,
//                Callback<PresentationIdsResponse> callback
//
//        );
    }

    public static void runCommand(String realtorId, final ProgressDialog mProgressDialog, final List<ServerPresentation> presentationsList, final Runnable postAction) {
/*
        a.runOnUiThread(new Runnable() {
            public void run() {
                mProgressDialog = new ProgressDialog(a);
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setMessage("Loading...");
                mProgressDialog.show();
            }
        });*/

        // Create REST adapter which points the getPresentationIds end point.
        GetPresentationsClient client = ServiceGenerator.createService(GetPresentationsClient.class, Constants.CIRCLEPIX_ENDPOINT);

        Call<PresentationIdsResponse> call = client.presentations(realtorId);

        call.enqueue(new Callback<PresentationIdsResponse>() {
            public void onResponse(Call<PresentationIdsResponse> call, Response<PresentationIdsResponse> response) {
                if (response.isSuccessful()) {
                    Log.d("GetPresentationIds", "Status: " + response.body().status + " (Message: " + response.body().message + ")");

                    if (response.body().data != null && response.body().data.RealtorPresentations != null) {
                        for (RealtorPresentation presentation : response.body().data.RealtorPresentations) {

                            ServerPresentation presentationInfo = new ServerPresentation();
                            presentationInfo.guid = presentation.GUID;
                            presentationInfo.modified = presentation.updated;
                            presentationsList.add(presentationInfo);
                        }

//                    if (mProgressDialog.isShowing()){
//                        mProgressDialog.dismiss();
//
//                     }

                    }
                    if (postAction != null) {
                        postAction.run();
                    }

                } else {
                    Log.v("GetPresentationId", "unsuccessful");
                }


            }


            @Override
            public void onFailure(Call<PresentationIdsResponse> call, Throwable throwable) {

                if (throwable instanceof HttpException){
                    //Add your code for displaying no network connection error

              //      RetrofitException error = (RetrofitException) throwable;
               //     ErrorHandler.log(error);

                    if ( mProgressDialog != null &&  mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                }
            }
        });
    }

/*
        // Create REST adapter which points the getPresentationIds end point.
        GetPresentationsClient client = ServiceGenerator.createService(GetPresentationsClient.class, Constants.CIRCLEPIX_ENDPOINT);

        Callback<PresentationIdsResponse> callback = new Callback<PresentationIdsResponse>() {

            public void onResponse(Call<PresentationIdsResponse> call, Response<PresentationIdsResponse> response) {
                if (response.isSuccessful()) {
                    Log.d("GetPresentationIds", "Status: " + response.body().status + " (Message: " + response.body().message + ")");

                    if(response.body().data != null && response.body().data.RealtorPresentations != null) {
                        for (RealtorPresentation presentation : response.body().data.RealtorPresentations) {

                            ServerPresentation presentationInfo = new ServerPresentation();
                            presentationInfo.guid = presentation.GUID;
                            presentationInfo.modified = presentation.updated;
                            presentationsList.add(presentationInfo);
                        }

//                    if (mProgressDialog.isShowing()){
//                        mProgressDialog.dismiss();
//
//                     }

                    }
                    if(postAction != null) {
                        postAction.run();
                    }

                } else {

                }


            }


            @Override
            public void onFailure(Call<PresentationIdsResponse> call, Throwable throwable) {
                RetrofitException error = (RetrofitException) throwable;
                ErrorHandler.log(error);
                if (mProgressDialog.isShowing()){
                    mProgressDialog.dismiss();
                }
            }





//            Callback<PresentationIdsResponse> callback = new Callback<PresentationIdsResponse>() {
//
//            }
//            @Override
//            public void success(PresentationIdsResponse getPresentationsResponse, Response response) {
//                Log.d("GetPresentationIds", "Status: " + getPresentationsResponse.status + " (Message: " + getPresentationsResponse.message + ")");
//
//                if(getPresentationsResponse.data != null && getPresentationsResponse.data.RealtorPresentations != null) {
//                    for (RealtorPresentation presentation : getPresentationsResponse.data.RealtorPresentations) {
//
//                        ServerPresentation presentationInfo = new ServerPresentation();
//                        presentationInfo.guid = presentation.GUID;
//                        presentationInfo.modified = presentation.updated;
//                        presentationsList.add(presentationInfo);
//                    }
//
////                    if (mProgressDialog.isShowing()){
////                        mProgressDialog.dismiss();
////
////                     }
//
//            }
//                if(postAction != null) {
//                    postAction.run();
//                }
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                ErrorHandler.log(error);
//                if (mProgressDialog.isShowing()){
//                    mProgressDialog.dismiss();
//                }
//            }
        };

        // Fetch and process a list of the realtor presentations
        client.presentations(realtorId, callback);
    }

    */
}

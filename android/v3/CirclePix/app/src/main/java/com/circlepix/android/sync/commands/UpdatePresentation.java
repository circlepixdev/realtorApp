package com.circlepix.android.sync.commands;

import android.util.Log;

import com.circlepix.android.data.Presentation;
import com.circlepix.android.data.PresentationWriter;
import com.circlepix.android.sync.utils.DateUtils;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;


/**
 * Created by sburns. Creates/Updates a presentation on the circlepix server with the data stored in the App.
 */
public class UpdatePresentation {

    /* Define the POJO structures that Gson will deserialize the server's JSON into */
    // UpdatePresentation is similar to GetPresentation in that the server returns all of the presentation in the response.
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
    /* END OF deserialize POJO classes */

    interface UpdatePresentationClient {
        // TODO: probably should switch this from a GET to a POST http method.

        @GET("updatePresentation.php")
        Call<PresentationResponse> presentation(
                @Query("realtorId") String realtorId,
                @Query("GUID") String GUID,
                @Query("name") String name,
                @Query("updated") String updated,
                @Query("json") String jsonString
        );
    }

    public static void runCommand(String realtorId, final Presentation b, final Runnable postAction) {

        Log.v("b.modified", b.getModified().toString());
        // Create REST adapter which points the getPresentationIds end point.
        UpdatePresentationClient client = ServiceGenerator.createService(UpdatePresentationClient.class, Constants.CIRCLEPIX_ENDPOINT);

        //   TypedFile typedFile = new TypedFile("multipart/form-data", new File( b.getPropertyImage()));
        //   FileBody propertyImage = new FileBody(new File(b.getPropertyImage()), "image/jpeg");


        Call<PresentationResponse> call = client.presentation(realtorId,
                b.getGuid(),
                b.getName(),
                DateUtils.formatDateForRestAPI(b.getModified()),
                PresentationWriter.toJson(b));

        call.enqueue(new Callback<PresentationResponse>() {

            public void onResponse(Call<PresentationResponse> call, Response<PresentationResponse> response) {
                if (response.isSuccessful()) {

                    Log.d("UpdatePresentation", "Status: " + response.body().status + " (Message: " + response.body().message + ")");

                    if (response.body().data != null && response.body().data.RealtorPresentation != null) {

                        /** If you want, you could do a test here to see that what the server returned is the same as what was sent.
                         One item that appears to never get set as expected is the CreatedAt field.
                         It has been observed as 0000-00-00 00:00:00 and CURRENT_TIMESTAMP.
                         Not sure what that's all about.

                         RealtorPresentationData rpd = updatePresentation.data.RealtorPresentation;
                         Presentation dbPresentation = new Presentation();
                         dbPresentation.guid = rpd.RealtorPresentationGUID;
                         dbPresentation.name = rpd.Name;
                         dbPresentation.property_image = rpd.PropertyImage;
                         dbPresentation.modified = rpd.UpdatedAt;
                         dbPresentation.jsonString = rpd.JsonString;
                         try {
                         database.updatePresentation(dbPresentation);
                         } catch(Exception e) {
                         ErrorHandler.log(" SQL exception: ", e.toString());
                         }
                         */

                        RealtorPresentationData rpd = response.body().data.RealtorPresentation;
                        Log.v("presentation GUID", rpd.RealtorPresentationGUID);
                        Log.v("update bgm2", b.getMusic().toString());
                        Log.v("UPDATE JSONSTRING", rpd.JsonString);
                        Log.v("Property Image", String.valueOf(rpd.PropertyImage));
                   /* RealtorPresentationData rpd = updatePresentation.data.RealtorPresentation;
                    try {
                        // Convert to actual Presentation object
                        Presentation p = new Presentation();
                        p.setJsonData(rpd.JsonString);
                        p.setGuid(rpd.RealtorPresentationGUID);
                        p.setName(rpd.Name);
                        p.setModified(DateUtils.parseAPIFormattedDateString(rpd.UpdatedAt));
                        p.setMedia24HourInfo(false);
                        p.setMediaQRCodes(false);
                        ds.open(true);
                        ds.updatePresentation(p);
                        ds.close();
                    } catch(Exception e) {
                        ErrorHandler.log(" SQL exception: ", e.toString());
                    }*/

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

                //    RetrofitException error = (RetrofitException) throwable;
                //    ErrorHandler.log(error);

                }
            }
        });
    }
}

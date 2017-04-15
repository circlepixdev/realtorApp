package com.circlepix.android.sync.commands;

import android.util.Log;

import com.circlepix.android.data.Presentation;
import com.circlepix.android.data.PresentationWriter;
import com.circlepix.android.sync.utils.DateUtils;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;

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

        @GET("/updatePresentation.php")
        void updatePresentation(
                @Query("realtorId") String realtorId,
                @Query("GUID") String GUID,
                @Query("name") String name,
                @Query("updated") String updated,
                @Query("json") String jsonString,
           //     @Part("propertyImage") FileBody propertyImage,
                Callback<PresentationResponse> callback
        );
    }

    public static void runCommand(String realtorId, final Presentation b,  final Runnable postAction) {

        Log.v("b.modified", b.getModified().toString());
        // Create REST adapter which points the getPresentationIds end point.
        UpdatePresentationClient client = ServiceGenerator.createService(UpdatePresentationClient.class, Constants.CIRCLEPIX_ENDPOINT);

     //   TypedFile typedFile = new TypedFile("multipart/form-data", new File( b.getPropertyImage()));
     //   FileBody propertyImage = new FileBody(new File(b.getPropertyImage()), "image/jpeg");

        Callback<PresentationResponse> callback = new Callback<PresentationResponse>() {

            @Override
            public void success(PresentationResponse updatePresentation, Response response) {
                Log.d("UpdatePresentation", "Status: " + updatePresentation.status + " (Message: " + updatePresentation.message + ")");

                if(updatePresentation.data != null && updatePresentation.data.RealtorPresentation != null) {

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

                    RealtorPresentationData rpd = updatePresentation.data.RealtorPresentation;
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
                if(postAction != null) {
                    postAction.run();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ErrorHandler.log(error);
            }
        };

        client.updatePresentation(
                realtorId,
                b.getGuid(),
                b.getName(),
                DateUtils.formatDateForRestAPI(b.getModified()),
                PresentationWriter.toJson(b),
           //     propertyImage,
                callback);

    }
}















/*
package com.circlepix.android.sync.commands;

import android.util.Log;

import com.circlepix.android.data.Presentation;
import com.circlepix.android.data.PresentationDataSource;
import com.circlepix.android.data.PresentationWriter;
import com.circlepix.android.sync.utils.DateUtils;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.POST;
import retrofit.http.Query;

*/
/**
 * Created by sburns. Creates/Updates a presentation on the circlepix server with the data stored in the App.
 *//*

public class UpdatePresentation {

    */
/* Define the POJO structures that Gson will deserialize the server's JSON into *//*

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
    */
/* END OF deserialize POJO classes *//*


    interface UpdatePresentationClient {
        // TODO: probably should switch this from a GET to a POST http method.
        @POST("/updatePresentation.php")
        void updatePresentation(
                @Query("GUID") String GUID,
                @Query("realtorId") String realtorId,
                @Query("name") String name,
                @Query("json") String jsonString,
                @Query("updated") String updated,
                Callback<PresentationResponse> callback
        );
    }

    public static void runCommand(String realtorId, final PresentationDataSource ds, final Presentation b, final Runnable postAction) {
 //   public static void runCommand(String realtorId,  final Presentation b, final Runnable postAction) {

        // Create REST adapter which points the getPresentationIds end point.
        UpdatePresentationClient client = ServiceGenerator.createService(UpdatePresentationClient.class, Constants.CIRCLEPIX_ENDPOINT);

        Callback<PresentationResponse> callback = new Callback<PresentationResponse>() {
            @Override
            public void success(PresentationResponse updatePresentation, Response response) {
                Log.d("UpdatePresentation", "Status: " + updatePresentation.status + " (Message: " + updatePresentation.message + ")");

                Log.d("UpdatePresentation", "Response: " + response);

                if(updatePresentation.data != null && updatePresentation.data.RealtorPresentation != null) {

                    Log.d("updatePresentation data", "is not null");

                    */
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
                *//*

                   */
/* PresentationDataSource database = null;

                    try{
                        database = new PresentationDataSource(activity.getApplicationContext());
                        database.open(true);

                        RealtorPresentationData rpd = updatePresentation.data.RealtorPresentation;

                        Presentation dbPresentation = new Presentation();
                        dbPresentation.setGuid(rpd.RealtorPresentationGUID);
                        dbPresentation.setName(rpd.Name);
                        dbPresentation.setPropertyImage(rpd.PropertyImage);
                        dbPresentation.setModified(rpd.UpdatedAt);
                        dbPresentation.setJsonData(rpd.JsonString);
                        database.updatePresentation(dbPresentation);
                    }catch(Exception e){
                        ErrorHandler.log(" SQL exception: ", e.toString());
                    }*//*



                //  PresentationDataSource ds = null;

*/
/*
                    RealtorPresentationData rpd = updatePresentation.data.RealtorPresentation;

                    Log.v("jsonString1", rpd.JsonString);
                    Log.v("PresentationGUID1", rpd.RealtorPresentationGUID);
                    Log.v("Name1", rpd.Name);
                    Log.v("UpdatedAt1", "2015-07-06 10:01:34");

                try {
                    // Convert to actual Presentation object
                    //   ds = new PresentationDataSource(activity.getApplicationContext());



                    Presentation dbPresentation = new Presentation();

                    dbPresentation.setGuid(rpd.RealtorPresentationGUID);
                    dbPresentation.setName(rpd.Name);
                    dbPresentation.setPropertyImage(rpd.PropertyImage);
//                    dbPresentation.setModified(DateUtils.parseAPIFormattedDateString(rpd.UpdatedAt));
                    dbPresentation.setModified(DateUtils.parseAPIFormattedDateString("2015-07-06 10:01:34"));

                    dbPresentation.setJsonData(rpd.JsonString);
                    ds.open(true);
                    ds.updatePresentation(dbPresentation);
                    ds.close();

                    Log.v("jsonData UpdatePres", rpd.JsonString);

                }catch(Exception e){
                    ErrorHandler.log(" SQL exception: ", e.toString());
                }*//*

            }
                if(postAction != null) {
                    postAction.run();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ErrorHandler.log(error);
            }
        };

        client.updatePresentation(
                b.getGuid(),
                realtorId,
                b.getName(),
                PresentationWriter.toJson(b),
                DateUtils.formatDateForRestAPI(b.getModified()),
                callback);
    }



}
*/

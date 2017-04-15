package com.circlepix.android.helpers;

import java.io.File;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Keuahn on 7/7/2016.
 */
public class RequestBuilder {

    public static HttpUrl buildURL() {
        return new HttpUrl.Builder()
                .scheme("http") //http
                .host("www.somehostname.com")
                .addPathSegment("pathSegment")//adds "/pathSegment" at the end of hostname
                .addQueryParameter("param1", "value1") //add query parameters to the URL
                .addEncodedQueryParameter("encodedName", "encodedValue")//add encoded query parameters to the URL
                .build();

       // The return URL:
      //  https://www.somehostname.com/pathSegment?param1=value1&encodedName=encodedValue

    }

    //For VideoUploadActivity
    public static MultipartBody uploadRequestBody(String title, String description, String code, String videoFormat, File file) {

        MediaType MEDIA_TYPE = MediaType.parse("video/" + videoFormat); // e.g. "image/png"
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", title)
                .addFormDataPart("description", description)
                .addFormDataPart("code", code)
               // .addFormDataPart("filename", title + "." + imageFormat) //e.g. title.png --> imageFormat = png
                .addFormDataPart("videoFile", "listingVideo.mp4", RequestBody.create(MEDIA_TYPE, file))
                .build();

    }
}

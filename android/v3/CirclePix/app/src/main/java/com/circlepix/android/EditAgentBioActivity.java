package com.circlepix.android;

import android.*;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.circlepix.android.beans.AgentData;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by keuahnlumanog on 11/12/2016.
 */

public class EditAgentBioActivity extends AppCompatActivity {

    private AgentData agentData;
    private TextView toolBarSave;
    private EditText agentYoutubeId, agentBio;
    private Button addVideoButton;
    private ProgressDialog mProgressDialog;

    private final int SELECT_VIDEO = 1;
    private final int CAPTURE_VIDEO = 101;

    private String selectedVideoPath;
    private Uri captureVideoFileUri;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 143;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_agent_bio);

        // Get global shared data
        agentData = AgentData.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolBarSave = (TextView) findViewById(R.id.toolbar_save);
        toolBarSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               // Toast.makeText(getApplicationContext(), "Note: API is not done yet", Toast.LENGTH_SHORT).show();

                editAgentBio();

            }
        });

        agentYoutubeId = (EditText) findViewById(R.id.editText_youtube_video_id);
        agentBio = (EditText) findViewById(R.id.editText_bio);
        addVideoButton = (Button) findViewById(R.id.add_video_button);
        addVideoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Toast.makeText(getApplicationContext(), "Note: API is not done yet", Toast.LENGTH_SHORT).show();

                chooseVideoSource(EditAgentBioActivity.this);

            }
        });

        if(!agentData.getAgentProfileInformation().getYoutubeId().equals(null) && !agentData.getAgentProfileInformation().getYoutubeId().isEmpty()){
            agentYoutubeId.setText(agentData.getAgentProfileInformation().getYoutubeId());
            agentYoutubeId.setSelection(agentYoutubeId.getText().length());

        }else{
            agentYoutubeId.setText("");
        }
        if(!agentData.getAgentProfileInformation().getBiography().equals(null) && !agentData.getAgentProfileInformation().getBiography().isEmpty()){
            agentBio.setText(agentData.getAgentProfileInformation().getBiography());
            agentBio.setSelection(agentBio.getText().length());

        }else{
            agentBio.setText("");
        }
    }

    public void chooseVideoSource(final Context context) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.alertdialog_video_source);

        LinearLayout youTubeURL = (LinearLayout) dialog.findViewById(R.id.youTubeUrlLayout);
        youTubeURL.setVisibility(View.GONE);

        LinearLayout recordVideo = (LinearLayout) dialog.findViewById(R.id.recordVideoLayout);
        recordVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if (Build.VERSION.SDK_INT >= 23){
                    // Marshmallow+

                    //   requestPermissions(new String[] {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
                    showCameraWithCheck();
                } else {
                    // Pre-Marshmallow
                    captureVideo();
                }

            }
        });

        LinearLayout selectVideo = (LinearLayout) dialog.findViewById(R.id.selectVideoLayout);
        selectVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (Build.VERSION.SDK_INT >= 23) {
                    // Marshmallow+
                    readFromStorageWithCheck();
                    //   showCameraWithCheck();
                } else {
                    // Pre-Marshmallow
                    selectVideoFromStorage();
                }

            }
        });


        TextView cancel = (TextView) dialog.findViewById(R.id.textCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

        dialog.show();

    }

    private void showCameraWithCheck() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, android.Manifest.permission.CAMERA))
            permissionsNeeded.add("Camera");
        if (!addPermission(permissionsList, android.Manifest.permission.RECORD_AUDIO))
            permissionsNeeded.add("Microphone");
        if (!addPermission(permissionsList, android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Storage");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "Allow CirclePix to access to your " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++) {
                    message = message + ", " + permissionsNeeded.get(i);
                }
                showMessageOKCancel(null, "OK", "Cancel", message + " to record and upload video.",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(EditAgentBioActivity.this, permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        });
                return;
            }
            ActivityCompat.requestPermissions(EditAgentBioActivity.this, permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);

            Toast.makeText(getApplicationContext(), "Deny with Never ask again: SHOWN", Toast.LENGTH_SHORT)
                    .show();

            return;
        }

        captureVideo();
    }

    private void readFromStorageWithCheck() {
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(EditAgentBioActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(EditAgentBioActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showMessageOKCancel(null, "OK", "Cancel", "Allow CirclePix to access to your Storage to upload video from your Gallery.",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(EditAgentBioActivity.this, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                        REQUEST_CODE_ASK_PERMISSIONS);

                            }
                        });
                return;
            }

            //Show with "Never ask again"
            ActivityCompat.requestPermissions(EditAgentBioActivity.this, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS);

            Toast.makeText(getApplicationContext(), "Deny with Never ask again: SHOWN", Toast.LENGTH_SHORT)
                    .show();

            return;
        }
        selectVideoFromStorage();
    }

    private void showMessageOKCancel(String title, String positiveBtnMsg, String negativeBtnMsg, String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(EditAgentBioActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveBtnMsg, okListener)
                .setNegativeButton(negativeBtnMsg, null)
                .create()
                .show();
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (ContextCompat.checkSelfPermission(EditAgentBioActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!ActivityCompat.shouldShowRequestPermissionRationale(EditAgentBioActivity.this, permission))
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.v ("request code", String.valueOf(requestCode));
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
            {
                Log.v ("SINGLE PERMISSION", "captureVideo");
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted

                    selectVideoFromStorage();
                } else {
                    // Permission Denied

                    boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(EditAgentBioActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE );
                    if (!showRationale) {
                        // user denied flagging NEVER ASK AGAIN
                        // you can either enable some fall back,
                        // disable features of your app
                        // or open another dialog explaining
                        // again the permission and directing to
                        // the app setting
                        Toast.makeText(getApplicationContext(), "Denied with Never ask again: TRUE", Toast.LENGTH_SHORT)
                                .show();

                        showMessageOKCancel("Allow CirclePix to use your phone's storage?", "App Settings", "Not now",
                                "This lets CirclePix store and access information like videos on your phone and its SD card.\n\n" +
                                        "To enable this, click App Settings below and activate Storage under the Permissions menu.",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent();
                                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                                        intent.setData(uri);
                                        startActivity(intent);

                                    }
                                });


                    } else {
                        Toast.makeText(getApplicationContext(), "READ_EXTERNAL_PERMISSION Denied", Toast.LENGTH_SHORT)
                                .show();
                        // this is a good place to explain the user
                        // why you need the permission and ask if he want
                        // to accept it (the rationale)
                    }

                }

            }break;

            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
            {
                Log.v ("MULTIPLE PERMISSIONS", "captureVideo");
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(android.Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++) {
                    perms.put(permissions[i], grantResults[i]);
                }
                // Check for CAMERA
                Log.v ("opening", "captureVideo");
                if (perms.get(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && perms.get(android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                        && perms.get(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted

                    captureVideo();

                } else {
                    // Permission Denied

                    for (int i = 0, len = permissions.length; i < len; i++) {
                        String permission = permissions[i];
                        if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                            boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(EditAgentBioActivity.this, permission);
                            if (!showRationale) {
                                // user denied flagging NEVER ASK AGAIN
                                // you can either enable some fall back,
                                // disable features of your app
                                // or open another dialog explaining
                                // again the permission and directing to
                                // the app setting

                                Toast.makeText(getApplicationContext(), "Permission: " + permission + "is Denied with Never ask again", Toast.LENGTH_SHORT)
                                        .show();

                                showMessageOKCancel("Allow CirclePix to use camera, microphone and storage?", "App Settings", "Not now",
                                        "This lets CirclePix capture video, record audio, store and access information like videos on your phone and its SD card.\n\n" +
                                                "To enable this, click App Settings below and activate Camera, Microphone, and Storage under the Permissions menu.",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent();
                                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                                                intent.setData(uri);
                                                startActivity(intent);

                                            }
                                        });

                            } else {
                                Toast.makeText(getApplicationContext(), "Permission: " + permission + "is Denied", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    }
                }
            }break;
            default:
                Log.v ("DEFAULT", "captureVideo");
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }




    public void captureVideo(){
        Log.v ("opened", "captureVideo");
        File mediaFile = new File(Environment.getExternalStorageDirectory() + File.separator + "CirclePix" + File.separator);
        if (!mediaFile.exists()) {
            mediaFile.mkdirs();
        }

        final String fname = "vid_" + System.currentTimeMillis() + ".mp4";//Utils.getUniqueImageFilename();

        final File sdImageMainDirectory = new File(mediaFile, fname);

        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        // Add file uri to intent
        captureVideoFileUri = Uri.fromFile(sdImageMainDirectory);
        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, captureVideoFileUri);
        startActivityForResult(takeVideoIntent, CAPTURE_VIDEO);

    }  //ends here

    public void selectVideoFromStorage(){

        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a Video"), SELECT_VIDEO);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("LOGCAT", "resultCode is " + resultCode + " requestCode is " + requestCode);
        Log.d("LOGCAT", "result_ok is " + RESULT_OK);

        String agentName = "";
        String agentAgency = "";
        String agentImage = "";

        if(!agentData.getAgentProfileInformation().getFullName().isEmpty() && agentData.getAgentProfileInformation().getFullName() != null){
            agentName = agentData.getAgentProfileInformation().getFullName();
        }

        if(!agentData.getAgentProfileInformation().getAgency().isEmpty() && agentData.getAgentProfileInformation().getAgency() != null){
            agentAgency = agentData.getAgentProfileInformation().getAgency();
        }

        if(!agentData.getAgentProfileInformation().getAgentImage().isEmpty() && agentData.getAgentProfileInformation().getAgentImage() != null){
            agentImage = agentData.getAgentProfileInformation().getAgentImage();
        }

        if (resultCode == RESULT_OK && requestCode == SELECT_VIDEO) {

            Uri selectedVideoUri = data.getData();

            Log.d("LOGCAT", "data is " + data.getData());
            selectedVideoPath = getPath(EditAgentBioActivity.this, selectedVideoUri);

            Log.d("LOGCAT", "Video path is this: " + selectedVideoPath);

            Intent intent = new Intent(EditAgentBioActivity.this, VideoUploadActivity.class);
            intent.putExtra("objectType", "realtor");
            intent.putExtra("videoPath", selectedVideoPath);
            intent.putExtra("id", agentData.getRealtor().getId());
            intent.putExtra("code", agentData.getRealtor().getCode());
            intent.putExtra("line1", agentName);
            intent.putExtra("line2", agentAgency);
            intent.putExtra("imgURL", agentImage);
            intent.putExtra("type", "agentprofile");
            startActivity(intent);

        } else if (requestCode == CAPTURE_VIDEO) {
            if (resultCode == RESULT_OK) {

                if (captureVideoFileUri != null) {
                    Log.d("LOGCAT", "Video saved to:\n" + captureVideoFileUri);
                    Log.d("LOGCAT", "Video path:\n" + captureVideoFileUri.getPath());

                    //solution to recently taken videos not showing in gallery unless device is restarted
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        File f = new File(captureVideoFileUri.getPath());
                        Uri contentUri = Uri.fromFile(f);
                        mediaScanIntent.setData(contentUri);
                        sendBroadcast(mediaScanIntent);
                    } else {
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse(captureVideoFileUri.getPath())));
                    }

                    Intent intent = new Intent(EditAgentBioActivity.this, VideoUploadActivity.class);
                    intent.putExtra("objectType", "realtor");
                    intent.putExtra("videoPath", captureVideoFileUri.getPath());
                    intent.putExtra("id", agentData.getRealtor().getId());
                    intent.putExtra("code", agentData.getRealtor().getCode());
                    intent.putExtra("line1", agentName);
                    intent.putExtra("line2", agentAgency);
                    intent.putExtra("imgURL", agentImage);
                    intent.putExtra("type", "agentprofile");
                    startActivity(intent);

                }

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Video recording cancelled.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to record video.", Toast.LENGTH_SHORT).show();
            }

        }

        if (resultCode == 2) {
            finish();
        }
    }


    //getPath starts here
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }
    //ends here


    public void editAgentBio(){

        Thread networkThread = new Thread() {

            public void run() {


                String strAgentYoutubeId = "";
                String strAgentBio = "";

                if(!agentYoutubeId.getText().toString().isEmpty()){
                    strAgentYoutubeId = agentYoutubeId.getText().toString();
                }else{
                    strAgentYoutubeId = "";
                }
                if(!agentBio.getText().toString().isEmpty()){
                    strAgentBio = agentBio.getText().toString();
                }else{
                    strAgentBio = "";
                }

                Log.v("strAgentYoutubeId", strAgentYoutubeId);
                Log.v("strAgentBio", strAgentBio);

                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog = ProgressDialog.show(EditAgentBioActivity.this, "", "Saving...");
                        }
                    });

                    String BASE_URL = "http://stag-mobile.circlepix.com/api/agentProfile.php";
                    //String BASE_URL = "http://keuahn.circlepix.dev/api/agentProfile.php";
                    MultipartBody.Builder buildernew = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("method", "updateAgentInfo")
                            .addFormDataPart("realtorId", agentData.getRealtor().getId())
                            .addFormDataPart("form", "agentBio")
                            .addFormDataPart("personalYoutubeId", strAgentYoutubeId)
                            .addFormDataPart("bio", strAgentBio);


                    MultipartBody requestBody = buildernew.build();

                    Request request = new Request.Builder()
                            .url(BASE_URL)
                            .post(requestBody)
                            .build();

                    Log.v("update credentials: ", String.valueOf(requestBody));
                    OkHttpClient client = new OkHttpClient();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, final IOException e) {
                            Log.i("Failed: " ,  e.getMessage());
//                            Intent intent = new Intent(getApplicationContext(), AddListingImagesActivity.class);
//                            intent.putExtra("responseBody","Failed: " + e.getLocalizedMessage() );
//                            startActivity(intent);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //stuff that updates ui
                                    mProgressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Failed: "+ e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, final Response response) throws IOException {

                            final String responseString;
                            String status;
                            String message;

                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    responseString = response.body().string();
                                    Log.i("Done", responseString);
                                    response.body().close();

                                    try {

                                        JSONObject Jobject = new JSONObject(responseString);

                                        status = Jobject.getString("status");
                                        message = Jobject.getString("message");

                                        Log.v("status: ", status);
                                        Log.v("message: ", message);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //stuff that updates ui
                                                mProgressDialog.dismiss();
                                            }
                                        });

                                    }
                                    catch (JSONException e) {
                                        Log.v("Error: ", e.getLocalizedMessage());
                                    }
                                    finish();

                                }
                            } else {
                                Log.i("Error", "unsuccessful");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mProgressDialog.dismiss();
                                    }
                                });

                                finish();
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        networkThread.start();
    }


}

package com.circlepix.android;

import android.*;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.circlepix.android.beans.AgentData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

public class EditAgentProfilePictureActivity extends AppCompatActivity {

    private AgentData agentData;

    private TextView toolBarSave;
    private ImageView agentImage, uploadImage, deleteImage;
    private ProgressDialog mProgressDialog;

    private List<File> cameraImageFiles;
 //   static final int ACTIVITY_REQUEST_CODE_IMAGE = 101;

    private String agentImageBase64Format = "";

    private Uri captureImageFileUri;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 143;

    private final int SELECT_IMAGE = 1;
    private final int CAPTURE_IMAGE = 101;

    private String selectedImagePath;
    private Boolean isDeletePhotoSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_agent_picture);

        // Get global shared data
        agentData = AgentData.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolBarSave = (TextView) findViewById(R.id.toolbar_save);
        toolBarSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(agentImageBase64Format.isEmpty()){
                    //no changes in current image
                    Toast.makeText(getApplicationContext(), "Please select a new photo to upload.", Toast.LENGTH_SHORT).show();
                }else{
                    updateAgentImage();
                }

            }
        });


        agentImage = (ImageView)findViewById(R.id.agent_picture);
        uploadImage = (ImageView)findViewById(R.id.upload_agent_image_side);
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              
                chooseImageSource();
            }
        });

        deleteImage = (ImageView)findViewById(R.id.delete_agent_image);
        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(agentImage.getDrawable() == null){
                    //no changes in current image
                    Toast.makeText(getApplicationContext(), "No existing Agent Picture to delete.", Toast.LENGTH_SHORT).show();
                }else{

                    Glide.with(getApplicationContext()).load("").into(agentImage);

                    if(!agentData.getAgentProfileInformation().getAgentImage().isEmpty() ){
                        isDeletePhotoSelected = true;
                        updateAgentImage();
                    }

                }
            }
        });


        if(!agentData.getAgentProfileInformation().getAgentImage().isEmpty()){

            Glide.with(getApplicationContext())
                    .load(agentData.getAgentProfileInformation().getAgentImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .error(R.drawable.broken_file_icon)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
//                    .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                    .into(agentImage);
        }
    }


    public void chooseImageSource() {

        final Dialog dialog = new Dialog(EditAgentProfilePictureActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.alerdialog_image_source);

        LinearLayout captureImage = (LinearLayout) dialog.findViewById(R.id.captureImageLayout);
        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();


                if (Build.VERSION.SDK_INT >= 23){
                    // Marshmallow+

                    //   requestPermissions(new String[] {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
                    showCameraWithCheck();
                } else {
                    // Pre-Marshmallow
                   captureImage();
                }

            }
        });

        LinearLayout selectImage = (LinearLayout) dialog.findViewById(R.id.selectImageLayout);
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if (Build.VERSION.SDK_INT >= 23) {
                    // Marshmallow+
                    readFromStorageWithCheck();

                } else {
                    // Pre-Marshmallow
                    selectImageFromStorage();

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
        if (!addPermission(permissionsList, Manifest.permission.RECORD_AUDIO))
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
                showMessageOKCancel(null, "OK", "Cancel", message + " to capture and upload photos.",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(EditAgentProfilePictureActivity.this, permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        });
                return;
            }
            ActivityCompat.requestPermissions(EditAgentProfilePictureActivity.this, permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);

            Toast.makeText(EditAgentProfilePictureActivity.this, "Deny with Never ask again: SHOWN", Toast.LENGTH_SHORT)
                    .show();

            return;
        }

        captureImage();
    }

    private void readFromStorageWithCheck() {
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(EditAgentProfilePictureActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(EditAgentProfilePictureActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showMessageOKCancel(null, "OK", "Cancel", "Allow CirclePix to access to your Storage to upload photos from your Gallery.",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(EditAgentProfilePictureActivity.this, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                        REQUEST_CODE_ASK_PERMISSIONS);

                            }
                        });
                return;
            }

            //Show with "Never ask again"
            ActivityCompat.requestPermissions(EditAgentProfilePictureActivity.this, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS);

            Toast.makeText(EditAgentProfilePictureActivity.this, "Deny with Never ask again: SHOWN", Toast.LENGTH_SHORT)
                    .show();

            return;
        }
        selectImageFromStorage();
    }

    private void showMessageOKCancel(String title, String positiveBtnMsg, String negativeBtnMsg, String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(EditAgentProfilePictureActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveBtnMsg, okListener)
                .setNegativeButton(negativeBtnMsg, null)
                .create()
                .show();
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (ContextCompat.checkSelfPermission(EditAgentProfilePictureActivity.this,permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!ActivityCompat.shouldShowRequestPermissionRationale(EditAgentProfilePictureActivity.this, permission))
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
                Log.v ("SINGLE PERMISSION", "captureImage");
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted

                    selectImageFromStorage();
                } else {
                    // Permission Denied

                    boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(EditAgentProfilePictureActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE );
                    if (!showRationale) {
                        // user denied flagging NEVER ASK AGAIN
                        // you can either enable some fall back,
                        // disable features of your app
                        // or open another dialog explaining
                        // again the permission and directing to
                        // the app setting
                        Toast.makeText(EditAgentProfilePictureActivity.this, "Denied with Never ask again: TRUE", Toast.LENGTH_SHORT)
                                .show();

                        showMessageOKCancel("Allow CirclePix to use your phone's storage?", "App Settings", "Not now",
                                "This lets CirclePix store and access information like photos on your phone and its SD card.\n\n" +
                                        "To enable this, click App Settings below and activate Storage under the Permissions menu.",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent();
                                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", EditAgentProfilePictureActivity.this.getPackageName(), null);
                                        intent.setData(uri);
                                        startActivity(intent);

                                    }
                                });
                    } else {
                        Toast.makeText(EditAgentProfilePictureActivity.this, "READ_EXTERNAL_PERMISSION Denied", Toast.LENGTH_SHORT)
                                .show();
                        // this is a good place to explain the user
                        // why you need the permission and ask if he want
                        // to accept it (the rationale)
                    }

                }

            }break;

            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
            {
                Log.v ("MULTIPLE PERMISSIONS", "captureImage");
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
                Log.v ("opening", "captureImage");
                if (perms.get(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && perms.get(android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                        && perms.get(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted

                    captureImage();

                } else {
                    // Permission Denied

                    for (int i = 0, len = permissions.length; i < len; i++) {
                        String permission = permissions[i];
                        if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                            boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(EditAgentProfilePictureActivity.this, permission);
                            if (!showRationale) {
                                // user denied flagging NEVER ASK AGAIN
                                // you can either enable some fall back,
                                // disable features of your app
                                // or open another dialog explaining
                                // again the permission and directing to
                                // the app setting

                                Toast.makeText(EditAgentProfilePictureActivity.this, "Permission: " + permission + "is Denied with Never ask again", Toast.LENGTH_SHORT)
                                        .show();

                                showMessageOKCancel("Allow CirclePix to use camera and storage?", "App Settings", "Not now",
                                        "This lets CirclePix capture photo, store and access information like photos on your phone and its SD card.\n\n" +
                                                "To enable this, click App Settings below and activate Camera and Storage under the Permissions menu.",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent();
                                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                Uri uri = Uri.fromParts("package", EditAgentProfilePictureActivity.this.getPackageName(), null);
                                                intent.setData(uri);
                                                startActivity(intent);

                                            }
                                        });
                            } else {
                                Toast.makeText(EditAgentProfilePictureActivity.this, "Permission: " + permission + "is Denied", Toast.LENGTH_SHORT)
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




    public void captureImage(){
        Log.v ("opened", "captureVideo");
        File mediaFile = new File(Environment.getExternalStorageDirectory() + File.separator + "CirclePix" + File.separator);
        if (!mediaFile.exists()) {
            mediaFile.mkdirs();
        }

        final String fname = "image_" + System.currentTimeMillis() + ".jpg";//Utils.getUniqueImageFilename();

        final File sdImageMainDirectory = new File(mediaFile, fname);

        Intent takeImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE );
        // Add file uri to intent
        captureImageFileUri = Uri.fromFile(sdImageMainDirectory);
        takeImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, captureImageFileUri);
        startActivityForResult(takeImageIntent, CAPTURE_IMAGE);
    }  //ends here


    public void selectImageFromStorage(){

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a Photo"), SELECT_IMAGE);
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("LOGCAT", "resultCode is " + resultCode + " requestCode is " + requestCode);
        Log.d("LOGCAT", "result_ok is " + RESULT_OK);

        if ((resultCode == RESULT_OK && requestCode == SELECT_IMAGE)) {
      //  if (requestCode == SELECT_IMAGE) {
         //   if (resultCode == RESULT_OK) {
                Uri selectedImageUri = data.getData();

                Log.d("LOGCAT", "data is " + data.getData());
                selectedImagePath = getPath(EditAgentProfilePictureActivity.this, selectedImageUri);

                Log.d("LOGCAT", "Video path is this: " + selectedImagePath);


                Glide.with(getApplicationContext()).load(selectedImageUri).centerCrop().into(agentImage);
                agentImageBase64Format = "data:image/jpeg;base64," + convertToBase64(getPath(EditAgentProfilePictureActivity.this, selectedImageUri));

                toolBarSave.setEnabled(true);
        //    }

        } else if (requestCode == CAPTURE_IMAGE) {
            if (resultCode == RESULT_OK) {

                if (captureImageFileUri != null) {
                    Log.d("LOGCAT", "Photo saved to:\n" + captureImageFileUri);
                    Log.d("LOGCAT", "Photo path:\n" + captureImageFileUri.getPath());

                    //solution to recently taken videos not showing in gallery unless device is restarted
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        File f = new File(captureImageFileUri.getPath());
                        Uri contentUri = Uri.fromFile(f);
                        mediaScanIntent.setData(contentUri);
                        EditAgentProfilePictureActivity.this.sendBroadcast(mediaScanIntent);
                    } else {
                        EditAgentProfilePictureActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse(captureImageFileUri.getPath())));
                    }

                    Glide.with(getApplicationContext()).load(captureImageFileUri).centerCrop().into(agentImage);
                    agentImageBase64Format = "data:image/jpeg;base64,"+ convertToBase64(getPath(EditAgentProfilePictureActivity.this, captureImageFileUri));

                    toolBarSave.setEnabled(true);
                }

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(EditAgentProfilePictureActivity.this.getApplicationContext(), "Capturing photo cancelled.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(EditAgentProfilePictureActivity.this.getApplicationContext(), "Failed to capture photo.", Toast.LENGTH_SHORT).show();
            }
        }


        if (resultCode == 2) {
            EditAgentProfilePictureActivity.this.finish();
        }
    }



 /*   private void addPhoto(){
        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);

        cameraImageFiles = new ArrayList<File>();

        int i=0;
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.MEDIA_IGNORE_FILENAME, ".nomedia");

            File cameraImageOutputFile = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    createCameraImageFileName());
            cameraImageFiles.add(cameraImageOutputFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraImageFiles.get(i)));
            i++;

            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Choose option:");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));
        startActivityForResult(chooserIntent, ACTIVITY_REQUEST_CODE_IMAGE);
    }


    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case ACTIVITY_REQUEST_CODE_IMAGE:
                if(resultCode == RESULT_OK){

                    Uri uri = null;
                    if(imageReturnedIntent == null){   //since we used EXTRA_OUTPUT for camera, so it will be null

                        for(int i=0;i<cameraImageFiles.size();i++){
                            if(cameraImageFiles.get(i).exists()){
                                uri = Uri.fromFile(cameraImageFiles.get(i));
                                break;
                            }
                        }
                        Log.d("attachimage", "from camera: "+uri);
                    }
                    else {  // from gallery
                        uri = imageReturnedIntent.getData();
                        Log.d("attachimage", "from gallery: "+uri.toString());
                    }

                    if(uri != null){
                        Glide.with(getApplicationContext()).load(uri).centerCrop().into(agentImage);

                        agentImageBase64Format = "data:image/jpeg;base64,"+ convertToBase64(getPath(EditAgentProfilePictureActivity.this, uri));
//                        agentImageBase64Format = "data:image/jpeg;base64,"+ encodeImage(getPath(EditAgentProfilePictureActivity.this, uri));
                    }
                }
        }
    }


    private String createCameraImageFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return timeStamp + ".jpg";
    }
    private String encodeImage(String path){
        File imagefile = new File(path);
        FileInputStream fis = null;
        try{
            fis = new FileInputStream(imagefile);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        Bitmap bm = BitmapFactory.decodeStream(fis);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        //Base64.de
        return encImage;

    }

  */

    private String convertToBase64(String imagePath) {

        int rotate = 0;
        try {
            //   getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(
                    imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        /****** Image rotation ****/

        Bitmap bmp = BitmapFactory.decodeFile(imagePath);

        Log.v("Rotate degress", String.valueOf(rotate));
        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);
        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 70, baos);

        byte[] byteArrayImage = baos.toByteArray();
        String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

        return encodedImage;

    }

   /* private String convertToBase64(String imagePath) {

        int rotate = 0;

        try {
            ExifInterface exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch(orientation) {
                case  ExifInterface.ORIENTATION_ROTATE_270:
                    rotate-=90;break;
                case  ExifInterface.ORIENTATION_ROTATE_180:
                    rotate-=90;break;
                case  ExifInterface.ORIENTATION_ROTATE_90:
                    rotate-=90;break;
            }
            Log.d("Fragment", "EXIF info for file " + imagePath + ": " + rotate);
        } catch (IOException e) {
            Log.d("Fragment", "Could not get EXIF info for file " + imagePath + ": " + e);
        }


        Bitmap bmp = BitmapFactory.decodeFile(imagePath);

        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);
        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

//        Bitmap bm = BitmapFactory.decodeFile(imagePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        bmp.compress(Bitmap.CompressFormat.JPEG, 70, baos);

        byte[] byteArrayImage = baos.toByteArray();
        String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

        return encodedImage;

    }*/

    public void updateAgentImage(){

        Thread networkThread = new Thread() {

            public void run() {


                String strAgentImage = "";


                if(!agentImageBase64Format.isEmpty()){
                    strAgentImage = agentImageBase64Format;
                }else{
                    strAgentImage = "";
                }

                Log.v("strAgentImageBase64", strAgentImage);

                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(isDeletePhotoSelected == true){
                                mProgressDialog = ProgressDialog.show(EditAgentProfilePictureActivity.this, "", "Deleting Agent Photo...");
                            }else{
                                mProgressDialog = ProgressDialog.show(EditAgentProfilePictureActivity.this, "", "Saving...");
                            }

                        }
                    });

                    String BASE_URL = "http://stag-mobile.circlepix.com/api/agentProfile.php";
                   // String BASE_URL = "http://keuahn.circlepix.dev/api/agentProfile.php";

                    MultipartBody.Builder buildernew;

                    if(isDeletePhotoSelected == true){
                         buildernew = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("method", "updateAgentInfo")
                                .addFormDataPart("realtorId", agentData.getRealtor().getId())
                                .addFormDataPart("form", "deleteAgentPicture");

                        Log.v("update agent image: ", "delete Image");
                    }else{

                        buildernew = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("method", "updateAgentInfo")
                                .addFormDataPart("realtorId", agentData.getRealtor().getId())
                                .addFormDataPart("form", "agentPicture")
                                .addFormDataPart("image", strAgentImage);

                        Log.v("update agent image: ", "upload image");
                    }


                    MultipartBody requestBody = buildernew.build();

                    Request request = new Request.Builder()
                            .url(BASE_URL)
                            .post(requestBody)
                            .build();

                    Log.v("update agent image: ", String.valueOf(buildernew.toString()));
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
                                    isDeletePhotoSelected = false;
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

                                                if(isDeletePhotoSelected == true){
                                                    Glide.with(getApplicationContext()).load("").centerCrop().into(agentImage);
                                                    isDeletePhotoSelected = false;
                                                    agentData.getAgentProfileInformation().setAgentImage("");
                                                    toolBarSave.setEnabled(false); // no need for user to click Save so just disable the Save navigation item button
                                                }else{
                                                    finish();
                                                }
                                            }
                                        });

                                    }
                                    catch (JSONException e) {
                                        Log.v("Error: ", e.getLocalizedMessage());
                                    }

                                }
                            } else {
                                Log.i("Error", "unsuccessful");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mProgressDialog.dismiss();

                                        if(isDeletePhotoSelected == true){

                                            isDeletePhotoSelected = false;
                                        }else{
                                            finish();
                                        }
                                    }
                                });


                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    isDeletePhotoSelected = false;
                }
            }
        };
        networkThread.start();
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
}
 /*   private void addPhoto() {
        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.MEDIA_IGNORE_FILENAME, ".nomedia");

            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image*//*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
//        final Intent chooserIntent = Intent.createChooser(galleryIntent, getString(R.string.add_new));
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Gallery");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));
        startActivityForResult(chooserIntent, 101);
    }*/


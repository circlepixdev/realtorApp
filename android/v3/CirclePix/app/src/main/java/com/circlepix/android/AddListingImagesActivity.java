package com.circlepix.android;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.circlepix.android.beans.AgentData;
import com.circlepix.android.beans.ListingInformation;
import com.circlepix.android.helpers.CountingRequestBody;
import com.circlepix.android.helpers.ExpandableHeightGridView;
import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Keuahn on 9/22/2016.
 */

public class AddListingImagesActivity extends AppCompatActivity {

    private AgentData agentData;
    private CirclePixAppState appState;

    protected InitTask initTask;
    private ProgressDialog progressBar;
    private ProgressDialog mProgressDialog;

    private ArrayList<String> images  =  new ArrayList<String>();
    private ArrayList<String> base64StringImages =  new ArrayList<String>();

    private MyAdapter mAdapter;
    private Button addPhotos;
    private TextView toolBarSave , noteTextView;

    private int selectedListingPosition;
    private ListingInformation selectedListing;
    private String homeId;

    private boolean cancelled = false;
    private boolean isActiveListing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_listing_images);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        toolBarSave = (TextView) findViewById(R.id.toolbar_save);
        toolBarSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            if(images.size() == 0){
           // if(agentImageBase64Format.isEmpty()){
                //no changes in current image
                Toast.makeText(getApplicationContext(), "Please add photos to upload.", Toast.LENGTH_SHORT).show();
            }else{

                if(isNetworkAvailable()){
                    base64StringImages.clear();
                    for (int i = 0; i < images.size(); i++){
                        base64StringImages.add("data:image/jpeg;base64,"+ convertToBase64(images.get(i).toString()));

                        if(i == images.size() - 1){
                            Log.v("gonna upload the images", "yeah");
                            //upload images
                            initTask = new InitTask();

                            if(Build.VERSION.SDK_INT >= 11) {
                                initTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }else {
                                initTask.execute();
                            }
                        }
                    }
                }else{

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddListingImagesActivity.this);
                    alertDialogBuilder.setTitle("No Connection");
                    alertDialogBuilder
                            .setMessage("Currently there is no network connection. Please connect to the Internet to upload images.")
                            .setPositiveButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog, int id) {

                                            dialog.dismiss();
                                        }
                                    });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();

                }
            }
            }
        });


        // Get global shared data
        agentData = AgentData.getInstance();

        appState = ((CirclePixAppState)getApplicationContext());
        appState.setContextForPreferences(this);

        appState.setActiveClassName("ListingsTabActivity");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if(extras.containsKey("selectedListingPosition")){
                selectedListingPosition = extras.getInt("selectedListingPosition");
                Log.v("selectedListingPos: ", String.valueOf(selectedListingPosition));

                selectedListing = agentData.getListingInformation().get(selectedListingPosition);
                homeId = selectedListing.getId();
                isActiveListing = true;
                Log.v("extras homeId: ", homeId);

            }else if(extras.containsKey("homeId")){
                homeId = extras.getString("homeId");
                isActiveListing = false;
                Log.v("extras homeId: ", extras.getString("homeId"));
            }

            images = (ArrayList<String>) getIntent().getSerializableExtra("images");
        }

        noteTextView = (TextView) findViewById(R.id.note_textView);
        if(isActiveListing == true){
            noteTextView.setVisibility(View.GONE);
        }else {
            noteTextView.setVisibility(View.VISIBLE);
        }

    //    GridView gallery = (GridView) findViewById(R.id.galleryGridView);
        ExpandableHeightGridView gallery = (ExpandableHeightGridView) findViewById(R.id.galleryGridView);
        gallery.setExpanded(true);
        mAdapter = new MyAdapter(this);

        // gallery.setAdapter(new ImageAdapter(this));
        gallery.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                if (null != images && !images.isEmpty())
                    Toast.makeText(
                            getApplicationContext(),
                            "position " + position + " " + images.get(position),
                            Toast.LENGTH_SHORT).show();

                    images.remove(position);
                    mAdapter.notifyDataSetChanged();

                if(images.size() >= 10){

                  //  addPhotos.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                    addPhotos.setBackgroundResource(R.drawable.circlepix_button_press);

                }else {
                    addPhotos.setEnabled(true);
                    addPhotos.setBackgroundResource(R.drawable.circlepix_button_selector);
                }
            }
        });

        addPhotos = (Button) findViewById(R.id.add_photos_button);
        addPhotos.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(images.size() >= 10) {
                    Toast.makeText(getApplicationContext(), "Cannot add more than 10 photos per upload", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(getApplicationContext() , AlbumSelectActivity.class);
                    //set limit on number of images that can be selected, default is 10
                    intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 10 - images.size());
                    startActivityForResult(intent, Constants.REQUEST_CODE);
                }

            }
        });

        if(images.size() >= 10){
           // addPhotos.setEnabled(false);
           // addPhotos.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
            addPhotos.setBackgroundResource(R.drawable.circlepix_button_press);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if  (requestCode == Constants.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            //The array list has the image paths of the selected images
            ArrayList<Image> imagePaths = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);


            for (int i = 0; i < imagePaths.size(); i++) {
                images.add(imagePaths.get(i).path);
                Log.v("images " + i  , String.valueOf(imagePaths.get(i).toString()));

                if(i == imagePaths.size() -1){

                    if(images.size() >= 10){

                        //  addPhotos.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                        addPhotos.setBackgroundResource(R.drawable.circlepix_button_press);

                    }else {
                        addPhotos.setEnabled(true);
                        addPhotos.setBackgroundResource(R.drawable.circlepix_button_selector);
                    }

                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private final class MyAdapter extends BaseAdapter {
        private final List<Item> mItems = new ArrayList<Item>();
        private final LayoutInflater mInflater;
        private Context context;

        public MyAdapter(Context context) {
            this.context = context;
            mInflater = LayoutInflater.from(context);
           // images = imagePaths;

//            mItems.add(new Item("Red",       R.drawable.red));
//            mItems.add(new Item("Magenta",   R.drawable.magenta));
//            mItems.add(new Item("Dark Gray", R.drawable.dark_gray));
//            mItems.add(new Item("Gray",      R.drawable.gray));
//            mItems.add(new Item("Green",     R.drawable.green));
//            mItems.add(new Item("Cyan",      R.drawable.cyan));
        }

        public int getCount() {
            return images.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = view;
            ImageView picture;
            final ProgressBar progressBar;

            TextView name;

            if (v == null) {
                v = mInflater.inflate(R.layout.grid_item, viewGroup, false);
                v.setTag(R.id.picture, v.findViewById(R.id.picture));
                v.setTag(R.id.progressBar, v.findViewById(R.id.progressBar));
            }

            picture = (ImageView) v.getTag(R.id.picture);
            progressBar = (ProgressBar) v.getTag(R.id.progressBar);

            Glide.with(context)
                    .load(images.get(i))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.circlepix_bg)
                    .error(R.drawable.broken_file_icon)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })

                    .into(picture);

//            Glide.with(context).load(images.get(i))
//                   // .placeholder(R.drawable.ic_launcher).centerCrop()
//                    .into(picture);

            return v;
        }

        private class Item {
            public final String name;
            public final int drawableId;

            Item(String name, int drawableId) {
                this.name = name;
                this.drawableId = drawableId;
            }
        }
    }

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

    public String getToArrayString(ArrayList<String> arrayData) {
        StringBuilder stringBuilder = new StringBuilder();
        Log.v("arrayData size", String.valueOf(arrayData.size()));
        for (int i = 0 ; i < arrayData.size(); i++) {
            stringBuilder.append(arrayData.get(i));
            if (i < arrayData.size() - 1) {
                stringBuilder.append(",");
            }
        }

        Log.v("stringBuilder()", stringBuilder.toString());
        return stringBuilder.toString();
    }

    public String getToString(String[] arrayData) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0 ; i < arrayData.length; i++) {
            stringBuilder.append(arrayData[i]);
            if (i < arrayData.length - 1) {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }

    protected class InitTask extends AsyncTask<Context, Integer, String> {
        int progressInt = 0;


        // -- gets called just before thread begins
        @Override
        protected void onPreExecute()
        {
            cancelled = false;


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar = new ProgressDialog(AddListingImagesActivity.this);
                    progressBar.setMessage("Uploading Images...");
                    progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressBar.setProgress(0);
                    progressBar.setMax(100);
                    progressBar.setCancelable(false);
                    progressBar.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {

                            cancel(true);
                        }
                    });
                    progressBar.show();
                }
            });


            Log.i( "makemachine", "onPreExecute()" );
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Context... params) {

            // Do the video upload on a new thread
//                	Thread networkThread = new Thread() {
//
//						public void run() {
            while (!cancelled) {
                try {

                    JSONArray jArry=new JSONArray();
                    for(int i=0; i<base64StringImages.size(); i++) {
                        jArry.put(base64StringImages.get(i));
                    }

                    Log.v("jArry size:", String.valueOf(jArry.length()));

                    String BASE_URL = "http://stag-mobile.circlepix.com/api/listing.php";

                    MultipartBody.Builder buildernew = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("method", "addListingPhotos")
                            .addFormDataPart("realtorId", agentData.getRealtor().getId())
                            .addFormDataPart("tourID", homeId)
                        //    .addFormDataPart("images", jArry.toString())  // "[" + getToArrayString(base64StringImages) + "]")
                            .addFormDataPart("mlsCompliant", "");

                        if (base64StringImages.size() > 1){
                            for (int i = 0; i < base64StringImages.size(); i++) {
                                buildernew.addFormDataPart("images[" + i + "]", base64StringImages.get(i));
                                Log.v("images[" + i + "]", base64StringImages.get(i).toString());
                            }
                        }else{
                            buildernew.addFormDataPart("images", base64StringImages.get(0));
                            Log.v("images", base64StringImages.get(0).toString());
                        }


                    MultipartBody requestBody = buildernew.build();

                    Log.i( "BASE_URL", BASE_URL);
                    Log.i( "realtorId", agentData.getRealtor().getId());
                    Log.i( "tourID", homeId);
                   // Log.i( "images", getToArrayString(base64StringImages));


                    CountingRequestBody monitoredRequest = new CountingRequestBody(requestBody, new CountingRequestBody.Listener() {
                        @Override
                        public void onRequestProgress(long bytesWritten, long contentLength) {
                            //Update a progress bar with the following percentage
                            float percentage = 100f * bytesWritten / contentLength;

                            publishProgress((int) ((bytesWritten / (float) contentLength) * 100));

                            progressInt = (int) ((bytesWritten / (float) contentLength) * 100);
                        }
                    });

                    Request request = new Request.Builder()
                            .url(BASE_URL)
                            .post(monitoredRequest)
                            .build();

                    OkHttpClient client = new OkHttpClient();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.i("Video Uploaaadd", "Failed: " + e.getLocalizedMessage());
                           // progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    progressBar.dismiss();
                                }
                            });

                            Toast.makeText(getApplicationContext(), "Upload Failed: Connection Timeout", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onResponse(Call call, final Response response) throws IOException {

                            String respBody;
                            String status;
                            String message;

                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    respBody = response.body().string();
                                    Log.i("image Uploaaadd", respBody);
                                    response.body().close();

                                    try {

                                        JSONObject Jobject = new JSONObject(respBody);

                                        status = Jobject.getString("status");
                                        message = Jobject.getString("message");

                                        Log.v("status: ", status);
                                        Log.v("message: ", message);



                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressBar.dismiss();
                                                sendSuccessAlertDialog();
                                            }
                                        });


                                    }
                                    catch (JSONException e) {
                                        Log.v("Error: ", e.getLocalizedMessage());
                                    }





                                 //   finish();
                                }
                            } else {
                                Log.i("image Uploaaadd", "unsuccessful");
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }

                //-- on every iteration
                //-- runs a while loop that causes the thread to sleep for 50 milliseconds
                //-- publishes the progress - calls the onProgressUpdate handler defined below
                //-- and increments the counter variable i by one
                while (progressInt <= 100)

                {
                    if (cancelled) break;

                    try {
                        Thread.sleep(50);
                        //publishProgress( i );
                    } catch (Exception e) {
                        Log.i("makemachine", (e.getMessage() == null) ? e.getMessage() : e.toString());
                    }
                }

                return "COMPLETE!";
            }
            return "cancelled";
        }

        // -- called from the publish progress
        // -- notice that the datatype of the second param gets passed to this method
        @Override
        protected void onProgressUpdate(final Integer... values)
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setProgress((int) (values[0]));
                }
            });

            super.onProgressUpdate(values);
            Log.i( "makemachine", "onProgressUpdate(): " +  String.valueOf( values[0] ) );

        }

        // -- called if the cancel button is pressed
        @Override
        protected void onCancelled()
        {
            super.onCancelled();
            initTask.cancel(true);

            finish();
            cancelled = true;
            Log.i( "makemachine", "onCancelled()" );
        }

        // -- called as soon as doInBackground method completes
        // -- notice that the third param gets passed to this method
        @Override
        protected void onPostExecute( String result )
        {
           // super.onPostExecute(result);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.dismiss();

                }
            });

           // finish();


            Log.i( "makemachine", "onPostExecute(): " + result );
        }
    }

    public void sendSuccessAlertDialog() {


        this.runOnUiThread(new Runnable() {
            public void run() {

                if (!cancelled) {
                    if(isActiveListing == true){ //don't ask to post listing because it's already posted as active listing
                        finish();
                    }else{
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddListingImagesActivity.this);
                        alertDialogBuilder.setTitle("Post Your Listing");
                        alertDialogBuilder
                                .setMessage("Do you want to Post this listing?.")
                                .setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog, int id) {

                                                if(isNetworkAvailable()){
                                                    postListing();
                                                }else{
                                                    dialog.dismiss();
                                                    Toast.makeText(getApplicationContext(), "Cannot be posted, No Internet Connection", Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        })
                                .setNegativeButton("No",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog, int id) {

                                                dialog.dismiss();
                                                finish();

                                            }
                                        });

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();
                    }


                }

            }
        });
    }


   public void postListing(){

        Thread networkThread = new Thread() {

            public void run() {

                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog = ProgressDialog.show(AddListingImagesActivity.this, "", "Saving...");
                        }
                    });

                    String BASE_URL = "http://stag-mobile.circlepix.com/api/listing.php";

                    MultipartBody.Builder buildernew = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("method", "postListing")
                            .addFormDataPart("realtorId", agentData.getRealtor().getId())
                            .addFormDataPart("tourId", homeId);

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
                         //   Log.i("Failed: " ,  e.getMessage());
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
                                                finish();
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

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected())
        {
            isAvailable = true;

        }
        return isAvailable;
    }
}

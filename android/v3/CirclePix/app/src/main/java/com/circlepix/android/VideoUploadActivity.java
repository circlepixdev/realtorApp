package com.circlepix.android;

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
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.circlepix.android.beans.AgentData;
import com.circlepix.android.helpers.CountingRequestBody;
import com.circlepix.android.helpers.RestrictedSocketFactory;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
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
 * Created by Keuahn on 7/2/2016.
 */
public class VideoUploadActivity extends AppCompatActivity{

    private CoordinatorLayout rootLayout;
    private EditText mVideoTitle;
    private EditText mVideoDescription;
    private Button mUploadVideoButton;
    private TextView mFileSize;

    private String mObjectType;
    private String mVideoPath;
    private String mId;
    private String mCode;
    private String mLine1;
    private String mLine2;
    private String mImgURL;
    private String type;

    private String mTitleStr;
    private String mDescriptionStr;

    private ImageView mVideoImageView;
    private ProgressBar progressBar;
//    private ProgressDialog progressDialog;
    private ProgressDialog notifProgressDialog;

    private AgentData agentData;
    private CirclePixAppState appState;

    private long totalSize;
    private boolean isWifi, is4g;
    private AlertDialog myAlertDialog;
    private boolean done = false;
    private boolean cancelled = false;

    protected InitTask initTask;
    private Intent notifIntent;
    private ComponentName rootActivity;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private int id = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_upload);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Setup application class
        appState = ((CirclePixAppState)getApplicationContext());
        appState.setContextForPreferences(this);

        agentData = AgentData.getInstance();

        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            mObjectType = extras.getString("objectType");
            mVideoPath = extras.getString("videoPath");
            mId = extras.getString("id");
            mCode = extras.getString("code");
            mLine1 = extras.getString("line1");
            mLine2 = extras.getString("line2");
            mImgURL = extras.getString("imgURL");
            type = extras.getString("type"); //listing or agentprofile
        }

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(mLine1 + ", " + mLine2);

        rootLayout = (CoordinatorLayout) findViewById(R.id.upload_video_root);
        // Setup Listeners
      // rootLayout.setOnTouchListener(this);

        mVideoTitle = (EditText) findViewById(R.id.upload_video_title);
        mVideoDescription = (EditText) findViewById(R.id.upload_video_description);
        mUploadVideoButton = (Button) findViewById(R.id.upload_video_button);
        mFileSize = (TextView) findViewById(R.id.upload_video_file_size);
        mVideoImageView = (ImageView) findViewById(R.id.video_upload_picture);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        if(agentData.getRealtor().getImage().equals("")){
            mVideoImageView.setVisibility(View.GONE);
        }else{
            if(type.equals("listing")){
                if(!agentData.getRealtor().getImage().equals("")){
                    Glide.with(getApplicationContext())
                            .load(mImgURL)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .centerCrop()
                            .placeholder(R.drawable.image_circle_gradient)
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
                            .into(mVideoImageView);
                }
            }else { //agentprofile

            }


        }

        //Get and set file size
        File file = new File(mVideoPath);
        totalSize = file.length();

        Long l = new Long (totalSize);
        double dTotalSize = (double) l.doubleValue();
        double dTotalSizeInMb = dTotalSize / 1000000;

        DecimalFormat df = new DecimalFormat("#.#");

        Log.d("LOGCAT", "Total size: " + totalSize);
        Log.d("LOGCAT", "Total size double: " + dTotalSize);
        Log.d("LOGCAT", "Video file size: " + df.format(dTotalSizeInMb));
        mFileSize.setText("File Size: " + df.format(dTotalSizeInMb) + " MB");


        mUploadVideoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                if (null != activeNetwork) {
                    if((activeNetwork.getType() == ConnectivityManager.TYPE_WIFI && activeNetwork.isConnectedOrConnecting())){
                        isWifi = true;
                    }else{
                        isWifi = false;
                    }

                    if((activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE && activeNetwork.isConnectedOrConnecting())){
                        is4g = true;
                    }else{
                        is4g = false;
                    }
                }

                mTitleStr = mVideoTitle.getText().toString();
                mDescriptionStr = mVideoDescription.getText().toString();

                if (mTitleStr.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter a title for your video", Toast.LENGTH_SHORT).show();
                }
                else if (mDescriptionStr.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter a description for your video", Toast.LENGTH_SHORT).show();
                }else if (!isWifi && !is4g){
                    if( myAlertDialog != null && myAlertDialog.isShowing() ) return;

                    final AlertDialog.Builder builder = new AlertDialog.Builder(VideoUploadActivity.this);
                    builder.setTitle("No Connection");
                    builder.setMessage("Currently there is no network connection. Please connect to the Internet to upload this video.");

                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                            dialog.dismiss();
                        }});
                    builder.setCancelable(false);
                    myAlertDialog = builder.create();
                    myAlertDialog.show();
                }
                else if (!isWifi) {
                    if (myAlertDialog != null && myAlertDialog.isShowing()) return;

                    AlertDialog.Builder builder = new AlertDialog.Builder(VideoUploadActivity.this);
                    builder.setTitle("WiFi Unavailable");
                    builder.setMessage("You are not connected to WiFi. This may take a long time to upload. For faster uploads, try recording the video again at a lower resolution.");
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                            dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                          //upload video task
                          //  uploadVideo();
                            initTask = new InitTask();
                            //	initTask.execute(VideoUploadActivity.this);

                            if(Build.VERSION.SDK_INT >= 11) {
                                initTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }else {
                                initTask.execute();
                            }

                            // Disable edittext
                            mVideoTitle.setEnabled(false);
                            mVideoDescription.setEnabled(false);
                            mUploadVideoButton.setEnabled(false);

                            Toast.makeText(getApplicationContext(), "Video uploading. You can view Video upload progress on the Notification Bar", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setCancelable(false);
                    myAlertDialog = builder.create();
                    myAlertDialog.show();

                }
                else { 	// If we have title and description, start upload
                    //upload video task
                   // uploadVideo();
                    initTask = new InitTask();
                    //	initTask.execute(VideoUploadActivity.this);

                    if(Build.VERSION.SDK_INT >= 11) {
                        initTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }else {
                        initTask.execute();
                    }

                    mVideoTitle.setEnabled(false);
                    mVideoDescription.setEnabled(false);
                    mUploadVideoButton.setEnabled(false);

                    Toast.makeText(getApplicationContext(), "Video uploading. You can view Video upload progress on the Notification Bar", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    protected class InitTask extends AsyncTask<Context, Integer, String> {
        int progressInt = 0;

        @Override
        protected String doInBackground(Context... params) {

            // Do the video upload on a new thread
//                	Thread networkThread = new Thread() {
//
//						public void run() {
            while (!cancelled) {
                try {
                    String BASE_URL = "http://videoupload.circlepix.com/thePearl/cpixVideoApp.xml?method=upload&objectType=%s&objectId=%s";
                    final String urlString = String.format(BASE_URL, mObjectType, mId);

                    //    final OkHttpClient client = new OkHttpClient();

                    final File sourceFile = new File(mVideoPath);
                    final long totalSize = sourceFile.length();
                    MediaType MEDIA_TYPE_MP4 = MediaType.parse("video/mp4");

                    Log.d("Video Upload ", "File...::::" + sourceFile + " : " + sourceFile.exists());
                    Log.d("Video Upload ", "URL String: " + urlString);
                    Log.d("Video Upload ", "title: " + mTitleStr);
                    Log.d("Video Upload ", "description: " + mDescriptionStr);
                    Log.d("Video Upload ", "code: " + mCode);

                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("title", mTitleStr)
                            .addFormDataPart("description", mDescriptionStr)
                        //    .addFormDataPart("code", mCode)
                            .addFormDataPart("videoFile", "listingVideo.mp4", RequestBody.create(MEDIA_TYPE_MP4, sourceFile))
                            .build();

                    CountingRequestBody monitoredRequest = new CountingRequestBody(requestBody, new CountingRequestBody.Listener() {
                        @Override
                        public void onRequestProgress(long bytesWritten, long contentLength) {
                            //Update a progress bar with the following percentage
                            float percentage = 100f * bytesWritten / contentLength;

                          //  publishProgress((int) (percentage));
                         //   progressInt = (int) percentage;

                            publishProgress((int) ((bytesWritten / (float) contentLength) * 100));

                            progressInt = (int) ((bytesWritten / (float) contentLength) * 100);

                           /* if (percentage >= 0) {
                                //TODO: Progress bar
                                progressDialog.setProgress((int) (percentage));
                                Log.v ("percentage", String.valueOf(percentage));
                            } else {
                                //Something went wrong
                                progressDialog.dismiss();
                                Log.v ("percentage", "something went wrong");
                            }*/
                        }
                    });

                    Request request = new Request.Builder()
                            .url(urlString)
                            .post(monitoredRequest)
                            .build();

                    // Customize
                    OkHttpClient client = new OkHttpClient();
//                    OkHttpClient client  = new OkHttpClient.Builder()
//                            .socketFactory(new RestrictedSocketFactory(16 * 1024))
//                          .connectTimeout(10, TimeUnit.SECONDS)
//                          .writeTimeout(10, TimeUnit.SECONDS)
//                          .readTimeout(30, TimeUnit.SECONDS)
//                          .readTimeout(1000, TimeUnit.MILLISECONDS)
//                          .retryOnConnectionFailure(true)
//                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.i("Video Uploaaadd", "Failed: " + e.getLocalizedMessage());
                            //progressDialog.dismiss();
                            //   Toast.makeText(getApplicationContext(), "Upload Failed: COnnection Timeout", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onResponse(Call call, final Response response) throws IOException {

                            String respBody;
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    respBody = response.body().string();
                                    Log.i("Video Uploaaadd", respBody);
                                    response.body().close();
                               //     progressDialog.dismiss();

                                    Log.d("In finally", "cancelled is : " + cancelled);

                                    mBuilder.setContentTitle("Video upload complete")
                                            .setContentText("Upload complete: " + mTitleStr)
                                            .setOngoing(false)
                                            .setAutoCancel(true)
                                            .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                                            //Removes progress bar
                                            .setProgress(0, 0, false);
                                    mNotifyManager.notify(id, mBuilder.build());

                                    ActivityManager am = (ActivityManager) VideoUploadActivity.this.getSystemService(ACTIVITY_SERVICE);
                                    List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
                                    Log.d("topActivity", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName());
                                    ComponentName componentInfo = taskInfo.get(0).topActivity;
                                    componentInfo.getPackageName();

                                    finish();
                                }
                            } else {
                                Log.i("Video Uploaaadd", "unsuccessful");
                               /* switch (response.code()){
                                    case 401:
                                        String body="HTTP_UNAUTHORIZED";
                                        progressDialog.dismiss();
                                        break;
                                }*/
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
////							VideoUploadActivity.this.handler.sendEmptyMessage(0);
//						}
//					};
//					networkThread.start();


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
                // WERE DONE!
                done = true;
//                      sendToast("Video Uploaded Successfully");
//                    	progressBar.dismiss();
//                    	finish();
                return "COMPLETE!";
            }
            return "cancelled";
        }


        // -- gets called just before thread begins
        @Override
        protected void onPreExecute() {
            cancelled = false;

            try{
                listTasks();
            }catch(Exception e){
                Log.v("listTasks error: ", e.toString());
            }
            ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(getApplicationContext().ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
            ActivityManager.RunningTaskInfo task = tasks.get(0); // get current task
            rootActivity = task.baseActivity;

            //	Intent resultIntent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);

//			PendingIntent contentIntent = PendingIntent.getActivity(VideoUploadActivity.this, 0,
//					notifIntent, 0);

            Log.v("Activity.getPackageName", String.valueOf(rootActivity.getPackageName()));
            Log.v("getPackageName", String.valueOf(getApplicationContext().getPackageName()));
            if (rootActivity.getPackageName().equalsIgnoreCase(getApplicationContext().getPackageName())) {
                //your app is open
                // Now build an Intent that will bring this task to the front
                notifIntent = new Intent();
                notifIntent.setComponent(rootActivity);
                notifIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);


            } else {
                //your app is not open,start it by calling launcher activity
                notifIntent = new Intent(getApplicationContext().getApplicationContext(), SplashActivity.class);


            }
            notifIntent.setAction(Intent.ACTION_MAIN);
            notifIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            notifIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);


            PendingIntent contentIntent = PendingIntent.getActivity(VideoUploadActivity.this, 0,
                    notifIntent, 0);

            mNotifyManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            mBuilder =
                    new NotificationCompat.Builder(VideoUploadActivity.this)
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setContentTitle("Uploading video")
                            .setContentText("Starting upload: " + mTitleStr)
                           // .setSmallIcon(R.drawable.ic_launcher)
                            .setOngoing(true)  //prevent dismissing on notif
                            .setContentIntent(contentIntent)
                            .setProgress(0, 0, false);

            //for test only
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBuilder.setSmallIcon(R.drawable.ic_notification_white);
                mBuilder.setColor(ContextCompat.getColor(getApplicationContext(),R.color.colorAccent));
            } else {
                mBuilder.setSmallIcon(R.drawable.ic_notification_green);
            }
            mNotifyManager.notify(id, mBuilder.build());

            super.onPreExecute();
        }

        // -- called from the publish progress
        // -- notice that the datatype of the second param gets passed to this method
        @Override
        protected void onProgressUpdate(Integer... values)
        {
            mBuilder.setContentTitle("Uploading video")
                    .setContentText(mTitleStr )
                    // Removes the progress bar
                    .setProgress(100, progressInt, false);
            mNotifyManager.notify(id, mBuilder.build());

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
            mBuilder.setContentTitle("Video upload complete")
                    .setContentText("Upload complete: " + mTitleStr)
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                    .setAutoCancel(true) //notif will be dismissed when clicked and opens a new intent (MainActivity)
                    .setOngoing(false)  //notif can be dismissed
                    // Removes the progress bar
                    .setProgress(0, 0, false);
            mNotifyManager.notify(id, mBuilder.build());

            ActivityManager am = (ActivityManager) VideoUploadActivity.this.getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            Log.d("topActivity", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName());
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            componentInfo.getPackageName();

            finish();


            super.onPostExecute(result);
            Log.i( "makemachine", "onPostExecute(): " + result );
        }
    }


    private void listTasks() throws PackageManager.NameNotFoundException {
        if (Build.VERSION.SDK_INT >= 21){
            ActivityManager mgr = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.AppTask>  tasks = mgr.getAppTasks();
            String packagename;
            String label;
            for (ActivityManager.AppTask task: tasks){
                packagename = task.getTaskInfo().baseIntent.getComponent().getPackageName();
                label = getPackageManager().getApplicationLabel(getPackageManager().getApplicationInfo(packagename, PackageManager.GET_META_DATA)).toString();
                Log.v("Hi ",packagename + ":" + label);
            }
        }
    }

    public static boolean isBackgroundRunning(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (String activeProcess : processInfo.pkgList) {
                    if (activeProcess.equals(context.getPackageName())) {
                        //If your app is the process in foreground, then it's not in running in background
                        return false;
                    }
                }
            }
        }

        return true;
    }


    private void uploadVideo() {
//        progressDialog = new ProgressDialog(VideoUploadActivity.this);
//        progressDialog.setMessage("Uploading Video...");
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        progressDialog.setProgress(0);
//        progressDialog.setMax(100);
//        progressDialog.setCancelable(false);
//        progressDialog.show();

        String BASE_URL = "http://videoupload1.circlepix.com/thePearl/cpixVideoApp.xml?method=upload&objectType=%s&objectId=%s";
        final String urlString = String.format(BASE_URL, mObjectType, mId);

    //    final OkHttpClient client = new OkHttpClient();

        final File sourceFile = new File(mVideoPath);
        final long totalSize = sourceFile.length();
        MediaType MEDIA_TYPE_MP4 = MediaType.parse("video/mp4");

        Log.d("Video Upload ", "File...::::" + sourceFile + " : " + sourceFile.exists());
        Log.d("Video Upload ", "URL String: " + urlString);
        Log.d("Video Upload ", "title: " + mTitleStr);
        Log.d("Video Upload ", "description: " + mDescriptionStr);
        Log.d("Video Upload ", "code: " + mCode);

    /*       new AsyncTask<Void, Void, Void>() {
               @Override
               protected Void doInBackground(Void... params) {
                   try {
                    //     ApiCall.POST(client, urlString, RequestBuilder.uploadRequestBody(mTitleStr, mDescriptionStr,mCode, "mp4", sourceFile));
                       //Parse the response string here
                   //    Log.d("Response", response);
                       MultipartBody body = RequestBuilder.uploadRequestBody(mTitleStr, mDescriptionStr, mCode, "mp4", sourceFile);

                       CountingRequestBody monitoredRequest = new CountingRequestBody(body, new CountingRequestBody.Listener() {
                           @Override
                           public void onRequestProgress(long bytesWritten, long contentLength) {
                               //Update a progress bar with the following percentage
                               float percentage = 100f * bytesWritten / contentLength;
                               if (percentage >= 0) {
                                   //TODO: Progress bar
                                   Log.v ("percentage", String.valueOf(percentage));
                               } else {
                                   //Something went wrong
                                   Log.v ("percentage", "something went wrong");
                               }
                           }
                       });

                       Request request = new Request.Builder()
                               .url(urlString)
                               .post(monitoredRequest)
                               .build();

                       OkHttpClient client = new OkHttpClient();
                       client.newCall(request).enqueue(new Callback() {
                           //   Handler handler = new Handler(VideoUploadActivity.this.getMainLooper());
                           @Override
                           public void onFailure(Call call, IOException e) {
                               Log.i("Video Uploaaadd", "Failed");
                           }

                           @Override
                           public void onResponse(Call call, final Response response) throws IOException {

                               String respBody;
                               if (response.isSuccessful()) {
                                   if (response.body() != null) {
                                       respBody = response.body().string();
                                       Log.i("Video Uploaaadd", respBody);
                                       response.body().close();
                                       progressDialog.dismiss();
                                   }
                               } else {

                                   switch (response.code()){
                                       case 401:
                                           String body="HTTP_UNAUTHORIZED";
                                           break;
                                   }
                               }
                           }
                       });


                   } catch (Exception e) {
                       e.printStackTrace();
                   }
                   return null;
               }
           }.execute();

       }
*/

       RequestBody requestBody = new MultipartBody.Builder()
               .setType(MultipartBody.FORM)
               .addFormDataPart("title", mTitleStr)
               .addFormDataPart("description", mDescriptionStr)
               .addFormDataPart("code", mCode)
               .addFormDataPart("videoFile", "listingVideo.mp4", RequestBody.create(MEDIA_TYPE_MP4, sourceFile))
               .build();

        CountingRequestBody monitoredRequest = new CountingRequestBody(requestBody, new CountingRequestBody.Listener() {
            @Override
            public void onRequestProgress(long bytesWritten, long contentLength) {
                //Update a progress bar with the following percentage
                float percentage = 100f * bytesWritten / contentLength;
                if (percentage >= 0) {
                    //TODO: Progress bar
                 //   progressDialog.setProgress((int) (percentage));

                    Log.v ("percentage", String.valueOf(percentage));
                } else {
                    //Something went wrong
                  //  progressDialog.dismiss();
                    Log.v ("percentage", "something went wrong");
                }
            }
        });
   /*    final CountingRequestBody countingBody = new CountingRequestBody(requestBody,
               new CountingRequestBody.Listener() {

                   @Override
                   public void onRequestProgress(long bytesWritten, long contentLength) {
                       float percentage = 100f * bytesWritten / contentLength;
                       // TODO: Do something useful with the values
                       Log.v("upload progress", String.valueOf(percentage));
                   }
               });
*/
       Request request = new Request.Builder()
               .url(urlString)
               .post(monitoredRequest)
               .build();

      OkHttpClient client  = new OkHttpClient.Builder()
                .socketFactory(new RestrictedSocketFactory(16 * 1024))
//                .connectTimeout(10, TimeUnit.SECONDS)
//                .writeTimeout(10, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                  .readTimeout(1000, TimeUnit.MILLISECONDS)
//                  .retryOnConnectionFailure(true)
                .build();
       client.newCall(request).enqueue(new Callback() {
        //   Handler handler = new Handler(VideoUploadActivity.this.getMainLooper());
           @Override
           public void onFailure(Call call, IOException e) {
               Log.i("Video Uploaaadd", "Failed: " + e.getLocalizedMessage());
            //   progressDialog.dismiss();
            //   Toast.makeText(getApplicationContext(), "Upload Failed: COnnection Timeout", Toast.LENGTH_SHORT).show();

           }

           @Override
           public void onResponse(Call call, final Response response) throws IOException {

               String respBody;
               if (response.isSuccessful()) {
                   if (response.body() != null) {
                       respBody = response.body().string();
                       Log.i("Video Uploaaadd", respBody);
                       response.body().close();
                 //      progressDialog.dismiss();
                   }
               } else {

                   switch (response.code()){
                       case 401:
                           String body="HTTP_UNAUTHORIZED";
                //           progressDialog.dismiss();
                           break;
                   }
               }
           }
       });

   }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_second_menu, menu);

        MenuItem item= menu.findItem(R.id.action_settings);
        item.setVisible(false);

        return true;
    }

    /**
     * Used to close keyboard when user clicks away from edittext
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        View v = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (v instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            Log.d("Activity", "Touch event "+event.getRawX()+","+event.getRawY()+" "+x+","+y+" rect "+w.getLeft()+","+w.getTop()+","+w.getRight()+","+w.getBottom()+" coords "+scrcoords[0]+","+scrcoords[1]);
            if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom()) ) {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }

}

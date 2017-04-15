package com.circlepix.android.presentations;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.circlepix.android.CirclePixAppState;
import com.circlepix.android.R;
import com.circlepix.android.beans.Realtor;
import com.circlepix.android.data.Presentation;

import java.io.InputStream;
import java.net.MalformedURLException;

/**
 * Created by relly on 2/14/15.
 */
public class PresentationEnd extends PresentationBase {

    private PresentationPageAudioPlayer player;
    private TextView realtorName;
    private TextView realtorPhone;
    private TextView realtorEmail;
    private ImageView realtorImage;
    private TextView companyName;

    //KBL
    private CirclePixAppState appState;
    private LinearLayout leftFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.presentation_end);

        // Setup application class
        appState = ((CirclePixAppState)getApplicationContext());
        appState.setContextForPreferences(this);

        realtorName = (TextView) findViewById(R.id.realtorName);
        realtorPhone = (TextView) findViewById(R.id.realtorPhone);
        realtorEmail = (TextView) findViewById(R.id.realtorEmail);
        realtorImage = (ImageView) findViewById(R.id.imgRealtor);
        companyName = (TextView) findViewById(R.id.companyName);
        leftFrame = (LinearLayout) findViewById(R.id.leftFrame);

        Realtor realtor = PresentationSequencingSet.getRealtorProfile(PresentationEnd.this);
        Presentation p = PresentationSequencingSet.getPresentation();

        if (realtor != null) {

            //temporarily unavail
           /* if(realtor.getName().equalsIgnoreCase("Jeremy Durrant")){
                leftFrame.setBackgroundColor(getResources().getColor(R.color.circlepix_brown));
            }else if(realtor.getName().equalsIgnoreCase("Greg Gehring")){
                leftFrame.setBackgroundColor(getResources().getColor(R.color.circlepix_dtan));
            }else{
                leftFrame.setBackgroundColor(getResources().getColor(R.color.circlepix_blue));
            }*/

            if (p.isDisplayAgentName()) {
                realtorName.setText(realtor.getName());
                realtorPhone.setText(realtor.getPhone());
                realtorEmail.setText(realtor.getEmail());
            } else {
                realtorName.setText("");
                realtorPhone.setText("");
                realtorEmail.setText("");
            }

            if (p.isDisplayAgentImage()) {
                new DownloadImageTask(realtorImage).execute(realtor.getImage());
            } else {
                realtorImage.setImageBitmap(null);
            }

            if (p.isDisplayCompanyName()) {
                companyName.setText(realtor.getAgency());
            } else {
                companyName.setText("");
            }
        }

        player = new PresentationPageAudioPlayer(this);

        player.setPrevAndNextPage(PresentationCommBatchTexting.class, null);
        player.playAudio();
    }

    @Override
    public void onResume() {
        super.onResume();

        CirclePixAppState myApp = (CirclePixAppState)this.getApplication();
        if (myApp.wasInBackground)
        {
            //   appState.setActivityStopped(false);
            //Do specific came-here-from-background code
            Log.v("came-here-from-background code", "called");
            player.setPrevAndNextPage(PresentationCommBatchTexting.class, null);
            player.playAudio();
        }

        myApp.stopActivityTransitionTimer();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v("isActivityStopped", String.valueOf(appState.isActivityStopped()));
        if(!appState.isActivityStopped()){
            ((CirclePixAppState)this.getApplication()).startActivityTransitionTimer();
            player.pauseAudio();

            appState.setActionBarStat(true);  //to show the actionbar to let the user know that presentation was paused when they pressed home or tas manager button
        }

        Log.v("going-to-background code", "called");
    }



    @Override
    public void pauseAnimation() {

    }

    @Override
    public void resumeAnimation() {

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        private ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            String urldisplay = params[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (MalformedURLException me) {
                Log.e("LazyAdapter", "Bad URL Error");
            }
            catch (Exception e) {
                Log.e("Bad URL Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            bmImage.setImageBitmap(bitmap);
        }
    }

    public void onBackPressed(){
        super.onBackPressed();
        player.stop();

        appState.setActivityStopped(true);
        appState.clearSharedPreferences();
        stopService(new Intent(PresentationEnd.this, BackgroundMusicService.class));
    }
}

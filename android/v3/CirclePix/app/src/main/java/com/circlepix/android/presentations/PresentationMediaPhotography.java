package com.circlepix.android.presentations;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.circlepix.android.CirclePixAppState;
import com.circlepix.android.R;
import com.circlepix.android.data.Presentation;

/**
 * Created by relly on 2/9/15.
 */
public class PresentationMediaPhotography extends PresentationBase {

    private PresentationPageAudioPlayer player;

    //KBL
    private CirclePixAppState appState;
    private LinearLayout propImgLayoutBottom, propImgLayoutMid, propImgLayoutTop, layouttop;
    private ImageView imgTopLayer, imgMidLayer, imgPhotoStack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.presentation_media_photography);

        // Setup application class
        appState = ((CirclePixAppState)getApplicationContext());
        appState.setContextForPreferences(this);


    //    propImgLayoutBottom = (LinearLayout)findViewById(R.id.PropImgLayoutBottom);
    //    propImgLayoutMid = (LinearLayout)findViewById(R.id.PropImgLayoutMid);
    //    propImgLayoutTop = (LinearLayout)findViewById(R.id.PropImgLayoutTop);

     //   layouttop = (LinearLayout) findViewById(R.id.layouttop);
    //    imgPhotoStack = (ImageView)findViewById(R.id.imgPhotoStack);
   //     imgMidLayer = (ImageView)findViewById(R.id.imgPhotoStackMidLayer);
    //    imgTopLayer = (ImageView)findViewById(R.id.imgPhotoTopLayer);

        Presentation p = PresentationSequencingSet.getPresentation();

        Log.v("isDisplayPropImg", String.valueOf(p.isDisplayPropImage()));

       // Bitmap bmp =  decodeFile(new File(p.getPropertyImage()));
/* temporarily unavailable

        if(p.isDisplayPropertyImage()){
            String path= p.getPropertyImage().toString();
            String file=path.substring(path.lastIndexOf("/") + 1);


            final File filename = new File(this.getExternalFilesDir(null) + File.separator + "Property Images" + File.separator +file);
            //File filename = new File(Environment.getExternalStorageDirectory()+file);
            //	Log.v("file", file + " - " + filename);

            if(filename.exists()){
                //  propImgLayoutBottom.setVisibility(View.GONE);
                //   propImgLayoutMid.setVisibility(View.VISIBLE);

                imgPhotoStack.setVisibility(View.INVISIBLE);
                imgMidLayer.setVisibility(View.VISIBLE);

                imgTopLayer.setImageBitmap(BitmapFactory.decodeFile(p.getPropertyImage()));
                Log.v("propImg", p.getPropertyImage());
                //   imgTopLayer.setImageBitmap(ImageHelper.getScaledBitmap(p.getPropertyImage(), 200, 200));
            }else{
                imgPhotoStack.setVisibility(View.VISIBLE);
                imgMidLayer.setVisibility(View.INVISIBLE);

                layouttop.setVisibility(View.INVISIBLE);
            //    holder.itemImage.setImageResource(R.drawable.prop_photo);
            }


        }else{
           // propImgLayoutBottom.setVisibility(View.VISIBLE);
           // propImgLayoutMid.setVisibility(View.GONE);
            imgPhotoStack.setVisibility(View.VISIBLE);
            imgMidLayer.setVisibility(View.INVISIBLE);
            layouttop.setVisibility(View.INVISIBLE);
        }

*/

        player = new PresentationPageAudioPlayer(this);

        player.setPrevAndNextPage(PresentationMarketingIntro.class, PresentationMediaPropertySite.class);
        player.playAudio();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                stopPresentation();

        }
        return (super.onOptionsItemSelected(menuItem));
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
            player.setPrevAndNextPage(PresentationMarketingIntro.class, PresentationMediaPropertySite.class);
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

    public void onBackPressed(){
        super.onBackPressed();
        stopPresentation();
    }

    public void stopPresentation() {
        player.stop();

        appState.setActivityStopped(true);
        appState.clearSharedPreferences();
        stopService(new Intent(PresentationMediaPhotography.this, BackgroundMusicService.class));
    }
}

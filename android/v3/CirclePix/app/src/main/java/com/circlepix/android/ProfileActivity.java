package com.circlepix.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.circlepix.android.beans.AgentData;
import com.circlepix.android.helpers.CropCircleTransformation;

/**
 * Created by Keuahn on 8/19/2016.
 */
public class ProfileActivity  extends AppCompatActivity  {

    private AgentData agentData;

    private ImageView agentImg;
    private ImageView agentLogo;
    private ProgressBar progressBar;
    private TextView agentName, agentUsername, agentPassword, agentIDNum, agentAgency, agentPhoneNum, agentCellNum,
                    agentCellProvider, agentTextNotifications, agentFaxNum, agentEmailAd, agentWebsite,
                    agentAddress, agentOffice, agentLeadBeePin, agentProductNum, agentBillingType, agentStateLicenseNum;
    private TextView agentYoutubeId, agentBio;
    private TextView agentFacebookUrl, agentTwitterUrl, agentYoutubeUrl, agentLinkedInUrl, agentPinterestUrl, agentBloggerUrl, agentGalleryLink;

    // Edits
    private ImageView  editPicture, editUsernamePassword, editAgentInfo, editBio, editSocialMediaLinks, editLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Get global shared data
        agentData = AgentData.getInstance();

        agentImg = (ImageView) findViewById(R.id.agent_img);
        agentLogo = (ImageView) findViewById(R.id.agent_logo);
        agentName = (TextView) findViewById(R.id.agent_name);
        agentUsername = (TextView) findViewById(R.id.text_username);
 //       agentPassword = (TextView) findViewById(R.id.text_password);
        agentIDNum = (TextView) findViewById(R.id.text_agent_id);
        agentPhoneNum = (TextView) findViewById(R.id.text_phone_number);
        agentAgency = (TextView) findViewById(R.id.text_agency);
        agentCellNum = (TextView) findViewById(R.id.text_cell_number);
        agentCellProvider = (TextView) findViewById(R.id.text_cell_provider);
        agentTextNotifications = (TextView) findViewById(R.id.text_notifications);
        agentFaxNum = (TextView) findViewById(R.id.text_fax_number);
        agentEmailAd = (TextView) findViewById(R.id.text_email_ad);
        agentWebsite = (TextView) findViewById(R.id.text_website);
        agentAddress = (TextView) findViewById(R.id.text_address);
        agentOffice = (TextView) findViewById(R.id.text_website);
        agentLeadBeePin = (TextView) findViewById(R.id.text_leadbee_pin);
        agentProductNum = (TextView) findViewById(R.id.text_product_number);
        agentBillingType = (TextView) findViewById(R.id.text_billing_type);
        agentStateLicenseNum = (TextView) findViewById(R.id.text_state_license_number);

        agentYoutubeId = (TextView) findViewById(R.id.text_youtube_id);
        agentBio = (TextView) findViewById(R.id.text_agent_bio);

        agentFacebookUrl = (TextView) findViewById(R.id.text_facebook_url);
        agentTwitterUrl = (TextView) findViewById(R.id.text_twitter_url);
        agentYoutubeUrl = (TextView) findViewById(R.id.text_youtube_url);
        agentLinkedInUrl = (TextView) findViewById(R.id.text_linkedin_url);
        agentPinterestUrl = (TextView) findViewById(R.id.text_pinterest_url);
        agentBloggerUrl = (TextView) findViewById(R.id.text_blogger_url);
        agentGalleryLink = (TextView) findViewById(R.id.text_agent_gallery_link);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Edit Profile
        editUsernamePassword = (ImageView) findViewById(R.id.edit_un_pword);
        editPicture = (ImageView) findViewById(R.id.edit_pic);
        editAgentInfo = (ImageView) findViewById(R.id.edit_agent_info);
        editBio = (ImageView) findViewById(R.id.edit_agent_bio);
        editSocialMediaLinks = (ImageView) findViewById(R.id.edit_social_media);
        editLogo = (ImageView) findViewById(R.id.edit_agent_logo);

        if(!agentData.getRealtor().getImage().equals("")){
            Glide.with(getApplicationContext())
                    .load(agentData.getRealtor().getImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
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
                    .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                    .into(agentImg);
        }else{
            agentImg.setImageResource(R.drawable.avatar);
            progressBar.setVisibility(View.GONE);
        }

        if(!agentData.getRealtor().getLogo().equals("")){
            Glide.with(getApplicationContext())
                    .load(agentData.getRealtor().getLogo())
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
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(agentLogo);
        }

        agentName.setText(agentData.getRealtor().getName());

        editPicture.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), EditAgentProfilePictureActivity.class);
                startActivity(intent);
            }
        });

        editUsernamePassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), EditAgentCredentialsActivity.class);
                startActivity(intent);
            }
        });

        editAgentInfo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), EditAgentInformationActivity.class);
                startActivity(intent);
            }
        });

        editBio.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), EditAgentBioActivity.class);
                startActivity(intent);
            }
        });

        editSocialMediaLinks.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), EditAgentSocialMediaLinks.class);
                startActivity(intent);
            }
        });

        editLogo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), EditAgentLogoActivity.class);
                startActivity(intent);
            }
        });
        setAgentInformation();
    }

    private void setAgentInformation(){
        // Agent Profile Section
        if(!agentData.getRealtor().getId().equals(null)){
            agentIDNum.setText(agentData.getRealtor().getId()); }

        if(!agentData.getRealtor().getAgency().equals(null)){
            agentAgency.setText(agentData.getRealtor().getAgency());}

        if(!agentData.getRealtor().getPhone().equals(null)){
            agentPhoneNum.setText(agentData.getRealtor().getPhone());}

        if(!agentData.getRealtor().getMobile().equals(null)){
            agentCellNum.setText(agentData.getRealtor().getMobile());}

        if(!agentData.getRealtor().getMobile().equals(null)){
            agentCellNum.setText(agentData.getRealtor().getMobile());}

        if(!agentData.getRealtor().getEmail().equals(null)){
            agentEmailAd.setText(agentData.getRealtor().getEmail());}

        // Agent Bio Section
        if(!agentData.getRealtor().getEmail().equals(null)){
            agentYoutubeId.setText(agentData.getRealtor().getVideo());}

        // Agent Gallery Section
        String agentGalleryURL = "https://www.circlepix.com/tour/agent.htm?agentid=";
        if(!agentData.getRealtor().getId().equals(null)){
             agentGalleryLink.setText(agentGalleryURL + agentData.getRealtor().getId());
        }

    }




  /*  public static void startActivity(Context context) {
        context.startActivity(new Intent(context, ProfileActivity.class));
    }

    private Button mExpandButton;
    private ExpandableRelativeLayout mExpandLayout;
    private TextView mOverlayText;
    private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expandable_layout);

     //   getSupportActionBar().setTitle(ProfileActivity.class.getSimpleName());

        mExpandButton = (Button) findViewById(R.id.expandButton);
        mExpandLayout = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout);
        mOverlayText = (TextView) findViewById(R.id.overlayText);
        mExpandButton.setOnClickListener(this);

        mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mExpandLayout.move(mOverlayText.getHeight(), 0, null);

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    mOverlayText.getViewTreeObserver().removeGlobalOnLayoutListener(mGlobalLayoutListener);
                } else {
                    mOverlayText.getViewTreeObserver().removeOnGlobalLayoutListener(mGlobalLayoutListener);
                }
            }
        };
        mOverlayText.getViewTreeObserver().addOnGlobalLayoutListener(mGlobalLayoutListener);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.expandButton:
                mExpandLayout.expand();
           //     mExpandButton.setVisibility(View.GONE);
           //     mOverlayText.setVisibility(View.GONE);
                mExpandLayout.toggle();
                break;
        }
    }*/
}
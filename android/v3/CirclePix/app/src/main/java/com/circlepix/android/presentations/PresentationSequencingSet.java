package com.circlepix.android.presentations;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.circlepix.android.AccountActivity;
import com.circlepix.android.R;
import com.circlepix.android.beans.PresentationSequencingPage;
import com.circlepix.android.beans.Realtor;
import com.circlepix.android.data.Presentation;
import com.circlepix.android.types.PhotographyType;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by relly on 3/20/15.
 */
public class PresentationSequencingSet {


    private static Map<Integer, PresentationSequencingPage> sp;
    private static Presentation presentation;
    public static int currentPageNum = 1;
    public static boolean isPause = false;


    public static void setSelectedPresentations(Presentation p) {
        setPresentation(p);
        currentPageNum = 1;
        int ctr = 1;

        sp = new HashMap<Integer, PresentationSequencingPage>();

        sp.put(ctr++, new PresentationSequencingPage(p.getNarration().name(), PresentationStart.class, R.raw.m_presentationintro, R.raw.f_presentationintro));

        sp.put(ctr++, new PresentationSequencingPage(p.getNarration().name(), PresentationStarMarketing.class, R.raw.m_starmarketing, R.raw.f_starmarketing));

        // Marketing Materials
        sp.put(ctr++, new PresentationSequencingPage(p.getNarration().name(), PresentationMarketingIntro.class, R.raw.m_marketingmaterials_intro, R.raw.f_marketingmaterials_intro));


        PhotographyType photoType = p.getPhotographyType();
        if (photoType.name().equalsIgnoreCase("Professional")) {
            sp.put(ctr++, new PresentationSequencingPage(p.getNarration().name(), PresentationMediaPhotography.class, R.raw.m_marketingmaterials_professionalphoto, R.raw.f_marketingmaterials_professionalphoto));
        } else  {
            sp.put(ctr++, new PresentationSequencingPage(p.getNarration().name(), PresentationMediaPhotography.class, R.raw.m_marketingmaterials_agentphoto, R.raw.f_marketingmaterials_agentphoto));
        }


        sp.put(ctr++, new PresentationSequencingPage(p.getNarration().name(), PresentationMediaPropertySite.class, R.raw.m_marketingmaterials_propertysite, R.raw.f_marketingmaterials_propertysite));

        if (p.isMediaListingVideo()) {
            sp.put(ctr++, new PresentationSequencingPage(p.getNarration().name(), PresentationMediaListingVideo.class, R.raw.m_marketingmaterials_listingvideo, R.raw.f_marketingmaterials_listingvideo));
        }
        if (p.isMediaQRCodes()) {
            sp.put(ctr++, new PresentationSequencingPage(p.getNarration().name(), PresentationMediaQRCodes.class, R.raw.m_marketingmaterials_qr, R.raw.f_marketingmaterials_qr));
        }
        if (p.isMedia24HourInfo()) {
            sp.put(ctr++, new PresentationSequencingPage(p.getNarration().name(), PresentationMedia24HourInfo.class, R.raw.m_marketingmaterials_24hour, R.raw.f_marketingmaterials_24hour));
        }
        if (p.isMediaShortCode()) {
            sp.put(ctr++, new PresentationSequencingPage(p.getNarration().name(), PresentationMediaShortcode.class, R.raw.m_marketingmaterials_shortcode, R.raw.f_marketingmaterials_shortcode));
        }
        if (p.isMediaFlyers()) {
            sp.put(ctr++, new PresentationSequencingPage(p.getNarration().name(), PresentationMediaFlyers.class, R.raw.m_marketingmaterials_flyers, R.raw.f_marketingmaterials_flyers));
        }


        if(p.isMediaDvds()){
            sp.put(ctr++, new PresentationSequencingPage(p.getNarration().name(), PresentationMediaDVDs.class, R.raw.m_marketingmaterials_dvd, R.raw.f_marketingmaterials_dvd));
        }


        // Exposure
        sp.put(ctr++, new PresentationSequencingPage(p.getNarration().name(), PresentationExposureIntro.class, R.raw.m_exposure_intro, R.raw.f_exposure_intro));

        if (p.isExpRealPortals()) {
            sp.put(ctr++, new PresentationSequencingPage(p.getNarration().name(), PresentationExposurePortals.class, R.raw.m_exposure_portals, R.raw.f_exposure_portals));
        }
        if (p.isExpPersonalSite()) {
            sp.put(ctr++, new PresentationSequencingPage(p.getNarration().name(), PresentationExposurePersonalWebsite.class, R.raw.m_exposure_personal, R.raw.f_exposure_personal));
        }
        if (p.isExpCompanySite()) {
            sp.put(ctr++, new PresentationSequencingPage(p.getNarration().name(), PresentationExposureCompanyWebsite.class, R.raw.m_exposure_company, R.raw.f_exposure_company));
        }
        if (p.isExpFacebook()) {
            sp.put(ctr++, new PresentationSequencingPage(p.getNarration().name(), PresentationExposureFacebook.class, R.raw.m_exposure_facebook, R.raw.f_exposure_facebook));
        }

        sp.put(ctr++, new PresentationSequencingPage(p.getNarration().name(), PresentationExposureYouTube.class, R.raw.m_exposure_youtube, R.raw.f_exposure_youtube));

        if (p.isExpTwitter()) {
            sp.put(ctr++, new PresentationSequencingPage(p.getNarration().name(), PresentationExposureTwitter.class, R.raw.m_exposure_twitter, R.raw.f_exposure_twitter));
        }
        if (p.isExpBlogger()) {
            sp.put(ctr++, new PresentationSequencingPage(p.getNarration().name(), PresentationExposureBlogger.class, R.raw.m_exposure_blog, R.raw.f_exposure_blog));
        }
        if (p.isExpCraigslist()) {
            sp.put(ctr++, new PresentationSequencingPage(p.getNarration().name(), PresentationExposureCraigslist.class, R.raw.m_exposure_craigslist, R.raw.f_exposure_craigslist));
        }
        if (p.isExpLinkedin()) {
            sp.put(ctr++, new PresentationSequencingPage(p.getNarration().name(), PresentationExposureLinkedIn.class, R.raw.m_exposure_linkedin, R.raw.f_exposure_linkedin));
        }
        if (p.isExpPinterest()) {
            sp.put(ctr++, new PresentationSequencingPage(p.getNarration().name(), PresentationExposurePinterest.class, R.raw.m_exposure_pinterest, R.raw.f_exposure_pinterest));
        }
        if (p.isExpSeoBoost()) {
            sp.put(ctr++, new PresentationSequencingPage(p.getNarration().name(), PresentationExposureSeo.class, R.raw.m_exposure_seo, R.raw.f_exposure_seo));
        }

        // Lead Generation
        sp.put(ctr++, new PresentationSequencingPage(p.getNarration().name(), PresentationLeadGenIntro.class, R.raw.m_leadgen_intro, R.raw.f_leadgen_intro));
        sp.put(ctr++, new PresentationSequencingPage(p.getNarration().name(), PresentationLeadGenPropertySite.class, R.raw.m_leadgen_propertysite, R.raw.f_leadgen_propertysite));
        sp.put(ctr++, new PresentationSequencingPage(p.getNarration().name(), PresentationLeadGen24hourinfo.class, R.raw.m_leadgen_24hour, R.raw.f_leadgen_24hour));
        sp.put(ctr++, new PresentationSequencingPage(p.getNarration().name(), PresentationLeadGenFacebook.class, R.raw.m_leadgen_facebook, R.raw.f_leadgen_facebook));
        sp.put(ctr++, new PresentationSequencingPage(p.getNarration().name(), PresentationLeadGenMobileTour.class, R.raw.m_leadgen_mobile, R.raw.f_leadgen_mobile));

        // Communication
        sp.put(ctr++, new PresentationSequencingPage(p.getNarration().name(), PresentationCommIntro.class, R.raw.m_comm_intro, R.raw.f_comm_intro));

        if (p.isCommStats()) {
            sp.put(ctr++, new PresentationSequencingPage(p.getNarration().name(), PresentationCommStats.class, R.raw.m_comm_stats, R.raw.f_comm_stats));
        }
        if (p.isCommEmail()) {
            sp.put(ctr++, new PresentationSequencingPage(p.getNarration().name(), PresentationCommEMarketing.class, R.raw.m_comm_email, R.raw.f_comm_email));
        }
        if (p.isCommBatchText()) {
            sp.put(ctr++, new PresentationSequencingPage(p.getNarration().name(), PresentationCommBatchTexting.class, R.raw.m_comm_text, R.raw.f_comm_text));
        }

        sp.put(ctr++, new PresentationSequencingPage(p.getNarration().name(), PresentationEnd.class, R.raw.m_presentationend, R.raw.f_presentationend));
    }

    public static Map<Integer, PresentationSequencingPage> getSelectedPresentations() {
        return sp;
    }

    public static Realtor getRealtorProfile(Context context) {
        SharedPreferences settings = context.getSharedPreferences(AccountActivity.PREFS_NAME, 0);
        String responseString = settings.getString("lastResponse", null);
        Realtor realtor = null;

        if (!TextUtils.isEmpty(responseString)) {
            Document doc = Jsoup.parse(responseString);

            Elements realtors = doc.select("realtor");

            Elements realtorId = realtors.select("id");
            Elements realtorCode = realtors.select("code");
            Elements realtorName = realtors.select("name");
            Elements realtorVideo = realtors.select("currentyoutubevideo");
            Elements realtorImage = realtors.select("agentimage");
            Elements realtorAgency = realtors.select("agency");
            Elements realtorPhone = realtors.select("phone");
            Elements realtorMobile = realtors.select("mobile");
            Elements realtorEmail = realtors.select("email");

            realtor = new Realtor();
            realtor.setId(realtorId.text());
            realtor.setCode(realtorCode.text());
            realtor.setName(realtorName.text());
            realtor.setVideo(realtorVideo.text());
            realtor.setImage(realtorImage.text());
            realtor.setAgency(realtorAgency.text());
            realtor.setEmail(realtorEmail.text());
            realtor.setPhone((realtorPhone == null)?"":realtorPhone.text());
            realtor.setMobile((realtorMobile == null)?"":realtorMobile.text());
        }

        return realtor;
    }

    public static Presentation getPresentation() {
        return presentation;
    }

    public static void setPresentation(Presentation presentation) {
        PresentationSequencingSet.presentation = presentation;
    }
}


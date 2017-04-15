package com.circlepix.android.beans;

import com.circlepix.android.R;
import com.circlepix.android.data.Presentation;
import com.circlepix.android.types.NarrationType;
import com.circlepix.android.types.PhotographyType;

import java.util.ArrayList;
import java.util.List;

public class PageSet {

    List<PresentationPage> fullSet;
    List<PresentationPage> validSet;

    public PageSet(Presentation presentation) {
        boolean male = presentation.getNarration() != NarrationType.female;
        fullSet = new ArrayList<PresentationPage>();
        validSet = new ArrayList<PresentationPage>();

        // Fill the full set
        int i = 1;
        fullSet.add(new PresentationPage(i, i++, "Introduction", male?R.raw.m_presentationintro:R.raw.f_presentationintro, "none", true));
        fullSet.add(new PresentationPage(i, i++, "Star Marketing", male?R.raw.m_starmarketing:R.raw.f_starmarketing, "none", true));

        // Marketing Materials - Pages 3 - 12
        fullSet.add(new PresentationPage(i, i++, "Marketing Materials", male?R.raw.m_marketingmaterials_intro:R.raw.f_marketingmaterials_intro, "none", true));
        int audio = (presentation.getPhotographyType() == PhotographyType.professional ?
                (male? R.raw.m_marketingmaterials_professionalphoto:R.raw.f_marketingmaterials_professionalphoto)
                : (male? R.raw.m_marketingmaterials_agentphoto:R.raw.f_marketingmaterials_agentphoto));
        fullSet.add(new PresentationPage(i, i++, "Photography", audio, "none", true));
        fullSet.add(new PresentationPage(i, i++, "Property Site", male?R.raw.m_marketingmaterials_propertysite:R.raw.f_marketingmaterials_propertysite, "none", presentation.isMediaPropertySite()));
        fullSet.add(new PresentationPage(i, i++, "Listing Video", male?R.raw.m_marketingmaterials_listingvideo:R.raw.f_marketingmaterials_listingvideo, "none", presentation.isMediaListingVideo()));
        fullSet.add(new PresentationPage(i, i++, "Mobile Platform", male?R.raw.m_marketingmaterials_mobileplatform:R.raw.f_marketingmaterials_mobileplatform, "none", presentation.isMediaMobile()));
        fullSet.add(new PresentationPage(i, i++, "QR Codes", male?R.raw.m_marketingmaterials_qr:R.raw.f_marketingmaterials_qr, "none", presentation.isMediaQRCodes()));
        if (presentation.isMediaShortCode() && presentation.isMedia24HourInfo()) {
            i += 2;
            fullSet.add(new PresentationPage(i, i++, "24hr Info or Short code", male?R.raw.m_marketingmaterials_24_short:R.raw.f_marketingmaterials_24_short, "none", true));
        } else {
            fullSet.add(new PresentationPage(i, i++, "24hr Info Line", male?R.raw.m_marketingmaterials_24hour:R.raw.f_marketingmaterials_24hour, "none", presentation.isMedia24HourInfo()));
            fullSet.add(new PresentationPage(i, i++, "Short Codes", male?R.raw.m_marketingmaterials_shortcode:R.raw.f_marketingmaterials_shortcode, "none", presentation.isMediaShortCode()));
            i++;
        }
        fullSet.add(new PresentationPage(i, i++, "Flyers", male?R.raw.m_marketingmaterials_flyers:R.raw.f_marketingmaterials_flyers, "none", presentation.isMediaFlyers()));
        fullSet.add(new PresentationPage(i, i++, "DVDs", male?R.raw.m_marketingmaterials_dvd:R.raw.f_marketingmaterials_dvd, "none", presentation.isMediaDvds()));

        // Exposure - Pages 13 - 24
        fullSet.add(new PresentationPage(i, i++, "Exposure", male?R.raw.m_exposure_intro:R.raw.f_exposure_intro, "none", true));
        fullSet.add(new PresentationPage(i, i++, "Real Estate Portals", male?R.raw.m_exposure_portals:R.raw.f_exposure_portals, "none", presentation.isExpRealPortals()));
        fullSet.add(new PresentationPage(i, i++, "Personal Site", male?R.raw.m_exposure_personal:R.raw.f_exposure_personal, "none", presentation.isExpPersonalSite()));
        fullSet.add(new PresentationPage(i, i++, "Company Site", male?R.raw.m_exposure_company:R.raw.f_exposure_company, "none", presentation.isExpCompanySite()));
        fullSet.add(new PresentationPage(i, i++, "Blogger", male?R.raw.m_exposure_blog:R.raw.f_exposure_blog, "none", presentation.isExpBlogger()));
        fullSet.add(new PresentationPage(i, i++, "YouTube", male?R.raw.m_exposure_youtube:R.raw.f_exposure_youtube, "none", presentation.isExpYouTube()));
        fullSet.add(new PresentationPage(i, i++, "Facebook", male?R.raw.m_exposure_facebook:R.raw.f_exposure_facebook, "none", presentation.isExpFacebook()));
        fullSet.add(new PresentationPage(i, i++, "Twitter", male?R.raw.m_exposure_twitter:R.raw.f_exposure_twitter, "none", presentation.isExpTwitter()));
        fullSet.add(new PresentationPage(i, i++, "Craigs List", male?R.raw.m_exposure_craigslist:R.raw.f_exposure_craigslist, "none", presentation.isExpCraigslist()));
        fullSet.add(new PresentationPage(i, i++, "LinkedIn", male?R.raw.m_exposure_linkedin:R.raw.f_exposure_linkedin, "none", presentation.isExpLinkedin()));
        fullSet.add(new PresentationPage(i, i++, "Pinterest", male?R.raw.m_exposure_pinterest:R.raw.f_exposure_pinterest, "none", presentation.isExpPinterest()));
        fullSet.add(new PresentationPage(i, i++, "SEO Boost", male?R.raw.m_exposure_seo:R.raw.f_exposure_seo, "none", presentation.isExpSeoBoost()));

        // Lead Gen - Pages 25 - 31
        fullSet.add(new PresentationPage(i, i++, "Lead Gen Intro", male?R.raw.m_leadgen_intro:R.raw.f_leadgen_intro, "none", true));
        fullSet.add(new PresentationPage(i, i++, "Property Site", male?R.raw.m_leadgen_propertysite:R.raw.f_leadgen_propertysite, "none", true));
        if (presentation.isMediaShortCode() && presentation.isMedia24HourInfo()) {
            i += 2;
            fullSet.add(new PresentationPage(i, i++, "24hr or Shortcode", male?R.raw.m_leadgen_24_short:R.raw.f_leadgen_24_short, "none", true));
        } else {
            fullSet.add(new PresentationPage(i, i++, "24 Hour", male?R.raw.m_leadgen_24hour:R.raw.f_leadgen_24hour, "none", presentation.isMedia24HourInfo()));
            fullSet.add(new PresentationPage(i, i++, "Short Code", male?R.raw.m_leadgen_shortcode:R.raw.f_leadgen_shortcode, "none", presentation.isMediaShortCode()));
            i++;
        }
        fullSet.add(new PresentationPage(i, i++, "Facebook", male?R.raw.m_leadgen_facebook:R.raw.f_leadgen_facebook, "none", presentation.isExpFacebook()));
        fullSet.add(new PresentationPage(i, i++, "Mobile", male?R.raw.m_leadgen_mobile:R.raw.f_leadgen_mobile, "none", presentation.isMediaMobile()));

        // Communication - Page 32 - 35
        fullSet.add(new PresentationPage(i, i++, "Communication", male?R.raw.m_comm_intro:R.raw.f_comm_intro, "none", true));
        fullSet.add(new PresentationPage(i, i++, "Communication", male?R.raw.m_comm_stats:R.raw.f_comm_stats, "none", presentation.isCommStats()));
        fullSet.add(new PresentationPage(i, i++, "Communication", male?R.raw.m_comm_email:R.raw.f_comm_email, "none", presentation.isCommEmail()));
        fullSet.add(new PresentationPage(i, i++, "Communication", male?R.raw.m_comm_text:R.raw.f_comm_text, "none", presentation.isCommBatchText()));

        // The end
        fullSet.add(new PresentationPage(i, i++, "The End", male?R.raw.m_presentationend:R.raw.f_presentationend, "none", true));

        // Create the valid set
        for (PresentationPage page : fullSet) {
            if (page.isActive()) {
                validSet.add(page);
            }
        }
    }

    public List<PresentationPage> getFullSet() {
        return fullSet;
    }

    public void setFullSet(List<PresentationPage> fullSet) {
        this.fullSet = fullSet;
    }

    public List<PresentationPage> getValidSet() {
        return validSet;
    }

    public void setValidSet(List<PresentationPage> validSet) {
        this.validSet = validSet;
    }
}

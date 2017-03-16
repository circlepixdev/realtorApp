//
//  PageSet.swift
//  CirclePix
//
//  Created by Mark Burns on 11/1/14.
//  Copyright (c) 2014 Mark Burns. All rights reserved.
//

import Foundation

class PageSet {
    
    var fullSet: NSMutableArray = NSMutableArray()
    var validSet: NSMutableArray = NSMutableArray()
    
    init(presentation: Presentation) {
        var male: Bool = true
        if presentation.narration == "Female" {
            male = false
        }
        var i: Int = 1;
        
        // Fill the full set
        fullSet.addObject(PresentationPage(storyboardId: "intro", pageId: i, pageOrder: i++, name: "Introduction", audioFile: male ? "m_presentationintro" : "f_presentationintro", field: "none", active: true))
        fullSet.addObject(PresentationPage(storyboardId: "starmarketing", pageId: i, pageOrder: i++, name: "Star Marketing", audioFile: male ? "m_starmarketing" : "f_starmarketing", field: "none", active: true))
        
        // Marketing Materials - Pages 3 - 12
        fullSet.addObject(PresentationPage(storyboardId: "marketingmaterials_intro", pageId: i, pageOrder: i++, name: "Marketing Materials", audioFile: male ? "m_marketingmaterials_intro" : "f_marketingmaterials_intro", field: "none", active: true))
        var audio = ""
        if presentation.photographyType == "Professional" {
            audio = male ? "m_marketingmaterials_professionalphoto" : "f_marketingmaterials_professionalphoto"
        } else {
            audio = male ? "m_marketingmaterials_agentphoto" : "f_marketingmaterials_agentphoto"
        }
        fullSet.addObject(PresentationPage(storyboardId: "photography", pageId: i, pageOrder: i++, name: "Photography", audioFile: audio, field: "none", active: true))
        fullSet.addObject(PresentationPage(storyboardId: "marketingmaterials_propertysite", pageId: i, pageOrder: i++, name: "Property Site", audioFile: male ? "m_marketingmaterials_propertysite" : "f_marketingmaterials_propertysite", field: "none", active: presentation.mediaPropertySite as Bool))
        fullSet.addObject(PresentationPage(storyboardId: "marketingmaterials_listingvideo", pageId: i, pageOrder: i++, name: "Listing Video", audioFile: male ? "m_marketingmaterials_listingvideo" : "f_marketingmaterials_listingvideo", field: "none", active: presentation.mediaListingVideo as Bool))
     //   fullSet.addObject(PresentationPage(storyboardId: "marketingmaterials_mobileplatform", pageId: i, pageOrder: i++, name: "Mobile Platform", audioFile: male ? ="m_marketingmaterials_mobileplatform" : "f_marketingmaterials_mobileplatform", field: "none", active: presentation.mediaMobile as Bool))
        fullSet.addObject(PresentationPage(storyboardId: "marketingmaterials_qr", pageId: i, pageOrder: i++, name: "QR Codes", audioFile: male ? "m_marketingmaterials_qr" : "f_marketingmaterials_qr", field: "none", active: presentation.mediaQRCodes as Bool))
        let sc: Bool = presentation.mediaShortCode as Bool
        let tw4: Bool = presentation.media24HourInfo as Bool
        if tw4 && sc {
            i += 2
            fullSet.addObject(PresentationPage(storyboardId: "marketingmaterials_24_short", pageId: i, pageOrder: i++, name: "24hr Info Line", audioFile: male ? "m_marketingmaterials_24_short" : "f_marketingmaterials_24_short", field: "none", active: true))
        } else {
            fullSet.addObject(PresentationPage(storyboardId: "marketingmaterials_24hour", pageId: i, pageOrder: i++, name: "24hr Info Line", audioFile: male ? "m_marketingmaterials_24hour" : "f_marketingmaterials_24hour", field: "none", active: presentation.media24HourInfo as Bool))
            fullSet.addObject(PresentationPage(storyboardId: "marketingmaterials_shortcode", pageId: i, pageOrder: i++, name: "Short Codes", audioFile: male ? "m_marketingmaterials_shortcode" : "f_marketingmaterials_shortcode", field: "none", active: presentation.mediaShortCode as Bool))
            i++
        }
        fullSet.addObject(PresentationPage(storyboardId: "marketingmaterials_flyers", pageId: i, pageOrder: i++, name: "Flyers", audioFile: male ? "m_marketingmaterials_flyers" : "f_marketingmaterials_flyers", field: "none", active: presentation.mediaFlyers as Bool))
        fullSet.addObject(PresentationPage(storyboardId: "marketingmaterials_dvd", pageId: i, pageOrder: i++, name: "DVDs", audioFile: male ? "m_marketingmaterials_dvd" : "f_marketingmaterials_dvd", field: "none", active: presentation.mediaDvds as Bool))
        
        // Exposure - Pages 13 - 24
        fullSet.addObject(PresentationPage(storyboardId: "exposure_intro", pageId: i, pageOrder: i++, name: "Exposure", audioFile: male ? "m_exposure_intro" : "f_exposure_intro", field: "none", active: true))
        fullSet.addObject(PresentationPage(storyboardId: "exposure_portals", pageId: i, pageOrder: i++, name: "Real Estate Portals", audioFile: male ? "m_exposure_portals" : "f_exposure_portals", field: "none", active: presentation.expRealPortals as Bool))
        fullSet.addObject(PresentationPage(storyboardId: "exposure_personal", pageId: i, pageOrder: i++, name: "Personal Site", audioFile: male ? "m_exposure_personal" : "f_exposure_personal", field: "none", active: presentation.expPersonalSite as Bool))
        fullSet.addObject(PresentationPage(storyboardId: "exposure_company", pageId: i, pageOrder: i++, name: "Company Site", audioFile: male ? "m_exposure_company" : "f_exposure_company", field: "none", active: presentation.expCompanySite as Bool))
        fullSet.addObject(PresentationPage(storyboardId: "exposure_blog", pageId: i, pageOrder: i++, name: "Blogger", audioFile: male ? "m_exposure_blog" : "f_exposure_blog", field: "none", active: presentation.expBlogger as Bool))
        fullSet.addObject(PresentationPage(storyboardId: "exposure_youtube", pageId: i, pageOrder: i++, name: "YouTube", audioFile: male ? "m_exposure_youtube" : "f_exposure_youtube", field: "none", active: presentation.expYouTube as Bool))
        fullSet.addObject(PresentationPage(storyboardId: "exposure_facebook", pageId: i, pageOrder: i++, name: "Facebook", audioFile: male ? "m_exposure_facebook" : "f_exposure_facebook", field: "none", active: presentation.expFacebook as Bool))
        fullSet.addObject(PresentationPage(storyboardId: "exposure_twitter", pageId: i, pageOrder: i++, name: "Twitter", audioFile: male ? "m_exposure_twitter" : "f_exposure_twitter", field: "none", active: presentation.expTwitter as Bool))
        fullSet.addObject(PresentationPage(storyboardId: "exposure_craigslist", pageId: i, pageOrder: i++, name: "Craigs List", audioFile: male ? "m_exposure_craigslist" : "f_exposure_craigslist", field: "none", active: presentation.expCraigslist as Bool))
        fullSet.addObject(PresentationPage(storyboardId: "exposure_linkedin", pageId: i, pageOrder: i++, name: "LinkedIn", audioFile: male ? "m_exposure_linkedin" : "f_exposure_linkedin", field: "none", active: presentation.expLinkedin as Bool))
        fullSet.addObject(PresentationPage(storyboardId: "exposure_pinterest", pageId: i, pageOrder: i++, name: "Pinterest", audioFile: male ? "m_exposure_pinterest" : "f_exposure_pinterest", field: "none", active: presentation.expPinterest as Bool))
        fullSet.addObject(PresentationPage(storyboardId: "exposure_seo", pageId: i, pageOrder: i++, name: "SEO Boost", audioFile: male ? "m_exposure_seo" : "f_exposure_seo", field: "none", active: presentation.expSeoBoost as Bool))
        
        // Lead Gen - Pages 25 - 31
        fullSet.addObject(PresentationPage(storyboardId: "leadgen_intro", pageId: i, pageOrder: i++, name: "Lead Gen Intro", audioFile: male ? "m_leadgen_intro" : "f_leadgen_intro", field: "none", active: true))
        fullSet.addObject(PresentationPage(storyboardId: "leadgen_propertysite", pageId: i, pageOrder: i++, name: "Property Site", audioFile: male ? "m_leadgen_propertysite" : "f_leadgen_propertysite", field: "none", active: true))
        if tw4 && sc {
            i += 2
            fullSet.addObject(PresentationPage(storyboardId: "leadgen_24_short", pageId: i, pageOrder: i++, name: "24hr Plus Shortcode", audioFile: male ? "m_leadgen_24_short" : "f_leadgen_24_short", field: "none", active: (sc && tw4)))
        } else {
            fullSet.addObject(PresentationPage(storyboardId: "leadgen_24hour", pageId: i, pageOrder: i++, name: "24 Hour", audioFile: male ? "m_leadgen_24hour" : "f_leadgen_24hour", field: "none", active: presentation.media24HourInfo as Bool))
            fullSet.addObject(PresentationPage(storyboardId: "leadgen_shortcode", pageId: i, pageOrder: i++, name: "Short Code", audioFile: male ? "m_leadgen_shortcode" : "f_leadgen_shortcode", field: "none", active: presentation.mediaShortCode as Bool))
            i++
        }
        fullSet.addObject(PresentationPage(storyboardId: "leadgen_facebook", pageId: i, pageOrder: i++, name: "Facebook", audioFile: male ? "m_leadgen_facebook" : "f_leadgen_facebook", field: "none", active: presentation.expFacebook as Bool))
        fullSet.addObject(PresentationPage(storyboardId: "leadgen_mobile", pageId: i, pageOrder: i++, name: "Mobile", audioFile: male ? "m_leadgen_mobile" : "f_leadgen_mobile", field: "none", active: presentation.mediaMobile as Bool))

        // Communication - Page 32 - 35
        fullSet.addObject(PresentationPage(storyboardId: "comm_intro", pageId: i, pageOrder: i++, name: "Communication", audioFile: male ? "m_comm_intro" : "f_comm_intro", field: "none", active: true))
        fullSet.addObject(PresentationPage(storyboardId: "comm_stats", pageId: i, pageOrder: i++, name: "Communication", audioFile: male ? "m_comm_stats" : "f_comm_stats", field: "none", active: presentation.commStats as Bool))
        fullSet.addObject(PresentationPage(storyboardId: "comm_email", pageId: i, pageOrder: i++, name: "Communication", audioFile: male ? "m_comm_email" : "f_comm_email", field: "none", active: presentation.commEmail as Bool))
        fullSet.addObject(PresentationPage(storyboardId: "comm_text", pageId: i, pageOrder: i++, name: "Communication", audioFile: male ? "m_comm_text" : "f_comm_text", field: "none", active: presentation.commBatchText as Bool))

        // The end
        fullSet.addObject(PresentationPage(storyboardId: "presentationend", pageId: i, pageOrder: i++, name: "The End", audioFile: male ? "m_presentationend" : "f_presentationend", field: "none", active: true))
        
        // Create the valid set
        for (index, item) in fullSet.enumerate() {
            let page = item as! PresentationPage
            if page.active {
                validSet.addObject(page)
            }
        }
        
        print(String(format: "ValidSet has %d pages", validSet.count))
    }
}
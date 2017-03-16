//
//  Presentation.swift
//  CirclePix
//
//  Created by Mark Burns on 10/28/14.
//  Copyright (c) 2014 Mark Burns. All rights reserved.
//

import Foundation
import CoreData

extension NSManagedObject {
    func setDefaults(p: Presentation) {
        // Set defaults (this is here because awakeFromInsert() is not working)
        p.name = "New Presentation"
        p.guid = NSUUID().UUIDString
        p.lastUpdated = NSDate()
        p.autoplay = true
        p.mediaPropertySite = true
        p.mediaListingVideo = true
        p.mediaQRCodes = true
        p.media24HourInfo = true
        p.mediaShortCode = true
        p.mediaFlyers = true
        p.mediaDvds = true
        p.mediaMobile = true
        p.expRealPortals = true
        p.expPersonalSite = true
        p.expCompanySite = true
        p.expBlogger = true
        p.expYouTube = true
        p.expFacebook = true
        p.expTwitter = true
        p.expCraigslist = true
        p.expLinkedin = true
        p.expPinterest = true
        p.expSeoBoost = true
        p.leadPropertySite = true
        p.leadLeadBee = true
        p.leadFacebook = true
        p.leadMobile = true
        p.leadOpenHouseAnnce = true
        p.commEmail = true
        p.commBatchText = true
        p.commStats = true
        p.displayCompanyLogo = true
        p.displayCompanyName = true
        p.displayAgentImage = true
        p.displayAgentName = true
        p.displayPropAddress = false
        p.displayPropImage = false
        p.desc = ""
        p.listingType = "Residential"
        p.photographyType = "Agent"
        p.music = "None"
        p.narration = "Male"
        p.theme = "CirclePix"
        p.propertyAddress = ""
        p.propertyImage = ""
        p.companyLogo = ""
        p.companyName = ""
        p.agentPhoneNum = ""
        p.agentPhoto = ""
    }
    
    func setDictionaryValues(p: Presentation, data: NSDictionary) {
        p.name = getStringSafe("name", dict: data)
        p.guid = getStringSafe("guid", dict: data)
        p.agentPhoneNum = getStringSafe("agentPhoneNum", dict: data)
        p.agentPhoto = getStringSafe("agentPhoto", dict: data)
        p.autoplay = getBoolSafe("autoplay", dict: data)
        p.commBatchText = getBoolSafe("commBatchText", dict: data)
        p.commEmail = getBoolSafe("commEmail", dict: data)
        p.commStats = getBoolSafe("commStats", dict: data)
        p.companyLogo = getStringSafe("companyLogo", dict: data)
        p.companyName = getStringSafe("companyName", dict: data)
        p.desc = getStringSafe("description", dict: data)
        p.displayAgentImage = getBoolSafe("displayAgentImage", dict: data)
        p.displayAgentName = getBoolSafe("displayAgentName", dict: data)
        p.displayCompanyLogo = getBoolSafe("displayCompanyLogo", dict: data)
        p.displayCompanyName = getBoolSafe("displayCompanyName", dict: data)
        p.displayPropAddress = getBoolSafe("displayPropAddress", dict: data)
        p.displayPropImage = getBoolSafe("displayPropImage", dict: data)
        p.expBlogger = getBoolSafe("expBlogger", dict: data)
        p.expCompanySite = getBoolSafe("expCompanySite", dict: data)
        p.expCraigslist = getBoolSafe("expCraigslist", dict: data)
        p.expFacebook = getBoolSafe("expFacebook", dict: data)
        p.expLinkedin = getBoolSafe("expLinkedin", dict: data)
        p.expPersonalSite = getBoolSafe("expPersonalSite", dict: data)
        p.expPinterest = getBoolSafe("expPinterest", dict: data)
        p.expRealPortals = getBoolSafe("expRealPortals", dict: data)
        p.expSeoBoost = getBoolSafe("expSeoBoost", dict: data)
        p.expTwitter = getBoolSafe("expTwitter", dict: data)
        p.expYouTube = getBoolSafe("expYouTube", dict: data)
        p.leadFacebook = getBoolSafe("leadFacebook", dict: data)
        p.leadLeadBee = getBoolSafe("leadLeadBee", dict: data)
        p.leadMobile = getBoolSafe("leadMobile", dict: data)
        p.leadOpenHouseAnnce = getBoolSafe("leadOpenHouseAnnce", dict: data)
        p.leadPropertySite = getBoolSafe("leadPropertySite", dict: data)
        p.listingType = getStringSafe("listingType", dict: data)
        p.media24HourInfo = getBoolSafe("media24HourInfo", dict: data)
        p.mediaDvds = getBoolSafe("mediaDvds", dict: data)
        p.mediaFlyers = getBoolSafe("mediaFlyers", dict: data)
        p.mediaListingVideo = getBoolSafe("mediaListingVideo", dict: data)
        p.mediaMobile = getBoolSafe("mediaMobile", dict: data)
        p.mediaPropertySite = getBoolSafe("mediaPropertySite", dict: data)
        p.mediaQRCodes = getBoolSafe("mediaQRCodes", dict: data)
        p.mediaShortCode = getBoolSafe("mediaShortCode", dict: data)
        p.music = getStringSafe("music", dict: data)
        p.narration = getStringSafe("narration", dict: data)
        p.photographyType = getStringSafe("photographyType", dict: data)
        p.propertyAddress = getStringSafe("propertyAddress", dict: data)
        p.propertyImage = getStringSafe("propertyImage", dict: data)
        p.theme = getStringSafe("theme", dict: data)
    }
    
    func isValid(dict: NSDictionary) -> Bool {
        let name: NSString? = getValueForKey("name", dict: dict)
        if name == nil {
            return false
        }
        let guid: NSString? = getValueForKey("guid", dict: dict)
        if guid == nil {
            return false
        }
        
        // TODO: Add more validation
        
        return true
    }
    
    func getValueForKey(key: String, dict: NSDictionary) -> NSString? {
        var result: AnyObject? = nil
        result = nullToNil(dict[key])
        
        if result == nil {
            return nil;
        } else {
            let resultStr: NSString? = (result as! NSString?)
            return resultStr
        }
    }
    
    //by KBL 021216
    //Convert NSNull to nil: to avoid error "Could not cast value of type 'nsnull' to 'nsstring' swift"
    func nullToNil(value : AnyObject?) -> AnyObject? {
        if value is NSNull {
            return nil
        } else {
            return value
        }
    }
    
    
    func getStringSafe(key: String, dict: NSDictionary) -> String {
        let nsStr: NSString? = getValueForKey(key, dict: dict)
        if (nsStr == nil) {
            return ""
        } else {
            return nsStr as! String
        }
    }

    func getBoolSafe(key: String, dict: NSDictionary) -> Bool {
        let nsStr: NSString? = getValueForKey(key, dict: dict)
        if (nsStr == nil) {
            return false
        } else {
            return str2Bool(nsStr!)
        }
    }
    
    func str2Bool(str: NSString) -> Bool {
        if str == "true" || str == "1" {
            return true
        }
        return false
    }
    
    func bool2Str(val: Bool) -> String {
        if val == true {
            return "true"
        }
        return "false"
    }
}

class Presentation: NSManagedObject {

    @NSManaged var guid: String
    @NSManaged var lastUpdated: NSDate
    @NSManaged var agentPhoneNum: String
    @NSManaged var agentPhoto: String
    @NSManaged var autoplay: NSNumber
    @NSManaged var commBatchText: NSNumber
    @NSManaged var commEmail: NSNumber
    @NSManaged var commStats: NSNumber
    @NSManaged var companyLogo: String
    @NSManaged var companyName: String
    @NSManaged var desc: String
    @NSManaged var displayAgentImage: NSNumber
    @NSManaged var displayAgentName: NSNumber
    @NSManaged var displayCompanyLogo: NSNumber
    @NSManaged var displayCompanyName: NSNumber
    @NSManaged var displayPropAddress: NSNumber
    @NSManaged var displayPropImage: NSNumber
    @NSManaged var expBlogger: NSNumber
    @NSManaged var expCompanySite: NSNumber
    @NSManaged var expCraigslist: NSNumber
    @NSManaged var expFacebook: NSNumber
    @NSManaged var expLinkedin: NSNumber
    @NSManaged var expPersonalSite: NSNumber
    @NSManaged var expPinterest: NSNumber
    @NSManaged var expRealPortals: NSNumber
    @NSManaged var expSeoBoost: NSNumber
    @NSManaged var expTwitter: NSNumber
    @NSManaged var expYouTube: NSNumber
    @NSManaged var leadFacebook: NSNumber
    @NSManaged var leadLeadBee: NSNumber
    @NSManaged var leadMobile: NSNumber
    @NSManaged var leadOpenHouseAnnce: NSNumber
    @NSManaged var leadPropertySite: NSNumber
    @NSManaged var listingType: String
    @NSManaged var media24HourInfo: NSNumber
    @NSManaged var mediaDvds: NSNumber
    @NSManaged var mediaFlyers: NSNumber
    @NSManaged var mediaListingVideo: NSNumber
    @NSManaged var mediaMobile: NSNumber
    @NSManaged var mediaPropertySite: NSNumber
    @NSManaged var mediaQRCodes: NSNumber
    @NSManaged var mediaShortCode: NSNumber
    @NSManaged var music: String
    @NSManaged var name: String
    @NSManaged var narration: String
    @NSManaged var photographyType: String
    @NSManaged var propertyAddress: String
    @NSManaged var propertyImage: String
    @NSManaged var theme: String

    var isAutoplay: Bool {
        get {
            return Bool(autoplay)
        }
        set {
            autoplay = NSNumber(bool: newValue)
        }
    }
    var isMediaPropertySite: Bool {
        get {
            return Bool(mediaPropertySite)
        }
        set {
            mediaPropertySite = NSNumber(bool: newValue)
        }
    }
    var isMediaPropSite: Bool {
        get {
            return Bool(mediaPropertySite)
        }
        set {
            mediaPropertySite = NSNumber(bool: newValue)
        }
    }
    var isMediaListingVideo: Bool {
        get {
            return Bool(mediaListingVideo)
        }
        set {
            mediaListingVideo = NSNumber(bool: newValue)
        }
    }
    var isMediaQRCodes: Bool {
        get {
            return Bool(mediaQRCodes)
        }
        set {
            mediaQRCodes = NSNumber(bool: newValue)
        }
    }
    var isMedia24HourInfo: Bool {
        get {
            return Bool(media24HourInfo)
        }
        set {
            media24HourInfo = NSNumber(bool: newValue)
        }
    }
    var isMediaShortCode: Bool {
        get {
            return Bool(mediaShortCode)
        }
        set {
            mediaShortCode = NSNumber(bool: newValue)
        }
    }
    var isMediaFlyers: Bool {
        get {
            return Bool(mediaFlyers)
        }
        set {
            mediaFlyers = NSNumber(bool: newValue)
        }
    }
    var isMediaDvds: Bool {
        get {
            return Bool(mediaDvds)
        }
        set {
            mediaDvds = NSNumber(bool: newValue)
        }
    }
    var isMediaMobile: Bool {
        get {
            return Bool(mediaMobile)
        }
        set {
            mediaMobile = NSNumber(bool: newValue)
        }
    }
    
    var isExpRealPortals: Bool {
        get {
            return Bool(expRealPortals)
        }
        set {
            expRealPortals = NSNumber(bool: newValue)
        }
    }
    var isExpPersonalSite: Bool {
        get {
            return Bool(expPersonalSite)
        }
        set {
            expPersonalSite = NSNumber(bool: newValue)
        }
    }
    var isExpCompanySite: Bool {
        get {
            return Bool(expCompanySite)
        }
        set {
            expCompanySite = NSNumber(bool: newValue)
        }
    }
    var isExpBlogger: Bool {
        get {
            return Bool(expBlogger)
        }
        set {
            expBlogger = NSNumber(bool: newValue)
        }
    }
    var isExpYouTube: Bool {
        get {
            return Bool(expYouTube)
        }
        set {
            expYouTube = NSNumber(bool: newValue)
        }
    }
    var isExpFacebook: Bool {
        get {
            return Bool(expFacebook)
        }
        set {
            expFacebook = NSNumber(bool: newValue)
        }
    }
    var isExpTwitter: Bool {
        get {
            return Bool(expTwitter)
        }
        set {
            expTwitter = NSNumber(bool: newValue)
        }
    }
    var isExpCraigslist: Bool {
        get {
            return Bool(expCraigslist)
        }
        set {
            expCraigslist = NSNumber(bool: newValue)
        }
    }
    var isExpLinkedin: Bool {
        get {
            return Bool(expLinkedin)
        }
        set {
            expLinkedin = NSNumber(bool: newValue)
        }
    }
    var isExpPinterest: Bool {
        get {
            return Bool(expPinterest)
        }
        set {
            expPinterest = NSNumber(bool: newValue)
        }
    }
    var isExpSeoBoost: Bool {
        get {
            return Bool(expSeoBoost)
        }
        set {
            expSeoBoost = NSNumber(bool: newValue)
        }
    }
    
    var isLeadPropertySite: Bool {
        get {
            return Bool(leadPropertySite)
        }
        set {
            leadPropertySite = NSNumber(bool: newValue)
        }
    }
    var isLeadLeadBee: Bool {
        get {
            return Bool(leadLeadBee)
        }
        set {
            leadLeadBee = NSNumber(bool: newValue)
        }
    }
    var isLeadFacebook: Bool {
        get {
            return Bool(leadFacebook)
        }
        set {
            leadFacebook = NSNumber(bool: newValue)
        }
    }
    var isLeadMobile: Bool {
        get {
            return Bool(leadMobile)
        }
        set {
            leadMobile = NSNumber(bool: newValue)
        }
    }
    var isLeadOpenHouseAnnce: Bool {
        get {
            return Bool(leadOpenHouseAnnce)
        }
        set {
            leadOpenHouseAnnce = NSNumber(bool: newValue)
        }
    }
    
    var isCommEmail: Bool {
        get {
            return Bool(commEmail)
        }
        set {
            commEmail = NSNumber(bool: newValue)
        }
    }
    var isCommBatchText: Bool {
        get {
            return Bool(commBatchText)
        }
        set {
            commBatchText = NSNumber(bool: newValue)
        }
    }
    var isCommStats: Bool {
        get {
            return Bool(commStats)
        }
        set {
            commStats = NSNumber(bool: newValue)
        }
    }
    
    var isDisplayCompanyLogo: Bool {
        get {
            return Bool(displayCompanyLogo)
        }
        set {
            displayCompanyLogo = NSNumber(bool: newValue)
        }
    }
    var isDisplayCompanyName: Bool {
        get {
            return Bool(displayCompanyName)
        }
        set {
            displayCompanyName = NSNumber(bool: newValue)
        }
    }
    var isDisplayAgentImage: Bool {
        get {
            return Bool(displayAgentImage)
        }
        set {
            displayAgentImage = NSNumber(bool: newValue)
        }
    }
    var isDisplayAgentName: Bool {
        get {
            return Bool(displayAgentName)
        }
        set {
            displayAgentName = NSNumber(bool: newValue)
        }
    }
    var isDisplayPropAddress: Bool {
        get {
            return Bool(displayPropAddress)
        }
        set {
            displayPropAddress = NSNumber(bool: newValue)
        }
    }
    var isDisplayPropImage: Bool {
        get {
            return Bool(displayPropImage)
        }
        set {
            displayPropImage = NSNumber(bool: newValue)
        }
    }

    var jsondata: String {
        get {
            let dataStr: String = ""
            let fields: NSMutableDictionary = NSMutableDictionary()
            
            fields.setValue(guid, forKey: "guid")
            fields.setValue(agentPhoneNum, forKey: "agentPhoneNum")
            fields.setValue(agentPhoto, forKey: "agentPhoto")
            fields.setValue(bool2Str(isAutoplay), forKey: "autoplay")
            fields.setValue(bool2Str(isCommBatchText), forKey: "commBatchText")
            fields.setValue(bool2Str(isCommEmail), forKey: "commEmail")
            fields.setValue(bool2Str(isCommStats), forKey: "commStats")
            fields.setValue(companyLogo, forKey: "companyLogo")
            fields.setValue(companyName, forKey: "companyName")
            fields.setValue(desc, forKey: "description")
            fields.setValue(bool2Str(isDisplayAgentImage), forKey: "displayAgentImage")
            fields.setValue(bool2Str(isDisplayAgentName), forKey: "displayAgentName")
            fields.setValue(bool2Str(isDisplayCompanyLogo), forKey: "displayCompanyLogo")
            fields.setValue(bool2Str(isDisplayCompanyName), forKey: "displayCompanyName")
            fields.setValue(bool2Str(isDisplayPropAddress), forKey: "displayPropAddress")
            fields.setValue(bool2Str(isDisplayPropImage), forKey: "displayPropImage")
            fields.setValue(bool2Str(isExpBlogger), forKey: "expBlogger")
            fields.setValue(bool2Str(isExpCompanySite), forKey: "expCompanySite")
            fields.setValue(bool2Str(isExpCraigslist), forKey: "expCraigslist")
            fields.setValue(bool2Str(isExpFacebook), forKey: "expFacebook")
            fields.setValue(bool2Str(isExpLinkedin), forKey: "expLinkedin")
            fields.setValue(bool2Str(isExpPersonalSite), forKey: "expPersonalSite")
            fields.setValue(bool2Str(isExpPinterest), forKey: "expPinterest")
            fields.setValue(bool2Str(isExpRealPortals), forKey: "expRealPortals")
            fields.setValue(bool2Str(isExpSeoBoost), forKey: "expSeoBoost")
            fields.setValue(bool2Str(isExpTwitter), forKey: "expTwitter")
            fields.setValue(bool2Str(isExpYouTube), forKey: "expYouTube")
            fields.setValue(bool2Str(isLeadFacebook), forKey: "leadFacebook")
            fields.setValue(bool2Str(isLeadLeadBee), forKey: "leadLeadBee")
            fields.setValue(bool2Str(isLeadMobile), forKey: "leadMobile")
            fields.setValue(bool2Str(isLeadOpenHouseAnnce), forKey: "leadOpenHouseAnnce")
            fields.setValue(bool2Str(isLeadPropertySite), forKey: "leadPropertySite")
            fields.setValue(listingType, forKey: "listingType")
            fields.setValue(bool2Str(isMedia24HourInfo), forKey: "media24HourInfo")
            fields.setValue(bool2Str(isMediaDvds), forKey: "mediaDvds")
            fields.setValue(bool2Str(isMediaFlyers), forKey: "mediaFlyers")
            fields.setValue(bool2Str(isMediaListingVideo), forKey: "mediaListingVideo")
            fields.setValue(bool2Str(isMediaMobile), forKey: "mediaMobile")
            fields.setValue(bool2Str(isMediaPropertySite), forKey: "mediaPropertySite")
            fields.setValue(bool2Str(isMediaQRCodes), forKey: "mediaQRCodes")
            fields.setValue(bool2Str(isMediaShortCode), forKey: "mediaShortCode")
            fields.setValue(music, forKey: "music")
            fields.setValue(name, forKey: "name")
            fields.setValue(narration, forKey: "narration")
            fields.setValue(photographyType, forKey: "photographyType")
            fields.setValue(propertyAddress, forKey: "propertyAddress")
            fields.setValue(propertyImage, forKey: "propertyImage")
            fields.setValue(theme, forKey: "theme")

            if NSJSONSerialization.isValidJSONObject(fields) {
                do {
                    let data = try NSJSONSerialization.dataWithJSONObject(fields, options: [])
                    if let str = NSString(data: data, encoding: NSUTF8StringEncoding) {
                        return str as String
                    }
                } catch let error as NSError {
                    print("json error: \(error.localizedDescription)")
                }
            }

            return dataStr
        }
        set {
            // Do nothing
        }
    }
}

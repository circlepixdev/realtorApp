//
//  LoginHelper.swift
//  CirclePix
//
//  Created by Mark Burns on 2/28/15.
//  Copyright (c) 2015 Mark Burns. All rights reserved.
//

import Foundation

let kConnectionFailure = -1111

class LoginHelper {
    
    
    func performLogin(useXml: Bool, username: String, password: String) -> UserData {
        // Call the login API and get result string
        var resType = "xml"
        if !useXml {
            resType = "json"
        }
        
        // Bail if username and password empty
        if username == "" || password == "" {
            let userData: UserData = UserData()
            userData.status = -1
            return userData
        }
        
        let urlString = String(format: "http://videoupload.circlepix.com/thePearl/cpixVideoApp.%@?method=login&username=%@&password=%@", resType, username, password)
        print(urlString)
        var url = NSURL(string: urlString)
        let response = getRequestResponse(urlString)

        
        if response.length == 0 {
            // The API must have timed out
            let userData: UserData = UserData()
            userData.status = kConnectionFailure
            return userData
        }
        
        if useXml {
            return parseXmlResponse(response)
        } else {
            return parseJsonResponse(response)
        }
    }
    
    func getRequestResponse(urlToRequest: String) -> NSData {
        let request = NSURLRequest(URL: NSURL(string: urlToRequest)!, cachePolicy: NSURLRequestCachePolicy.ReloadIgnoringLocalCacheData, timeoutInterval: 3)
        var response: NSURLResponse?
        var error: NSError?
        let data: NSData?
        do {
            data = try NSURLConnection.sendSynchronousRequest(request, returningResponse: &response)
        } catch let error1 as NSError {
            error = error1
            data = nil
        }
        
        // TODO: Check the response code
        if error != nil {
            print("Error calling login url!")
            print(error!.code, terminator: "")
            print("  " + error!.localizedDescription, terminator: "")
            return NSData()
        }
        
        return data!
    }
    
    func parseXmlResponse(xmlToParse: NSData) -> UserData {
        // Parse the xml and get data
        let parser = XMLParser()
        let xml = parser.parse(xmlToParse)
        var realtorObj: Realtor?
        let userData: UserData = UserData()
        
        let statusStr: String? = xml["response"]["status"].element?.text
        let status: NSInteger = Int(statusStr!)!
        let message: NSString? = xml["response"]["message"].element?.text
        userData.status = status
        
        if status == 1 {
            let realtorId: NSString? = xml["response"]["realtor"]["id"].element?.text
            let realtorName: NSString? = xml["response"]["realtor"]["name"].element?.text
            let realtorCode: NSString? = xml["response"]["realtor"]["code"].element?.text
            var agency: NSString? = xml["response"]["realtor"]["agency"].element?.text
            if agency == nil {
                agency = ""
            }
            var email: NSString? = xml["response"]["realtor"]["email"].element?.text
            if email == nil {
                email = ""
            }
            var hasVideo: Bool = false
            // TODO: does the xml indicate video count for agent?
            var agentImageURL: NSString? = xml["response"]["realtor"]["agentImage"].element?.text
            let agentImageCharset = NSCharacterSet(charactersInString: "%2F")
            if agentImageURL == nil {
                agentImageURL = ""
            }else if agentImageURL!.lowercaseString.rangeOfCharacterFromSet(agentImageCharset) != nil {
                //there is an issue on the server side: agentImage url contains hex code %2F instead of just / so we need to handle this
                agentImageURL = agentImageURL!.stringByReplacingOccurrencesOfString("%2F", withString: "/")
            }

            var agentLogo: NSString? = xml["response"]["realtor"]["agentLogo"].element?.text
            let agentLogoCharset = NSCharacterSet(charactersInString: "%2F")
            if agentLogo == nil {
                agentLogo = ""
            }else if agentLogo!.lowercaseString.rangeOfCharacterFromSet(agentLogoCharset) != nil {
                //there is an issue on the server side: agentLogo url contains hex code %2F instead of just / so we need to handle this
               agentLogo = agentLogo!.stringByReplacingOccurrencesOfString("%2F", withString: "/")
            }
            
            var phone: NSString? = xml["response"]["realtor"]["phone"].element?.text
            if phone == nil {
                phone = ""
            }
            var mobile: NSString? = xml["response"]["realtor"]["mobile"].element?.text
            if mobile == nil {
                mobile = ""
            }
            var youTube: NSString? = xml["response"]["realtor"]["currentYouTubeVideo"].element?.text
            if youTube == nil {
                youTube = ""
            }
            
            realtorObj = Realtor(realtorId: realtorId!, realtorName: realtorName!, realtorCode: realtorCode!, agency: agency!, agentImageURL: agentImageURL!, agentLogo: agentLogo!, email: email!, phone: phone!, mobile: mobile!, youTubeVideoURL: youTube!)
            userData.realtor = realtorObj
            
            // Loop through listings
            for listing in xml["response"]["listings"]["listing"] {
                var mlsNum: NSString? = listing["mlsnum"].element?.text
                if mlsNum == nil {
                    mlsNum = ""
                }
                let listingId: NSString? = listing["listingid"].element?.text
                let listingCode: NSString? = listing["listingcode"].element?.text
                var address1: NSString? = listing["address1"].element?.text
                if address1 == nil {
                    address1 = ""
                }
                var address2: NSString? = listing["address2"].element?.text
                if address2 == nil {
                    address2 = ""
                }
                var listingURL: NSString? = listing["listingImage"].element?.text
                if listingURL == nil {
                    listingURL = ""
                }
                var cntStr: String? = listing["videoCount"].element?.text
                if cntStr == nil {
                    cntStr = "0"
                }
                let videoCount: NSInteger = Int(cntStr!)!
                
                let listingObj: Listing = Listing(mlsNum: mlsNum!, listingId: listingId!, listingCode: listingCode!, address1: address1!, address2: address2!, listingURL: listingURL!, videoCount: videoCount)
                userData.listings.addObject(listingObj)
            }
        }
        
        return userData
    }
    
    func parseJsonResponse(jsonToParse: NSData) -> UserData {
        // TODO: This code does not work because the json response
        // is a mess. It is a dump of several objects and it is
        // a pain to find the values.
        // It has been left here as a reference in case it is
        // useful at a later date.
        
        // Parse the JSON and get data
        let parsed = parseJSON(jsonToParse)
        var realtorObj: Realtor?
        let userData: UserData = UserData()
        
        // First get the top level values
        let status: NSInteger = parsed.valueForKey("status") as! NSInteger
        var message: NSString = parsed.valueForKey("message") as! NSString
        userData.status = status
        
        if status == 1 {
            // Get realtor data
            let realtor: NSDictionary = parsed.valueForKey("realtor") as! NSDictionary
            let fname: NSString = realtor.valueForKey("fname") as! NSString
            let lname: NSString = realtor.valueForKey("lname") as! NSString
            let fullname = (fname as String) + " " + (lname as String)
            let realtorId = realtor.valueForKey("realtorid") as! NSString
            let realtorCode = parsed.valueForKey("realtorCode") as! NSString
            var agentImageURL = parsed.valueForKey("agentPic") as! NSString
            if agentImageURL != "" {
                agentImageURL = "http://media.circlepix.com" + (agentImageURL as String)
            }
            let agency = realtor.valueForKey("agency") as! NSString
            var agentLogo = parsed.valueForKey("agentLogo") as! NSString
            if agentLogo != "" {
                agentLogo = "http://media.circlepix.com" + (agentLogo as String)
            }
            let email = realtor.valueForKey("email") as! NSString
            let phone = realtor.valueForKey("phone") as! NSString
            let mobile = realtor.valueForKey("cell") as! NSString
            let youTube = ""
            
            realtorObj = Realtor(realtorId: realtorId, realtorName: fullname, realtorCode: realtorCode, agency: agency, agentImageURL: agentImageURL, agentLogo: agentLogo, email: email, phone: phone, mobile: mobile, youTubeVideoURL: youTube)
            userData.realtor = realtorObj
            
            // Now get listings
            let listings: NSDictionary = parsed.valueForKey("listings") as! NSDictionary
            for (key, listing) in listings {
                print(listing, terminator: "")
                var mlsNum: NSString?
                if let tempVal: NSString = listing.valueForKey("mlsnum") as? NSString {
                    mlsNum = tempVal
                }
                let listingId = listing["id"]! as! NSString
                let listingCode = listing["shortHash"]! as! NSString
                let address1 = listing["addr"]! as! NSString
                let city = listing["city"]! as! NSString
                let state = listing.valueForKey("state") as! NSString
                let zip = listing.valueForKey("zip") as! NSString
                
                let address2 = String(format: "@, @ @", city, state, zip)
                let listingURL: NSString = listing.valueForKey("frontImage") as! NSString
                var videoCount: NSInteger = 0
                if let vcounts = parsed.valueForKey("videoCounts") as? NSDictionary {
                    if let cnt = vcounts.valueForKey(listingId as String) as? String {
                        videoCount = Int(cnt)!
                    }
                }
                
                let listingObj: Listing = Listing(mlsNum: mlsNum!, listingId: listingId, listingCode: listingCode, address1: address1, address2: address2, listingURL: listingURL, videoCount: videoCount)
                userData.listings.addObject(listingObj)
            }
        }
        else {
            // Failed
        }
        
        return userData
    }
    
    func parseJSON(inputData: NSData) -> NSDictionary{
        var error: NSError?
        do{
            let jsonResult: NSDictionary = try! NSJSONSerialization.JSONObjectWithData(inputData, options: NSJSONReadingOptions.MutableContainers) as! NSDictionary
            return jsonResult

        } catch let error as NSError {
            print("json error: \(error.localizedDescription)")
        }
    }
     

    func refreshListingData(appDel: AppDelegate) {
        let savedData = appDel.getSavedCredentials()
        let savedUsername: NSString = savedData.valueForKey("username") as! NSString
        let savedPassword: NSString = savedData.valueForKey("password") as! NSString
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), {
            let userData = self.performLogin(true, username: savedUsername as String, password: savedPassword as String)
            if userData.status == 1 {
                // Update the lastloggedin
                appDel.updateLastLoggedIn()
                appDel.userData = userData
                appDel.isLoggedIn = true
                appDel.isOfflineMode = false
                appDel.saveUserData()
            }
        })
    }
}

//
//  AppDelegate.swift
//  CirclePix
//
//  Created by Mark Burns on 10/7/14.
//  Copyright (c) 2014 CirclePix. All rights reserved.
//

import UIKit
import CoreData

let REACHABLE_WITH_WIFI = "ReachableViaWiFi"
let REACHABLE_WITH_4G = "ReachableViaWWAN"
let NOT_REACHABLE = "NOCONNECTION"

var reachability: Reachability?
var reachabilityStatus = NOT_REACHABLE

// Authentication properties
let kTheUsername = "Username"
let kThePassword = "Password"
let kLastLogin = "lastLogin"
let kFirstPresent = "firstPresent"
let TOKEN_EXPIRY = 7 * 24 * 60 * 60

// Agent data properties
let kRealtorId = "realtorId"
let kRealtorName = "realtorName"
let kRealtorCode = "realtorCode"
let kAgency = "realtorAgency"
let kAgentImageURL = "agentImageURL"
let kAgentLogo = "agentLogo"
let kRealtorEmail = "realtorEmail"
let kRealtorPhone = "realtorPhone"
let kRealtorMobile = "realtorMobile"
let kRealtorVideoURL: NSString = "realtorVideoURL"

// App settings properties
let kApplyToExistingPres = "applyToExistingPres"
let kDisplayCompanyLogo = "displayCompanyLogo"
let kDisplayCompanyName = "displayCompanyName"
let kDisplayAgentImage = "displayAgentImage"
let kDisplayAgentName = "displayAgentName"
let kGlobalAgentName = "globalAgentName"
let kDisplayColistingAgent = "displayColistingAgent"
let kGlobalBgMusic = "globalBgMusic"
let kGlobalNarration = "globalNarration"
let kGlobalThemeTemplate = "globalThemeTemplate"
let kGlobalThemeColor = "globalThemeColor"
let kDisplayDisclaimer = "displayDisclaimer"
let kGlobalDisclaimer = "globalDisclaimer"


@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?
    var internetReach: Reachability?
    var savedCredentials: NSMutableDictionary?
    var isLoggedIn: Bool = false
    var isOfflineMode: Bool = false
    var lastLogin: NSDate?
    var userData: UserData = UserData()
    
    func saveUserNameAndPassword(username: NSString, password: NSString) {
        
        // Save data to app preferences
        NSUserDefaults.standardUserDefaults().setObject(username, forKey: kTheUsername)
        NSUserDefaults.standardUserDefaults().setObject(password, forKey: kThePassword)
        updateLastLoggedIn()
        
        savedCredentials?.setValue(username, forKey: "username")
        savedCredentials?.setValue(password, forKey: "password")
    }
    
    func clearLogin() {
        saveUserNameAndPassword("", password: "")
        resetDefaults()
        isLoggedIn = false
    }
    
    func resetDefaults() {
        let defs: NSUserDefaults = NSUserDefaults.standardUserDefaults()
//        var dict: NSDictionary = defs.dictionaryRepresentation()   //was previously commented
        for key in defs.dictionaryRepresentation().keys {
//            defs.removeObjectForKey(key.description)
            defs.removeObjectForKey(key.debugDescription)

        }
        defs.synchronize()
    }
    
    func clearPresentations() {
        let context: NSManagedObjectContext = self.managedObjectContext!
        let fetchReq = NSFetchRequest(entityName: "Presentation")
        let presentations = try! context.executeFetchRequest(fetchReq)

        for p in presentations {
            context.deleteObject(p as! NSManagedObject)
        }
    }
    
    func updateLastLoggedIn() {
        let now: NSDate = NSDate()
        NSUserDefaults.standardUserDefaults().setObject(now, forKey: kLastLogin)
    }
    
    func getSavedCredentials() -> NSMutableDictionary {
        
        if savedCredentials != nil {
            return savedCredentials!
        }
        else {
            savedCredentials = NSMutableDictionary()
        }
        
        let username: NSString? = NSUserDefaults.standardUserDefaults().objectForKey(kTheUsername as String) as! NSString?
        let password: NSString? = NSUserDefaults.standardUserDefaults().objectForKey(kThePassword as String) as! NSString?
        let lastLogin: NSDate? = NSUserDefaults.standardUserDefaults().objectForKey(kLastLogin) as AnyObject? as! NSDate?

        // Set username/password if nil
        var returnUser: NSString = ""
        var returnPassword: NSString = ""
        
        if username != nil {
            returnUser = username!
        }
        
        if password != nil {
            returnPassword = password!
        }
        
        savedCredentials?.setValue(returnUser, forKey: "username")
        savedCredentials?.setValue(returnPassword, forKey: "password")
        savedCredentials?.setValue(lastLogin, forKey: kLastLogin)

        return savedCredentials!
    }
    
    func isFirstRun() -> Bool {
        let firstPresentDate: NSDate? = NSUserDefaults.standardUserDefaults().objectForKey(kFirstPresent) as AnyObject? as! NSDate?
        if firstPresentDate == nil {
            return true
        }
        
        return false
    }
    
    func setFirstRun() {
        let now: NSDate = NSDate()
        NSUserDefaults.standardUserDefaults().setObject(now, forKey: kFirstPresent)
    }
    
    func loadSavedUserData() {
        var realtorId: NSString? = NSUserDefaults.standardUserDefaults().objectForKey(kRealtorId as String) as! NSString?
        var realtorName: NSString? = NSUserDefaults.standardUserDefaults().objectForKey(kRealtorName as String) as! NSString?
        var realtorCode: NSString? = NSUserDefaults.standardUserDefaults().objectForKey(kRealtorCode as String) as! NSString?
        var agency: NSString? = NSUserDefaults.standardUserDefaults().objectForKey(kAgency as String) as! NSString?
        var agentImageURL: NSString? = NSUserDefaults.standardUserDefaults().objectForKey(kAgentImageURL as String) as! NSString?
        var agentLogo: NSString? = NSUserDefaults.standardUserDefaults().objectForKey(kAgentLogo as String) as! NSString?
        var email: NSString? = NSUserDefaults.standardUserDefaults().objectForKey(kRealtorEmail as String) as! NSString?
        var phone: NSString? = NSUserDefaults.standardUserDefaults().objectForKey(kRealtorPhone as String) as! NSString?
        var mobile: NSString? = NSUserDefaults.standardUserDefaults().objectForKey(kRealtorMobile as String) as! NSString?
        var videoUrl: NSString? = NSUserDefaults.standardUserDefaults().objectForKey(kRealtorVideoURL as String) as! NSString?

        if realtorId == nil {
            realtorId = ""
        }
        if realtorName == nil {
            realtorName = ""
        }
        if realtorCode == nil {
            realtorCode = ""
        }
        if agency == nil {
            agency = ""
        }
        if agentImageURL == nil {
            agentImageURL = ""
        }
        if agentLogo == nil {
            agentLogo = ""
        }
        if email == nil {
            email = ""
        }
        if phone == nil {
            phone = ""
        }
        if mobile == nil {
            mobile = ""
        }
        if videoUrl == nil {
            videoUrl = ""
        }
        
        let realtor = Realtor(realtorId: realtorId!, realtorName: realtorName!, realtorCode: realtorCode!, agency: agency!, agentImageURL: agentImageURL!, agentLogo: agentLogo!, email: email!, phone: phone!, mobile: mobile!, youTubeVideoURL: videoUrl!)
        
        userData.realtor = realtor
    }
    
    func saveUserData() {
        if let realtor = userData.realtor {
            NSUserDefaults.standardUserDefaults().setObject(realtor.realtorId, forKey: kRealtorId)
            NSUserDefaults.standardUserDefaults().setObject(realtor.realtorName, forKey: kRealtorName)
            NSUserDefaults.standardUserDefaults().setObject(realtor.realtorCode, forKey: kRealtorCode)
            NSUserDefaults.standardUserDefaults().setObject(realtor.agency, forKey: kAgency)
            NSUserDefaults.standardUserDefaults().setObject(realtor.agentImageURL, forKey: kAgentImageURL)
            NSUserDefaults.standardUserDefaults().setObject(realtor.agentLogo, forKey: kAgentLogo)
            NSUserDefaults.standardUserDefaults().setObject(realtor.email, forKey: kRealtorEmail)
            NSUserDefaults.standardUserDefaults().setObject(realtor.phone, forKey: kRealtorPhone)
            NSUserDefaults.standardUserDefaults().setObject(realtor.mobile, forKey: kRealtorMobile)
            NSUserDefaults.standardUserDefaults().setObject(realtor.youTubeVideoURL, forKey: kRealtorVideoURL as String)
            
            // Download image file for use in presentation
            if realtor.agentImageURL.length > 0 {
                let downloader = FileDownloader(urlString: realtor.agentImageURL, subfolder: kAgentDir, force: false)
                downloader.downloadAsync()
            }
            if realtor.agentLogo.length > 0 {
                let downloader2 = FileDownloader(urlString: realtor.agentLogo, subfolder: kAgentDir, force: false)
                downloader2.downloadAsync()
            }
            
        }
    }
    
    func getSettingAsString(key: String) -> NSString {
        let value: NSString? = NSUserDefaults.standardUserDefaults().objectForKey(key) as! NSString?
        if value == nil {
            return ""
        }
        return value!
    }
    
    func getSettingAsBool(key: String) -> Bool {
        var value: Bool = true
        if let tempVal: Bool = NSUserDefaults.standardUserDefaults().objectForKey(key) as! Bool? {
            value = (tempVal == true)
        }
        return value
    }
    
    
    
    func setStringSetting(key: String, value: String) {
        NSUserDefaults.standardUserDefaults().setObject(value, forKey: key)
    }
    
    func setBoolSetting(key: String, value: Bool) {
        NSUserDefaults.standardUserDefaults().setBool(value, forKey: key)
    }
    
    
    func application(application: UIApplication, didFinishLaunchingWithOptions launchOptions: [NSObject: AnyObject]?) -> Bool {
        
        // Listen for changes in network reachability
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "reachabilityChanged:", name: kReachabilityChangedNotification, object: nil)
        internetReach = Reachability.reachabilityForInternetConnection()
        internetReach?.startNotifier()
        if internetReach != nil {
            self.statusChangedWithReachability(internetReach!)
        }
        
        
        let pageControl = UIPageControl.appearance()
        pageControl.pageIndicatorTintColor = UIColor.lightGrayColor()
        pageControl.currentPageIndicatorTintColor = UIColor.blackColor()
        pageControl.backgroundColor = UIColor.whiteColor()
        
        return true
    }
    
    func reachabilityChanged(notification: NSNotification) {
        print("Reachability status changed ...")
        reachability = notification.object as? Reachability
        self.statusChangedWithReachability(reachability!)
    }
    
    func statusChangedWithReachability(currentReachabilityStatus: Reachability) {
        let networkStatus: NetworkStatus = currentReachabilityStatus.currentReachabilityStatus()
        var statusString: String = ""
        
        print("StatusValue: \(networkStatus.rawValue)")
        
        if networkStatus.rawValue == NotReachable.rawValue {
            print("The network is not reachable")
            reachabilityStatus = NOT_REACHABLE
        }
        else if networkStatus.rawValue == ReachableViaWiFi.rawValue {
            print("Reachable via wifi")
            reachabilityStatus = REACHABLE_WITH_WIFI
        }
        else if networkStatus.rawValue == ReachableViaWWAN.rawValue {
            print("Reachable via 4g")
            reachabilityStatus = REACHABLE_WITH_4G
        }
    }

    func applicationWillResignActive(application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
    }

    func applicationDidEnterBackground(application: UIApplication) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    }

    func applicationWillEnterForeground(application: UIApplication) {
        // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
    }

    func applicationDidBecomeActive(application: UIApplication) {
        // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    }

    func applicationWillTerminate(application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
        
        // remove observer (may not need this)
        NSNotificationCenter.defaultCenter().removeObserver(self, name: kReachabilityChangedNotification, object: nil)

        // Saves changes in the application's managed object context before the application terminates.
        self.saveContext()
    }
    
    
    //KBL: 020416 - Handle screen orientation of all views
    
    func application(application: UIApplication, supportedInterfaceOrientationsForWindow window: UIWindow?) -> UIInterfaceOrientationMask {
        //set orientation to Landscape only if it is on PresentationPageViewController
        print("ClassName: \(ClassNameHelper.ClassName.nameOfClass)")
        if(ClassNameHelper.ClassName.nameOfClass != nil && ClassNameHelper.ClassName.nameOfClass == "PresentationPageViewController"){
            return UIInterfaceOrientationMask.Landscape
        }else{
            return UIInterfaceOrientationMask.All
        }

    }
    
    // MARK: - Core Data stack

    lazy var applicationDocumentsDirectory: NSURL = {
        // The directory the application uses to store the Core Data store file. This code uses a directory named "com.circlepix.CirclePix" in the application's documents Application Support directory.
        let urls = NSFileManager.defaultManager().URLsForDirectory(.DocumentDirectory, inDomains: .UserDomainMask)
        return urls[urls.count-1] 
    }()

    lazy var managedObjectModel: NSManagedObjectModel = {
        // The managed object model for the application. This property is not optional. It is a fatal error for the application not to be able to find and load its model.
        let modelURL = NSBundle.mainBundle().URLForResource("CirclePix", withExtension: "momd")!
        return NSManagedObjectModel(contentsOfURL: modelURL)!
    }()
    
    

    lazy var persistentStoreCoordinator: NSPersistentStoreCoordinator? = {
        // The persistent store coordinator for the application. This implementation creates and return a coordinator, having added the store for the application to it. This property is optional since there are legitimate error conditions that could cause the creation of the store to fail.
        // Create the coordinator and store
        var coordinator: NSPersistentStoreCoordinator? = NSPersistentStoreCoordinator(managedObjectModel: self.managedObjectModel)
        let url = self.applicationDocumentsDirectory.URLByAppendingPathComponent("CirclePix.sqlite")
     //   var error: NSError? = nil
        var failureReason = "There was an error creating or loading the application's saved data."
        do {
            try coordinator!.addPersistentStoreWithType(NSSQLiteStoreType, configuration: nil, URL: url, options: nil)
        } catch { //var error1 as NSError {
           // error = error1
            coordinator = nil
         
            // Report any error we got.
            var dict = [String: AnyObject]()
            
            dict[NSLocalizedDescriptionKey] = "Failed to initialize the application's saved data"
            dict[NSLocalizedFailureReasonErrorKey] = failureReason
 //           dict[NSUnderlyingErrorKey] = error
 //           error = NSError(domain: "CPIX_ERROR_DOMAIN", code: 9999, userInfo: dict)
           //  Replace this with code to handle the error appropriately.
            // abort() causes the application to generate a crash log and terminate. You should not use this function in a shipping application, although it may be useful during development.
            NSLog("Unresolved error \(error), \(error)")
            abort()
            
            
            
            
//commented by keu 082915
//            // Report any error we got.
////            let dict = NSMutableDictionary() as [NSObject : AnyObject]
//            let dict = NSMutableDictionary()
//
//            dict[NSLocalizedDescriptionKey] = "Failed to initialize the application's saved data"
//            dict[NSLocalizedFailureReasonErrorKey] = failureReason
//            dict[NSUnderlyingErrorKey] = error
////            error = NSError(domain: "CPIX_ERROR_DOMAIN", code: 9999, userInfo: dict as [NSObject : AnyObject])
//            error = NSError(domain: "CPIX_ERROR_DOMAIN", code: 9999, userInfo: dict)
//
//            // Replace this with code to handle the error appropriately.
//            // abort() causes the application to generate a crash log and terminate. You should not use this function in a shipping application, although it may be useful during development.
//            NSLog("Unresolved error \(error), \(error!.userInfo)")
//            abort()
//        } catch {
//            fatalError()
        }
        
        return coordinator
    }()

    lazy var managedObjectContext: NSManagedObjectContext? = {
        // Returns the managed object context for the application (which is already bound to the persistent store coordinator for the application.) This property is optional since there are legitimate error conditions that could cause the creation of the context to fail.
        let coordinator = self.persistentStoreCoordinator
        if coordinator == nil {
            return nil
        }
        var managedObjectContext = NSManagedObjectContext()
        managedObjectContext.persistentStoreCoordinator = coordinator
        return managedObjectContext
    }()

    // MARK: - Core Data Saving support

    func saveContext () {
        if let moc = self.managedObjectContext {
            var error: NSError? = nil
            if moc.hasChanges {
                do {
                    try moc.save()
                } catch let error1 as NSError {
                    error = error1
                    // Replace this implementation with code to handle the error appropriately.
                    // abort() causes the application to generate a crash log and terminate. You should not use this function in a shipping application, although it may be useful during development.
                    NSLog("Unresolved error \(error), \(error!.userInfo)")
                    abort()
                }
            }
        }
    }

}


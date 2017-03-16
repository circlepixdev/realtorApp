//
//  SyncService.swift
//  CirclePix
//
//  Created by Mark Burns on 12/2/14.
//  Copyright (c) 2014 Mark Burns. All rights reserved.
//

import Foundation
import UIKit
import CoreData

protocol SyncServiceDelegate {
    func uploadSuccess(didSucceed: Bool, message: NSString?);
}

class SyncService: NSObject, NSURLConnectionDelegate, NSURLConnectionDataDelegate , UIAlertViewDelegate {
    
    var presentations: Array<AnyObject> = []
    var receivedData: NSMutableData = NSMutableData()
    var delegate: SyncServiceDelegate?
    var alert: UIAlertView!
    var loadingIndicator: UIActivityIndicatorView!
    var totalPres: Int = 0
    var totalSize: Int?
    
    var df = NSDateFormatter()
   
    func performAsyncSync() {
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), {
           
            self.performSync()
        })
    }
       
    // Perform a full sync
    func performSync() {
       // Check for realtor data (must have)
        let appDel: AppDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
        if appDel.userData.realtor == nil {
            // This should never happen
           NSLog("Could not sync because realtor was nil")
           return

         }
        
        // Check for connection (must have)
        if reachabilityStatus == NOT_REACHABLE {
            // This should never happen
            NSLog("Could not sync because no connection")
            return
        }
       
        //show alertview
        dispatch_async(dispatch_get_main_queue(), {() -> Void in
            
            self.alert = UIAlertView(title: "Loading...", message: nil , delegate: nil, cancelButtonTitle: nil)
            let viewBack:UIView = UIView(frame: CGRectMake(83,0,100,60))
            
            self.loadingIndicator = UIActivityIndicatorView(activityIndicatorStyle: .WhiteLarge)
            self.loadingIndicator.frame = CGRectMake(50, 10, 37, 37)
            self.loadingIndicator.center = viewBack.center
            self.loadingIndicator.hidesWhenStopped = true
            self.loadingIndicator.color = UIColor.blackColor()
            self.loadingIndicator.startAnimating()
            viewBack.addSubview(self.loadingIndicator)
            viewBack.center = CGPointMake(self.alert.bounds.size.width / 2, self.alert.bounds.size.height / 2)
            self.alert.setValue(viewBack, forKey: "accessoryView")
            self.loadingIndicator.startAnimating()
            self.alert.show()
            
        })
        
        
        let realtor = appDel.userData.realtor!
       
        // Get the list of items from the server
        let handler = SyncApiHandler()
        let pa:NSArray = handler.getPresentations(realtor.realtorId)
        // Create a map of guid,updated from the server results
        var serverDict = Dictionary<NSString, NSDate>(minimumCapacity: pa.count)
        df.dateFormat = "yyyy-MM-dd HH:mm:ss"
        df.timeZone = NSTimeZone.systemTimeZone()
        
        
        totalPres = pa.count
        if(totalPres == 0){
            dispatch_async(dispatch_get_main_queue()) {
                self.alert.dismissWithClickedButtonIndex(0, animated: true)
                self.loadingIndicator.stopAnimating()
            }
            totalPres = 0
        }
        
        for p in pa {
            let guid:NSString = p.valueForKey("GUID") as! NSString
            var dateStr:NSString = p.valueForKey("updated") as! NSString
            var splittedArray = dateStr.componentsSeparatedByString("-")
            
            let year: String = splittedArray[0]
            
            if (year == "0000" || year == "1970" || year == "1969"){
                NSLog("Bad date from server data. Year: \(year)")
                
                //replace bad date to current date
                dateStr = df.stringFromDate(NSDate())
            }
            
            let date:NSDate = df.dateFromString(dateStr as String)!
            serverDict[guid] = date
            // DEBUG
            NSLog("%@, %@", guid, dateStr)
            // DEBUG
        }
        
        
        // Compare with local data
        let context: NSManagedObjectContext = appDel.managedObjectContext!
        let fetchReq = NSFetchRequest(entityName: "Presentation")
        presentations = try! context.executeFetchRequest(fetchReq)
     //        if appDel.isFirstRun() && presentations.count == 0 {  // edited by Keu 011316
        if presentations.count == 0 { //update app when presentation is = 0 ; not just only on first run
            
            if pa.count == 0 {
                
                return
            }
            
            
            // There are records on the server and this is the first run
            // so insert the server records into the local system. This
            // will only happen if the user has created presentations in
            // the past and then logged in from a name device (or deleted
            // the app from the device and reinstalled).
            let en = NSEntityDescription.entityForName("Presentation", inManagedObjectContext: context)
            for p in pa {
                // Get the detail data using the guid
                let guid:NSString = p.valueForKey("GUID") as! NSString
                let detail:NSDictionary = handler.getPresentation(guid)
                let name:NSString = detail.valueForKey("name") as! NSString
                let fields:NSDictionary = detail.valueForKey("fields") as! NSDictionary
                
                // Create new instance from server data and save
                var p: Presentation!
                p = Presentation(entity: en!, insertIntoManagedObjectContext: context)
                p.setDefaults(p)
                if p.isValid(fields) {
                    p.setDictionaryValues(p, data: fields)
                    p.name = name as String
                    p.lastUpdated = NSDate()
                    p.guid = guid as String
                
                    do {
                        // Save
                        try context.save()
                    } catch _ {
                    }
                    
                    
                } else {
                    // TODO: Figure out how to notify of bad data
                }
                
                
                
                if(totalPres != 0){
                    totalSize = totalPres - 1
                    print("totalSize count: \(totalSize)")
                    
                    totalPres = totalSize!
                    print("totalPres count: \(totalPres)")
                }
                
                if(totalPres == 0){
                    dispatch_async(dispatch_get_main_queue()) {
                        self.alert.dismissWithClickedButtonIndex(0, animated: true)
                        self.loadingIndicator.stopAnimating()
                    }
                    totalPres = 0
                }

            }
            
            
        } else {
            //by keu 011316
            //this is for multiple devices; check if there are presentations that needed to be updated and added to the app
           // for serverPresentations in pa {
           //     let serverguid:NSString = serverPresentations.valueForKey("GUID") as! NSString
           
            for (key, value) in serverDict {
                let serverguid: String = key as String
                var found: Bool = false
               
                for pres in presentations as! [Presentation] {
                    if(serverguid == pres.guid){
                        found = true
                        print("equal guid: update")
                        let guid:NSString = serverguid
                        let detail:NSDictionary = handler.getPresentation(serverguid)
                        let name:NSString = detail.valueForKey("name") as! NSString
                        let fields:NSDictionary = detail.valueForKey("fields") as! NSDictionary
                    
                //        let context: NSManagedObjectContext = appDel.managedObjectContext!
                //        let en = NSEntityDescription.entityForName("Presentation", inManagedObjectContext: context)

                        let p: Presentation = try! context.existingObjectWithID(pres.objectID) as! Presentation

                        if p.isValid(fields) {
                            p.setDictionaryValues(p, data: fields)
                            p.name = name as String
                            p.lastUpdated = NSDate()
                            p.guid = guid as String
                            
                            do {
                                // Save
                                try context.save()
                            } catch _ {
                            }
                            
                        } else {
                            // TODO: Figure out how to notify of bad data
                        }
                        
                        
                        if(totalPres != 0){
                            totalSize = totalPres - 1
                            print("totalSize count: \(totalSize)")
                            
                            totalPres = totalSize!
                            
                            print("totalPres count found: \(totalPres)")
                            
                        }
                        
                        if(totalPres == 0){
                            dispatch_async(dispatch_get_main_queue()) {
                                self.alert.dismissWithClickedButtonIndex(0, animated: true)
                                self.loadingIndicator.stopAnimating()
                            }
                            totalPres = 0
                            
                        }
                    }
                }
                
                if(!found){
                    print("not equal guid: add new")
                    
                    let en = NSEntityDescription.entityForName("Presentation", inManagedObjectContext: context)
                    // Get the detail data using the guid
                    let guid:NSString = serverguid
                    let detail:NSDictionary = handler.getPresentation(serverguid)
                    let name:NSString = detail.valueForKey("name") as! NSString
                    let fields:NSDictionary = detail.valueForKey("fields") as! NSDictionary
                     print("error6")
                    // Create new instance from server data and save
                    var p: Presentation!
                    p = Presentation(entity: en!, insertIntoManagedObjectContext: context)
                    p.setDefaults(p)
                    if p.isValid(fields) {
                        p.setDictionaryValues(p, data: fields)
                        p.name = name as String
                        p.lastUpdated = NSDate()
                        p.guid = guid as String

                        do {
                            // Save
                            try context.save()
                        } catch _ {
                        }
                    } else {
                        // TODO: Figure out how to notify of bad data
                    }
                    
                    
                    if(totalPres != 0){
                        totalSize = totalPres - 1
                        print("totalSize count: \(totalSize)")
                        
                        totalPres = totalSize!
                        print("totalPres count not found: \(totalPres)")
                    }
                    
                    if(totalPres == 0){
                        dispatch_async(dispatch_get_main_queue()) {
                            self.alert.dismissWithClickedButtonIndex(0, animated: true)
                            self.loadingIndicator.stopAnimating()
                        }
                        totalPres = 0
                        
                    }
                }
               
               
 
             }//ends here
            
            
            // 2-way comparison
            var localDict = Dictionary<String, NSDate>()
            
            // if they exist locally then push to server
            print("2way comparison")
            var i:Int = 0
            while i < presentations.count {
                let b = presentations[i] as! Presentation
                
                let isNew = (serverDict[b.guid] == nil)
                var isNewer = false
                if serverDict[b.guid] != nil {
                    let serverDate:NSDate = serverDict[b.guid]!
                    print(serverDate)
                    print(b.lastUpdated)
                    let ti = b.lastUpdated.timeIntervalSinceDate(serverDate)
                    if ti > 30.0 {
                        isNewer = true
                    }
                }
                localDict[b.guid] = b.lastUpdated
                
                // Do insert/update to server
                if isNew || isNewer {
                    // Save to server
                    handler.savePresentation(realtor.realtorId, presentation: b)
                }
                i++
            }
            
 //            If one on the server does not exist locally then delete from the server
            for (key, value) in serverDict {
                let guid: String = key as String
                if localDict[guid] == nil {
                    // Delete from server
                    handler.deletePresentation(realtor.realtorId, guid: guid)
                }
            }
        }
        // Done
    }
    
    func syncOne(p: Presentation) {
        // Check for realtor data (must have)
        let appDel: AppDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
        if appDel.userData.realtor == nil {
            // This should never happen
            NSLog("Could not sync because realtor was nil")
            return
        }
        
        // Check for connection (must have)
        if reachabilityStatus == NOT_REACHABLE {
            // This should never happen
            NSLog("Could not sync because no connection")
            return
        }
        
        let realtor = appDel.userData.realtor!
        
        // Call to perform the save
        let handler = SyncApiHandler()
        handler.savePresentation(realtor.realtorId, presentation: p)
    }
    
    func deleteOne(guid: NSString) {
        // Check for realtor data (must have)
        let appDel: AppDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
        if appDel.userData.realtor == nil {
            // This should never happen
            NSLog("Could not sync because realtor was nil")
            return
        }
        
        // Check for connection (must have)
        if reachabilityStatus == NOT_REACHABLE {
            // This should never happen
            NSLog("Could not sync because no connection")
            return
        }
        
        let realtor = appDel.userData.realtor!
        
        // Call to perform the delete
        let handler = SyncApiHandler()
        handler.deletePresentation(realtor.realtorId, guid: guid)
    }
    
    
}
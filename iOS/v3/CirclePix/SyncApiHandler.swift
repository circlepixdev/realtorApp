//
//  SyncApiHandler.swift
//  CirclePix
//
//  Created by Mark Burns on 12/2/14.
//  Copyright (c) 2014 Mark Burns. All rights reserved.
//

import Foundation
import UIKit

class SyncApiHandler: NSObject, NSURLConnectionDelegate, NSURLConnectionDataDelegate {
    
    var receivedData: NSMutableData = NSMutableData()
    var progressBar: UIProgressView?
    var status: NSInteger = 0
    var message: NSString = ""
    
    func getPresentations(realtorId: NSString) -> NSArray {
        let results:NSArray = NSArray()
        
        // Make the call and parse the response
        let urlString = String(format: "http://www.circlepix.com/api/getPresentationIDs.php?realtorId=%@", realtorId)
        var url = NSURL(string: urlString)
        let response = getRequestResponse(urlString)
        let parsed = parseJSON(response)
        
        // First get the top level values
        status = parsed.valueForKey("status") as! NSInteger
        message = parsed.valueForKey("message") as! NSString
        
        if status == 1 {
            let data: NSDictionary = parsed.valueForKey("data") as! NSDictionary
            let pa:NSArray = data.valueForKey("RealtorPresentations") as! NSArray
            return pa
        }
        
        return results
    }
    
    func getPresentation(guid: NSString) -> NSDictionary {
        let results:NSMutableDictionary = NSMutableDictionary()
        
        // Make the call and parse the response
        let urlString = String(format: "http://www.circlepix.com/api/getPresentation.php?GUID=%@", guid)
        var url = NSURL(string: urlString)
        let response = getRequestResponse(urlString)
        let parsed = parseJSON(response)
        
        // First get the top level values
        status = parsed.valueForKey("status") as! NSInteger
        message = parsed.valueForKey("message") as! NSString
        
        if status == 1 {
            let data: NSDictionary = parsed.valueForKey("data") as! NSDictionary
            let detail:NSDictionary = data.valueForKey("RealtorPresentation") as! NSDictionary
            let jsonStr:NSString = detail.valueForKey("JsonString") as! NSString
            let fields:NSDictionary = parseJSON(jsonStr.dataUsingEncoding(NSUTF8StringEncoding)!)

            results.setValue(detail.valueForKey("Name"), forKey: "name")
            results.setValue(detail.valueForKey("RealtorPresentationID"), forKey: "id")
            results.setValue(detail.valueForKey("RealtorPresentationGUID"), forKey: "guid")
            results.setValue(detail.valueForKey("RealtorID"), forKey: "realtorId")
            results.setValue(fields, forKey: "fields")
        }
        
        return results
    }
    
    func deletePresentation(realtorId: NSString, guid: NSString) -> Int {
        
        // Make the call and parse the response
        let urlString = String(format: "http://www.circlepix.com/api/deletePresentation.php?GUID=%@&realtorId=%@", guid, realtorId)
        var url = NSURL(string: urlString)
        let response = getRequestResponse(urlString)
        let parsed = parseJSON(response)
        
        // First get the top level values
        status = parsed.valueForKey("status") as! NSInteger
        message = parsed.valueForKey("message") as! NSString
        
        if status != 1 {
            NSLog("Error deleting %@. %@", guid, message)
        }
        
        return status
    }
    
    func savePresentation(realtorId: NSString, presentation: Presentation) -> Int {
        
        // Get the lastUpdate date to t string
        let df = NSDateFormatter()
        df.dateFormat = "yyyy-MM-dd HH:mm:ss"
        let strDate = df.stringFromDate(presentation.lastUpdated)

        // Make the call and parse the response
        let postString: NSMutableString = NSMutableString()
        // Debug
        let urlStringdb = String(format: "http://www.circlepix.com/api/updatePresentation.php?GUID=%@&realtorId=%@&name=%@&updated=%@", presentation.guid, realtorId, presentation.name, strDate)
        print(urlStringdb, terminator: "")
        // Debug
        let urlString: String = "http://www.circlepix.com/api/updatePresentation.php"
        let url = NSURL(string: urlString)
        let request: NSMutableURLRequest = NSMutableURLRequest(URL: url!)
        request.HTTPMethod = "POST"
        let boundary = "---------------------------14737809831466499882746641449"
        let contentType = "multipart/form-data; boundary=" + boundary
        request.addValue(contentType, forHTTPHeaderField: "Content-Type")
        let postbody: NSMutableData = NSMutableData()
        let endBoundary: NSString = NSString(format: "\r\n--%@\r\n", boundary)
        postString.appendFormat("--%@\r\n", boundary)
        postString.appendFormat("Content-Disposition: form-data; name=\"GUID\"\r\n\r\n%@", presentation.guid)
        postString.appendString(endBoundary as String)
        postString.appendFormat("Content-Disposition: form-data; name=\"realtorId\"\r\n\r\n%@", realtorId)
        postString.appendString(endBoundary as String)
        postString.appendFormat("Content-Disposition: form-data; name=\"name\"\r\n\r\n%@", presentation.name)
        postString.appendString(endBoundary as String)
        postString.appendFormat("Content-Disposition: form-data; name=\"updated\"\r\n\r\n%@", strDate)
        postString.appendString(endBoundary as String)
        postString.appendFormat("Content-Disposition: form-data; name=\"json\"\r\n\r\n%@", presentation.jsondata)
        postString.appendString(endBoundary as String)
        NSLog("%@",postString)
        postbody.appendData(postString.dataUsingEncoding(NSUTF8StringEncoding)!)
        request.HTTPBody = postbody
        NSLog("%@",request)
        
        if let conn: NSURLConnection = NSURLConnection(request: request, delegate: self) {
            //mutable data to receive data
            //self.receivedData = NSMutableData()
        }
        else {
            // Tell the user the connection failed
            //return -99
        }
        
        // TODO: Actually check the result
//        if status != 1 {
//            NSLog("Error saving to server. %@", message)
//        }
        
        return status
    }
    
    func getRequestResponse(urlToRequest: String) -> NSData {
        return NSData(contentsOfURL: NSURL(string: urlToRequest)!)!
    }

    func parseJSON(inputData: NSData) -> NSDictionary{
        
        do {
            var error: NSError?
            let jsonResult: NSDictionary = try! NSJSONSerialization.JSONObjectWithData(inputData, options: NSJSONReadingOptions.MutableContainers) as! NSDictionary
            print("json result : \(jsonResult)")
            return jsonResult
        } catch let error as NSError {
            print("json error: \(error.localizedDescription)")
        }
    }

    func connection(connection: NSURLConnection, didFailWithError error: NSError) {
        // inform the user
        NSLog("Connection failed! Error - %@", error.localizedDescription)
    }
    
    func connection(connection: NSURLConnection, didRecieveResponse response: NSURLResponse)  {
        NSLog("Recieved response")
    }
    
    func connection(connection: NSURLConnection, didReceiveResponse response: NSURLResponse) {
        // Recieved a new request, clear out the data object
        // This method is called when the server has determined that it
        // has enough information to create the NSURLResponse.
        
        // It can be called multiple times, for example in the case of a
        // redirect, so each time we reset the data.
        
        // receivedData is an instance variable declared elsewhere.
        self.receivedData = NSMutableData()
    }
    
    func connection(connection: NSURLConnection, didReceiveData data: NSData) {
        // Append the recieved chunk of data to our data object
        self.receivedData.appendData(data)
        
    }
    
    func connection(connection: NSURLConnection, didSendBodyData bytesWritten: Int, totalBytesWritten: Int, totalBytesExpectedToWrite: Int) {
        // Update the progressbar
        if self.progressBar != nil {
            let theBytesWritten: NSDecimalNumber = NSDecimalNumber(string: NSString(format: "%ld", totalBytesWritten) as String)
            let totalBytes: NSDecimalNumber = NSDecimalNumber(string: NSString(format: "%ld", totalBytesExpectedToWrite) as String)
            let divided: NSDecimalNumber = theBytesWritten.decimalNumberByDividingBy(totalBytes)
            self.progressBar?.setProgress(divided.floatValue, animated: true)
        }
    }
    
    func connectionDidFinishLoading(connection: NSURLConnection) {
        
        // do something with the data
        NSLog("Succeeded! Received %d bytes of data", receivedData.length);
        var returnString: NSString = NSString(data: self.receivedData, encoding: NSUTF8StringEncoding)!
        //NSLog("%@", returnString)
        
        // Parse the response
        let parsed = parseJSON(self.receivedData)
        status = parsed.valueForKey("status") as! NSInteger
        message = parsed.valueForKey("message") as! NSString
        
        if status != 1 {
            NSLog("Error saving to server. %@", message)
        }
        
    }

}
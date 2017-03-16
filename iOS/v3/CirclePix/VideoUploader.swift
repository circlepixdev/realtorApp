//
//  VideoUploader.swift
//  CirclePix
//
//  Created by Mark Burns on 11/7/14.
//  Copyright (c) 2014 Mark Burns. All rights reserved.
//

import Foundation
import UIKit

protocol VideoUploaderDelegate {
    func uploadSuccess(didSucceed: Bool, message: NSString?);
}

class VideoUploader: NSObject, NSURLConnectionDelegate, NSURLConnectionDataDelegate {
    
    var receivedData: NSMutableData = NSMutableData()
    //var viewController: UIViewController?
    var delegate: VideoUploaderDelegate?
    var progressBar: UIProgressView?
    
    func uploadFile(fileData: NSData, isRealtorVideo: Bool, objectId: NSString, objectCode: NSString, title: NSString, description: NSString) -> Int {
        
        let methodString: NSString = "upload"
        //var identifierString: NSString = objectId
        var typeStr: NSString = "realtor"
        if !isRealtorVideo {
            typeStr = "tour"
        }
        let uploadId: Int32 = rand();
        
        // Setup the POST request
        let postString: NSMutableString = NSMutableString()
        let urlString: NSString = NSString(format: "http://videoupload1.circlepix.com/thePearl/cpixVideoApp.xml?method=upload&objectType=%@&objectId=%@", typeStr, objectId)
        let filename: NSString = "listingVideo"
        let url = NSURL(string: urlString as String)
        let request: NSMutableURLRequest = NSMutableURLRequest(URL: url!)
        request.HTTPMethod = "POST"
        let boundary: NSString = "---------------------------14737809831466499882746641449"
        let contentType: NSString = NSString(format: "multipart/form-data; boundary=%@", boundary)
        request.addValue(contentType as String, forHTTPHeaderField: "Content-Type")
        let postbody: NSMutableData = NSMutableData()
        let endBoundary: NSString = NSString(format: "\r\n--%@\r\n", boundary)
        postString.appendFormat("--%@\r\n", boundary)
        postString.appendFormat("Content-Disposition: form-data; name=\"method\"\r\n\r\n%@", methodString)
        postString.appendString(endBoundary as String)
        postString.appendFormat("Content-Disposition: form-data; name=\"title\"\r\n\r\n%@", title)
        postString.appendString(endBoundary as String)
        postString.appendFormat("Content-Disposition: form-data; name=\"description\"\r\n\r\n%@", description)
        postString.appendString(endBoundary as String)
        postString.appendFormat("Content-Disposition: form-data; name=\"objectType\"\r\n\r\n%@", typeStr)
        postString.appendString(endBoundary as String)
        postString.appendFormat("Content-Disposition: form-data; name=\"objectId\"\r\n\r\n%@", objectId)
        postString.appendString(endBoundary as String)
        postString.appendFormat("Content-Disposition: form-data; name=\"code\"\r\n\r\n%@", objectCode)
        postString.appendString(endBoundary as String)
        postString.appendFormat("Content-Disposition: form-data; name=\"UPLOAD_IDENTIFIER\"\r\n\r\n%d", uploadId)
        postString.appendString(endBoundary as String)
        postString.appendFormat("Content-Disposition: form-data; name=\"videoFile\"; filename=\"%@.m4v\"\r\n", filename)
        postString.appendString("Content-Type: video/mp4\r\n\r\n")
        NSLog("%@",postString)
        postbody.appendData(postString.dataUsingEncoding(NSUTF8StringEncoding)!)
        postbody.appendData(NSData(data: fileData))
        postbody.appendData(endBoundary.dataUsingEncoding(NSUTF8StringEncoding)!)
        request.HTTPBody = postbody
        NSLog("%@",request)

        if let conn: NSURLConnection = NSURLConnection(request: request, delegate: self) {
            //mutable data to receive data
            //self.receivedData = NSMutableData()
        }
        else {
            // Tell the user the connection failed
            return -99
        }
        
        return Int(uploadId)
    }
    
    func connection(connection: NSURLConnection, didFailWithError error: NSError) {
        // inform the user
        NSLog("Connection failed! Error - %@", error.localizedDescription)
    }
    
    func connection(connection: NSURLConnection, didRecieveResponse response: NSURLResponse)  {
        print("Recieved response")
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
        let theBytesWritten: NSDecimalNumber = NSDecimalNumber(string: NSString(format: "%ld", totalBytesWritten) as String)
        let totalBytes: NSDecimalNumber = NSDecimalNumber(string: NSString(format: "%ld", totalBytesExpectedToWrite) as String)
        let divided: NSDecimalNumber = theBytesWritten.decimalNumberByDividingBy(totalBytes)
        print("didSendBodyData")
        self.progressBar?.setProgress(divided.floatValue, animated: true)
    }
    
    func connectionDidFinishLoading(connection: NSURLConnection) {
        
        // do something with the data
        NSLog("Succeeded! Received %d bytes of data", receivedData.length);
        let returnString: NSString = NSString(data: self.receivedData, encoding: NSUTF8StringEncoding)!
        NSLog("%@", returnString)
        
        // Parse the response
        let parser = XMLParser()
        let xml = parser.parse(self.receivedData)
        
        let statusStr: String? = xml["response"]["status"].element?.text
        let status: NSInteger = Int(statusStr!)!
        let message: NSString? = xml["response"]["message"].element?.text

        if status == 1 {
            // Tell the caller that it was a success
            self.delegate!.uploadSuccess(true, message: message)
        }
        else {
            // Tell the caller that the upload failed
            self.delegate!.uploadSuccess(false, message: message)
        }
        
    }
    
}

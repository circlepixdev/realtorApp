//
//  FileDownloader.swift
//  CirclePix
//
//  Created by Mark Burns on 3/18/15.
//  Copyright (c) 2015 Mark Burns. All rights reserved.
//

import Foundation

let kAgentDir = "agent"

class FileDownloader: NSObject, NSURLConnectionDelegate, NSURLConnectionDataDelegate {
    
    var urlString: NSString?
    var subfolder: NSString?
    var forceDownload: Bool = false
    
    init (urlString: NSString, subfolder: NSString, force: Bool) {
        self.urlString = urlString
        self.subfolder = subfolder
        self.forceDownload = force
    }
    
    func downloadAsync() {
        dispatch_async(dispatch_get_global_queue( DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), {
            self.downloadFile()
        })
    }
    
    func downloadFile() {
        
        // Abort if bad url or no connection
        if urlString == nil || urlString?.length == 0 {
            return
        }
        if reachabilityStatus == NOT_REACHABLE {
            return
        }
        
        // Get the name of the file
        let url: NSURL = NSURL(string: urlString! as String)!
        
        print(url)
        var fileName = url.lastPathComponent
        // Setup the storage location
        let path: String = FileHelper().cachedFilePathFromUrl(urlString!, subfolder: subfolder, createFolders: true) as String
        
        // Check if the file exists
        let fileExists: Bool = NSFileManager.defaultManager().fileExistsAtPath(path)
        if fileExists && !self.forceDownload {
            return
        }
        
        // Download the image and save it
        let error: NSError? = nil
        let data = try! NSData(contentsOfURL: url, options: [])
        if error == nil {
            var result = NSFileManager.defaultManager().createFileAtPath(path, contents: data, attributes: nil)
            //NSLog("%@ download %@", result ? "succeeded" : "failed")
        } else {
            NSLog("%@ download returned nil", urlString!)
        }
    }
    
    func hasLocalCachedFile() -> Bool {
        return FileHelper().cachedFileExists(urlString!, subfolder: subfolder)
    }
}
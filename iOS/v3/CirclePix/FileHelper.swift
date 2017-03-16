//
//  FileHelper.swift
//  CirclePix
//
//  Created by Mark Burns on 3/19/15.
//  Copyright (c) 2015 Mark Burns. All rights reserved.
//

import Foundation

class FileHelper: NSObject {
    
    /*
    *   Return the last segment of a url. This assumes that the last segment
    *   is a filename. It does not checking to see if it is a directory rather
    *   than a filename.
    */
    func filenameFromUrl(urlString: NSString) -> NSString {
        let url: NSURL = NSURL(string: urlString as String)!
        let fileName = url.lastPathComponent!
        return fileName
    }
    
    /*
    *   Return the path that a file would be stored in if that file were downloaded
    *   to our documents folder. The optional subfolder is added to the path if is
    *   non-empty.
    */
    func cachedFilePathFromUrl(urlString: NSString, subfolder: NSString?, createFolders: Bool) -> NSString {
        // Get the filename form the url
        let filename = filenameFromUrl(urlString)
        
        // Get the path in the documents
        let documentsPath = NSSearchPathForDirectoriesInDomains(.DocumentDirectory, .UserDomainMask, true)[0] as NSString
        var path: NSString = documentsPath as NSString
        if subfolder != nil && subfolder?.length > 0 {
            path = documentsPath.stringByAppendingPathComponent(subfolder! as String)
            
            // If folder does not exist then create it
            if createFolders && !NSFileManager.defaultManager().fileExistsAtPath(path as String) {
                var error: NSError? = nil
                do {
                    try NSFileManager.defaultManager().createDirectoryAtPath(path as String, withIntermediateDirectories: true, attributes: nil)
                } catch let error1 as NSError {
                    error = error1
                }
            }
        }
        
        path = path.stringByAppendingPathComponent(filename as NSString as String)
        return path
    }
    
    /*
    *   Return the file url that a file would be stored in if that file were downloaded
    *   to our documents folder. The optional subfolder is added to the path if is
    *   non-empty.
    */
    func cachedFileUrlFromUrl(urlString: NSString, subfolder: NSString?, createFolders: Bool) -> NSURL? {
        // Get the filename form the url
        let filename = filenameFromUrl(urlString)
        
        // Get the path in the documents
        let documentsPath = NSSearchPathForDirectoriesInDomains(.DocumentDirectory, .UserDomainMask, true)[0] as NSString
        var path: NSString = documentsPath
        if subfolder != nil && subfolder?.length > 0 {
            path = documentsPath.stringByAppendingPathComponent(subfolder! as String) as String
            
            // If folder does not exist then create it
            if createFolders && !NSFileManager.defaultManager().fileExistsAtPath(path as String) {
                var error: NSError? = nil
                do {
                    try NSFileManager.defaultManager().createDirectoryAtPath(path as String, withIntermediateDirectories: true, attributes: nil)
                } catch let error1 as NSError {
                    error = error1
                }
            }
        }
        
        path = path.stringByAppendingPathComponent(filename as String)
        return NSURL(fileURLWithPath: path as String)
    }
    
    func cachedFileExists(urlString: NSString, subfolder: NSString?) -> Bool {
        let path = cachedFilePathFromUrl(urlString, subfolder: subfolder, createFolders: false)
        return NSFileManager.defaultManager().fileExistsAtPath(path as String)
    }
}
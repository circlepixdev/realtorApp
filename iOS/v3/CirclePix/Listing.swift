//
//  Listing.swift
//  CirclePix
//
//  Created by Mark Burns on 10/10/14.
//  Copyright (c) 2014 CirclePix. All rights reserved.
//

import Foundation
import UIKit

class Listing {
    var mlsNum: NSString = ""
    var listingId: NSString = ""
    var listingCode: NSString = ""
    var address1: NSString = ""
    var address2: NSString = ""
    var listingURL: NSString = ""
    var listingImage: UIImage?
    var videoCount: NSInteger?
    
    init (mlsNum: NSString, listingId: NSString, listingCode: NSString, address1: NSString, address2: NSString, listingURL: NSString, videoCount: NSInteger) {
        self.mlsNum = mlsNum
        self.listingId = listingId
        self.listingCode = listingCode
        self.address1 = address1
        self.address2 = address2
        self.listingURL = listingURL
        self.videoCount = videoCount
        
//        if reachabilityStatus != NOT_REACHABLE {
//            populateImage()
//        }
    }
    
//    func populateImage () {
//        // Get the photo from the url and save it
//        dispatch_async(dispatch_get_global_queue( DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), {
//            self.listingImage =  UIImage(data: NSData(contentsOfURL: NSURL(string:self.listingURL)!)!)
//        })
//    }
}
//
//  Realtor.swift
//  CirclePix
//
//  Created by Mark Burns on 10/10/14.
//  Copyright (c) 2014 CirclePix. All rights reserved.
//

import Foundation

class Realtor {
    var realtorId: NSString = ""
    var realtorName: NSString = ""
    var realtorCode: NSString = ""
    //var imageURL: NSString = ""
    var agency: NSString = ""
    var hasVideo: Bool = false
    var agentImageURL: NSString = ""
    var agentLogo: NSString = ""
    var email: NSString = ""
    var phone: NSString = ""
    var mobile: NSString = ""
    var youTubeVideoURL: NSString = ""
    
    init(realtorId: NSString, realtorName: NSString, realtorCode: NSString, agency: NSString, agentImageURL: NSString, agentLogo: NSString, email: NSString, phone: NSString, mobile: NSString, youTubeVideoURL: NSString) {
        self.realtorId = realtorId
        self.realtorName = realtorName
        self.realtorCode = realtorCode
        self.agency = agency
        self.agentImageURL = agentImageURL
        self.agentLogo = agentLogo
        self.email = email
        self.phone = phone
        self.mobile = mobile
        self.youTubeVideoURL = youTubeVideoURL
    }
}
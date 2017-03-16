//
//  PresentationPage.swift
//  CirclePix
//
//  Created by Mark Burns on 11/1/14.
//  Copyright (c) 2014 Mark Burns. All rights reserved.
//

import Foundation

class PresentationPage {
    
    var storyboardId: String = ""
    var pageId: Int = 0
    var pageOrder: Int = 0
    var name: String = ""
    var audioFile: String = ""
    var field: String = ""
    var active: Bool = true
    
    init(storyboardId: String, pageId: Int, pageOrder: Int, name: String, audioFile: String, field: String, active: Bool) {
        self.storyboardId = storyboardId
        self.pageId = pageId
        self.pageOrder = pageOrder
        self.name = name
        self.audioFile = audioFile
        self.field = field
        self.active = active
    }
    
}
//
//  DismissSegue.swift
//  CirclePix
//
//  Created by Mark Burns on 10/9/14.
//  Copyright (c) 2014 CirclePix. All rights reserved.
//

import UIKit

@objc(DismissSegue) class DismissSegue: UIStoryboardSegue {
    
    override func perform() {
        (sourceViewController.presentingViewController as UIViewController!).dismissViewControllerAnimated(true, completion: nil)
    }
}

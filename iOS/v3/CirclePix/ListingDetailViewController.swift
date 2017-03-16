//
//  ListingDetailViewController.swift
//  CirclePix
//
//  Created by Mark Burns on 10/11/14.
//  Copyright (c) 2014 Mark Burns. All rights reserved.
//

import UIKit

class ListingDetailViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()

        // Style the navbar
        let logo = UIImage(named: "logo_lt.png")
        let logoView = UIImageView(image: logo)
        self.navigationItem.titleView = logoView
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue!, sender: AnyObject!) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}

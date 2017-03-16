//
//  StyledNavController.swift
//  CirclePix
//
//  Created by Mark Burns on 11/26/14.
//  Copyright (c) 2014 Mark Burns. All rights reserved.
//

import UIKit

class StyledNavController: UINavigationController {

    override func viewDidLoad() {
        super.viewDidLoad()

        // Style the nav bar
        self.navigationBar.barTintColor = UIColor(red: 100.0/255.0, green: 100/255.0, blue: 100/255.0, alpha: 1.0) //UIColor(red: 32.0/255.0, green: 41/255.0, blue: 69/255.0, alpha: 1.0)
        self.navigationBar.tintColor = UIColor.whiteColor()  //UIColor(red: 219.0/255.0, green: 199.0/255.0, blue: 157.0/255.0, alpha: 1.0)
        let logo = UIImage(named: "logo_lt.png")
        let logoView = UIImageView(image: logo)
        self.navigationItem.titleView = logoView
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

}

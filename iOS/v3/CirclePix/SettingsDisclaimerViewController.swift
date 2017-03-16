//
//  SettingsDisclaimerViewController.swift
//  CirclePix
//
//  Created by Mark Burns on 2/26/15.
//  Copyright (c) 2015 Mark Burns. All rights reserved.
//

import UIKit

class SettingsDisclaimerViewController: UITableViewController {

    @IBOutlet weak var disclaimerTextView: UITextView!
    @IBOutlet weak var displayDisclaimerSwitch: UISwitch!
    
    let appDel: AppDelegate = UIApplication.sharedApplication().delegate as! AppDelegate

    override func viewDidLoad() {
        super.viewDidLoad()

        // Apply settings
        displayDisclaimerSwitch.on = appDel.getSettingAsBool(kDisplayDisclaimer)
        
        // Style the navbar
        let logo = UIImage(named: "logo_lt.png")
        let logoView = UIImageView(image: logo)
        self.navigationItem.titleView = logoView
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    @IBAction func displayDisclaimerSwitchClicked(sender: AnyObject) {
        appDel.setBoolSetting(kDisplayDisclaimer, value: displayDisclaimerSwitch.on)
    }

}

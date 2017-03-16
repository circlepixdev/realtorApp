//
//  SettingAgentNameViewController.swift
//  CirclePix
//
//  Created by Mark Burns on 2/25/15.
//  Copyright (c) 2015 Mark Burns. All rights reserved.
//

import UIKit

class SettingAgentNameViewController: UITableViewController {

    @IBOutlet weak var agentNameSwitch: UISwitch!
    @IBOutlet weak var agentNameField: UITextField!
    @IBOutlet weak var colistingSwitch: UISwitch!
    let appDel: AppDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Set controls
        agentNameSwitch.on = appDel.getSettingAsBool(kDisplayAgentName)
        agentNameField.text = appDel.getSettingAsString(kGlobalAgentName) as String
        colistingSwitch.on = appDel.getSettingAsBool(kDisplayColistingAgent)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    @IBAction func agentNameSwitchClicked(sender: AnyObject) {
        appDel.setBoolSetting(kDisplayAgentName, value: agentNameSwitch.on)
    }

    @IBAction func agentNameEdited(sender: AnyObject) {
        appDel.setStringSetting(kGlobalAgentName, value: agentNameField.text!)
    }
    
    @IBAction func colistingSwitchClicked(sender: AnyObject) {
        appDel.setBoolSetting(kDisplayColistingAgent, value: colistingSwitch.on)
    }
    
}

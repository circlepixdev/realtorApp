//
//  SettingsMainViewController.swift
//  CirclePix
//
//  Created by Mark Burns on 2/25/15.
//  Copyright (c) 2015 Mark Burns. All rights reserved.
//

import UIKit

class SettingsMainViewController: UITableViewController {
    
    @IBOutlet weak var globalPresentSettings: UITableViewCell!

    override func viewDidLoad() {
        super.viewDidLoad()

        // Add a logout button to right navbar
//        let logoutBtn = UIBarButtonItem(title: "Logout", style: UIBarButtonItemStyle.Plain, target: self, action: "handleLogout:")
//        self.navigationItem.rightBarButtonItem = logoutBtn

        // Style the navbar
        self.navigationItem.title = "Save"
     //   self.navigationItem.backBarButtonItem?.tintColor = UIColor.blackColor() // for testing arrow back on navbar
        
        let logo = UIImage(named: "logo_lt.png")
        let logoView = UIImageView(image: logo)
        self.navigationItem.titleView = logoView
        
        //Disable back swipe gesture in UINavigationController - so Loading UIAlertView won't show when swiping from the very left of the screen
        if self.navigationController!.respondsToSelector("interactivePopGestureRecognizer") {
            self.navigationController!.interactivePopGestureRecognizer!.enabled = false
        }

    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    override func tableView(tableView: UITableView?, didSelectRowAtIndexPath indexPath: NSIndexPath?) {
        // Handle the rows that have subviews
        if let ip = indexPath {
            let cell = tableView?.cellForRowAtIndexPath(ip)
            print("You selected row at #\(ip.row)!")
            if cell == globalPresentSettings {
                let settingsPresentView = self.storyboard?.instantiateViewControllerWithIdentifier("settingsPresentView") as! SettingsPresentViewController
                self.navigationController?.pushViewController(settingsPresentView, animated: true)
            }
        }
    }
    
    func handleLogout(sender: AnyObject) {
        // Reset the login and push to login view
        let appDel: AppDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
        appDel.clearLogin()
        appDel.clearPresentations()
        let loginView = self.storyboard?.instantiateViewControllerWithIdentifier("loginView") as! LoginViewController
        loginView.isLaunch = false
        self.navigationController?.pushViewController(loginView, animated: true)
    }

}

//
//  HomeTableViewController.swift
//  CirclePix
//
//  Created by Mark Burns on 11/27/14.
//  Copyright (c) 2014 Mark Burns. All rights reserved.
//

import UIKit

class HomeTableViewController: UITableViewController {
   
//    struct HomeTableViewController1 {
//        static var yourVariable = "someString"
//    }
    @IBOutlet weak var presentationsCell: UITableViewCell!
    @IBOutlet weak var listingsCell: UITableViewCell!
    @IBOutlet weak var agentCell: UITableViewCell!
    @IBOutlet weak var settingsCell: UITableViewCell!
    
    // TODO: Try putting auth/sync code in loadView method to try to get the splash screen to stay up longer. Try this in a test app first.
    
    var appDel: AppDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
   
    
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
        
        print("view will appear")
        
   //     if !appDel.isFirstRun(){
   //         print("is not FirstRun")
            if appDel.isLoggedIn {
                             
                // Try silent auth
                let helper = LoginHelper()
                let savedData = appDel.getSavedCredentials()
                let savedUsername: NSString = savedData.valueForKey("username") as! NSString
                let savedPassword: NSString = savedData.valueForKey("password") as! NSString
                
                //URI encode
                let un = savedUsername.stringByAddingPercentEncodingWithAllowedCharacters(.URLHostAllowedCharacterSet())!
                let pw = savedPassword.stringByAddingPercentEncodingWithAllowedCharacters(.URLHostAllowedCharacterSet())!
                
                let userData = helper.performLogin(true, username: un as String, password: pw as String)
                if userData.status == 1 {
                    // Update the lastloggedin
                    appDel.updateLastLoggedIn()
                    appDel.userData = userData
                    appDel.isLoggedIn = true
                    appDel.isOfflineMode = false
                    appDel.saveUserData()
                    self.navigationController?.setNavigationBarHidden(false, animated: true)
                    
                    print("will sync from viewWillAppear")
                    
                    let sync = SyncService()
                    sync.performAsyncSync()
                }
            }
     //   }
        
    }
    
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Handle auth/login here
        print("isLoggedIn \(appDel.isLoggedIn)")

        if (!appDel.isLoggedIn) {
            if reachabilityStatus == NOT_REACHABLE {
                // Check for a saved token (for offline mode)
                let now = NSDate()
                if let ll = appDel.getSavedCredentials().valueForKey(kLastLogin) as? NSDate {
                    let expires = ll.dateByAddingTimeInterval(NSTimeInterval(TOKEN_EXPIRY))
                    if expires.compare(now) == NSComparisonResult.OrderedDescending {
                        // Token is good
                        // Load old listing and agent data
                        appDel.loadSavedUserData()
                        appDel.isOfflineMode = true
                        
                        // Alert user about limited functionality
                        let alert = UIAlertController(title: "Internet Unavailable", message: "Currently there is no network connection. You can use the app but some functionality will be disabled.", preferredStyle: UIAlertControllerStyle.Alert)
                        alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.Cancel, handler: { action -> Void in
                            return
                        }))
                        self.presentViewController(alert, animated: true, completion: nil)
                        
                        return
                    }
                    else {
                        // Warn about no connection and Go to login page
                        let alert = UIAlertController(title: "Internet Unavailable", message: "Currently there is no network connection and it has been too long since you last logged in. Please connect to a network and login.", preferredStyle: UIAlertControllerStyle.Alert)
                        alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.Cancel, handler: { action -> Void in
                            return
                        }))
                        self.presentViewController(alert, animated: true, completion: nil)
                    }
                }
            }
            
            // Try silent auth
            let helper = LoginHelper()
            let savedData = appDel.getSavedCredentials()
            let savedUsername: NSString = savedData.valueForKey("username") as! NSString
            let savedPassword: NSString = savedData.valueForKey("password") as! NSString

            //URI encode
            let un = savedUsername.stringByAddingPercentEncodingWithAllowedCharacters(.URLHostAllowedCharacterSet())!
            let pw = savedPassword.stringByAddingPercentEncodingWithAllowedCharacters(.URLHostAllowedCharacterSet())!

            let userData = helper.performLogin(true, username: un as String, password: pw as String)
            if userData.status == 1 {
                print("going to sync")
                // Update the lastloggedin
                appDel.updateLastLoggedIn()
                appDel.userData = userData
                appDel.isLoggedIn = true
                appDel.isOfflineMode = false
                appDel.saveUserData()
                self.navigationController?.setNavigationBarHidden(false, animated: true)
                
                print("will sync from viewDidLoad")

                // Sync data with the server
              //  let sync = SyncService()
              //  sync.performAsyncSync()
                
             
            } else {
                // Push to the login view
                let loginView = self.storyboard?.instantiateViewControllerWithIdentifier("loginView") as! LoginViewController
                loginView.isLaunch = true
                self.navigationController?.pushViewController(loginView, animated: true)
            }
        }else{
            // Sync data with the server
             print("is not LoggedIn \(appDel.isLoggedIn)")
            if appDel.isFirstRun(){

                let sync = SyncService()
                sync.performAsyncSync()
            }

        }
        
        // Add a logout button to right navbar
        let logoutBtn = UIBarButtonItem(title: "Logout", style: UIBarButtonItemStyle.Plain, target: self, action: "handleLogout:")
        self.navigationItem.rightBarButtonItem = logoutBtn
        
        // Style the navbar
        let logo = UIImage(named: "logo_lt.png")
        let logoView = UIImageView(image: logo)
        self.navigationItem.titleView = logoView
    }
    
    // This doesn't seem to work
    override func preferredStatusBarStyle() -> UIStatusBarStyle {
        return UIStatusBarStyle.LightContent;
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func tableView(tableView: UITableView?, didSelectRowAtIndexPath indexPath: NSIndexPath?) {
        // Handle the rows that have subviews
        if let ip = indexPath {
            let cell = tableView?.cellForRowAtIndexPath(ip)
            
            if cell == presentationsCell {
                let presentationsView = self.storyboard?.instantiateViewControllerWithIdentifier("presentationsListView") as! PresentationsTableViewController
                self.navigationController?.pushViewController(presentationsView, animated: true)
            }
            else if cell == listingsCell {
                let listingsView = self.storyboard?.instantiateViewControllerWithIdentifier("listingsView") as! ListingsTableViewController
                //listingsView.listings = self.listings
                self.navigationController?.pushViewController(listingsView, animated: true)
            }
            else if cell == agentCell {
                let agentView = self.storyboard?.instantiateViewControllerWithIdentifier("agentView") as! AgentViewController
                self.navigationController?.pushViewController(agentView, animated: true)
            }
            else if cell == settingsCell {
                let settingsView = self.storyboard?.instantiateViewControllerWithIdentifier("settingsView") as! SettingsMainViewController
                self.navigationController?.pushViewController(settingsView, animated: true)
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

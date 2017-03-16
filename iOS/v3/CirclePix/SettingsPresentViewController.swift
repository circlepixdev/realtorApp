//
//  SettingsPresentViewController.swift
//  CirclePix
//
//  Created by Mark Burns on 2/25/15.
//  Copyright (c) 2015 Mark Burns. All rights reserved.
//

import UIKit
import CoreData

class SettingsPresentViewController: UITableViewController, UIAlertViewDelegate {

    @IBOutlet weak var applyToExistingPresSwitch: UISwitch!
    @IBOutlet weak var displayCompanyLogoSwitch: UISwitch!
    @IBOutlet weak var displayCompanyNameSwitch: UISwitch!
    @IBOutlet weak var displayAgentImageSwitch: UISwitch!
    @IBOutlet weak var displayAgentNameSwitch: UISwitch!
    @IBOutlet weak var bgMusicCell: UITableViewCell!
//    @IBOutlet weak var displayAgentNameCell: UITableViewCell!
//    @IBOutlet weak var themeCell: UITableViewCell!
    @IBOutlet weak var narrationCell: UITableViewCell!
//    @IBOutlet weak var bgMusicCell: UITableViewCell!
//    @IBOutlet weak var disclaimerCell: UITableViewCell!
    let appDel: AppDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Style the navbar
        self.navigationItem.title = "Save"
        let logo = UIImage(named: "logo_lt.png")
        let logoView = UIImageView(image: logo)
        self.navigationItem.titleView = logoView
        
        // Set switches based on app settings
        applyToExistingPresSwitch.on = appDel.getSettingAsBool(kApplyToExistingPres)
        displayCompanyLogoSwitch.on = appDel.getSettingAsBool(kDisplayCompanyLogo)
        displayCompanyNameSwitch.on = appDel.getSettingAsBool(kDisplayCompanyName)
        displayAgentImageSwitch.on = appDel.getSettingAsBool(kDisplayAgentImage)
        displayAgentNameSwitch.on = appDel.getSettingAsBool(kDisplayAgentName)
    }
    
 
    func backAction(sender: UIBarButtonItem) {
        //Your functionality
        print("back button is pressed")
    }
    
    @IBAction func applyToExistingPresBtnClicked(sender: AnyObject) {
        appDel.setBoolSetting(kApplyToExistingPres, value: applyToExistingPresSwitch.on)
    }

    @IBAction func displayCompanyLogoBtnClicked(sender: AnyObject) {
        appDel.setBoolSetting(kDisplayCompanyLogo, value: displayCompanyLogoSwitch.on)
    }
    
    @IBAction func displayCompanyNameBtnClicked(sender: AnyObject) {
        appDel.setBoolSetting(kDisplayCompanyName, value: displayCompanyNameSwitch.on)
    }
    
    @IBAction func displayAgentImageBtnClicked(sender: AnyObject) {
        appDel.setBoolSetting(kDisplayAgentImage, value: displayAgentImageSwitch.on)
    }
    
    @IBAction func displayAgentNameBtnClicked(sender: AnyObject) {
        appDel.setBoolSetting(kDisplayAgentName, value: displayAgentNameSwitch.on)
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
            if cell == narrationCell {
                let settingsNarrationView = self.storyboard?.instantiateViewControllerWithIdentifier("settingsNarrationView") as! SettingsNarrationViewController
                self.navigationController?.pushViewController(settingsNarrationView, animated: true)
            }
//            if cell == displayAgentNameCell {
//                let settingsAgentView = self.storyboard?.instantiateViewControllerWithIdentifier("settingsAgentNameView") as SettingAgentNameViewController
//                self.navigationController?.pushViewController(settingsAgentView, animated: true)
//            }
//            else if cell == themeCell {
//                let settingsThemeView = self.storyboard?.instantiateViewControllerWithIdentifier("settingsThemeView") as SettingsThemeViewController
//                self.navigationController?.pushViewController(settingsThemeView, animated: true)
//            }
            else if cell == bgMusicCell {
                let settingsBgMusicView = self.storyboard?.instantiateViewControllerWithIdentifier("settingsBgMusicView") as! SettingsBgMusicViewController
                self.navigationController?.pushViewController(settingsBgMusicView, animated: true)
            }
//            else if cell == disclaimerCell {
//                let settingsDisclaimerView = self.storyboard?.instantiateViewControllerWithIdentifier("settingsDisclaimerView") as SettingsDisclaimerViewController
//                self.navigationController?.pushViewController(settingsDisclaimerView, animated: true)
//            }
        }
    }
    
    
    func checkSave(){
        
        // Validate form items
        if applyToExistingPresSwitch.on {
            let alert = UIAlertController(title: "Apply to existing presentations", message: "Do you want to save and apply current Global Settings to your existing and new presentations?", preferredStyle: UIAlertControllerStyle.Alert)
            alert.addAction(UIAlertAction(title: "Yes", style: UIAlertActionStyle.Default, handler: { action -> Void in
                self.applyToExistingPres()
            }))
            
            alert.addAction(UIAlertAction(title: "No", style: UIAlertActionStyle.Cancel, handler: { action -> Void in
                //do nothing
            }))
            
            self.presentViewController(alert, animated: true, completion: nil)
            
        }else{
            let alert = UIAlertController(title:"Warning", message: "\"Apply to Existing Presentations\" is currently disabled. Current Global Settings will not be applied.", preferredStyle: UIAlertControllerStyle.Alert)
            alert.addAction(UIAlertAction(title: "Ok", style: UIAlertActionStyle.Default, handler: { action -> Void in
                //do nothing
            }))
            
            self.presentViewController(alert, animated: true, completion: nil)
        }

    }
  
    
     //update presentations before coming back to HomeActivity so it will be updated both server and app Db data
    func applyToExistingPres(){
        var presentations: Array<AnyObject> = []
        let context: NSManagedObjectContext = appDel.managedObjectContext!
        let fetchReq = NSFetchRequest(entityName: "Presentation")
        presentations = try! context.executeFetchRequest(fetchReq)
        var p: Presentation
        
       
        for pres in presentations as! [Presentation] {
            
            p = try! context.existingObjectWithID(pres.objectID) as! Presentation
            p.displayCompanyLogo = appDel.getSettingAsBool(kDisplayCompanyLogo)
            p.displayCompanyName = appDel.getSettingAsBool(kDisplayCompanyName)
            p.displayAgentImage = appDel.getSettingAsBool(kDisplayAgentImage)
            p.displayAgentName = appDel.getSettingAsBool(kDisplayAgentName)
            p.narration = appDel.getSettingAsString(kGlobalNarration) as String
            p.music = appDel.getSettingAsString(kGlobalBgMusic) as String
            p.lastUpdated = NSDate()

            do {
                // Save
                try context.save()
                print("presentation saved")
            } catch _ {
            }
            
            let sync = SyncService()
            sync.syncOne(p)
            
       }

    }

    
    override func willMoveToParentViewController(parent: UIViewController?) {
        if parent == nil {
            print("The user hit the back button")
            checkSave()
        }
    }


}

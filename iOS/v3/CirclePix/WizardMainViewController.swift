//
//  WizardMainViewController.swift
//  CirclePix
//
//  Created by Mark Burns on 10/20/14.
//  Copyright (c) 2014 Mark Burns. All rights reserved.
//

import UIKit
import CoreData

class WizardMainViewController: UITableViewController, UIGestureRecognizerDelegate, UIAlertViewDelegate {
    
    @IBOutlet weak var nameField: UITextField!
    
    var presentationId: NSManagedObjectID!
    let appDel: AppDelegate = UIApplication.sharedApplication().delegate as! AppDelegate

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        //self.title = "Presentation"
        
        // Set control values from object
        if presentationId != nil {
            let context: NSManagedObjectContext = appDel.managedObjectContext!
            var errorp: NSError?
            let p: Presentation = try! context.existingObjectWithID(presentationId!) as! Presentation
            nameField.text = p.name
        }
        
        // Style the navbar
        self.navigationItem.title = "Save"
        let logo = UIImage(named: "logo_lt.png")
        let logoView = UIImageView(image: logo)
        self.navigationItem.titleView = logoView
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func gestureRecognizer(_: UIGestureRecognizer,
        shouldRecognizeSimultaneouslyWithGestureRecognizer:UIGestureRecognizer) -> Bool {
            return true
    }
    
    func gestureRecognizer(gestureRecognizer: UIGestureRecognizer, shouldReceiveTouch touch: UITouch) -> Bool {
            return true
    }
    
    @IBAction func mediaBtnPressed(sender: AnyObject) {
        savePresentationData()
        let wizardMediaView = self.storyboard?.instantiateViewControllerWithIdentifier("wizardMediaView") as! WizardMediaViewController
        wizardMediaView.presentationId = self.presentationId
        self.navigationController?.pushViewController(wizardMediaView, animated: true)
    }

    @IBAction func expBtnPressed(sender: AnyObject) {
        savePresentationData()
        let wizardExpView = self.storyboard?.instantiateViewControllerWithIdentifier("wizardExpView") as! WizardExposureViewController
        wizardExpView.presentationId = self.presentationId
        self.navigationController?.pushViewController(wizardExpView, animated: true)
    }

    @IBAction func commBtnPressed(sender: AnyObject) {
        savePresentationData()
        let wizardCommView = self.storyboard?.instantiateViewControllerWithIdentifier("wizardCommView") as! WizardCommViewController
        wizardCommView.presentationId = self.presentationId
        self.navigationController?.pushViewController(wizardCommView, animated: true)
    }
    
    @IBAction func settingsBtnPressed(sender: AnyObject) {
        savePresentationData()
        let wizardSettingsView = self.storyboard?.instantiateViewControllerWithIdentifier("wizardSettingsView") as! WizardSettingsViewController
        wizardSettingsView.presentationId = self.presentationId
        self.navigationController?.pushViewController(wizardSettingsView, animated: true)
    }
    
    @IBAction func starSettingsBtnPressed(sender: AnyObject) {
        savePresentationData()
        let wizardStarSettingsView = self.storyboard?.instantiateViewControllerWithIdentifier("wizardStarSettingsView") as! WizardStarViewController
        wizardStarSettingsView.presentationId = self.presentationId
        self.navigationController?.pushViewController(wizardStarSettingsView, animated: true)
    }
    
    override func tableView(tableView: UITableView?, didSelectRowAtIndexPath indexPath: NSIndexPath?) {
        // Handle the rows that have subviews
//        if let ip = indexPath {
//            var cell = tableView?.cellForRowAtIndexPath(ip)
//            println("You selected row at #\(ip.row)!")
//        }
    }
    
    func savePresentationData() -> Presentation {
        // Reference to app delegate
        let context: NSManagedObjectContext = appDel.managedObjectContext!
        let en = NSEntityDescription.entityForName("Presentation", inManagedObjectContext: context)
        var p: Presentation!
        
        if self.presentationId != nil {
            p = try! context.existingObjectWithID(presentationId) as! Presentation
        } else {
            // Create new instance of Presentation
            p = Presentation(entity: en!, insertIntoManagedObjectContext: context)
            p.setDefaults(p)
            // Set global overrides
            p.displayCompanyLogo = appDel.getSettingAsBool(kDisplayCompanyLogo)
            p.displayCompanyName = appDel.getSettingAsBool(kDisplayCompanyName)
            p.displayAgentImage = appDel.getSettingAsBool(kDisplayAgentImage)
            p.displayAgentName = appDel.getSettingAsBool(kDisplayAgentName)
            // TODO p.agentName = appDel.getSettingAsString(kGlobalAgentName)
            // TODO p.displayColistingAgent = appDel.getSettingAsBool(kDisplayColistingAgent)
            p.narration = appDel.getSettingAsString(kGlobalNarration) as String
            p.music = appDel.getSettingAsString(kGlobalBgMusic) as String
            // TODO: Theme
            // TODO p.displayDisclaimer = appDel.getSettingAsBool(kDisplayDisclaimer)
            // TODO p.disclaimer = appDel.getSettingAsString(kGlobalDisclaimer)
        }
        
        // Assign our values
        var name = nameField.text
        if name == nil || name == "" {
            name = "New Presentation"
        }
        p.name = name!
        p.lastUpdated = NSDate()
        
        do {
            // Save
            try context.save()
        } catch _ {
        }
        
        if self.presentationId == nil {
            self.presentationId = p.objectID
        }
        
        return p
    }

    override func willMoveToParentViewController(parent: UIViewController?) {
        if self.nameField != nil {
            print("The user hit the back button")
            let p = savePresentationData()
            
            // Save to the server
            let sync = SyncService()
            sync.syncOne(p)
        }
    }
}

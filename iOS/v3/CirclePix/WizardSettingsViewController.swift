//
//  WizardSettingsViewController.swift
//  CirclePix
//
//  Created by Mark Burns on 10/14/14.
//  Copyright (c) 2014 Mark Burns. All rights reserved.
//

import UIKit
import CoreData

class WizardSettingsViewController: UITableViewController {

//    @IBOutlet weak var bgMusicNavCell: UITableViewCell!
    @IBOutlet weak var bgMusicNavCell: UITableViewCell!
    @IBOutlet weak var narrationNavCell: UITableViewCell!
    @IBOutlet weak var themeNavCell: UITableViewCell!
    @IBOutlet weak var photoNavCell: UITableViewCell!
    
    @IBOutlet weak var swDisplayCompanyLogo: UISwitch!
    @IBOutlet weak var swDisplayCompanyName: UISwitch!
    @IBOutlet weak var swDisplayAgentImage: UISwitch!
    @IBOutlet weak var swDisplayAgentName: UISwitch!
    
    var presentationId: NSManagedObjectID!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        //self.title = "Presentation Settings"
        
        // Set control values from object
        let appDel: AppDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
        let context: NSManagedObjectContext = appDel.managedObjectContext!
        let p: Presentation = try! context.existingObjectWithID(presentationId) as! Presentation
        swDisplayCompanyLogo.on = p.displayCompanyLogo as Bool
        swDisplayCompanyName.on = p.displayCompanyName as Bool
        swDisplayAgentImage.on = p.displayAgentImage as Bool
        swDisplayAgentName.on = p.displayAgentName as Bool
        
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
    
    override func tableView(tableView: UITableView?, didSelectRowAtIndexPath indexPath: NSIndexPath?) {
        // Handle the rows that have subviews
        if let ip = indexPath {
            let cell = tableView?.cellForRowAtIndexPath(ip)
            if cell == narrationNavCell {
                let wizardNarrationView = self.storyboard?.instantiateViewControllerWithIdentifier("wizardNarrationView") as! WizardNarrationViewController
                // Push presentationId
                wizardNarrationView.presentationId = self.presentationId
                self.navigationController?.pushViewController(wizardNarrationView, animated: true)
            }
            else if cell == photoNavCell {
                let wizardPhotoView = self.storyboard?.instantiateViewControllerWithIdentifier("wizardPhotoView") as! WizardPhotoViewController
                // Push presentationId
                wizardPhotoView.presentationId = self.presentationId
                self.navigationController?.pushViewController(wizardPhotoView, animated: true)
            }
            else if cell == bgMusicNavCell {
                let wizardMusicView = self.storyboard?.instantiateViewControllerWithIdentifier("wizardMusicView") as! WizardMusicViewController
                // Push presentationId
                wizardMusicView.presentationId = self.presentationId
                self.navigationController?.pushViewController(wizardMusicView, animated: true)
            }
            else if cell == themeNavCell {
                let wizardThemeView = self.storyboard?.instantiateViewControllerWithIdentifier("wizardThemeView") as! WizardThemeViewController
                // Push presentationId
                wizardThemeView.presentationId = self.presentationId
                self.navigationController?.pushViewController(wizardThemeView, animated: true)
            }
        }
    }
    
    func savePresentationData() {
        // Get the presentation object
        let appDel: AppDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
        let context: NSManagedObjectContext = appDel.managedObjectContext!
        let p: Presentation = try! context.existingObjectWithID(presentationId) as! Presentation
        
        // Assign our values
        p.displayCompanyLogo = swDisplayCompanyLogo.on
        p.displayCompanyName = swDisplayCompanyName.on
        p.displayAgentImage = swDisplayAgentImage.on
        p.displayAgentName = swDisplayAgentName.on
        
        do {
            // Save
            try context.save()
        } catch _ {
        }
    }
    
    override func willMoveToParentViewController(parent: UIViewController?) {
        if self.swDisplayCompanyLogo != nil {
            print("The user hit the back button")
            savePresentationData()
        }
    }

}

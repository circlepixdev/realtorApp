//
//  WizardLeadViewController.swift
//  CirclePix
//
//  Created by Mark Burns on 10/14/14.
//  Copyright (c) 2014 Mark Burns. All rights reserved.
//

import UIKit
import CoreData

class WizardLeadViewController: UITableViewController {

    @IBOutlet weak var propertySiteSwitch: UISwitch!
    @IBOutlet weak var leadbeeSwitch: UISwitch!
    @IBOutlet weak var facebookSwitch: UISwitch!
    @IBOutlet weak var mobileTourSwitch: UISwitch!
    @IBOutlet weak var openHouseSwitch: UISwitch!
    
    var presentationId: NSManagedObjectID!

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        self.title = "Leads"
        
        // Set control values from object
        let appDel: AppDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
        let context: NSManagedObjectContext = appDel.managedObjectContext!
        let p: Presentation = try! context.existingObjectWithID(presentationId) as! Presentation
        propertySiteSwitch.on = p.leadPropertySite as Bool
        leadbeeSwitch.on = p.leadLeadBee as Bool
        facebookSwitch.on = p.leadFacebook as Bool
        mobileTourSwitch.on = p.leadMobile as Bool
        openHouseSwitch.on = p.leadOpenHouseAnnce as Bool
        
        // Style the navbar
        let logo = UIImage(named: "logo_lt.png")
        let logoView = UIImageView(image: logo)
        self.navigationItem.titleView = logoView
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func savePresentationData() {
        // Get the presentation object
        let appDel: AppDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
        let context: NSManagedObjectContext = appDel.managedObjectContext!
        let p: Presentation = try! context.existingObjectWithID(presentationId) as! Presentation
        
        // Assign our values
        p.leadPropertySite = propertySiteSwitch.on
        p.leadLeadBee = leadbeeSwitch.on
        p.leadFacebook = facebookSwitch.on
        p.leadMobile = mobileTourSwitch.on
        p.leadOpenHouseAnnce = openHouseSwitch.on
        
        do {
            // Save
            try context.save()
        } catch _ {
        }
    }
    
    func validate() -> Bool {
        
        // Validate form items
        if !propertySiteSwitch.on && !leadbeeSwitch.on &&
            !facebookSwitch.on && !mobileTourSwitch.on &&
            !openHouseSwitch.on
        {
            let alert = UIAlertController(title: "Required Fields", message: "You must select at least 1 Leads item.", preferredStyle: UIAlertControllerStyle.Alert)
            alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.Cancel, handler: { action -> Void in
                return
            }))
            self.presentViewController(alert, animated: true, completion: nil)
            
            return false
        }
        
        return true
    }

    override func willMoveToParentViewController(parent: UIViewController?) {
        if self.propertySiteSwitch != nil {
            print("The user hit the back button")
            savePresentationData()
        }
    }

}

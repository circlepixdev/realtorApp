//
//  WizardListingTypeViewController.swift
//  CirclePix
//
//  Created by Mark Burns on 10/15/14.
//  Copyright (c) 2014 Mark Burns. All rights reserved.
//

import UIKit
import CoreData

class WizardListingTypeViewController: UITableViewController {

    @IBOutlet weak var listingResidential: UITableViewCell!
    @IBOutlet weak var listingCommercial: UITableViewCell!
    @IBOutlet weak var listingLand: UITableViewCell!
    
    var presentationId: NSManagedObjectID!

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        self.title = "Listing Type"
        
        // Pick setting based on current data
        let appDel: AppDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
        let context: NSManagedObjectContext = appDel.managedObjectContext!
        let p: Presentation = try! context.existingObjectWithID(presentationId) as! Presentation
        uncheckAll()
        checkCellByName(p.listingType)
        
        // Style the navbar
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
            print("You selected cell #\(ip.row)!")
            
            uncheckAll()
            let cell = tableView?.cellForRowAtIndexPath(ip)
            cell?.accessoryType = UITableViewCellAccessoryType.Checkmark
            
            // Save the selection
            let appDel: AppDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
            let context: NSManagedObjectContext = appDel.managedObjectContext!
            let p: Presentation = try! context.existingObjectWithID(presentationId) as! Presentation
            p.listingType = nameForCell(cell!)
            do {
                try context.save()
            } catch _ {
            }
        }
    }
    
    func checkCellByName(name: String) {
        if name == "Residential" {
            listingResidential.accessoryType = UITableViewCellAccessoryType.Checkmark
        }
        else if name == "Commercial" {
            listingCommercial.accessoryType = UITableViewCellAccessoryType.Checkmark
        }
        else if name == "Land" {
            listingLand.accessoryType = UITableViewCellAccessoryType.Checkmark
        }
    }
    
    func uncheckAll() {
        listingResidential.accessoryType = UITableViewCellAccessoryType.None
        listingCommercial.accessoryType = UITableViewCellAccessoryType.None
        listingLand.accessoryType = UITableViewCellAccessoryType.None
    }
    
    func nameForCell(cell: UITableViewCell) -> String {
        if cell == listingResidential {
            return "Residential"
        }
        else if cell == listingCommercial {
            return "Commercial"
        }
        else if cell == listingLand {
            return "Land"
        }
        return ""
    }

}

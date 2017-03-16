//
//  WizardThemeViewController.swift
//  CirclePix
//
//  Created by Mark Burns on 10/22/14.
//  Copyright (c) 2014 Mark Burns. All rights reserved.
//

import UIKit
import CoreData

class WizardThemeViewController: UITableViewController {

    @IBOutlet weak var themeCirclePixCell: UITableViewCell!
    @IBOutlet weak var themeRedCell: UITableViewCell!
    @IBOutlet weak var themeGreenCell: UITableViewCell!
    @IBOutlet weak var themeBlueCell: UITableViewCell!
    
    var presentationId: NSManagedObjectID!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        self.title = "Theme"
        
        // Pick setting based on current data
        let appDel: AppDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
        let context: NSManagedObjectContext = appDel.managedObjectContext!
        let p: Presentation = try! context.existingObjectWithID(presentationId) as! Presentation
        uncheckAll()
        checkCellByName(p.theme)
        
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
            p.theme = nameForCell(cell!)
            do {
                try context.save()
            } catch _ {
            }
        }
    }
    
    func checkCellByName(name: String) {
        if name == "CirclePix" {
            themeCirclePixCell.accessoryType = UITableViewCellAccessoryType.Checkmark
        }
        else if name == "Red" {
            themeRedCell.accessoryType = UITableViewCellAccessoryType.Checkmark
        }
        else if name == "Green" {
            themeGreenCell.accessoryType = UITableViewCellAccessoryType.Checkmark
        }
        else if name == "Blue" {
            themeBlueCell.accessoryType = UITableViewCellAccessoryType.Checkmark
        }
    }
    
    func uncheckAll() {
        themeCirclePixCell.accessoryType = UITableViewCellAccessoryType.None
        themeRedCell.accessoryType = UITableViewCellAccessoryType.None
        themeGreenCell.accessoryType = UITableViewCellAccessoryType.None
        themeBlueCell.accessoryType = UITableViewCellAccessoryType.None
    }
    
    func nameForCell(cell: UITableViewCell) -> String {
        if cell == themeCirclePixCell {
            return "CirclePix"
        }
        else if cell == themeRedCell {
            return "Red"
        }
        else if cell == themeGreenCell {
            return "Green"
        }
        else if cell == themeBlueCell {
            return "Blue"
        }
        return ""
    }
}

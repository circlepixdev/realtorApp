//
//  WizardStarViewController.swift
//  CirclePix
//
//  Created by Mark Burns on 11/29/14.
//  Copyright (c) 2014 Mark Burns. All rights reserved.
//

import UIKit
import CoreData

class WizardStarViewController: UITableViewController {

    @IBOutlet weak var listingTypeNavCell: UITableViewCell!
    @IBOutlet weak var photoNavCell: UITableViewCell!
    @IBOutlet weak var propertyNavCell: UITableViewCell!
    var presentationId: NSManagedObjectID!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Style the navbar
        self.navigationItem.title = " "
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
            if cell == listingTypeNavCell {
                let wizardListingTypeView = self.storyboard?.instantiateViewControllerWithIdentifier("wizardListingTypeView") as! WizardListingTypeViewController
                // Push presentationId
                wizardListingTypeView.presentationId = self.presentationId
                self.navigationController?.pushViewController(wizardListingTypeView, animated: true)
            }
            else if cell == photoNavCell {
                let wizardPhotoView = self.storyboard?.instantiateViewControllerWithIdentifier("wizardPhotoView") as! WizardPhotoViewController
                // Push presentationId
                wizardPhotoView.presentationId = self.presentationId
                self.navigationController?.pushViewController(wizardPhotoView, animated: true)
            }
            else if cell == propertyNavCell {
                let wizardPropertyView = self.storyboard?.instantiateViewControllerWithIdentifier("wizardPropertyView") as! WizardPropertyViewController
                // Push presentationId
                wizardPropertyView.presentationId = self.presentationId
                self.navigationController?.pushViewController(wizardPropertyView, animated: true)
            }
        }
    }
    
}

//
//  PresentationsViewController.swift
//  CirclePix
//
//  Created by Keuahn Lumanog on 9/8/16.
//  Copyright © 2016 Mark Burns. All rights reserved.
//


import UIKit

class PresentationsViewController: UITableViewController {
    
    @IBOutlet weak var navBar: UINavigationItem!
    @IBOutlet weak var menuButton: UIBarButtonItem!
     
    override func viewDidLoad() {
        super.viewDidLoad()
        
        let logo = UIImage(named: "logo_lt.png")
        let imageView = UIImageView(image:logo)
        self.navBar.titleView = imageView
        
        // Uncomment the following line to preserve selection between presentations
        // self.clearsSelectionOnViewWillAppear = false
        
        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        // self.navigationItem.rightBarButtonItem = self.editButtonItem()
        
        // burger side bar menu
        if self.revealViewController() != nil {
            menuButton.target = self.revealViewController()
            menuButton.action = "revealToggle:"
            // add swipe gesture
            self.view.addGestureRecognizer(self.revealViewController().panGestureRecognizer())
            
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
}


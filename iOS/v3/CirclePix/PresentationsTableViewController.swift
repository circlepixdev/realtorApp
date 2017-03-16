//
//  PresentationsTableViewController.swift
//  CirclePix
//
//  Created by Mark Burns on 10/8/14.
//  Copyright (c) 2014 CirclePix. All rights reserved.
//

import UIKit
import CoreData

class PresentationsTableViewController: UITableViewController, SwipeableCellDelegate, UIGestureRecognizerDelegate {
    
    var presentations: Array<AnyObject> = []
    var cellsCurrentlyEditing: NSMutableSet?
    
     var isPresented = true
  
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do some init
        self.cellsCurrentlyEditing = NSMutableSet()
        
        // Add the "Plus" button
        let plusImage = UIImage(named: "plus_present.png")
        let plusBtn = UIBarButtonItem(image: plusImage, style: UIBarButtonItemStyle.Plain, target: self, action: Selector("addButtonPressed:"))
        self.navigationItem.rightBarButtonItem = plusBtn

        // Style the navbar
        self.navigationItem.title = "Done"
        let logo = UIImage(named: "logo_lt.png")
        let logoView = UIImageView(image: logo)
        self.navigationItem.titleView = logoView
      
        if self.navigationController!.respondsToSelector("interactivePopGestureRecognizer") {
            self.navigationController!.interactivePopGestureRecognizer!.enabled = false
        }
    }
    
    override func viewDidAppear(animated: Bool) {
        // Reference to app delegate
        let appDel: AppDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
        let context: NSManagedObjectContext = appDel.managedObjectContext!
        let fetchReq = NSFetchRequest(entityName: "Presentation")
        
        presentations = try! context.executeFetchRequest(fetchReq)
        
        // If first run then add a default entity
        if appDel.isFirstRun() {
            appDel.setFirstRun()
            if presentations.count == 0 {
                let en = NSEntityDescription.entityForName("Presentation", inManagedObjectContext: context)
                let p: Presentation = Presentation(entity: en!, insertIntoManagedObjectContext: context)
                p.setDefaults(p)
                p.name = "Sample Presentation"
                do {
                    try context.save()
                } catch _ {
                }
                presentations = try! context.executeFetchRequest(fetchReq)
            }
        }
        
        tableView.reloadData()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Clear states before
        clearEditingCells()
    }

    func cellDidOpen(cell :SwipeableCell) {
        let ip: NSIndexPath = self.tableView.indexPathForRowAtPoint(cell.center)!
        self.cellsCurrentlyEditing!.addObject(ip)
    }
    
    func cellDidClose(cell :SwipeableCell) {
        let ip: NSIndexPath = self.tableView.indexPathForRowAtPoint(cell.center)!
        self.cellsCurrentlyEditing!.removeObject(ip)
    }

    override func numberOfSectionsInTableView(tableView: UITableView?) -> Int {
        // Return the number of sections.
        return 1
    }

    override func tableView(tableView: UITableView?, numberOfRowsInSection section: Int) -> Int {
        // Return the number of rows in the section.
        return presentations.count
    }
    
    override func tableView(tableView: UITableView?, cellForRowAtIndexPath indexPath: NSIndexPath?) -> UITableViewCell {

        // Configure the cell...
        let cellId: String = "presentationCell"
        let cell: SwipeableCell = tableView?.dequeueReusableCellWithIdentifier(cellId) as! SwipeableCell
        
        // If the indexPath is available then get the data
        if let ip = indexPath {
            let rowData: NSManagedObject = presentations[ip.row] as! NSManagedObject
            
            let detail = "" //String(format: "Narration: %@", (rowData.valueForKeyPath("narration") as String))
            cell.setRow(ip)
            cell.setItemTextValue(rowData.valueForKeyPath("name") as! String)
            cell.setDetailTextValue(detail)
            cell.cellDelegate = self
            
            if self.cellsCurrentlyEditing!.containsObject(ip) {
                cell.openCell()
            }
        }
        
        return cell
    }

    // Override to support conditional editing of the table view.
    override func tableView(tableView: UITableView?, canEditRowAtIndexPath indexPath: NSIndexPath?) -> Bool {
        // Return NO if you do not want the specified item to be editable OR to implement custom cell functionality.
        return false
    }

    func sendButtonActionForPath(ip: NSIndexPath) {
        // The "Send"/"Share" button has been pressed
        let p = presentations[ip.row] as! Presentation
        let subject = "Presentation"
        let msg = String(format: "http://www.circlepix.com/realtorPresentation/?id=%@" + "\n" + "You have been sent a presentation. Click the link above to view the presentation.", p.guid)
        let actItems = NSArray(object: msg)
        let activityView = UIActivityViewController(activityItems: actItems as [AnyObject], applicationActivities: nil)
        activityView.setValue(subject, forKey: "Subject")
        // iPad and iphone do this differently
        if UIDevice.currentDevice().userInterfaceIdiom == UIUserInterfaceIdiom.Pad {
            let popup: UIPopoverController = UIPopoverController(contentViewController: activityView)
            let rect = CGRectMake(self.view.frame.size.width/2, self.view.frame.size.height/4, 0, 0)
            popup.presentPopoverFromRect(rect, inView: self.view, permittedArrowDirections: UIPopoverArrowDirection.Any, animated: true)
        } else {
            self.navigationController?.presentViewController(activityView, animated: true, completion: nil)
        }
    }
    
    func editButtonActionForPath(ip: NSIndexPath) {
        // Clear any cells that are opened
        clearEditingCells()
        
        // Implement edit mode on long press
        let wizardMainView = self.storyboard?.instantiateViewControllerWithIdentifier("wizardMainAltView") as! WizardMainAltViewController
        let p = presentations[ip.row] as! Presentation
        wizardMainView.presentationId = p.objectID
        self.navigationController?.pushViewController(wizardMainView, animated: true)
    }
    
    func deleteButtonActionForPath(ip: NSIndexPath) {
        // println("Got DELETE button press in delegate")
        let p = self.presentations[ip.row] as! Presentation
        let presName = p.name
        
        let alert = UIAlertController(title: "Delete presentation", message: "\"\(presName)\" will be deleted", preferredStyle: UIAlertControllerStyle.Alert)
        alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.Default, handler: { action -> Void in
            // Delete the row from the data source
            let appDel: AppDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
            let context: NSManagedObjectContext = appDel.managedObjectContext!
            self.clearEditingCells()
//            let p = self.presentations[ip.row] as! Presentation
            let guid = p.guid
            context.deleteObject(self.presentations[ip.row] as! NSManagedObject)
            self.presentations.removeAtIndex(ip.row)
            self.tableView.deleteRowsAtIndexPaths([ip], withRowAnimation: UITableViewRowAnimation.Fade)
            
            var error: NSError? = nil
            do {
                try context.save()
            } catch let error1 as NSError {
                error = error1
                abort()
            }
            
            self.tableView.reloadData()
            
            // Sync the delete to the server
            let sync = SyncService()
            sync.deleteOne(guid)

        }))
        
        alert.addAction(UIAlertAction(title: "Cancel", style: UIAlertActionStyle.Cancel, handler: { action -> Void in
                   
        }))
            
        self.presentViewController(alert, animated: true, completion: nil)

        
        
             
        
        
   }
    
    func addButtonPressed(ender: AnyObject) {
        // Create new presentation
        let wizardMainView = self.storyboard?.instantiateViewControllerWithIdentifier("wizardMainAltView") as! WizardMainAltViewController
        self.navigationController?.pushViewController(wizardMainView, animated: true)
    }
    
    override func tableView(tableView: UITableView?, didSelectRowAtIndexPath indexPath: NSIndexPath?) {
        if let ip = indexPath {
            print("You selected row at #\(ip.row)!")
            clearEditingCells()
            
            // Nav to the "Play" presentation view
//            let playPresentationView = self.storyboard?.instantiateViewControllerWithIdentifier("playPresentationView") as! PlayPresentationViewController
//            // Push the presentationid
//            let b = presentations[ip.row] as! Presentation
//            playPresentationView.presentationId = b.objectID
//            self.navigationController?.pushViewController(playPresentationView, animated: true)
            
            
            
//            // Native screens - by Mark
//            var sb = UIStoryboard(name: "presentation", bundle: nil)
//            let presentationPageView = sb.instantiateViewControllerWithIdentifier("presentationPageView") as PresentationPageViewController
//             Push the presentationid
//            let b = presentations[ip.row] as Presentation
//            presentationPageView.presentationId = b.objectID
//            self.navigationController?.pushViewController(presentationPageView, animated: true)
            
            
            // Native screens - edited by Keu
            let sb = UIStoryboard(name: "presentation", bundle: nil)
//            let presentationPageView = sb.instantiateViewControllerWithIdentifier("presentationPageView") as! PresentationPageViewController
            let presentationPageView = sb.instantiateViewControllerWithIdentifier("presentationContainerView") as! PresentationPageViewController
            
            
//          Push the presentationid
            let b = presentations[ip.row] as! Presentation
            presentationPageView.presentationId = b.objectID
            self.navigationController?.pushViewController(presentationPageView, animated: true)
            
                        
        }
    }

    func clearEditingCells() {
        self.cellsCurrentlyEditing?.removeAllObjects()
    }
}

//
//  WizardPropertyViewController.swift
//  CirclePix
//
//  Created by Mark Burns on 10/27/14.
//  Copyright (c) 2014 Mark Burns. All rights reserved.
//

import UIKit
import CoreData
import AssetsLibrary

class WizardPropertyViewController: UITableViewController, UIGestureRecognizerDelegate, UIImagePickerControllerDelegate, UINavigationControllerDelegate {

    @IBOutlet weak var swDisplayPropAddress: UISwitch!
    @IBOutlet weak var propAddress: UITextField!
    @IBOutlet weak var swDisplayPropImage: UISwitch!
    @IBOutlet weak var propImageView: UIImageView!
    @IBOutlet weak var propImageCell: UITableViewCell!

    var presentationId: NSManagedObjectID!
    var propImageFileNew: String?
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Set control values from object
        let appDel: AppDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
        let context: NSManagedObjectContext = appDel.managedObjectContext!
        let p: Presentation = try! context.existingObjectWithID(presentationId) as! Presentation
        swDisplayPropAddress.on = p.displayPropAddress as Bool
        swDisplayPropImage.on = p.displayPropImage as Bool
        propAddress.text = p.propertyAddress
        if p.propertyImage != "" {
            let library = ALAssetsLibrary()
            let url = NSURL(string: p.propertyImage)
            library.assetForURL(url, resultBlock: {
                (asset: ALAsset!) in
                if asset != nil {
                    // Load the image
                    let assetRep: ALAssetRepresentation = asset.defaultRepresentation()
                    let iref = assetRep.fullResolutionImage().takeUnretainedValue()
                    let image = UIImage(CGImage: iref)
                    self.propImageView.image = image
                }
                },
                failureBlock: {
                    (error: NSError!) in
                    NSLog("Error loading image!")
            })
            
        }
        
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
            let cell = tableView?.cellForRowAtIndexPath(ip)
            if cell == propImageCell {
                
                if UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.SavedPhotosAlbum){
                    print("Button capture")
                    
                    let imagePicker = UIImagePickerController()
                    imagePicker.modalPresentationStyle = UIModalPresentationStyle.CurrentContext
                    imagePicker.delegate = self
                    imagePicker.sourceType = UIImagePickerControllerSourceType.SavedPhotosAlbum;
                    imagePicker.allowsEditing = false
                    
                    self.presentViewController(imagePicker, animated: true, completion: nil)
                }

            }
        }
    }
  
    func imagePickerController(picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String : AnyObject]) {
        self.dismissViewControllerAnimated(true, completion: { () -> Void in
        })
        
        print("Image was picked")
        let path = info[UIImagePickerControllerReferenceURL] as! NSURL
        let gotImage = info[UIImagePickerControllerOriginalImage] as! UIImage
        propImageView.image = gotImage
//        println(path)
        // TODO: figure out correct way to get reusable image path
//        var absStr = path.absoluteString
//        println(absStr)
//        var relStr = path.relativeString
//        println(relStr)
//        var relPath = path.relativePath
//        println(relPath)
        propImageFileNew = path.absoluteString
    }

    func savePresentationData() {
        // Get the presentation object
        let appDel: AppDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
        let context: NSManagedObjectContext = appDel.managedObjectContext!
        let p: Presentation = try! context.existingObjectWithID(presentationId) as! Presentation
        
        // Assign our values
        p.displayPropAddress = swDisplayPropAddress.on
        p.displayPropImage = swDisplayPropImage.on
        if swDisplayPropAddress.on {
            if propImageFileNew != nil && propImageFileNew != p.propertyImage {
                p.propertyImage = propImageFileNew!
            }
        }
        else {
            p.propertyImage = ""
        }
        if swDisplayPropAddress.on {
            if p.propertyAddress != propAddress.text {
                p.propertyAddress = propAddress.text!
            }
        }
        else {
            p.propertyAddress = ""
        }
        
        do {
            // Save
            try context.save()
        } catch _ {
        }
    }
    
    override func willMoveToParentViewController(parent: UIViewController?) {
        if self.swDisplayPropAddress != nil {
            print("The user hit the back button")
            savePresentationData()
        }
    }

}

//
//  ListingsTableViewController.swift
//  CirclePix
//
//  Created by Mark Burns on 10/10/14.
//  Copyright (c) 2014 CirclePix. All rights reserved.
//

import UIKit
import MobileCoreServices
import AssetsLibrary

class ListingsTableViewController: UITableViewController, UIImagePickerControllerDelegate, UINavigationControllerDelegate {
    
    let PICK_VIDEO:NSInteger = 1
    let RECORD_VIDEO:NSInteger = 2
    var listings: NSArray = []
    var isNewRecording: Bool = false
    var hasLoaded: Bool = false
    var pickedCellPath: NSIndexPath?
    let appDel: AppDelegate = UIApplication.sharedApplication().delegate as! AppDelegate

    override func viewDidLoad() {
        super.viewDidLoad()
        hasLoaded = true

        // Get the listings data
        let userData = appDel.userData
        self.listings = userData.listings
        
        // If no listing then tell the user
        if self.listings.count == 0 {
            let msg = appDel.isOfflineMode ? "There are no listings to display in offline mode." : "Presently, there are no active listings for your account."
            let alert = UIAlertController(title: "No Listings", message: msg, preferredStyle: UIAlertControllerStyle.Alert)
            alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.Cancel, handler: { action -> Void in
                print("UIAlertAction pressed")
            }))
            self.presentViewController(alert, animated: true, completion: nil)
        }
        
        // Style the navbar
        let logo = UIImage(named: "logo_lt.png")
        let logoView = UIImageView(image: logo)
        self.navigationItem.titleView = logoView
        
        //Disable back swipe gesture in UINavigationController - so Loading UIAlertView won't show when swiping from the very left of the screen
        if self.navigationController!.respondsToSelector("interactivePopGestureRecognizer") {
            self.navigationController!.interactivePopGestureRecognizer!.enabled = false
        }

    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - Table view data source

    override func numberOfSectionsInTableView(tableView: UITableView?) -> Int {
        // Return the number of sections.
        return 1
    }

    override func tableView(tableView: UITableView?, numberOfRowsInSection section: Int) -> Int {
        // Return the number of rows in the section.
        return listings.count
    }
    
    override func tableView(tableView: UITableView?, cellForRowAtIndexPath indexPath: NSIndexPath?) -> UITableViewCell {

        // Configure the cell...
        let cellId: String = "listingsCell"
        let cell: UITableViewCell = (tableView?.dequeueReusableCellWithIdentifier(cellId))!
        // If the indexPath is available then get the data
        if let ip = indexPath {
            let rowData: Listing = listings[ip.row] as! Listing
            cell.textLabel!.text = (rowData.address1 as String)
            cell.detailTextLabel?.text = (rowData.address2 as String)
            
            if (rowData.listingImage != nil) {
                cell.imageView!.image = rowData.listingImage
            }
            else if rowData.listingURL != "" && reachabilityStatus != NOT_REACHABLE {
                // Get the image
                let imgURL: NSURL = NSURL(string: rowData.listingURL as String)!
                
                // Download an NSData representation of the image at the URL
                let request: NSURLRequest = NSURLRequest(URL: imgURL)
                NSURLConnection.sendAsynchronousRequest(request, queue: NSOperationQueue.mainQueue(), completionHandler: {(response: NSURLResponse?,data: NSData?,error: NSError?) -> Void in
                    if error == nil {
                        let theImage = UIImage(data: data!)
                        cell.imageView!.image = theImage
                        rowData.listingImage = theImage
                    }
                    else {
                        print("Error: \(error!.localizedDescription)")
                    }
                })
            }
        }
        
        return cell
    }
    
    override func tableView(tableView: UITableView?, didSelectRowAtIndexPath indexPath: NSIndexPath?) {
        if let ip = indexPath {
            print("You selected cell #\(ip.row)!")
            pickedCellPath = ip
            
            if reachabilityStatus == NOT_REACHABLE {
                // Tell the user that they cannot upload video
                let alert = UIAlertController(title: "Internet Unavailable", message: "You must be connected to the internet to upload a video.  Please check your connection and try again.", preferredStyle: UIAlertControllerStyle.Alert)
                alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.Cancel, handler: { action -> Void in
                    return
                }))
                self.presentViewController(alert, animated: true, completion: nil)
            }
            else if reachabilityStatus == REACHABLE_WITH_4G {
                // Tell the user that uploads may be slow
                let alert = UIAlertController(title: "Wifi Unavailable", message: "It is recommended that you use a wifi connection for uploading videos.  Do you wish to continue anyway?", preferredStyle: UIAlertControllerStyle.Alert)
                alert.addAction(UIAlertAction(title: "Cancel", style: UIAlertActionStyle.Cancel, handler: { action -> Void in
                    return
                }))
                alert.addAction(UIAlertAction(title: "Continue", style: UIAlertActionStyle.Default, handler: { action -> Void in
                    self.proceedToPickType()
                }))
                self.presentViewController(alert, animated: true, completion: nil)
            }
            
            proceedToPickType()
        }
    }
    
    func proceedToPickType() {
        
        let alert = UIAlertController(title: "Listing Video", message: "Would you like to record a new video or select from existing videos?", preferredStyle: UIAlertControllerStyle.Alert)
        alert.addAction(UIAlertAction(title: "Cancel", style: UIAlertActionStyle.Cancel, handler: { action -> Void in
            // Do nothing on cancel
        }))
        alert.addAction(UIAlertAction(title: "Record", style: UIAlertActionStyle.Default, handler: { action -> Void in
            // Launch "Record"
            print("Record a video was selected")
            self.setupToPickVideo(self.RECORD_VIDEO)
        }))
        alert.addAction(UIAlertAction(title: "Existing", style: UIAlertActionStyle.Default, handler: { action -> Void in
            // Select a video
            print("Select a video was pressed")
            self.setupToPickVideo(self.PICK_VIDEO)
        }))
        
        self.presentViewController(alert, animated: true, completion: nil)

    }
    
    func setupToPickVideo(index: NSInteger) {
        let imagePicker:UIImagePickerController = UIImagePickerController()
        imagePicker.delegate = self
        imagePicker.allowsEditing = true
        imagePicker.videoQuality = UIImagePickerControllerQualityType.TypeHigh
        
        if index == PICK_VIDEO {
            imagePicker.sourceType = UIImagePickerControllerSourceType.PhotoLibrary
            imagePicker.mediaTypes = [kUTTypeMovie as String]
            isNewRecording = false
        }
        else if index == RECORD_VIDEO {
            imagePicker.sourceType = UIImagePickerControllerSourceType.Camera
            imagePicker.mediaTypes = [kUTTypeMovie as String]
            isNewRecording = true
        }
        
        if UIImagePickerController.isSourceTypeAvailable(imagePicker.sourceType){
            self.presentViewController(imagePicker, animated: true, completion: nil)
        }
    }
    
    func imagePickerController(picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String : AnyObject]) {
        print("I've got a video");
        print(info, terminator: "")
        
        // Dismiss the picker
        picker.dismissViewControllerAnimated(true, completion: nil)

        if self.isNewRecording {
            let videoURL: NSURL = info[UIImagePickerControllerMediaURL] as! NSURL
            if UIVideoAtPathIsCompatibleWithSavedPhotosAlbum(videoURL.path!) {
                let library = ALAssetsLibrary()
                library.writeVideoAtPathToSavedPhotosAlbum(videoURL, completionBlock: {(url: NSURL!, error: NSError!) in
                    
                    if let theError = error {
                        NSLog("Error saving video")
                    } else {
                        NSLog("Saved the video")
                        self.navToUploader(url)
                    }
                })
            }
        } else {
            navToUploader(info[UIImagePickerControllerReferenceURL] as! NSURL)
        }
    }
    
    func imagePickerControllerDidCancel(picker: UIImagePickerController) {
        // User cancelled so dismiss the picker
        print("Cancelled picking video")
        self.dismissViewControllerAnimated(true, completion: nil)
    }
    
    func navToUploader(videoURL: NSURL) {
        
        // Nav to the uploader view to perform the upload
        let videoUploadView = self.storyboard?.instantiateViewControllerWithIdentifier("videoUploadView") as! VideoUploadViewController
        // Push data
        let listing: Listing = listings[pickedCellPath!.row] as! Listing
        videoUploadView.sourceType = kListingSourceView
        videoUploadView.videoURL = videoURL
        videoUploadView.listingId = listing.listingId
        videoUploadView.listingCode = listing.listingCode
        self.navigationController?.pushViewController(videoUploadView, animated: true)
    }

    override func willMoveToParentViewController(parent: UIViewController?) {
        if hasLoaded {
            LoginHelper().refreshListingData(appDel)
        }
    }

}

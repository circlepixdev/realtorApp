//
//  AgentViewController.swift
//  CirclePix
//
//  Created by Mark Burns on 11/1/14.
//  Copyright (c) 2014 Mark Burns. All rights reserved.
//

import UIKit
import MobileCoreServices
import AssetsLibrary

class AgentViewController: UIViewController, UIGestureRecognizerDelegate, UIImagePickerControllerDelegate, UINavigationControllerDelegate {

    @IBOutlet weak var agentImage: UIImageView!
    @IBOutlet weak var agentName: UILabel!
    @IBOutlet weak var agencyName: UILabel!
    @IBOutlet weak var agentEmail: UILabel!
    var userData: UserData?
    let PICK_VIDEO:NSInteger = 1
    let RECORD_VIDEO:NSInteger = 2
    var isNewRecording: Bool = false
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        let appDel: AppDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
        userData = appDel.userData
        if userData != nil {
            let name = userData?.realtor?.realtorName
            let agency = userData?.realtor?.agency
            let imageUrl = userData?.realtor?.agentImageURL
            let email = userData?.realtor?.email
            if name != nil {
                agentName.text = name as? String
            }
            if agencyName != nil {
                agencyName.text = agency as? String
            }
            if email != nil {
                agentEmail.text = email as? String
            }
            if imageUrl != nil && imageUrl != "" {
               
                self.agentImage.hidden = false

                // See if the image is in the cache
                let help = FileHelper()
                if help.cachedFileExists(imageUrl!, subfolder: kAgentDir) {
                    let data = NSData(contentsOfFile: help.cachedFilePathFromUrl(imageUrl as! String, subfolder: kAgentDir, createFolders: false) as String)
                    self.agentImage.image = UIImage(data: data!)
                }
                else if reachabilityStatus == NOT_REACHABLE {
                    // Download an NSData representation of the image at the URL
                    let imgURL: NSURL = NSURL(string: imageUrl as! String)!
                    let request: NSURLRequest = NSURLRequest(URL: imgURL)
                    NSURLConnection.sendAsynchronousRequest(request, queue: NSOperationQueue.mainQueue(), completionHandler: {(response: NSURLResponse?,data: NSData?,error: NSError?) -> Void in
                        if error == nil {
                            self.agentImage.image = UIImage(data: data!)
                        }
                        else {
                            print("Error: \(error!.localizedDescription)")
                        }
                    })
                }
            }else{
                print("no agent image: \(imageUrl)")
                //012616
                //change constant to 0 to move labels to the left if agentimage will be hidden
                if #available(iOS 9.0, *) {
                   let widthConstraint = agentImage.widthAnchor.constraintEqualToAnchor(nil, constant: 0)
                    NSLayoutConstraint.activateConstraints([widthConstraint])
                } else {
                    // Fallback on earlier versions
                    let widthConstraint = NSLayoutConstraint(item: agentImage, attribute: NSLayoutAttribute.Width, relatedBy: NSLayoutRelation.Equal, toItem: nil, attribute: NSLayoutAttribute.NotAnAttribute, multiplier: 1, constant: 0)
                    view.addConstraint(widthConstraint)
                }
                
                self.agentImage.hidden = true

            }
            
            // Register to get clicks in agentInfoView
            let tgr: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: Selector("handleTap:"))
            agentName.superview?.addGestureRecognizer(tgr)
            tgr.delegate = self
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
    
    func gestureRecognizer(_: UIGestureRecognizer,
        shouldRecognizeSimultaneouslyWithGestureRecognizer:UIGestureRecognizer) -> Bool {
            return true
    }
    
    func gestureRecognizer(gestureRecognizer: UIGestureRecognizer, shouldReceiveTouch touch: UITouch) -> Bool {
            return true
    }
    
    func handleTap(recognizer: UITapGestureRecognizer) {
        print("Got a tap")
        
        // Pick the video
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
        
        self.proceedToPickType()
    }
    
    func proceedToPickType() {
        let alert = UIAlertController(title: "Agent Video", message: "Would you like to record a new video or select from existing videos?", preferredStyle: UIAlertControllerStyle.Alert)
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
//            imagePicker.mediaTypes = [kUTTypeMovie]
            
            imagePicker.mediaTypes =  [kUTTypeMovie as String]


            isNewRecording = false
        }
        else if index == RECORD_VIDEO {
            imagePicker.sourceType = UIImagePickerControllerSourceType.Camera
//            imagePicker.mediaTypes = [kUTTypeMovie]
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
        videoUploadView.sourceType = kAgentSourceView
        videoUploadView.realtorId = self.userData?.realtor!.realtorId
        videoUploadView.realtorCode = self.userData?.realtor?.realtorCode
        videoUploadView.videoURL = videoURL
        self.navigationController?.pushViewController(videoUploadView, animated: true)
    }

}

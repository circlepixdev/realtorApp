//
//  VideoUploadViewController.swift
//  CirclePix
//
//  Created by Mark Burns on 11/7/14.
//  Copyright (c) 2014 Mark Burns. All rights reserved.
//

import UIKit
import AssetsLibrary

let kAgentSourceView: Int = 1
let kListingSourceView: Int = 2

class VideoUploadViewController: UIViewController, VideoUploaderDelegate, UITextFieldDelegate, UITextViewDelegate {

    @IBOutlet weak var videoTitle: UITextField!
    @IBOutlet weak var videoDescription: UITextView!
    @IBOutlet weak var fileSizeLabel: UILabel!
    @IBOutlet weak var progressBar: UIProgressView!
    @IBOutlet weak var uploadBtn: UIButton!
    
    var videoURL: NSURL?
    var sourceType: Int = 0
    var realtorId: NSString?
    var realtorCode: NSString?
    var listingId: NSString?
    var listingCode: NSString?
    var videoData: NSData?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.videoDescription.text = ""
        self.videoTitle.delegate = self
        self.videoDescription.delegate = self
        
        // Set progressbar to zero
        progressBar.setProgress(0.0, animated: false)
        
        // Load the video size
        let fileBytes = getFileSize(videoURL!)
        let myBytes: NSDecimalNumber = NSDecimalNumber(string: NSString(format: "%ld", fileBytes) as String)
        let kBytes: NSDecimalNumber = NSDecimalNumber(string: "1024")
        let kiloBytes: NSDecimalNumber = myBytes.decimalNumberByDividingBy(kBytes)
        let mb: NSDecimalNumber = kiloBytes.decimalNumberByDividingBy(kBytes)
        let decimalHandler: NSDecimalNumberHandler = NSDecimalNumberHandler(roundingMode: NSRoundingMode.RoundPlain, scale: 1, raiseOnExactness: false, raiseOnOverflow: false, raiseOnUnderflow: false, raiseOnDivideByZero: false)
        let rounded: NSDecimalNumber = mb.decimalNumberByRoundingAccordingToBehavior(decimalHandler)
        self.fileSizeLabel.text = NSString(format: "File Size: %@ MB", rounded) as String

    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func uploadVideoPressed(sender: AnyObject) {
        
        // Validate the form
        if videoTitle.text!.isEmpty || videoDescription.text!.isEmpty {
            let alert = UIAlertController(title: "Required Fields", message: "You must enter a title and description.", preferredStyle: UIAlertControllerStyle.Alert)
            alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.Cancel, handler: { action -> Void in
                return
            }))
            self.presentViewController(alert, animated: true, completion: nil)
            
            return
        }
        
        // Disable the button
        uploadBtn.enabled = false
        
        // Get the bytes
        videoData = getFileData(videoURL!)
        let isRealtor = (sourceType == kAgentSourceView)
        let id = isRealtor ? realtorId : listingId
        let code = isRealtor ? realtorCode : listingCode
        let uploader = VideoUploader()
        uploader.delegate = self
        uploader.progressBar = self.progressBar
        uploader.uploadFile(videoData!, isRealtorVideo: isRealtor, objectId: id!, objectCode: code!, title: videoTitle.text!, description: videoDescription.text)
    }
    
    func uploadSuccess(didSucceed: Bool, message: NSString?) {
        // Let the user know
        var title:NSString?
        var msg:NSString?
        if didSucceed {
            title = "Success"
            msg = NSString(format: "The video \"%@\" has been successfully uploaded!", videoTitle.text!)
        } else {
            title = "Upload Failed"
            msg = NSString(format: "The upload failed with the following error code: %@.", message!)
        }
        let alert = UIAlertController(title: title! as String, message: msg! as String, preferredStyle: UIAlertControllerStyle.Alert)
        alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.Cancel, handler: { action -> Void in
            if didSucceed {
                // Navigate to the parent
                self.navigationController?.popViewControllerAnimated(true)
            }
            return
        }))
        self.presentViewController(alert, animated: true, completion: nil)
    }
    
    func getFileData(urlMedia: NSURL) -> NSData? {
        var iData: NSData?
        var bBusy: Bool = true
        
        // Setup code blocks
        let resultblock: ALAssetsLibraryAssetForURLResultBlock = {
            (asset: ALAsset!) in
            if asset != nil {
                let rep: ALAssetRepresentation = asset.defaultRepresentation()
                let size: Int = Int(rep.size())
                let rawData: NSMutableData = NSMutableData(length: size)!
                let buffer = rawData.mutableBytes
                let buffer8 = UnsafeMutablePointer<UInt8>(buffer)
                let start: Int64 = 0
                rep.getBytes(buffer8, fromOffset: start, length: size, error: nil)
                iData = NSData(bytes: buffer, length: size)
                bBusy = false
            }
        }
        
        let failureblock: ALAssetsLibraryAccessFailureBlock = { (myerror: NSError!) in
            NSLog("Oops, cant get media asset - %@", myerror.localizedDescription)
        }
        
        // Execute it
        let library = ALAssetsLibrary()
        library.assetForURL(self.videoURL!, resultBlock: resultblock, failureBlock: failureblock)

        while (bBusy) {
            NSRunLoop.currentRunLoop().runMode(NSDefaultRunLoopMode, beforeDate: NSDate.distantFuture() )
        }
        
        return iData;
    }
    
    func getFileSize(urlMedia: NSURL) -> NSInteger {
        var size: NSInteger = 0
        var bBusy: Bool = true
        
        let resultblock: ALAssetsLibraryAssetForURLResultBlock = {
            (asset: ALAsset!) in
            if asset != nil {
                let rep: ALAssetRepresentation = asset.defaultRepresentation()
                size = NSInteger(rep.size())
                bBusy = false
            } else {
                NSLog("It must not exist")
                return
            }
        }
        
        let failureblock: ALAssetsLibraryAccessFailureBlock = { (myerror: NSError!) in
            NSLog("Oops, cant get media asset - %@", myerror.localizedDescription)
        }
        
        // Execute it
        let library = ALAssetsLibrary()
        library.assetForURL(self.videoURL!, resultBlock: resultblock, failureBlock: failureblock)
        
        while (bBusy) {
            NSRunLoop.currentRunLoop().runMode(NSDefaultRunLoopMode, beforeDate: NSDate.distantFuture() )
        }
        
        return size
    }
    
    @IBAction func dismissKeyboard(sender: AnyObject) {
        if videoDescription.text == "" {
            videoDescription.becomeFirstResponder();
            return
        }
        
        // Dismiss the keyboard
        videoDescription.becomeFirstResponder();
        videoDescription.resignFirstResponder();
        
        // Submit the form
        uploadVideoPressed(sender)
    }
    
    func textFieldShouldReturn(textField: UITextField) -> Bool {
        if textField == self.videoTitle {
            textField.resignFirstResponder()
            videoDescription.becomeFirstResponder()
        }
        return true
    }
    
    func textView(textView: UITextView, shouldChangeTextInRange range: NSRange, replacementText text: String) -> Bool {
        if text == "\n" {
            textView.resignFirstResponder()
        }
        return true
        
//        if ([text isEqualToString:@"\n"])
//        [textView resignFirstResponder];
//        return YES;
    }
    
//    func textViewShouldEndEditing(textView: UITextView) -> Bool {
//        if textView.text != "" {
//            return true
//        }
//        return false
//    }

}

//
//  PresentationDataViewController.swift
//  CirclePix
//
//  Created by Mark Burns on 2/26/15.
//  Copyright (c) 2015 Mark Burns. All rights reserved.
//

import UIKit
import AVFoundation
import CoreData


class PresentationDataViewController: UIViewController {
    var presentationPage: PresentationPage?
    var p: Presentation?
   
    var image = UIImage(named: "logo_lt.png")
    @IBOutlet weak var agentImg: UIImageView!
    @IBOutlet weak var agentName: UILabel!
    @IBOutlet weak var agentEmail: UILabel!
    @IBOutlet weak var agentPhoneNum: UILabel!
    @IBOutlet weak var agencyName: UILabel!
    @IBOutlet weak var agentImgView: UIView!
    @IBOutlet weak var agentImgViewPresEnd: UIView!
    
    @IBOutlet weak var viewback: UIView!
    
    var userData: UserData?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        let appDel: AppDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
        let context: NSManagedObjectContext = appDel.managedObjectContext!
        let p: Presentation = try! context.existingObjectWithID(PresentationPageViewController.PresId.presId) as! Presentation
         
        userData = appDel.userData
        
        
        let storyboardId: String! = presentationPage?.storyboardId
        print(storyboardId)
        if((presentationPage?.pageId == 1) || (storyboardId == "presentationend")) {
            
            if userData != nil {
                let name = userData?.realtor?.realtorName
                let agency = userData?.realtor?.agency
                let imageUrl = userData?.realtor?.agentImageURL
                let email = userData?.realtor?.email
                let mobile = userData?.realtor?.mobile
                let phone = userData?.realtor?.phone
             
              
                if p.displayAgentName as Bool {
                    
                    if name != nil {
                        agentName.text = name as? String
                    }
                   
                    if email != nil {
                        agentEmail.text = email as? String
                    }
                    
                    if phone != "" {
                        agentPhoneNum.text = phone as? String
                    }else if(mobile != ""){
                        agentPhoneNum.text = mobile as? String
                    }

                }else{
                    agentName.text = ""
                    agentEmail.text = ""
                    agentPhoneNum.text = ""

                }
                
                if p.displayCompanyName as Bool {
                    if agency != nil {
                        agencyName.text = agency as? String
                    }
                }else{
                    agencyName.text = ""
                }
                
                if p.displayAgentImage as Bool {
                    if imageUrl != nil && imageUrl != "" {
                        if(storyboardId == "presentationend"){
                            self.agentImgViewPresEnd.hidden = false
                        }else{
                             self.agentImgView.hidden = false
                        }
                        
                       
                        // See if the image is in the cache
                        let help = FileHelper()
                        if help.cachedFileExists(imageUrl!, subfolder: kAgentDir) {
                            let data = NSData(contentsOfFile: help.cachedFilePathFromUrl(imageUrl as! String, subfolder: kAgentDir, createFolders: false) as String)
                            self.agentImg.image = UIImage(data: data!)
                        }
                        else if reachabilityStatus == NOT_REACHABLE {
                            // Download an NSData representation of the image at the URL
                            let imgURL: NSURL = NSURL(string: imageUrl as! String)!
                            let request: NSURLRequest = NSURLRequest(URL: imgURL)
                            NSURLConnection.sendAsynchronousRequest(request, queue: NSOperationQueue.mainQueue(), completionHandler: {(response: NSURLResponse?,data: NSData?,error: NSError?) -> Void in
                                if error == nil {
                                    self.agentImg.image = UIImage(data: data!)
                                }
                                else {
                                    print("Error: \(error!.localizedDescription)")
                                }
                            })
                        }
                    }else{
                        //agent has no image
                        //set multiplier to 0(set in storyboard as 0.35 height of the screen) so all agent info layout will move to center
                        if(storyboardId == "presentationend"){
                            let heightConstraint = NSLayoutConstraint(item: agentImgViewPresEnd, attribute: NSLayoutAttribute.Height, relatedBy: NSLayoutRelation.Equal, toItem: nil, attribute: NSLayoutAttribute.NotAnAttribute, multiplier: 0, constant: 0)
                            view.addConstraint(heightConstraint)
                            self.agentImgViewPresEnd.hidden = true
                        }else{
                            let heightConstraint = NSLayoutConstraint(item: agentImgView, attribute: NSLayoutAttribute.Height, relatedBy: NSLayoutRelation.Equal, toItem: nil, attribute: NSLayoutAttribute.NotAnAttribute, multiplier: 0, constant: 0)
                            view.addConstraint(heightConstraint)
                            self.agentImgView.hidden = true
                        }
                       
                        
                    }
                }else{
                    //just hide the agentImageview so agent info will be centered
                    if(storyboardId == "presentationend"){
                        let heightConstraint = NSLayoutConstraint(item: agentImgViewPresEnd, attribute: NSLayoutAttribute.Height, relatedBy: NSLayoutRelation.Equal, toItem: nil, attribute: NSLayoutAttribute.NotAnAttribute, multiplier: 0, constant: 0)
                        view.addConstraint(heightConstraint)
                        self.agentImgViewPresEnd.hidden = true
                        
                    }else{
                        let heightConstraint = NSLayoutConstraint(item: agentImgView, attribute: NSLayoutAttribute.Height, relatedBy: NSLayoutRelation.Equal, toItem: nil, attribute: NSLayoutAttribute.NotAnAttribute, multiplier: 0, constant: 0)
                        view.addConstraint(heightConstraint)
                        self.agentImgView.hidden = true
                    }
                   
                }
            }
        }
        
        
//         if(storyboardId == "starmarketing") {
//            UIGraphicsBeginImageContext(viewback.frame.size) //   self.view.frame.size)
//            UIImage(named: "view_bg.png")?.drawInRect(viewback.bounds)
//            
//            let image: UIImage = UIGraphicsGetImageFromCurrentImageContext()
//            
//            UIGraphicsEndImageContext()
//            
//            viewback.backgroundColor = UIColor(patternImage: image)
//        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
 
    
//    class TextField: UITextField {
//        
//        let padding = UIEdgeInsets(top: 0, left: 5, bottom: 0, right: 5);
//        
//        override func textRectForBounds(bounds: CGRect) -> CGRect {
//            return self.newBounds(bounds)
//        }
//        
//        override func placeholderRectForBounds(bounds: CGRect) -> CGRect {
//            return self.newBounds(bounds)
//        }
//        
//        override func editingRectForBounds(bounds: CGRect) -> CGRect {
//            return self.newBounds(bounds)
//        }
//        
//        private func newBounds(bounds: CGRect) -> CGRect {
//            
//            var newBounds = bounds
//            newBounds.origin.x += padding.left
//            newBounds.origin.y += padding.top
//            newBounds.size.height -= padding.top + padding.bottom
//            newBounds.size.width -= padding.left + padding.right
//            return newBounds
//        }
//    }
}

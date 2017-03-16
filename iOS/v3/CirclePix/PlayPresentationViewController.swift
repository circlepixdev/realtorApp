//
//  PlayPresentationViewController.swift
//  CirclePix
//
//  Created by Mark Burns on 10/16/14.
//  Copyright (c) 2014 Mark Burns. All rights reserved.
//

import UIKit
import WebKit
import CoreData
import Foundation
import AVFoundation

class PlayPresentationViewController: UIViewController, UIWebViewDelegate, UIGestureRecognizerDelegate, AVAudioPlayerDelegate {

    @IBOutlet var containerView: UIView!
    @IBOutlet weak var myWebView: UIWebView!
    @IBOutlet weak var myToolbar: UIToolbar!
    @IBOutlet weak var pauseBtn: UIBarButtonItem!
    @IBOutlet weak var playBtn: UIBarButtonItem!
    
    let kMaxIdleTimeSeconds = 5.0
    let MIN_SWIPE_DISTANCE: CGFloat = 20.0
    var p: Presentation?
    var userData: UserData?
    var presentationId: NSManagedObjectID!
    var presentationData: String!
    var idleTimer: NSTimer?
    var player: AVAudioPlayer?
    var autoPlay: Bool = true
    var firstLoad: Bool = true
    var isPaused: Bool = true
    var isMuted: Bool = false
    //var isManualMode: Bool = false
    var originalVolume: Float?
    var pageSet: PageSet?
    var currentPage: PresentationPage?
    var startPoint: CGPoint?
    var endPoint: CGPoint?
    var startedPan: Bool = false
    
    override func loadView() {
        super.loadView()
        
        originalVolume = 0.0
        
        // Do any additional setup after loading the view
        if presentationId != nil {
//            let appDel: AppDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
//            let context: NSManagedObjectContext = appDel.managedObjectContext!
//           var errorp: NSError?
//            p = context.existingObjectWithID(presentationId!) as? Presentation
        
            
            
            let appDel: AppDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
            let context: NSManagedObjectContext = appDel.managedObjectContext!
            let errorp: NSError?
            
            do{
               
                p = try context.existingObjectWithID(presentationId!) as? Presentation

            }catch{
                print(error)
            }

            presentationData = p!.jsondata
            NSLog("GUID: %@", p!.guid)
            //println(presentationData)
            autoPlay = (p!.narration != "None")
            userData = appDel.userData
        }
        
        // Setup pause/play btn
        togglePauseBtn()
        
        // Get the pageSet
        pageSet = PageSet(presentation: p!)
        currentPage = pageSet?.validSet[0] as? PresentationPage
        
        // Launch the start page
        let path = NSBundle.mainBundle().pathForResource("startpage", ofType: "html", inDirectory: "html")
        let requestURL = NSURL(string: path!)
        let request = NSURLRequest(URL: requestURL!)
        myWebView.delegate = self
        myWebView.scrollView.bounces = false
        myWebView.dataDetectorTypes = UIDataDetectorTypes.None
        myWebView.loadRequest(request)
        
    }
    
    // This is the only way that I could force landscape orientation
    override func viewDidAppear(animated: Bool) {
        UIDevice.currentDevice().setValue(UIInterfaceOrientation.LandscapeRight.rawValue, forKey: "orientation")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        print("Entered viewDidLoad")

        // Hide the navbar and toolbar
        hideShowBars(true)

        // Register gesture recognizers
        let tgr: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: Selector("handleTap:"))
        self.view.addGestureRecognizer(tgr)
        tgr.delegate = self
        let pangr: UIPanGestureRecognizer = UIPanGestureRecognizer(target: self, action: Selector("handlePan:"))
        self.view.addGestureRecognizer(pangr)
        pangr.delegate = self
        myWebView.delegate = self
        
        // Start a timer
        resetIdleTimer()
        
        // If configured for audio then start playing first file
        playAudioFile(currentPage!)
    }
    
    func setAgentInfo() {
        var agentName: NSString? = ""
        var email: NSString? = ""
        var mobile: NSString? = ""
        var agentPhone: NSString? = ""
        var picturePath: NSString? = ""
        var companyName: NSString? = ""
        var companyLogo: NSString? = ""
        
        if userData != nil {
            let help = FileHelper()
            
            if p!.displayAgentName as Bool {
                agentName = userData?.realtor?.realtorName
                email = (userData?.realtor?.email != nil) ? userData?.realtor?.email : ""
                mobile = userData?.realtor?.mobile
                agentPhone = (mobile != nil && mobile?.length > 0) ? mobile : userData?.realtor?.phone
            }
            
            if p!.displayAgentImage as Bool {
                if userData?.realtor?.agentImageURL != nil {
                    picturePath = userData?.realtor?.agentImageURL
                }
                if picturePath!.length > 0 && help.cachedFileExists(picturePath!, subfolder: kAgentDir) {
                    if let picUrl = help.cachedFileUrlFromUrl(picturePath!, subfolder: kAgentDir, createFolders: false) {
                        picturePath = picUrl.path
                    }
                }
            }
            if p!.displayCompanyName as Bool {
                companyName = userData?.realtor?.agency
            }
            if p!.displayCompanyLogo as Bool {
                if userData?.realtor?.agentLogo != nil {
                    companyLogo = userData?.realtor?.agentLogo
                }
                if companyLogo!.length > 0 && help.cachedFileExists(companyLogo!, subfolder: kAgentDir) {
                    if let logoUrl = help.cachedFileUrlFromUrl(companyLogo!, subfolder: kAgentDir, createFolders: false) {
                        companyLogo = logoUrl.path
                    }
                }
            }
        }
        
        // Call the javascript function to send data
        let scriptStr = String(format: "setAgentInfo('%@', '%@', '%@', '%@', '%@', '%@')", agentName!, agentPhone!, email!, picturePath!, companyName!, companyLogo!)
        var result = myWebView.stringByEvaluatingJavaScriptFromString(scriptStr)
    }
    
    func moveToNextPage() {
        if isPaused && player != nil {
            player?.stop()
            player = nil
        }
        
        let curIndex = pageSet?.validSet.indexOfObject(currentPage!)
        let nextIndex = curIndex! + 1
        if nextIndex < pageSet?.validSet.count {
            let nextPage: PresentationPage = pageSet?.validSet[nextIndex] as! PresentationPage
            
            // Set the current page
            currentPage = nextPage as PresentationPage
            
            // Call Javascript
            let scriptStr = String(format: "goToPage(%d)", nextPage.pageId)
            let result = myWebView.stringByEvaluatingJavaScriptFromString(scriptStr)
            if result == nil {
                print("Got a nil result from goToPage Javascript call")
            }
            else {
                print("goToPage Javascript result:")
                print(result)
            }
            
            webViewDidFinishLoad(myWebView)
            
            // Play the audio
            if !isPaused {
                self.playAudioFile(nextPage)
            }
        }
    }
    
    func moveToPreviousPage() {
        if isPaused && player != nil {
            player?.stop()
            player = nil
        }

        let curIndex = pageSet?.validSet.indexOfObject(currentPage!)
        let prevIndex = curIndex! - 1
        if prevIndex >= 0 {
            let prevPage: PresentationPage = pageSet?.validSet[prevIndex] as! PresentationPage
            
            // Set the current page
            currentPage = prevPage as PresentationPage
            
            // Call Javascript
            let scriptStr = String(format: "goToPage(%d)", prevPage.pageId)
            let result = myWebView.stringByEvaluatingJavaScriptFromString(scriptStr)
            if result == nil {
                print("Got a nil result from goToPage Javascript call")
            }
            else {
                print("goToPage Javascript result:")
                print(result)
            }
            
            webViewDidFinishLoad(myWebView)
            
            // Play the audio
            if !isPaused {
                self.playAudioFile(prevPage)
            }
        }
    }
    
    func playAudioFile(page: PresentationPage) {
        
        if p?.narration == "None" {
            return
        }

        if player != nil {
            player?.stop()
        }
        
        // Create the new player
        let path = NSBundle.mainBundle().pathForResource(page.audioFile, ofType: "mp3", inDirectory: "audio")
        let audioUrl = NSURL(string: path!)
        var error: NSError?
        do {
            player = try AVAudioPlayer(contentsOfURL: audioUrl!)
        } catch let error1 as NSError {
            error = error1
            player = nil
        }
        player?.delegate = self
        player?.prepareToPlay()
        if isMuted {
            player?.volume = 0.0
        } else {
            self.originalVolume = player?.volume
        }
        player?.play()
    }
    
    func audioPlayerDidFinishPlaying(player: AVAudioPlayer, successfully flag: Bool) {
        // See if we need to move to the next page
        moveToNextPage()
    }
    
    
    func hideShowBars(hide: Bool) {
        if (hide) {
            myToolbar.hidden = true
            self.navigationController?.setNavigationBarHidden(true, animated: true)
            self.navigationController?.setToolbarHidden(true, animated: true)
        }
        else {
            myToolbar.hidden = false
            self.navigationController?.setNavigationBarHidden(false, animated: true)
            self.navigationController?.setToolbarHidden(true, animated: true)
        }
    }
    
    func handlePan(pgr: UIPanGestureRecognizer) {
        if pgr.state == UIGestureRecognizerState.Began {
            self.startedPan = true
            startPoint = pgr.locationInView(myWebView)
        }
        else if pgr.state == UIGestureRecognizerState.Cancelled {
            self.startedPan = false
            startPoint = nil
            endPoint = nil
        }
        else if pgr.state == UIGestureRecognizerState.Ended && startedPan {
            endPoint = pgr.locationInView(myWebView)
            
            let xmag = abs(endPoint!.x - startPoint!.x)
            let ymag = abs(endPoint!.y - startPoint!.y)
            if xmag > MIN_SWIPE_DISTANCE {
                if startPoint!.x > endPoint!.x {
                    moveToNextPage()
                } else {
                    moveToPreviousPage()
                }
            }
            else if ymag > MIN_SWIPE_DISTANCE {
                if startPoint!.y > endPoint!.y {
                    moveToNextPage()
                } else {
                    moveToPreviousPage()
                }
            }
            else {
                // Do we need to handle a tap here?
            }
        }
    }
    
    func webViewDidFinishLoad(webView: UIWebView) {
        if firstLoad {
            firstLoad = false
            setAgentInfo()
            
            let scriptStr = "passJsonStr('{\"name\":\"John Wayne\"}')"
            //let scriptStr = "passJsonStr('" + presentationData + "')"
            let result = myWebView.stringByEvaluatingJavaScriptFromString(scriptStr)
            if result == nil {
                print("Got a nil result from passJsonStr Javascript call")
            }
            else {
                print("setJsonString Javascript result:")
                print(result)
            }
        }
    }
    
    func webViewDidStartLoad(webView: UIWebView) {
        print("Got webViewDidStartLoad")
    }

    override func viewDidLayoutSubviews() {
        // Make the webview fill the screen
        myWebView.frame = CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height)
        myWebView.sizeThatFits(CGSize(width: self.view.frame.size.width, height: self.view.frame.size.height))
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
        print("Entered PlayPresentationViewController.didReceiveMemoryWarning method")
    }
    
    /*
    * Handle toolbar buttons
    */
    @IBAction func prevBtnPressed(sender: AnyObject) {
        moveToPreviousPage()
    }
    
    func togglePauseBtn() {
        if isPaused {
            pauseBtn.enabled = true
            playBtn.enabled = false
            isPaused = false
        } else {
            pauseBtn.enabled = false
            playBtn.enabled = true
            isPaused = true
        }
    }
    
    @IBAction func pauseBtnPressed(sender: AnyObject) {
        if isPaused {
            // This is a play command
            if player != nil {
                player?.play()
            } else {
                moveToNextPage()
            }
        } else {
            // This is a pause command
            if player != nil {
                player?.pause()
            }
        }
        togglePauseBtn()
    }
    
    @IBAction func playBtnPressed(sender: AnyObject) {
        if isPaused {
            // This is a play command
            if player != nil {
                player?.play()
            } else {
                //moveToNextPage()
                playAudioFile(currentPage!)
            }
            togglePauseBtn()
        }
    }
    
    @IBAction func nextBtnPressed(sender: AnyObject) {
        moveToNextPage()
    }
    
    /*
    * Idle Timer code
    */
    override func nextResponder() -> UIResponder? {
        resetIdleTimer()
        return super.nextResponder()
    }

    func resetIdleTimer() {
        if idleTimer == nil {
            // Create a new timer
            idleTimer = NSTimer.scheduledTimerWithTimeInterval(kMaxIdleTimeSeconds, target: self, selector: Selector("idleTimerExceeded"), userInfo: nil, repeats: false)
        }
        else {
            // User action so reset the timer
            let timeSince = idleTimer?.fireDate.timeIntervalSinceNow
            if (fabs(timeSince!) < kMaxIdleTimeSeconds-1.0) {
                idleTimer?.fireDate = NSDate(timeIntervalSinceNow: kMaxIdleTimeSeconds)
            }
        }
    }
    
    func idleTimerExceeded() {
        idleTimer = nil
        
        // Hide the bars
        if let isHidden = self.navigationController?.navigationBarHidden {
            if !isHidden {
                hideShowBars(true)
            }
        }
        
        resetIdleTimer()
    }
    
    override func willMoveToParentViewController(parent: UIViewController?) {
        if player != nil {
            player?.stop()
        }
    }

}

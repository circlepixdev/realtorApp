//
//  PresentationPageViewController.swift
//  CirclePix
//
//  Created by Mark Burns on 2/26/15.
//  Copyright (c) 2015 Mark Burns. All rights reserved.
//

import UIKit
import WebKit
import CoreData
import Foundation
import AVFoundation

/*
This is the root view controller that gets loaded in order to display views as pages.
*/
class PresentationPageViewController: UIPageViewController, UIPageViewControllerDelegate, UIGestureRecognizerDelegate, AVAudioPlayerDelegate {
  
    //global variable for presentationID for PresentationDataViewController
    struct PresId {
        static var presId: NSManagedObjectID!
    }
  
    var pageViewController: UIPageViewController!

    @IBOutlet weak var containerView: UIView!
    
    let MIN_SWIPE_DISTANCE: CGFloat = 10.0 //10.0
    
    var userData: UserData?

    var appDel: AppDelegate!
    var presentationId: NSManagedObjectID!
    var p: Presentation?
    var pageSet: PageSet?
    var currentPage: PresentationPage?
    var isPaused: Bool = false
    var isMuted: Bool = false
    var originalVolume: Float?
    var idleTimer: NSTimer?
    var autoPlay: Bool = false
    var firstLoad: Bool = true
    
    var playAtPos: NSTimeInterval = 0.00
    var playBGMAtPos: NSTimeInterval = 0.00
    
    @IBOutlet weak var myToolbar: UIToolbar!
    @IBOutlet weak var ContentView: UIView!
    
    var audioPlayer: AVAudioPlayer!
    var bgmPlayer: AVAudioPlayer!
    
    var nextBtn : UIBarButtonItem!
    var prevBtn : UIBarButtonItem!
    var playBtn : UIBarButtonItem!
    var pauseBtn : UIBarButtonItem!
    
    var startPoint: CGPoint?
    var endPoint: CGPoint?
    var startedPan: Bool = false
    
   
    var prevOrientation: String?
    
    let nextImage = UIImage(named: "forward_arrow.png")!
    let prevImage = UIImage(named: "backwards_arrow.png")!
    let playImage = UIImage(named: "play_button.png")!
    let pauseImage = UIImage(named: "pause_button.png")!
    
    
    override func loadView() {
        super.loadView()
        //set classname
        ClassNameHelper.ClassName.nameOfClass = NSStringFromClass(self.dynamicType).componentsSeparatedByString(".").last!
        
        originalVolume = 0.0
        
        // Do any additional setup after loading the view
        if presentationId != nil {
            appDel = UIApplication.sharedApplication().delegate as! AppDelegate
            let context: NSManagedObjectContext = appDel.managedObjectContext!
            var errorp: NSError?
            
            do{
                 p = try context.existingObjectWithID(presentationId!) as? Presentation
                 PresId.presId = presentationId
               
            }catch{
                print(errorp)
            }
           
            autoPlay = p!.isAutoplay
            userData = appDel.userData
            
        }
        
        // Get the pageSet
        pageSet = PageSet(presentation: p!)
        currentPage = pageSet?.validSet[0] as? PresentationPage
        
        self.navigationController?.setNavigationBarHidden(false, animated: true)
        self.navigationController?.setToolbarHidden(true, animated: true)
        
      
        //Disable back swipe gesture in UINavigationController - so Loading UIAlertView won't show when swiping from the very left of the screen
        if self.navigationController!.respondsToSelector("interactivePopGestureRecognizer") {
            self.navigationController!.interactivePopGestureRecognizer!.enabled = false
        }

       
    }

    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        nextBtn = UIBarButtonItem(image: nextImage, style: UIBarButtonItemStyle.Plain, target: self, action: Selector("nextButtonPressed:"))
        prevBtn = UIBarButtonItem(image: prevImage, style: UIBarButtonItemStyle.Plain, target: self, action: Selector("prevButtonPressed:"))
        playBtn = UIBarButtonItem(image: playImage, style: UIBarButtonItemStyle.Plain, target: self, action: Selector("playButtonPressed:"))
        pauseBtn = UIBarButtonItem(image: pauseImage, style: UIBarButtonItemStyle.Plain, target: self, action: Selector("pauseButtonPressed:"))
      
        self.navigationItem.setRightBarButtonItems([nextBtn, playBtn, pauseBtn, prevBtn], animated: true)
      
            
        // Do any additional setup after loading the view, typically from a nib.
        // Configure the page view controller and add it as a child view controller.
        
        let optionsDict = [UIPageViewControllerOptionInterPageSpacingKey : 20]  ///space between pages
       
        self.pageViewController = UIPageViewController(transitionStyle: .Scroll, navigationOrientation: .Horizontal, options: optionsDict) //nil
        self.pageViewController!.delegate = self
        
        let startingViewController: PresentationDataViewController = self.modelController.viewControllerAtIndex(0, storyboard: self.storyboard!)!

        let viewControllers: NSArray = [startingViewController]
        self.pageViewController!.setViewControllers(viewControllers as? [UIViewController], direction: .Forward, animated: true, completion: {done in })
        self.view.backgroundColor = UIColor.whiteColor()
        self.pageViewController!.dataSource = self.modelController
        self.addChildViewController(self.pageViewController!)
        self.view.addSubview(self.pageViewController!.view)
        self.pageViewController!.didMoveToParentViewController(self)
        
        let pangr: UIPanGestureRecognizer = UIPanGestureRecognizer(target: self, action: Selector("handlePan:"))
        self.view.addGestureRecognizer(pangr)
        pangr.delegate = self

        //handle tap gesture to hide/show navigation bar
        let tgr: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: Selector("handleTap:"))
        self.view.addGestureRecognizer(tgr)
        tgr.delegate = self
        
        //added by KBL: force the layout in landscape orientation
        let value = UIInterfaceOrientation.LandscapeRight.rawValue
        UIDevice.currentDevice().setValue(value, forKey: "orientation")
        prevOrientation = "landscapeRight"
        
        playBtn.enabled = false
        playAudio(currentPage!)
        playBGMAudio()
        
    }
    
    func nextButtonPressed(sender:UIButton) {
        print("next pressed")
        
        playAtPos = 0.00
        moveToNextPage()
   
    }
    
    func prevButtonPressed (sender:UIButton) {
        print("prev pressed")
        
        playAtPos = 0.00
        moveToPreviousPage()
    }
    
    func playButtonPressed(sender:UIButton) {
        print("play pressed")
        print(currentPage?.audioFile)
        
        playBtn.enabled = false
        pauseBtn.enabled = true
        
        isPaused = false
        playAudio(currentPage!)
        playBGMAudio()
        
    }
    
    func pauseButtonPressed (sender:UIButton) {
        print("pause pressed")
        
        playBtn.enabled = true
        pauseBtn.enabled = false
        
        isPaused = true
        pauseAudio((currentPage?.audioFile)!)
        pauseBGMAudio((p?.music)!)
        
    }

    func playBGMAudio() {
        
        
        if bgmPlayer != nil {
            bgmPlayer?.stop()
            bgmPlayer = nil
        }
        
       
        var audioFile: String?
        var volume: Float?
         
        if((p?.music == "None") || (p?.music == nil) || (p?.music == "")){
            audioFile = "none"
            
            //set back to "None"
            if((p?.music == nil) || (p?.music == "")){
                  p?.music = "None"
           }
          
        }
        else if(p?.music == "Song 1"){
            audioFile = "modern_business"
            volume = 0.6
        }
        else if(p?.music == "Song 2"){
            audioFile = "under_current"
            volume = 0.6
        }
        else if(p?.music == "Song 3"){
            audioFile = "when_everything_goes_right"
            volume = 0.3
        }
        
        print("p.music:  \(p?.music)")
        print("music name: \(audioFile)")
        
        if(audioFile != ""){
            if(audioFile != "none"){
                // Create the new player
                let path = NSBundle.mainBundle().pathForResource(audioFile , ofType: "mp3", inDirectory: "audio")
                let audioUrl = NSURL(string: path!)
                
                var error: NSError?
                do {
                    
                    bgmPlayer = try AVAudioPlayer(contentsOfURL: audioUrl!)
                    
                } catch let error1 as NSError {
                    error = error1
                    
                    bgmPlayer = nil
                }
                
               // bgmPlayer.delegate = self
                bgmPlayer.currentTime = playBGMAtPos - 0.2
                bgmPlayer.volume = volume!
                bgmPlayer.numberOfLoops = -1 //put any negative integer to loop infinitely unless being stopped
                bgmPlayer.prepareToPlay()
                bgmPlayer.play()
                
            }
        }
    }

    func pauseBGMAudio(audioName: String){
      
        if(audioName != "None"){
            if (bgmPlayer.playing) {
                bgmPlayer.pause()
                
                playBGMAtPos = (bgmPlayer.currentTime)
                print(String(format:"%.2f", (playBGMAtPos)))
                
            } else {
                print("Nothing is playing")
            }

        }else{
            print("no BGM")
        }
        
   }
    

    
    func playAudio(page: PresentationPage) {
       
        // Create the new player
        let path = NSBundle.mainBundle().pathForResource(page.audioFile, ofType: "mp3", inDirectory: "audio")
        let audioUrl = NSURL(string: path!)
    
        var error: NSError?
        do {
            audioPlayer = try AVAudioPlayer(contentsOfURL: audioUrl!)
            
        } catch let error1 as NSError {
            error = error1
            audioPlayer = nil
        }
        audioPlayer.delegate = self
        audioPlayer.currentTime = playAtPos - 0.2 //deduct 0.2 to have a short preview
        audioPlayer.prepareToPlay()
        audioPlayer.play()
        
    }
    
    func pauseAudio(audioName: String){
        if (audioPlayer.playing) {
            audioPlayer.pause()
            playAtPos = (audioPlayer.currentTime)
            print(String(format:"%.2f", (playAtPos)))
         } else {
            print("Nothing is playing")
        }
    }
    
    
    //Reload the UIPageViewController view on orientation change
    override func willAnimateRotationToInterfaceOrientation(toInterfaceOrientation: UIInterfaceOrientation, duration: NSTimeInterval) {
        self.pageViewController.view!.setNeedsLayout()
    }
    
    //to hide status bar(battery stat etc)
    override func prefersStatusBarHidden() -> Bool {
        return true
    }
   
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func audioPlayerDidFinishPlaying(player: AVAudioPlayer, successfully flag: Bool) {
        // See if we need to move to the next page
        playAtPos = 0.00
        moveToNextPage()
    }
    
    func gestureRecognizer(_: UIGestureRecognizer,
        shouldRecognizeSimultaneouslyWithGestureRecognizer:UIGestureRecognizer) -> Bool {
            return true
    }
    
    func gestureRecognizer(gestureRecognizer: UIGestureRecognizer, shouldReceiveTouch touch: UITouch) -> Bool {
        return true
    }
        
    func handlePan(pgr: UIPanGestureRecognizer) {
  
        if pgr.state == UIGestureRecognizerState.Began {
            self.startedPan = true
            startPoint = pgr.locationInView(self.view)
            print("handle pan began")
        
        }
        else if pgr.state == UIGestureRecognizerState.Cancelled {
            self.startedPan = false
            startPoint = nil
            endPoint = nil
            print("handle cancelled")

        }
        else if pgr.state == UIGestureRecognizerState.Ended && startedPan {
            endPoint = pgr.locationInView(self.view)
            
            print("handle pan: ended")
            
            let xmag = abs(endPoint!.x - startPoint!.x)

            if xmag > MIN_SWIPE_DISTANCE {
                if startPoint!.x > endPoint!.x {
                    playAtPos = 0.00
                    moveToNextPage()
                    print("handle pan xmag: movetonextpage")
            
                } else {
                    playAtPos = 0.00
                    moveToPreviousPage()
                    print("handle pan xmag: movetoprevpage")
                }
              
            }
        }
    }

    
    func handleTap(recognizer: UITapGestureRecognizer) {
        NSLog("Got a tap")
        
        // Hide/show the navbar and toolbar
        if let isHidden = self.navigationController?.navigationBarHidden {
            hideShowBars(!isHidden)
        }
    }
    
    func hideShowBars(hide: Bool) {
        if (hide) {
            self.navigationController?.setNavigationBarHidden(true, animated: true)
            self.navigationController?.setToolbarHidden(true, animated: true)
        }
        else {
            self.navigationController?.setNavigationBarHidden(false, animated: true)
            self.navigationController?.setToolbarHidden(true, animated: true)
        }
    }

    
    func moveToNextPage() {
        
        if isPaused && audioPlayer != nil {
            audioPlayer?.stop()
            audioPlayer = nil
        }
        
        let curIndex = pageSet?.validSet.indexOfObject(currentPage!)
        let nextIndex = curIndex! + 1
        if nextIndex < pageSet?.validSet.count {
            let nextPage: PresentationPage = pageSet?.validSet[nextIndex] as! PresentationPage
            
            // Set the current page
            currentPage = nextPage as PresentationPage
            
            print("movetoNext page: \(nextIndex)")
        
            let startingViewControllers: PresentationDataViewController = self.modelController.viewControllerAtIndex(nextIndex, storyboard: self.storyboard!)!
            let viewControllers: NSArray = [startingViewControllers]
            self.pageViewController!.setViewControllers(viewControllers as? [UIViewController], direction: .Forward, animated: true, completion: nil)
            
            
            // Play the audio
            if !isPaused {
                playAudio(nextPage)
            }
        }
    }

    
    func moveToPreviousPage() {
        if isPaused && audioPlayer != nil {
            audioPlayer?.stop()
            audioPlayer = nil
        }
        
        let curIndex = pageSet?.validSet.indexOfObject(currentPage!)
        let prevIndex = curIndex! - 1
        if prevIndex >= 0 {
            let prevPage: PresentationPage = pageSet?.validSet[prevIndex] as! PresentationPage
            
            // Set the current page
            currentPage = prevPage as PresentationPage
            
            print("movetoPrevious page: \(prevIndex)")
            
            let startingViewControllers: PresentationDataViewController = self.modelController.viewControllerAtIndex(prevIndex, storyboard: self.storyboard!)!
            let viewControllers: NSArray = [startingViewControllers]
            self.pageViewController!.setViewControllers(viewControllers as? [UIViewController], direction: .Reverse, animated: true, completion: nil)
            
            // Play the audio
            if !isPaused {
                playAudio(prevPage)
            }
        }
    }

        
    var modelController: PresentationModelController {
        // Return the model controller object, creating it if necessary.
        // In more complex implementations, the model controller may be passed to the view controller.
        if _modelController == nil {
            _modelController = PresentationModelController()
            _modelController!.setPageSetValue(pageSet!.validSet)
        }
        return _modelController!
        
    }
    
    var _modelController: PresentationModelController? = nil
    
    
    override func willMoveToParentViewController(parent: UIViewController?) {
       //set classname back to nil
        ClassNameHelper.ClassName.nameOfClass = nil
        print("classname after pres: \(ClassNameHelper.ClassName.nameOfClass)")
     
    }
 }

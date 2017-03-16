//
//  WizardExposureViewController.swift
//  CirclePix
//
//  Created by Mark Burns on 10/14/14.
//  Copyright (c) 2014 Mark Burns. All rights reserved.
//

import UIKit
import CoreData
import AVFoundation

class WizardExposureViewController: UITableViewController, AVAudioPlayerDelegate {
    
    @IBOutlet weak var realPortalSwitch: UISwitch!
    @IBOutlet weak var personalWebSwitch: UISwitch!
    @IBOutlet weak var companyWebSwitch: UISwitch!
    @IBOutlet weak var facebookSwitch: UISwitch!
    @IBOutlet weak var youTubeSwitch: UISwitch!
    @IBOutlet weak var twitterSwitch: UISwitch!
    @IBOutlet weak var bloggerSwitch: UISwitch!
    @IBOutlet weak var craigslistSwitch: UISwitch!
    @IBOutlet weak var linkedinSwitch: UISwitch!
    @IBOutlet weak var pinterestSwitch: UISwitch!
    @IBOutlet weak var seoboostSwitch: UISwitch!

    
    @IBOutlet weak var btn1: UIButton!
    @IBOutlet weak var btn2: UIButton!
    @IBOutlet weak var btn3: UIButton!
    @IBOutlet weak var btn4: UIButton!
    @IBOutlet weak var btn5: UIButton!
    @IBOutlet weak var btn6: UIButton!
    @IBOutlet weak var btn7: UIButton!
    @IBOutlet weak var btn8: UIButton!
    @IBOutlet weak var btn9: UIButton!
    @IBOutlet weak var btn10: UIButton!
    @IBOutlet weak var btn11: UIButton!
    
    var presentationId: NSManagedObjectID!
    var p: Presentation?
    var audioPlayer: AVAudioPlayer!
    var currentPos: Int?
    var previousPos: Int = 11 //assign dummy value for previousPos just to compare later
    
    var F_audioFiles: [String] = ["f_exposure_portals", "f_exposure_personal", "f_exposure_personalcompany", "f_exposure_facebook", "f_exposure_youtube", "f_exposure_twitter", "f_exposure_blog", "f_exposure_craigslist", "f_exposure_linkedin", "f_exposure_pinterest", "f_exposure_seo"]
    
    var M_audioFiles: [String] = ["m_exposure_portals", "m_exposure_personal", "m_exposure_personalcompany", "m_exposure_facebook", "m_exposure_youtube", "m_exposure_twitter", "m_exposure_blog", "m_exposure_craigslist", "m_exposure_linkedin", "m_exposure_pinterest", "m_exposure_seo"]

    
    var CardCallers: [UIButton] = [UIButton]() // Empty UIButton array -  to programatically change buttons background images


    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        self.title = "Exposure"
        
        // Set control values from object
        let appDel: AppDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
        let context: NSManagedObjectContext = appDel.managedObjectContext!
        p = try! context.existingObjectWithID(presentationId) as! Presentation
        
        realPortalSwitch.on = p!.expRealPortals as Bool
        personalWebSwitch.on = p!.expPersonalSite as Bool
        companyWebSwitch.on = p!.expCompanySite as Bool
        facebookSwitch.on = p!.expFacebook as Bool
        youTubeSwitch.on = p!.expYouTube as Bool
        twitterSwitch.on = p!.expTwitter as Bool
        bloggerSwitch.on = p!.expBlogger as Bool
        craigslistSwitch.on = p!.expCraigslist as Bool
        linkedinSwitch.on = p!.expLinkedin as Bool
        pinterestSwitch.on = p!.expPinterest as Bool
        seoboostSwitch.on = p!.expSeoBoost as Bool
        
        // Style the navbar
        let logo = UIImage(named: "logo_lt.png")
        let logoView = UIImageView(image: logo)
        self.navigationItem.titleView = logoView
        
        self.CardCallers = [self.btn1, self.btn2, self.btn3, self.btn4, self.btn5, self.btn6, self.btn7, self.btn8, self.btn9, self.btn10, self.btn11] // Buttons have now loaded in the view
    

    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func savePresentationData() {
        // Get the presentation object
        let appDel: AppDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
        let context: NSManagedObjectContext = appDel.managedObjectContext!
        let p: Presentation = try! context.existingObjectWithID(presentationId) as! Presentation
        
        // Assign our values
        p.expRealPortals = realPortalSwitch.on
        p.expPersonalSite = personalWebSwitch.on
        p.expCompanySite = companyWebSwitch.on
        p.expFacebook = facebookSwitch.on
        p.expYouTube = youTubeSwitch.on
        p.expTwitter = twitterSwitch.on
        p.expBlogger = bloggerSwitch.on
        p.expCraigslist = craigslistSwitch.on
        p.expLinkedin = linkedinSwitch.on
        p.expPinterest = pinterestSwitch.on
        p.expSeoBoost = seoboostSwitch.on
        
        do {
            // Save
            try context.save()
        } catch _ {
        }
    }
    
    func validate() -> Bool {

        // Validate form items
        if !realPortalSwitch.on && !personalWebSwitch.on &&
            !companyWebSwitch.on && !facebookSwitch.on &&
            !youTubeSwitch.on && !twitterSwitch.on &&
            !bloggerSwitch.on && !craigslistSwitch.on &&
            !linkedinSwitch.on && !pinterestSwitch.on &&
            !seoboostSwitch.on
        {
            let alert = UIAlertController(title: "Required Fields", message: "You must select at least 1 Exposure item.", preferredStyle: UIAlertControllerStyle.Alert)
            alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.Cancel, handler: { action -> Void in
                return
            }))
            self.presentViewController(alert, animated: true, completion: nil)
            
            return false
        }
        
        return true
    }

    override func willMoveToParentViewController(parent: UIViewController?) {
        if self.realPortalSwitch != nil {
            print("The user hit the back button")
            savePresentationData()
        }
    }

    
    //Toggle buttons
    @IBAction func playStopBtn1(sender: AnyObject) {
        currentPos = 0
        
        if(currentPos == previousPos){
            stopAudio()
        }else{
            playAudio()
        }
    }
    
    
    
    @IBAction func playStopBtn2(sender: AnyObject) {
        currentPos = 1
        
        if(currentPos == previousPos){
            stopAudio()
        }else{
            playAudio()
        }
    }
    
    
    @IBAction func playStopBtn3(sender: AnyObject) {
        currentPos = 2
        
        if(currentPos == previousPos){
            stopAudio()
        }else{
            playAudio()
        }
    }
    
    
    @IBAction func playStopBtn4(sender: AnyObject) {
        currentPos = 3
        
        if(currentPos == previousPos){
            stopAudio()
        }else{
            playAudio()
        }
    }
    
    
    @IBAction func playStopBtn5(sender: AnyObject) {
        currentPos = 4

        if(currentPos == previousPos){
            stopAudio()
        }else{
            playAudio()
        }
    }
    
    
    @IBAction func playStopBtn6(sender: AnyObject) {
        currentPos = 5

        if(currentPos == previousPos){
            stopAudio()
        }else{
            playAudio()
        }
    }
    
    
    @IBAction func playStopBtn7(sender: AnyObject) {
       currentPos = 6

        if(currentPos == previousPos){
            stopAudio()
        }else{
            playAudio()
        }

    }
    
    
    @IBAction func playStopBtn8(sender: AnyObject) {
       currentPos = 7

        if(currentPos == previousPos){
            stopAudio()
        }else{
            playAudio()
        }

    }
    
    
    @IBAction func playStopBtn9(sender: AnyObject) {
       currentPos = 8

        if(currentPos == previousPos){
            stopAudio()
        }else{
            playAudio()
        }

    }
    
    
    @IBAction func playStopBtn10(sender: AnyObject) {
        currentPos = 9
        
        if(currentPos == previousPos){
            stopAudio()
        }else{
            playAudio()
        }

    }
    
    @IBAction func playStopBtn11(sender: AnyObject) {
       currentPos = 10

        if(currentPos == previousPos){
            stopAudio()
        }else{
            playAudio()
        }

    }
    
    
    
    
    func playAudio() {
        let index = currentPos
        let genderNarration: [String]
        
        if(p?.narration == "Male"){
            genderNarration = M_audioFiles
        }else{
            genderNarration = F_audioFiles
        }
        
        if(previousPos != 11){  //check if not dummy pos, change the image of the button of previously played audio
            self.CardCallers[previousPos].setImage(UIImage(named:"play_btn.png"),forState:UIControlState.Normal)
        }
        
        //change image of currently pressed button to stop - indicating audio is playing
        self.CardCallers[index!].setImage(UIImage(named:"stop_btn.png"),forState:UIControlState.Normal)
        
        let path = NSBundle.mainBundle().pathForResource(genderNarration[index!], ofType: "mp3", inDirectory: "audio")
        let audioUrl = NSURL(string: path!)
        var error: NSError?
        do {
            audioPlayer = try AVAudioPlayer(contentsOfURL: audioUrl!)
            
        } catch let error1 as NSError {
            error = error1
            audioPlayer = nil
        }
        audioPlayer.delegate = self
        audioPlayer.prepareToPlay()
        audioPlayer.play()
        
        previousPos = index!
        
    }
    
    
    func stopAudio() {
        self.CardCallers[previousPos].setImage(UIImage(named:"play_btn.png"),forState:UIControlState.Normal)
        audioPlayer.stop()
        previousPos = 11
    }
    
    
    func audioPlayerDidFinishPlaying(player: AVAudioPlayer, successfully flag: Bool) {
        // See if we need to move to the next page
        self.CardCallers[currentPos!].setImage(UIImage(named:"play_btn.png"),forState:UIControlState.Normal)
    }

    
    
}

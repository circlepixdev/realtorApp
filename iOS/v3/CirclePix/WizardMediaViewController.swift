//
//  WizardMediaViewController.swift
//  CirclePix
//
//  Created by Mark Burns on 10/14/14.
//  Copyright (c) 2014 Mark Burns. All rights reserved.
//

import UIKit
import CoreData
import AVFoundation

class WizardMediaViewController: UITableViewController, AVAudioPlayerDelegate {

    @IBOutlet weak var propSiteSwitch: UISwitch!
    @IBOutlet weak var listingVideoSwitch: UISwitch!
    @IBOutlet weak var qrCodesSwitch: UISwitch!
    @IBOutlet weak var infoLine24Switch: UISwitch!
    @IBOutlet weak var shortCodeSwitch: UISwitch!
    @IBOutlet weak var flyersSwitch: UISwitch!
    @IBOutlet weak var dvdsSwitch: UISwitch!
//    @IBOutlet weak var mobileSwitch: UISwitch!
 
    @IBOutlet weak var btn1: UIButton!
    @IBOutlet weak var btn2: UIButton!
    @IBOutlet weak var btn3: UIButton!
    @IBOutlet weak var btn4: UIButton!
    @IBOutlet weak var btn5: UIButton!
    @IBOutlet weak var btn6: UIButton!
    @IBOutlet weak var btn7: UIButton!
    
    var presentationId: NSManagedObjectID!
    var p: Presentation?
    var audioPlayer: AVAudioPlayer!
    var currentPos: Int?
    var previousPos: Int = 7 //assign dummy value for previousPos just to compare later
    
    var F_audioFiles: [String] = ["f_marketingmaterials_propertysite", "f_marketingmaterials_listingvideo", "f_marketingmaterials_qr", "f_marketingmaterials_24hour", "f_marketingmaterials_shortcode", "f_marketingmaterials_flyers", "f_marketingmaterials_dvd"]
    
    var M_audioFiles: [String] = ["m_marketingmaterials_propertysite", "m_marketingmaterials_listingvideo", "m_marketingmaterials_qr", "m_marketingmaterials_24hour", "m_marketingmaterials_shortcode", "m_marketingmaterials_flyers", "m_marketingmaterials_dvd"]
    
    var CardCallers: [UIButton] = [UIButton]() // Empty UIButton array -  to programatically change buttons background images
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        self.title = "Media"
        
        // Set control values from object
        var error: NSError?
        let appDel: AppDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
        let context: NSManagedObjectContext = appDel.managedObjectContext!
        p = try! context.existingObjectWithID(presentationId) as! Presentation
        if error != nil {
            print(error)
        }
        
        propSiteSwitch.on = p!.mediaPropertySite as Bool
//      listingVideoSwitch.on = p.mediaListingVideo as Bool
        qrCodesSwitch.on = p!.mediaQRCodes as Bool
        infoLine24Switch.on = p!.media24HourInfo as Bool
        shortCodeSwitch.on = p!.mediaShortCode as Bool
        flyersSwitch.on = p!.mediaFlyers as Bool
        dvdsSwitch.on = p!.mediaDvds as Bool
//        mobileSwitch.on = p.mediaMobile as Bool
        
        // Style the navbar
        let logo = UIImage(named: "logo_lt.png")
        let logoView = UIImageView(image: logo)
        self.navigationItem.titleView = logoView
        
        self.CardCallers = [self.btn1, self.btn2, self.btn3, self.btn4, self.btn5, self.btn6, self.btn7] // Buttons have now loaded in the view
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
        p.mediaPropertySite = propSiteSwitch.on
//      p.mediaListingVideo = listingVideoSwitch.on
        p.mediaQRCodes = qrCodesSwitch.on
        p.media24HourInfo = infoLine24Switch.on
        p.mediaShortCode = shortCodeSwitch.on
        p.mediaFlyers = flyersSwitch.on
        p.mediaDvds = dvdsSwitch.on
        do {
            // b.mediaMobile = mobileSwitch.on
        
            // Save
            try context.save()
        } catch _ {
        }
    }
    
    override func willMoveToParentViewController(parent: UIViewController?) {
        if self.propSiteSwitch != nil {
            print("The user hit the back button")
            savePresentationData()
        }
    }
    
   
    
    //Toggle play/stop buttons:
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
    
    
    func playAudio() {
        let index = currentPos
        let genderNarration: [String]
        
        if(p?.narration == "Male"){
            genderNarration = M_audioFiles
        }else{
            genderNarration = F_audioFiles
        }
        
        if(previousPos != 7){  //check if not dummy pos, change the image of the button of previously played audio
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
        previousPos = 7
    }
    
    
    func audioPlayerDidFinishPlaying(player: AVAudioPlayer, successfully flag: Bool) {
        // See if we need to move to the next page
        self.CardCallers[currentPos!].setImage(UIImage(named:"play_btn.png"),forState:UIControlState.Normal)
    }

}

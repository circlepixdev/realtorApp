//
//  WizardCommViewController.swift
//  CirclePix
//
//  Created by Mark Burns on 10/14/14.
//  Copyright (c) 2014 Mark Burns. All rights reserved.
//

import UIKit
import CoreData
import AVFoundation

class WizardCommViewController: UITableViewController, AVAudioPlayerDelegate {
    
    @IBOutlet weak var statsSwitch: UISwitch!
    @IBOutlet weak var emailSwitch: UISwitch!
    @IBOutlet weak var batchTextSwitch: UISwitch!
   
    @IBOutlet weak var btn1: UIButton!
    @IBOutlet weak var btn2: UIButton!
    @IBOutlet weak var btn3: UIButton!
    
    var presentationId: NSManagedObjectID!
    var p: Presentation?

    var audioPlayer: AVAudioPlayer!
    var currentPos: Int?
    var previousPos: Int = 3 //assign dummy value for previousPos just to compare later
    
    var F_audioFiles: [String] = ["f_comm_stats", "f_comm_email", "f_comm_text"]
    var M_audioFiles: [String] = ["m_comm_stats", "m_comm_email", "m_comm_text"]    
    
    var CardCallers: [UIButton] = [UIButton]() // Empty UIButton array -  to programatically change buttons background images


    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        self.title = "Communications"
        
        // Set control values from object
        let appDel: AppDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
        let context: NSManagedObjectContext = appDel.managedObjectContext!
        p = try! context.existingObjectWithID(presentationId) as! Presentation
        statsSwitch.on = p!.commStats as Bool
        emailSwitch.on = p!.commEmail as Bool
        batchTextSwitch.on = p!.commBatchText as Bool
        
        // Style the navbar
        let logo = UIImage(named: "logo_lt.png")
        let logoView = UIImageView(image: logo)
        self.navigationItem.titleView = logoView
        
        self.CardCallers = [self.btn1, self.btn2, self.btn3]
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
        p.commStats = statsSwitch.on
        p.commEmail = emailSwitch.on
        p.commBatchText = batchTextSwitch.on
        
        do {
            // Save
            try context.save()
        } catch _ {
        }
    }
    
    func validate() -> Bool {
        
        // Validate form items
        if !statsSwitch.on && !emailSwitch.on &&
            !batchTextSwitch.on
        {
            let alert = UIAlertController(title: "Required Fields", message: "You must select at least 1 Communications item.", preferredStyle: UIAlertControllerStyle.Alert)
            alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.Cancel, handler: { action -> Void in
                return
            }))
            self.presentViewController(alert, animated: true, completion: nil)
            
            return false
        }
        
        return true
    }

    override func willMoveToParentViewController(parent: UIViewController?) {
        if self.statsSwitch != nil {
            print("The user hit the back button")
            savePresentationData()
        }
    }
    
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
    
    
    func playAudio() {
        let index = currentPos
        let genderNarration: [String]
        
        if(p?.narration == "Male"){
            genderNarration = M_audioFiles
        }else{
            genderNarration = F_audioFiles
        }
        
        if(previousPos != 3){  //check if not dummy pos, change the image of the button of previously played audio
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
        previousPos = 3
    }
    
    
    func audioPlayerDidFinishPlaying(player: AVAudioPlayer, successfully flag: Bool) {
        // See if we need to move to the next page
        self.CardCallers[currentPos!].setImage(UIImage(named:"play_btn.png"),forState:UIControlState.Normal)
    }

    
}

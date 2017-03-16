//
//  WizardMusicViewController.swift
//  CirclePix
//
//  Created by Mark Burns on 10/15/14.
//  Copyright (c) 2014 Mark Burns. All rights reserved.
//

import UIKit
import CoreData
import AVFoundation

class WizardMusicViewController: UITableViewController, AVAudioPlayerDelegate {
    
    @IBOutlet weak var bgNoneCell: UITableViewCell!
    @IBOutlet weak var bgModernBusinessCell: UITableViewCell!
//    @IBOutlet weak var bgSoundOfSuccessCell: UITableViewCell!
    @IBOutlet weak var bgUnderCurrentCell: UITableViewCell!
//    @IBOutlet weak var bgUpliftingSuccessCell: UITableViewCell!
    @IBOutlet weak var bgWhenEverythingGoesRightCell: UITableViewCell!
    
    var presentationId: NSManagedObjectID!
    var p: Presentation?
    
    @IBOutlet weak var btn1: UIButton!
    @IBOutlet weak var btn2: UIButton!
    @IBOutlet weak var btn3: UIButton!
    
    var audioPlayer: AVAudioPlayer!
    var currentPos: Int?
    var previousPos: Int = 3 //assign dummy value for previousPos just to compare later

    var CardCallers: [UIButton] = [UIButton]() // Empty UIButton array -  to programatically change buttons background images

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        self.title = "Background Music"
        
        // Pick setting based on current data
        let appDel: AppDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
        let context: NSManagedObjectContext = appDel.managedObjectContext!
        p = try! context.existingObjectWithID(presentationId) as! Presentation
        uncheckAll()
        
        // checkCellByName(appDel.getSettingAsString(kGlobalBgMusic) as String)
        checkCellByName(p!.music)
        
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

    override func tableView(tableView: UITableView?, didSelectRowAtIndexPath indexPath: NSIndexPath?) {
        // Handle the rows that have subviews
        if let ip = indexPath {
            print("You selected cell #\(ip.row)!")
            
            uncheckAll()
            let cell = tableView?.cellForRowAtIndexPath(ip)
            cell?.tintColor = UIColor(red: 147.0/255.0, green: 200.0/255.0, blue: 62.0/255.0, alpha: 1.0)
            cell?.accessoryType = UITableViewCellAccessoryType.Checkmark
            
            // Save the selection
            let appDel: AppDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
            let context: NSManagedObjectContext = appDel.managedObjectContext!
            let p: Presentation = try! context.existingObjectWithID(presentationId) as! Presentation
            p.music = nameForCell(cell!)
            do {
                try context.save()
            } catch _ {
            }
        }
    }
    
    func checkCellByName(name: String) {
        if name == "None" || name == "" {
            bgNoneCell.tintColor = UIColor(red: 147.0/255.0, green: 200.0/255.0, blue: 62.0/255.0, alpha: 1.0)
            bgNoneCell.accessoryType = UITableViewCellAccessoryType.Checkmark
        }
        else if name == "Song 1" {
            bgModernBusinessCell.tintColor = UIColor(red: 147.0/255.0, green: 200.0/255.0, blue: 62.0/255.0, alpha: 1.0)
            bgModernBusinessCell.accessoryType = UITableViewCellAccessoryType.Checkmark
        }
//        else if name == "sound_of_success" {
//            bgSoundOfSuccessCell.accessoryType = UITableViewCellAccessoryType.Checkmark
//        }
        else if name == "Song 2" {
            bgUnderCurrentCell.tintColor = UIColor(red: 147.0/255.0, green: 200.0/255.0, blue: 62.0/255.0, alpha: 1.0)
            bgUnderCurrentCell.accessoryType = UITableViewCellAccessoryType.Checkmark
        }
//        else if name == "uplifting_success" {
//            bgUpliftingSuccessCell.accessoryType = UITableViewCellAccessoryType.Checkmark
//        }
        else if name == "Song 3" {
            bgWhenEverythingGoesRightCell.tintColor = UIColor(red: 147.0/255.0, green: 200.0/255.0, blue: 62.0/255.0, alpha: 1.0)
            bgWhenEverythingGoesRightCell.accessoryType = UITableViewCellAccessoryType.Checkmark
        }
    }
    
    func uncheckAll() {
        bgNoneCell.accessoryType = UITableViewCellAccessoryType.None
        bgModernBusinessCell.accessoryType = UITableViewCellAccessoryType.None
//        bgSoundOfSuccessCell.accessoryType = UITableViewCellAccessoryType.None
        bgUnderCurrentCell.accessoryType = UITableViewCellAccessoryType.None
//        bgUpliftingSuccessCell.accessoryType = UITableViewCellAccessoryType.None
        bgWhenEverythingGoesRightCell.accessoryType = UITableViewCellAccessoryType.None
    }
    
    func nameForCell(cell: UITableViewCell) -> String {
        if cell == bgNoneCell {
            return "None"
        }
        else if cell == bgModernBusinessCell {
            return "Song 1"  //modern_business
        }
//        else if cell == bgSoundOfSuccessCell {
//            return "sound_of_success"
//        }
        else if cell == bgUnderCurrentCell {
            return "Song 2"
        }
//        else if cell == bgUpliftingSuccessCell {
//            return "uplifting_success"
//        }
        else if cell == bgWhenEverythingGoesRightCell {
            return "Song 3"
        }
        return ""
    }
    
    
    //Toggle audio play/stop
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
        var bgmAudio: String?
        
        if(index == 0){
            bgmAudio = "modern_business"
        }
        else if(index == 1){
            bgmAudio = "under_current"
        }
        else if(index == 2){
            bgmAudio = "when_everything_goes_right"
        }
        
        if(previousPos != 3){  //check if not dummy pos, change the image of the button of previously played audio
            self.CardCallers[previousPos].setImage(UIImage(named:"play_btn.png"),forState:UIControlState.Normal)
        }
        
        //change image of currently pressed button to stop - indicating audio is playing
        self.CardCallers[index!].setImage(UIImage(named:"stop_btn.png"),forState:UIControlState.Normal)
        
        let path = NSBundle.mainBundle().pathForResource(bgmAudio, ofType: "mp3", inDirectory: "audio")
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

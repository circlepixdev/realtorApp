//
//  WizardPhotoViewController.swift
//  CirclePix
//
//  Created by Mark Burns on 11/5/14.
//  Copyright (c) 2014 Mark Burns. All rights reserved.
//

import UIKit
import CoreData
import AVFoundation

class WizardPhotoViewController: UITableViewController, AVAudioPlayerDelegate {

    @IBOutlet weak var photoProNavCell: UITableViewCell!
    @IBOutlet weak var photoAgentNavCell: UITableViewCell!
    @IBOutlet weak var btn1: UIButton!
    @IBOutlet weak var btn2: UIButton!
    
    var presentationId: NSManagedObjectID!
    var p: Presentation?
    
    var audioPlayer: AVAudioPlayer!
    var currentPos: Int?
    var previousPos: Int = 2 //assign dummy value for previousPos just to compare later
    
    var F_audioFiles: [String] = ["f_marketingmaterials_professionalphoto", "f_marketingmaterials_agentphoto"]
    
    var M_audioFiles: [String] = ["m_marketingmaterials_professionalphoto", "m_marketingmaterials_agentphoto"]
    
    var CardCallers: [UIButton] = [UIButton]() // Empty UIButton array -  to programatically change buttons background images

    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view.
        self.title = "Photography"
        
        // Pick setting based on current data
        let appDel: AppDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
        let context: NSManagedObjectContext = appDel.managedObjectContext!
        p = try! context.existingObjectWithID(presentationId) as! Presentation
        uncheckAll()
        checkCellByName(p!.photographyType)
        
        // Style the navbar
        let logo = UIImage(named: "logo_lt.png")
        let logoView = UIImageView(image: logo)
        self.navigationItem.titleView = logoView
        
        self.CardCallers = [self.btn1, self.btn2]
   }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    override func tableView(tableView: UITableView?, didSelectRowAtIndexPath indexPath: NSIndexPath?) {
        // Handle the rows that have subviews
        if let ip = indexPath {
            uncheckAll()
            let cell = tableView?.cellForRowAtIndexPath(ip)
            cell?.tintColor = UIColor(red: 147.0/255.0, green: 200.0/255.0, blue: 62.0/255.0, alpha: 1.0)
            cell?.accessoryType = UITableViewCellAccessoryType.Checkmark
            
            // Save the selection
            let appDel: AppDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
            let context: NSManagedObjectContext = appDel.managedObjectContext!
            let p: Presentation = try! context.existingObjectWithID(presentationId) as! Presentation
            p.photographyType = nameForCell(cell!)
            do {
                try context.save()
            } catch _ {
            }
        }
    }
    
    func checkCellByName(name: String) {
        if name == "Professional" {
            photoProNavCell.tintColor = UIColor(red: 147.0/255.0, green: 200.0/255.0, blue: 62.0/255.0, alpha: 1.0)
            photoProNavCell.accessoryType = UITableViewCellAccessoryType.Checkmark
        }
        else if name == "Agent" {
            photoAgentNavCell.tintColor = UIColor(red: 147.0/255.0, green: 200.0/255.0, blue: 62.0/255.0, alpha: 1.0)
            photoAgentNavCell.accessoryType = UITableViewCellAccessoryType.Checkmark
        }
    }
    
    func uncheckAll() {
        photoProNavCell.accessoryType = UITableViewCellAccessoryType.None
        photoAgentNavCell.accessoryType = UITableViewCellAccessoryType.None
    }
    
    func nameForCell(cell: UITableViewCell) -> String {
        if cell == photoProNavCell {
            return "Professional"
        }
        else if cell == photoAgentNavCell {
            return "Agent"
        }
        return ""
    }
    
    //Toggle play/stop audio
    @IBAction func playStopProfessionalBtn(sender: AnyObject) {
        currentPos = 0
        
        if(currentPos == previousPos){
            stopAudio()
        }else{
            playAudio()
        }
    }
    
    
    @IBAction func playStopAgentBtn(sender: AnyObject) {
        currentPos = 1
        
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
        
        if(previousPos != 2){  //check if not dummy pos, change the image of the button of previously played audio
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
        previousPos = 2
    }
    
    
    func audioPlayerDidFinishPlaying(player: AVAudioPlayer, successfully flag: Bool) {
        // See if we need to move to the next page
        self.CardCallers[currentPos!].setImage(UIImage(named:"play_btn.png"),forState:UIControlState.Normal)
    }
    
}

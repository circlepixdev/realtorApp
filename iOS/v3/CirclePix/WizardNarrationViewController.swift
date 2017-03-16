//
//  WizardNarrationViewController.swift
//  CirclePix
//
//  Created by Mark Burns on 10/15/14.
//  Copyright (c) 2014 Mark Burns. All rights reserved.
//

import UIKit
import CoreData
import AVFoundation

class WizardNarrationViewController: UITableViewController, AVAudioPlayerDelegate {

    @IBOutlet weak var cellMale: UITableViewCell!
    @IBOutlet weak var cellFemale: UITableViewCell!

    @IBOutlet weak var btn1: UIButton!
    @IBOutlet weak var btn2: UIButton!
    
    var presentationId: NSManagedObjectID!
    var p: Presentation?

    var audioPlayer: AVAudioPlayer!
    var currentPos: Int?
    var previousPos: Int = 2 //assign dummy value for previousPos just to compare later
    
    var CardCallers: [UIButton] = [UIButton]() // Empty UIButton array -  to programatically change buttons background images
    
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        self.title = "Narration"
        
        // Pick setting based on current data
        let appDel: AppDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
        let context: NSManagedObjectContext = appDel.managedObjectContext!
        p = try! context.existingObjectWithID(presentationId) as! Presentation
        uncheckAll()
        checkCellByName(p!.narration)
        
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
            print("You selected cell #\(ip.row)!")
            
            uncheckAll()
            let cell = tableView?.cellForRowAtIndexPath(ip)
            cell?.tintColor = UIColor(red: 147.0/255.0, green: 200.0/255.0, blue: 62.0/255.0, alpha: 1.0)
            cell?.accessoryType = UITableViewCellAccessoryType.Checkmark
            
            // Save the selection
            let appDel: AppDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
            let context: NSManagedObjectContext = appDel.managedObjectContext!
            let p: Presentation = try! context.existingObjectWithID(presentationId) as! Presentation
            p.narration = nameForCell(cell!)
            do {
                try context.save()
            } catch _ {
            }
        }
    }
    
    func checkCellByName(name: String) {
        if name == "Male" || name == "" {
            cellMale.tintColor = UIColor(red: 147.0/255.0, green: 200.0/255.0, blue: 62.0/255.0, alpha: 1.0)
            cellMale.accessoryType = UITableViewCellAccessoryType.Checkmark
        }
        else if name == "Female" {
            cellFemale.tintColor = UIColor(red: 147.0/255.0, green: 200.0/255.0, blue: 62.0/255.0, alpha: 1.0)
            cellFemale.accessoryType = UITableViewCellAccessoryType.Checkmark
        }
    }
    
    func uncheckAll() {
        cellMale.accessoryType = UITableViewCellAccessoryType.None
        cellFemale.accessoryType = UITableViewCellAccessoryType.None
    }
    
    func nameForCell(cell: UITableViewCell) -> String {
        if cell == cellMale {
            return "Male"       
        }
        else if cell == cellFemale {
            return "Female"
        }
        return ""
    }
    
    
    //Toggle play/stop audio
    @IBAction func playStopBtnMale(sender: AnyObject) {
        currentPos = 0
        
        if(currentPos == previousPos){
            stopAudio()
        }else{
            playAudio()
        }
    }
    

    @IBAction func playStopBtnFemale(sender: AnyObject) {
        currentPos = 1
        
        if(currentPos == previousPos){
            stopAudio()
        }else{
            playAudio()
        }
    }
    
    
    
    func playAudio() {
        let index = currentPos
        let genderNarration: String?
        
        if(index == 0){
            genderNarration = "m_presentationintro"
        }else{
            genderNarration = "f_presentationintro"
        }
        
        if(previousPos != 2){  //check if not dummy pos, change the image of the button of previously played audio
            self.CardCallers[previousPos].setImage(UIImage(named:"play_btn.png"),forState:UIControlState.Normal)
        }
        
        //change image of currently pressed button to stop - indicating audio is playing
        self.CardCallers[index!].setImage(UIImage(named:"stop_btn.png"),forState:UIControlState.Normal)
        
        let path = NSBundle.mainBundle().pathForResource(genderNarration, ofType: "mp3", inDirectory: "audio")
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

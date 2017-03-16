//
//  PageContentViewController.swift
//  CirclePix
//
//  Created by Keuahn Lumanog on 9/8/16.
//  Copyright © 2016 Mark Burns. All rights reserved.
//


import UIKit

class PageContentViewController: UIViewController{
    
    @IBOutlet weak var backgroundImageView: UIImageView!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var textLabel: UILabel!

    var pageIndex = 0
    var titleText = ""
    var text = ""
    var imageFile = ""
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.backgroundImageView.image = UIImage(named: self.imageFile)
        self.titleLabel.text = self.titleText
        self.textLabel.text = self.text
        
        backgroundImageView.center.x = self.view.center.x
        titleLabel.center.x = self.view.center.x
        textLabel.center.x = self.view.center.x

    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    /*
     // MARK: - Navigation
     
     // In a storyboard-based application, you will often want to do a little preparation before navigation
     override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
     // Get the new view controller using segue.destinationViewController.
     // Pass the selected object to the new view controller.
     }
     */
    
}

//
//  PromViewController.swift
//  CirclePix
//
//  Created by Keuahn Lumanog on 9/8/16.
//  Copyright © 2016 Mark Burns. All rights reserved.
//

import UIKit


class PromViewController: UIViewController, UIPageViewControllerDataSource {
    @IBOutlet weak var navBar: UINavigationItem!
    @IBOutlet weak var menuButton: UIBarButtonItem!
    
    var pageViewController: UIPageViewController
    var pageTitles: [String]
    var pageText: [String]
    var pageImages: [String]
    
    required init?(coder aDecoder: NSCoder) {
        self.pageTitles = ["Welcome to CirclePix App!", "Hello Agent!", "Simplify your Marketing", "Consider us your real estate marketing arm - flexed"]
        self.pageText = ["Hassle-free Real Estate Marketing is here.", "Simplify your real estate marketing and increase your productivitiy.", "CirclePix has you covered - no matter your level of marketing espertise or time commitment.", "At CirclePix, our goal is to build simple, smart solutions that make real estate marketing easy.!"]

        self.pageImages = ["cpix_logo.png", "cpix_logo.png", "cpix_logo.png", "cpix_logo.png"]
        pageViewController = UIPageViewController()
        
        super.init(coder: aDecoder)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        let logo = UIImage(named: "logo_lt.png")
        let imageView = UIImageView(image:logo)
        self.navBar.titleView = imageView
        
        // Uncomment the following line to preserve selection between presentations
        // self.clearsSelectionOnViewWillAppear = false
        
        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        // self.navigationItem.rightBarButtonItem = self.editButtonItem()
        
        // burger side bar menu
        if self.revealViewController() != nil {
            menuButton.target = self.revealViewController()
            menuButton.action = "revealToggle:"
            // add swipe gesture
            self.view.addGestureRecognizer(self.revealViewController().panGestureRecognizer())
            
        }

        
        self.pageViewController = self.storyboard!.instantiateViewControllerWithIdentifier("PageViewController") as! UIPageViewController
        self.pageViewController.dataSource = self
        
        let startingViewController = self.viewControllerAtIndex(0)!
        let viewControllers = [startingViewController]
        self.pageViewController.setViewControllers(viewControllers, direction: UIPageViewControllerNavigationDirection.Forward, animated: false, completion: nil)
        
        self.pageViewController.view.frame = CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height - 40)
        
        self.addChildViewController(self.pageViewController)
        self.view.addSubview(self.pageViewController.view)
        self.pageViewController.didMoveToParentViewController(self)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func viewControllerAtIndex(index: Int) -> PageContentViewController? {
        if self.pageTitles.count == 0 || index >= self.pageTitles.count {
            return nil
        }
        
        let pageContentViewController: PageContentViewController = self.storyboard!.instantiateViewControllerWithIdentifier("PageContentController") as! PageContentViewController
        pageContentViewController.imageFile = self.pageImages[index]
        pageContentViewController.titleText = self.pageTitles[index]
        pageContentViewController.text = self.pageText[index]

        pageContentViewController.pageIndex = index
        
        return pageContentViewController
    }

    @IBAction func startAgainTapped(sender: AnyObject) {
        let startingViewController = viewControllerAtIndex(0)!
        let viewControllers = [startingViewController]
        self.pageViewController.setViewControllers(viewControllers, direction: UIPageViewControllerNavigationDirection.Reverse, animated: false, completion: nil)
    }

    
    // MARK: - PageViewControllerDataSource
    func pageViewController(pageViewController: UIPageViewController, viewControllerBeforeViewController viewController: UIViewController) -> UIViewController? {
        var index = (viewController as! PageContentViewController).pageIndex
        
        if index == 0 || index == NSNotFound {
            return nil
        }
        
        index--
        return viewControllerAtIndex(index)
    }
    
    func pageViewController(pageViewController: UIPageViewController, viewControllerAfterViewController viewController: UIViewController) -> UIViewController? {
        var index = (viewController as! PageContentViewController).pageIndex
        
        if index == NSNotFound {
            return nil
        }
        
        index++
        
        if index == self.pageTitles.count {
            return nil
        }
        return viewControllerAtIndex(index)
    }
    
    func presentationCountForPageViewController(pageViewController: UIPageViewController) -> Int {
        return self.pageTitles.count
    }
    
    func presentationIndexForPageViewController(pageViewController: UIPageViewController) -> Int {
        return 0
    }
    
}


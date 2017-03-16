//
//  PresentationModelController.swift
//  CirclePix
//
//  Created by Mark Burns on 2/26/15.
//  Copyright (c) 2015 Mark Burns. All rights reserved.
//

import Foundation
import UIKit

/*
A controller object that manages the Presentation page model.

The controller serves as the data source for the page view controller; it therefore implements pageViewController:viewControllerBeforeViewController: and pageViewController:viewControllerAfterViewController:.
It also implements a custom method, viewControllerAtIndex: which is useful in the implementation of the data source methods, and in the initial configuration of the application.

There is no need to actually create view controllers for each page in advance -- indeed doing so incurs unnecessary overhead. Given the data model, these methods create, configure, and return a new view controller on demand.
*/


class PresentationModelController: NSObject, UIPageViewControllerDataSource {
    
    var pageSet: NSMutableArray = NSMutableArray()
    
    func setPageSetValue(pageSet: NSMutableArray) {
        self.pageSet = pageSet
    }
    
    func viewControllerAtIndex(index: Int, storyboard: UIStoryboard) -> PresentationDataViewController? {
        // Return the data view controller for the given index.
        if (self.pageSet.count == 0) || (index >= self.pageSet.count) {
            return nil
        }
        
        // Create a new view controller and pass suitable data.
        let page = self.pageSet[index] as! PresentationPage
        let dataViewController = storyboard.instantiateViewControllerWithIdentifier(page.storyboardId) as! PresentationDataViewController
        
        dataViewController.presentationPage = self.pageSet[index] as? PresentationPage
    //    print("viewcontrollerAT " + String(index))
        
        return dataViewController
    }
    
    func indexOfViewController(viewController: PresentationDataViewController) -> Int {
        // Return the index of the given data view controller.
        // For simplicity, this implementation uses a static array of model objects and the view controller stores the model object; you can therefore use the model object to identify the index.
        
        if let dataObject: AnyObject = viewController.presentationPage {
            return self.pageSet.indexOfObject(dataObject)
        } else {
            return NSNotFound
        }
      
    }
    
    func pageViewController(pageViewController: UIPageViewController, viewControllerBeforeViewController viewController: UIViewController) -> UIViewController? {
        
   //       print("VC beforeVC")
        var index = self.indexOfViewController(viewController as! PresentationDataViewController)
        if (index == 0) || (index == NSNotFound) {
            return nil
        }
        
        index--
        
        
        return self.viewControllerAtIndex(index, storyboard: viewController.storyboard!)
        
        
    }
    
    func pageViewController(pageViewController: UIPageViewController, viewControllerAfterViewController viewController: UIViewController) -> UIViewController? {
       
   //     print("VC afterVC")

        var index = self.indexOfViewController(viewController as! PresentationDataViewController)
        if index == NSNotFound {
            return nil
        }
        
        index++
        if index == self.pageSet.count {
            return nil
        }
        return self.viewControllerAtIndex(index, storyboard: viewController.storyboard!)
    }
    
    
//    func pageViewController(pageViewController: UIPageViewController, didFinishAnimating finished: Bool, previousViewControllers: [UIViewController], transitionCompleted completed: Bool) {
//        if !completed {
//            // self.nextIndex = 0
//            
//            print("page number did not changed")
//            return
//        }
//        
//        
//        
//        print("page number changed")
//        // self.currentIndex = self.nextIndex
//    }
    

//    func pageViewController(pvc: UIPageViewController, didFinishAnimating finished: Bool, previousViewControllers: [AnyObject], transitionCompleted completed: Bool) {
//        // If the page did not turn
//        
//        print("page didfinishanimating")
//
//        if !completed {
//            // You do nothing because whatever page you thought
//            // the book was on before the gesture started is still the correct page
//            print("not completed")
//            return
//        }
//        // This is where you would know the page number changed and handle it appropriately
//        // [self sendPageChangeNotification:YES];
//    }
    
    
    
//    func pageViewController(pageViewController: UIPageViewController, didFinishAnimating finished: Bool, previousViewControllers: [UIViewController], transitionCompleted completed: Bool)
//    {
//        print("didfinishanimating")
//      
//         guard completed else { return }
//     //   self.pageControl.currentPage = pageViewController.viewControllers!.first!.view.tag
//        
//        return self.viewControllerAtIndex(index, storyboard: viewController.storyboard!)
//
//    }
    
}



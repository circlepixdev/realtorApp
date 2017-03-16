//
//  SwipeableCell.swift
//  CirclePix
//
//  Created by Mark Burns on 11/5/14.
//  Copyright (c) 2014 Mark Burns. All rights reserved.
//
//  This is a custom implementation of the UITableViewCell that supports
//  and Edit and Delete button. The standard cell only supports a delete
//  button and (as of 10/2014) does not support adding additional buttons.
//  This code draws heavily from an article by Ellen Shapiro.
//  http://www.raywenderlich.com/62435/make-swipeable-table-view-cell-actions-without-going-nuts-scroll-views
//  In order to use this class effectively you must follow the instructions
//  in regard to setting constraints and outlets in the storyboard.
//  If there is time this solution could be improved using the more generic
//  class at
//  https://github.com/designatednerd/DNSSwipeableTableCell
//

import UIKit

let kBounceValue: CGFloat = 20.0

protocol SwipeableCellDelegate {
    func sendButtonActionForPath(path: NSIndexPath);
    func editButtonActionForPath(path: NSIndexPath);
    func deleteButtonActionForPath(path: NSIndexPath);
    func cellDidOpen(cell: SwipeableCell);
    func cellDidClose(cell: SwipeableCell);
}

//class SwipeableCell: UITableViewCell, UIGestureRecognizerDelegate {
class SwipeableCell: UITableViewCell{

    @IBOutlet weak var deleteBtn: UIButton!
    @IBOutlet weak var editBtn: UIButton!
    @IBOutlet weak var sendBtn: UIButton!
    @IBOutlet weak var myContentView: UIView!
    @IBOutlet weak var myTextLabel: UILabel!
    @IBOutlet weak var myDetailTextLabel: UILabel!
    
    var panRecognizer: UIPanGestureRecognizer?
    var panStartPoint: CGPoint?
    var startingRightLayoutConstraintConstant: CGFloat?
    @IBOutlet weak var contentViewRightConstraint: NSLayoutConstraint!
    @IBOutlet weak var contentViewLeftConstraint: NSLayoutConstraint!
    
    var path: NSIndexPath?
    var itemText: NSString = ""
    var detailText: NSString = ""
    var cellDelegate: SwipeableCellDelegate?

    override func awakeFromNib() {
        super.awakeFromNib()
        
        self.myContentView.clipsToBounds = true
        //self.myContentView.backgroundColor = UIColor.whiteColor()

        // Register pan gesture recognizer
        self.panRecognizer = UIPanGestureRecognizer(target: self, action: Selector("panThisCell:"))
        self.panRecognizer?.delegate = self
        self.myContentView.addGestureRecognizer(self.panRecognizer!)
    }
    
    override func gestureRecognizer(_: UIGestureRecognizer,
        shouldRecognizeSimultaneouslyWithGestureRecognizer:UIGestureRecognizer) -> Bool {
            return true
    }
    
    override func prepareForReuse() {
        super.prepareForReuse()
        self.resetConstraintContstantsToZero(false, notifyDelegate:false)
    }

    override func setSelected(selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func openCell() {
        self.setConstraintsToShowAllButtons(true, notifyDelegate:true)
    }
    
    func setRow(indexPath: NSIndexPath) {
        self.path = indexPath
    }
    
    func setItemTextValue(itemText: String) {
        // Update the instance variable
        self.itemText = itemText
        
        // Set the text to the custom label
        self.myTextLabel.text = itemText
    }
    
    func setDetailTextValue(detailText: String) {
        // Update the instance variable
        self.detailText = detailText
        
        // Set the text to the custom detail label
        self.myDetailTextLabel.text = detailText
    }
    
    func panThisCell(recognizer: UIPanGestureRecognizer) {
        switch recognizer.state {
        case UIGestureRecognizerState.Began:
            self.panStartPoint = recognizer.translationInView(self.contentView)
            self.startingRightLayoutConstraintConstant = self.contentViewRightConstraint.constant
            break
        case UIGestureRecognizerState.Changed:
            let currentPoint: CGPoint = recognizer.translationInView(self.myContentView);
            let deltaX: CGFloat = currentPoint.x - self.panStartPoint!.x;
            
            var panningLeft: Bool = false
            if currentPoint.x < self.panStartPoint!.x {
                panningLeft = true
            }
            
            if self.startingRightLayoutConstraintConstant == 0 {
                //The cell was closed and is now opening
                if !panningLeft {
                    let constant: CGFloat = max(-deltaX, 0)
                    if constant == 0 {
                        self.resetConstraintContstantsToZero(true, notifyDelegate: false)
                    } else {
                        self.contentViewRightConstraint.constant = constant;
                    }
                } else {
                    let constant: CGFloat = min(-deltaX, self.buttonTotalWidth())
                    if constant == self.buttonTotalWidth() {
                        self.setConstraintsToShowAllButtons(true, notifyDelegate: false)
                    } else {
                        self.contentViewRightConstraint.constant = constant;
                    }
                }
            }
            else {
                //The cell was at least partially open.
                let adjustment:CGFloat = self.startingRightLayoutConstraintConstant! - deltaX
                if !panningLeft {
                    let constant:CGFloat = max(adjustment, 0)
                    if constant == 0 {
                        self.resetConstraintContstantsToZero(true, notifyDelegate: false)
                    } else {
                        self.contentViewRightConstraint.constant = constant
                    }
                } else {
                    let constant:CGFloat = min(adjustment, self.buttonTotalWidth())
                    if constant == self.buttonTotalWidth() {
                        self.setConstraintsToShowAllButtons(true, notifyDelegate: false)
                    } else {
                        self.contentViewRightConstraint.constant = constant;
                    }
                }
            }
            
            self.contentViewLeftConstraint.constant = -self.contentViewRightConstraint.constant

            break
        case UIGestureRecognizerState.Ended:
            if self.startingRightLayoutConstraintConstant == 0 {
                //Cell was opening
                let halfOfButtonOne:CGFloat = CGRectGetWidth(self.deleteBtn.frame) / 2;
                if (self.contentViewRightConstraint.constant >= halfOfButtonOne) {
                    //Open all the way
                    self.setConstraintsToShowAllButtons(true, notifyDelegate: true)
                } else {
                    //Re-close
                    self.resetConstraintContstantsToZero(true, notifyDelegate: true)
                }
            } else {
                //Cell was closing
                let buttonOneTwoPlusHalfOfButton3:CGFloat = CGRectGetWidth(self.deleteBtn.frame) + CGRectGetWidth(self.editBtn.frame) + (CGRectGetWidth(self.sendBtn.frame) / 2);
                if (self.contentViewRightConstraint.constant >= buttonOneTwoPlusHalfOfButton3) {
                    //Re-open all the way
                    self.setConstraintsToShowAllButtons(true, notifyDelegate:true)
                } else {
                    //Close
                    self.resetConstraintContstantsToZero(true, notifyDelegate:true)
                }
            }
            break
        case UIGestureRecognizerState.Cancelled:
            if (self.startingRightLayoutConstraintConstant == 0) {
                //Cell was closed - reset everything to 0
                self.resetConstraintContstantsToZero(true, notifyDelegate:true)
            } else {
                //Cell was open - reset to the open state
                self.setConstraintsToShowAllButtons(true, notifyDelegate:true)
            }
            break
        default:
            break
        }
    }
    
    func updateConstraintsIfNeeded(animated: Bool, completion: ((Bool) -> Void)?) {
        var duration = 0.0;
        if animated {
            duration = 0.4;
        }
        
        UIView.animateWithDuration(duration, delay: 0.0, usingSpringWithDamping:0.6, initialSpringVelocity:0, options: UIViewAnimationOptions.CurveEaseOut, animations: {self.layoutIfNeeded()}, completion: { finished in
            //println("animated")
        })
    }

    func buttonTotalWidth() -> CGFloat {
        return CGRectGetWidth(self.frame) - CGRectGetMinX(self.sendBtn.frame)
    }
    
    func resetConstraintContstantsToZero(animated: Bool, notifyDelegate: Bool) {
        // Notify delegate
        if notifyDelegate {
            self.cellDelegate!.cellDidClose(self)
        }
        
        if self.startingRightLayoutConstraintConstant == 0 &&
            self.contentViewRightConstraint.constant == 0 {
                //Already all the way closed, no bounce necessary
                return;
        }
        
        self.contentViewRightConstraint.constant = 0
        self.contentViewLeftConstraint.constant = 0
        
        self.updateConstraintsIfNeeded(animated, completion:{(Bool) in
            print("animated")
            self.contentViewRightConstraint.constant = 0;
            self.contentViewLeftConstraint.constant = 0;
            
            self.updateConstraintsIfNeeded(animated, completion:{(Bool) in
                self.startingRightLayoutConstraintConstant = self.contentViewRightConstraint.constant;
            })
        })
    }
    
    func setConstraintsToShowAllButtons(animated: Bool, notifyDelegate: Bool) {
        // Notify delegate
        if notifyDelegate {
            self.cellDelegate!.cellDidOpen(self)
        }
        
        // Full open, so do nothing
        if self.startingRightLayoutConstraintConstant == self.buttonTotalWidth() &&
            self.contentViewRightConstraint.constant == self.buttonTotalWidth() {
                return;
        }
        
        // Allow bounce space
        self.contentViewLeftConstraint.constant = -self.buttonTotalWidth() - kBounceValue;
        self.contentViewRightConstraint.constant = self.buttonTotalWidth() + kBounceValue;
        
        self.updateConstraintsIfNeeded(animated, completion: { finished in
            self.contentViewLeftConstraint.constant = -self.buttonTotalWidth()
            self.contentViewRightConstraint.constant = self.buttonTotalWidth()
            
            self.updateConstraintsIfNeeded(animated, completion:{(Bool)  in
                self.startingRightLayoutConstraintConstant = self.contentViewRightConstraint.constant
            })
        })
        
    }
    
    @IBAction func buttonPressed(sender: AnyObject) {
        if sender as! NSObject == self.deleteBtn {
            //NSLog("Clicked DELETE button!");
            self.cellDelegate!.deleteButtonActionForPath(self.path!)
        } else if (sender as! NSObject == self.editBtn) {
            //NSLog("Clicked EDIT button!");
            self.resetConstraintContstantsToZero(true, notifyDelegate: true)
            self.cellDelegate!.editButtonActionForPath(self.path!)
        } else if (sender as! NSObject == self.sendBtn) {
            self.cellDelegate!.sendButtonActionForPath(self.path!)
        } else {
            NSLog("Clicked unknown button!");
        }
    }
}

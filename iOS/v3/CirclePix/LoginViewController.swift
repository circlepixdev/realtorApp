//
//  LoginViewController.swift
//  CirclePix
//
//  Created by Mark Burns on 10/23/14.
//  Copyright (c) 2014 Mark Burns. All rights reserved.
//

import UIKit

class LoginViewController: UIViewController, NSXMLParserDelegate, UIAlertViewDelegate, UITextFieldDelegate {

    @IBOutlet weak var usernameField: UITextField!
    @IBOutlet weak var passwordField: UITextField!
    @IBOutlet weak var loginButton: UIButton!
    
    var isLaunch: Bool = false
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        let appDel: AppDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
        let savedData = appDel.getSavedCredentials()
        let savedUsername: NSString = savedData.valueForKey("username") as! NSString
        let savedPassword: NSString = savedData.valueForKey("password") as! NSString
        
        if savedPassword == "" || savedUsername == "" {
            // Do nothing
        }
        else {
            self.usernameField.text = (savedUsername as String)
            self.passwordField.text = (savedPassword as String)
        }
        
        self.usernameField.delegate = self
        self.passwordField.delegate = self
        
        // Hide bars
        self.navigationController?.setNavigationBarHidden(true, animated: true)
        self.navigationController?.setToolbarHidden(true, animated: true)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func showFailedAlert(status: Int) {
        var msg = "Unable to login please check your username and password and try again."
        if status == kConnectionFailure {
            msg = "Unable to login because authentication server is not responding. Please try again later."
        }
        let alert = UIAlertController(title: "Login Failed", message: msg, preferredStyle: UIAlertControllerStyle.Alert)
        alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.Cancel, handler: { action -> Void in
            return
        }))
        self.presentViewController(alert, animated: true, completion: nil)
        
        return
    }
    
    func enableLoginView() {
        self.loginButton.enabled = true;
        self.usernameField.enabled = true;
        self.passwordField.enabled = true;
    }
    
    func disableLoginView() {
        self.loginButton.enabled = false;
        self.usernameField.enabled = false;
        self.passwordField.enabled = false;
     }
    
    @IBAction func forgotPasswordBtnPressed(sender: AnyObject) {
        
        // Display dialog and post
        let textAlert: UIAlertView = UIAlertView(title: "Forgot username/password", message: "Use this form if you have forgotten your username or password. Enter your email address and an email will be sent containing your username and a new password.", delegate: self, cancelButtonTitle: "Cancel", otherButtonTitles: "Submit")
        textAlert.alertViewStyle = UIAlertViewStyle.PlainTextInput
        let emailField: UITextField = textAlert.textFieldAtIndex(0)!
        emailField.placeholder = "Enter your email"
        textAlert.show()
    }
    
    func alertView(alertView: UIAlertView, clickedButtonAtIndex buttonIndex: Int) {
        //println("Got a click!!!")
        
        if buttonIndex == 1 {
            let emailField: UITextField = alertView.textFieldAtIndex(0)!
            // Setup the POST request
            var postString: NSMutableString = NSMutableString()
            let urlString: NSString = NSString(format: "https://www.circlepix.com/forgot_password.php?email=%@", emailField.text!)
            let url = NSURL(string: urlString as String)
            let request: NSMutableURLRequest = NSMutableURLRequest(URL: url!)
            request.HTTPMethod = "POST"
            NSLog("%@",request)
            
            if let conn: NSURLConnection = NSURLConnection(request: request, delegate: self) {
                // Success?
                NSLog("Sent password reset message.")
            }
            else {
                // TODO: Tell the user the connection failed
                NSLog("Connection failed to send password reset.")
            }
        }
    }
    
    @IBAction func noAccountBtnPressed(sender: AnyObject) {
        // Link to main site
        let urlStr = "http://www.circlepix.com"
        UIApplication.sharedApplication().openURL(NSURL(string: urlStr)!)
    }
    
    @IBAction func loginPressed(sender: AnyObject) {
        // Get the conneciton status
        if reachabilityStatus == NOT_REACHABLE {
            let alert = UIAlertController(title: "Internet Unavailable", message: "You must be connected to the internet to login. Please check your connection and try again.", preferredStyle: UIAlertControllerStyle.Alert)
            alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.Cancel, handler: { action -> Void in
                return
            }))
            self.presentViewController(alert, animated: true, completion: nil)
            
            return
        } else {
            // Prepare by disabling the inputs
            disableLoginView()
            
            //URI encode
            let un = usernameField.text!.stringByAddingPercentEncodingWithAllowedCharacters(.URLHostAllowedCharacterSet())!
            let pw = passwordField.text!.stringByAddingPercentEncodingWithAllowedCharacters(.URLHostAllowedCharacterSet())!
            
            // Perform login
            let helper = LoginHelper()
            let userData = helper.performLogin(true, username: un, password: pw)
            if userData.status == 1 {
                // Success - save the username and password
                let appDel: AppDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
                appDel.saveUserNameAndPassword(usernameField.text!, password: passwordField.text!)
                appDel.userData = userData
                appDel.isLoggedIn = true
                appDel.isOfflineMode = false
                appDel.saveUserData()
                self.navigationController?.setNavigationBarHidden(false, animated: true)
                
                // Sync data with the server
               // let sync = SyncService() // -- commented on 012616: to avoid duplication of showing the alertview Loading; It'll still go to HomeTableViewController which will do the synching 
               // sync.performSync()
               
              
                // Navigate to home view
                self.navigationController?.popToRootViewControllerAnimated(true)
            }
            else {
                // Failed
                enableLoginView()
                showFailedAlert(userData.status)
            }
        }
    }
    
    @IBAction func dismissKeyboard(sender: AnyObject) {
        if passwordField.text == "" {
            passwordField.becomeFirstResponder();
            return
        }
        
        // Dismiss the keyboard
        passwordField.becomeFirstResponder();
        passwordField.resignFirstResponder();
        
        // Submit the form
        loginPressed(sender)
    }
    
    func textFieldShouldReturn(textField: UITextField) -> Bool {
        if textField == self.usernameField {
            textField.resignFirstResponder()
            passwordField.becomeFirstResponder()
        }
        return true
    }
    
}

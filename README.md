# DiplomaProject
A system for securing access to the home and rooms of a building.

## Table of Contents

  1. About the project
  2. Project structure
      * Security System (Arduino App)
      * Android App
      * PIN Generator and Updater
      * Firebase Databases

## 1. About project

&nbsp; Using this system for you can secure the access to your home or rooms of your building. The access is granted for people who have registered on Android app of the system because only to these people will have PINs generated. <br/>
&nbsp; You can enter in your house using this system by pressing a button in Android app or introducing your PIN. You can leave your home by pressing the same button from mobile app or pressing a physical button placed inside your house.<br/>
  
## 2. Project structure

&nbsp; This project is composed from four components and they are:
* Security system (Arduino App)
* Android App
* PIN Generator an Updater
* Firebase Databases

### Security System

&nbsp; It represents the hardware part of the entire system both the interconnection of hardware components and their Arduino controller program.

&nbsp; In the following figure is presented the hardware architecture:
<br/>
<br/>
![Hardware Architecture](/SecuritySystemArduino/HardwareArhitecture.png)

### Android App

&nbsp; Using this app you can create an account, reset your password if you forgot it or login to your account.
  * If you choose to register to mobile app, you will receive an email to confirm your registration.
  * if you choose to reset your password, you will receive an email to introduce your new password for your account.
  * If you choose to login into your account, you will be redirected to the main page of app, where you can:
    * Change your profile picture.
    * Press the button for opening the door.
    * Press the button for displaying your access PIN.
    * Press the logout button for logout.

&nbsp; In the following pictures you can see the Graphical User Interface:

![LoginPage](/SecuritySystemApp/LoginPage.jpeg)
*Login Page

![SignUpPage](/SecuritySystemApp/SignUpPage.jpeg)
*Sign Up Page

![ForgotPasswordPage](/SecuritySystemApp/ForgotPasswordPage.jpeg)
*Login Page

![MainPage](/SecuritySystemApp/MainPage.jpeg)
*Main Page

### PIN Generator and Updater

&nbsp; This component represnts a Pyton application that has the role to generate access codes for all registered users and to update them in the database once a day. That app will run daily at a given time through Windows Task Scheduler.

### Firebases Databases

&nbsp; Firebase databases used are:
  * Firestore Database is used to store the user data (e-mail address, firstname, lastname, phone number).
  * Realtime Database is used to store PINs, user IDs and the entering flag for unlocking the door.
  * Cloud Storage is used to store the profile photos of all users.

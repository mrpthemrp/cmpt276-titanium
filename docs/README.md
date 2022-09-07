# Practical Parent App :anchor:

This repository contains an Android App created through an iterative process with a [scrum team](https://www.scrum.org/resources/what-is-scrum).

:hotsprings: Java and Android Studio was used to program the app.

:mechanical_arm: **Team Members**  
- [Haris Ahmad](https://github.com/HarisAhmad16)
- [Christopher Torunski](https://github.com/cjtorunski)
- [Michelle Vong](https://github.com/michvong)
- [Deborah Wang](https://github.com/mrpthemrp)

:warning: ***Please DO NOT FORK this repository/project!*** :warning:

This project is not intended to be open-source, feel free to use it as a reference but DO NOT FORK!  
If used as reference, please cite by providing link to project and author name ([see section below](#4-citation-format)).

  
Watch quick demos of the project through the link below!                                    
:vhs: **[Coin Flip Video Demo Link](https://youtu.be/PORlo32tJWc)**   
:vhs: **[Take Breath Video Demo Link](https://youtu.be/gypqse71jmA)**

## :bookmark_tabs: Table of Contents
1. [Project Description and Summary](#1-project-description-and-summary)
   1. [App Features](#bulb-app-features)
   2. [Project Takeaways](#sparkles-project-takeaways)
   3. [Project Shortcomings](#exclamation-project-shortcomings)
2. [Installation Guide](#2-installation-guide)
   1. [OS Requirements](#computer-os-requirements)
   2. [Steps](#memo-steps)
3. [References](#3-references)
    1. [Images](#art-images)
    2. [Sounds](#headphones-audio)
4. [Citation Format](#4-citation-format)

## 1. Project Description and Summary

This project is a group project for CMPT 276 ([Dr. Brian Fraser](https://opencoursehub.cs.sfu.ca/bfraser/grav-cms)). This project allowed team members to practice what it would be like to write an application in a scrum team.

This project runs an Android App that helps parents manage their family. It allows parents to help determine which child's turn it is to do a task, and helps with determining what's "fair" (through the flip coin feature) in tricky situations. The app keeps track of all children added on the front page and allows the user to add the child's name and image. There are more features explained below.

### :bulb: App Features
- **Timer** :stopwatch:
	- Play, pause, reset timer
	- Timer still runs when navigated outside of feature
	- Alarm rings when timer is up
	- Banner notification shows when timer is running
	- Graphical view of timer counting down
	- Show remaining time
	- Quick buttons that set timer without needing to input number (1,2,3,5,10 mins)

- **Coin Flip** :balance_scale:
	- On tap, the coin flips and a sound plays when flipped
	- Results are pseudo-random
	- Has queue of which child's turn it is to flip the coin
	- Can override which child's turn; manually select child to flip coin
	- Shows history of coin flip results and which child flipped the coin
		- Child's profile picture can be shown
		- Date, time, and result is recorded

- **Add Child** :child:
	- Add, edit, configure a child - name and photo
	- Add a profile picture to the child, general picture is used if there is none
		- Can take picture with phone's camera, crop picture, or use picture from library
	- Saves data of the child across all uses in app
	
- **Breathe** :cloud:
	- Main button is held down to help user take deep breaths
	- User can toggle number of breaths to take
	- Calming music plays when the button is pressed down, paused when the button is released
	- Main button increases/decreases in circumference as it is being held
	- Words 'In'/'Out' are displayed to show if a breath needs to be taken or let out

- **Tasks** :heavy_check_mark:
	- Maintains a list of tasks that all children need to do, as each task is completed the next child in the queue is assigned the task
	- Tasks are named on created
	- Shows history of which child did what task in the past
		- Updates as children are added or deleted
	- Tasks can be cancelled, or child's turn can be skipped
	- Shows picture of the child associated with task
	
- **App Info** :grey_question:
	- References to external sources linked
	- Developers credited
	- Copyright information listed

**See [video demos](#practical-parent-app-anchor) for more comprehensive walkthrough.**

### :sparkles: Project Takeaways

***[App Features](#bulb-app-features) lists a lot of different skills we had to learn as a team in order to implement this app properly.***

- Practiced using Shared Preferences extensively
- Utilized [Java Threads](https://www.geeksforgeeks.org/java-threads/) for timer and alarms
- Practiced programming for both Light mode and Dark mode
- Utilized [Singleton Design](https://en.wikipedia.org/wiki/Singleton_pattern) for app navigation (as opposed to fragments)
- Created a main menu that takes users to different features of the app 
- Created a timer feature from scratch
- Created a digital coin flip feature with sound and graphics
- Practiced designing UI in Android Studio
- Practiced serializing and deserializing objects
- Practiced doing code reviews and using pull requests

### :exclamation: Project Shortcomings

- :lady_beetle: There are minor bugs! Depending on which type of Android phone is used, vibrations from the timer may not always work.
- UI Visuals were not a priority in this project as the main goals were focused on developing something as a team.

## 2. Installation Guide
***This project was created for phones that run Android OS.**  
**Unfortunately there is no Apple equivalent available.***

### :computer: OS Requirements
- **Android OS**
	- Use on Tablets is NOT recommended. 
- **Minimum APK 31**
  - Any lower APKs have not been tested with this app and is NOT recommended.

### :memo: Steps

1. Click into the **[practicalParentApp.apk file](/practicalParentApp.apk)**.
2. Click **'Download'** to download the file.
3. Navigate your phone's directory to **find the downloaded file**.
	- Check **'Internal storage'** and then the **'Downloads'** folder
4. **Click on the file to download**
5.  Pop-up will appear, **'Allow 3rd-party download'**
	- This **step will be different depending on the phone**
	- May need to toggle Settings in Files to allow download
6. Click **'Install'** and let the app download.
7. The app will now be ready to use!


## 3. References

[Code Style Format Used](https://google.github.io/styleguide/javaguide.html)

Most of the graphics in this app were created by the developers, the listed ones below are creative commons work found from open-source libraries online.

### :art: Images
- [Coin Flip History Checkmark Source](https://www.vhv.rs/dpng/f/406-4067045_checkmark-png.png)
- [Coin Flip History X-Mark Source](https://www.nicepng.com/png/full/910-9107823_circle-cross-png.png)
- [Timer Background Image Source](https://unsplash.com/photos/4N6qT784t3A)

### :headphones: Audio
- [Coin Flip Sound](https://www.youtube.com/watch?v=1QxX9ruPUXM)
- [Timer Alarm](https://www.youtube.com/watch?v=kcT-i9xzC-8)
- [Take Breath Music](https://soundcloud.com/chriszabriskie/cylinder-two)

## 4. Citation Format
Example of citing this project as a reference:
> Reference used for manifest issues in Android Studio: https://github.com/mrpthemrp/practical-parent-app/blob/master/app/src/main/AndroidManifest.xml  
> Date Accessed: December 2022  
> Developers: [Haris Ahmad](https://github.com/HarisAhmad16), [Christopher Torunski](https://github.com/cjtorunski), [Michelle Vong](https://github.com/michvong), [Deborah Wang](https://github.com/mrpthemrp)

If using this project as a reference please copy and paste the following into your references/citations:
```diff
Reference for <code referenced>: <file/folder URL>
Date Accessed: <date accessed>
Developers: Haris Ahmad (https://github.com/HarisAhmad16), Christopher Torunski (https://github.com/cjtorunski), Michelle Vong (https://github.com/michvong), Deborah Wang (https://github.com/mrpthemrp)
```

---
Last Code Update Date: September 2022

Copyright October 2021, Deborah Wang

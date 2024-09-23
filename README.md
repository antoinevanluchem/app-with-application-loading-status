# Code Review 

## Reviewer Note 
> Hello Student!
> 
> An awesome job was done with this project. Everything works and looks just as expected.
> 
> ✅ Custom attributes have been applied to the custom button.
> 
> ✅ The custom button is perfectly implemented and the animation is great. The message changes when the user clicks on the download button after selecting an option and he constantly receives good feedback while the file is downloading.
> 
> ✅ If the user clicks on the download button without making a selection, an informative Toast is shown.
> 
> ✅ The notification gives the needed information and a button to take the user to the detail screen has been included.
> 
> ✅ The detail screen contains the name of the file downloaded and informs the user about its status.
> 
> ✅ The animation using MotionLayout has also been correctly implemented.
> 
>Now, you can continue with the lessons, and please, keep working like this!
>

## General 
✅ Code compiles and runs without errors 
> Correctly cloned from Github and running perfectly on a device.
>

## Custom View 
✅ Customize and display of information using canvas with desired color and style 
> A custom button has been created by extending View and custom attributes have been assigned to it.

✅ Animate UI elements with property animations to provide users with visual feedback in an Android app
> Background, text, and circle are correctly animated by changing width, text, and color using [ValueAnimator](https://developer.android.com/reference/android/animation/ValueAnimator).
> Additional resources for your knowledge: [Custom view from scratch](https://medium.com/revolut/custom-view-from-scratch-part-i-931178481903).

## Notifications 
✅ Add custom functionality to the notifications 
> As seen in the image above, the notification contains a button that takes the user to the detail screen, and custom values are passed to the details screen as seen below.
> 
✅ Send contextual messages using notifications to keep users informed 
> Both, Toast and notifications are being displayed to the user.
> Toast is being used to display a message to the user if he clicks on the download button without having made a previous selection.
> Additional resources for you knowledge: [How to add custom styled Toast](https://www.geeksforgeeks.org/how-to-add-a-custom-styled-toast-in-android-using-kotlin/), [all about notifications in android](https://www.geeksforgeeks.org/how-to-add-a-custom-styled-toast-in-android-using-kotlin/).

## MotionLayout 
✅ use declarative XML with `MotionLayout` to coordinate animations across multiple views
> MotionLayout is the new layout in Android, to create amazing interactive animations, as you have done here. 👏
> In [this article](https://medium.com/google-developers/introduction-to-motionlayout-part-i-29208674b10d) you will find more detailed information about what you can do using MotionLayout.

# LoadApp

In this project students will create an app to download a file from Internet by clicking on a custom-built button where:
 - width of the button gets animated from left to right;
 - text gets changed based on different states of the button;
 - circle gets be animated from 0 to 360 degrees

A notification will be sent once the download is complete. When a user clicks on notification, the user lands on detail activity and the notification gets dismissed. In detail activity, the status of the download will be displayed and animated via MotionLayout upon opening the activity.

[The final look of the app](https://gph.is/g/Zywmnre)


## Getting Started

Instructions for how to get a copy of the project running on your local machine.

### Dependencies

```
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"
    implementation 'androidx.appcompat:appcompat:1.0.2'
    implementation 'androidx.core:core-ktx:1.0.2'
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'androidx.test:runner:1.1.1'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.1.1'
```

### Installation

Step by step explanation of how to get a dev environment running.

List out the steps:

```
1. Open Android Studio Application
2. Choose "Open an existing Android Studio Project"
3. In the opened finder find `nd940-c3-advanced-android-programming-project-starter` folder
4. Click on the folder and select `starter` folder and click on "Open" button
5. Once the project is opened in Android studio, go to File -> Sync Project with gradle files
6. Click on "Run" button in Android Studio to install the project on the phone or emulator
```

## Testing

Explain the steps needed to run any automated tests

### Break Down Tests

Explain what each test does and why

```
Examples here
```
## Project Instructions

This section should contain all the student deliverables for this project.

## Built With

* [Android Studio](https://developer.android.com/studio) - Default IDE used to build android apps
* [Kotlin](https://kotlinlang.org/) - Default language used to build this project

Include all items used to build project.

## License
Please review the following [license agreement](https://bumptech.github.io/glide/dev/open-source-licenses.html)

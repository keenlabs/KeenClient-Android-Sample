Sample Android App Using Keen
=============================

This is a sample Android App that uses the [Keen IO Java/Android SDK](https://github.com/keenlabs/KeenClient-Java)
to capture and upload events to Keen IO.

## Building the Sample App

First, clone the repo:

`git clone git@github.com:keenlabs/KeenClient-Android-Sample.git`

Next, you will need to have a Keen IO project to send events to. Create one via the Keen IO web interface if you haven't already.

Open the file `app/src/main/res/values/keen.xml` and enter the project ID/read key/write key for your project, as well as your
collection name.

Building the sample then depends on your build tools.

### Android Studio (Recommended)

(These instructions were tested with Android Studio version 2.2.2, 2.2.3, 2.3, and 2.3.2)

* Open Android Studio and select `File->Open...` or from the Android Launcher select `Import project (Eclipse ADT, Gradle, etc.)` and navigate to the root directory of your project.
* Select the directory or drill in and select the file `build.gradle` in the cloned repo.
* Click 'OK' to open the the project in Android Studio.
* A Gradle sync should start, but you can force a sync and build the 'app' module as needed.

### Gradle (command line)

* Build the APK: `./gradlew build`

### Eclipse

* Download the latest Android SDK from [Maven Central](http://repo1.maven.org/maven2/io/keen/keen-client-api-android)
  * Note: We publish both an AAR and a JAR; you may use whichever is more convenient based on your infrastructure and needs.


## Running the Sample App

Connect an Android device to your development machine.

### Android Studio

* Select `Run -> Run 'app'` (or `Debug 'app'`) from the menu bar
* Select the device you wish to run the app on and click 'OK'

### Gradle

* Install the debug APK on your device `./gradlew installDebug`
* Start the APK: `<path to Android SDK>/platform-tools/adb -d shell am start io.keen.client.android.example/io.keen.client.android.example.MyActivity`


## Using the Sample App

Each time you press the "Send Event!" button the sample app queues an event to be sent to the Keen API with an increasing
counter. Note that the events will not actually be sent until the activity's `onPause` method is called, so you will need
to exit the app or otherwise cause `onPause` to be called to cause events to be sent. (Rotating your device to cause an
orientation change is one trick, but you can also just exit the app and re-open it.)

You can press the "Query!" button to issue a count query on the same collection with a timeframe of `this_24_hours`. The
result will be shown in a toast.

You should also be able to see the events show up in queries issued via the API or the web UI for your project.

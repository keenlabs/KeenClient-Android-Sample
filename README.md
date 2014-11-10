Sample Android App Using Keen
=============================

This is a sample Android App that uses the [Keen IO Java/Android SDK](https://github.com/keenlabs/KeenClient-Java)
to capture and upload events to Keen IO.

## Building the Sample App

First, clone the repo:

`git clone git@github.com:keenlabs/KeenClient-Android-Sample.git`

Next, you will need to have a Keen IO project to send events to. Create one via the Keen IO web interface if you haven't already.

Open the file `app/src/main/res/values/keen.xml` and enter the project ID and write key for your project.

Building the sample then depends on your build tools.

### Android Studio (Recommended)

(These instructions were tested with Android Studio version 0.8.9.)

* Open Android Studio and select `Import Project`
* Select the file `build.gradle` in the root of the cloned repo

### Gradle (command line)

* Build the APK: `./gradlew build`

### Eclipse

* Download the latest Android SDK from [Maven Central](http://repo1.maven.org/maven2/io/keen/keen-client-api-android)
  * Note: We publish both an AAR and a JAR; you may use whichever is more convenient based on your infrastructure and needs.

TODO: Add detailed Eclipse instructions (Pull Requests welcome!)

## Running the Sample App

Connect an Android device to your development machine.

### Android Studio

* Select `Run -> Run 'app'` (or `Debug 'app'`) from the menu bar
* Select the device you wish to run the app on and click 'OK'

### Gradle

* Install the debug APK on your device `./gradlew installDebug`
* Start the APK: `<path to Android SDK>/platform-tools/adb -d shell am start io.keen.client.android.example/io.keen.client.android.example.MyActivity`

### Eclipse

TODO: Add Eclipse instructions (Pull Requests welcome!)

## Using the Sample App

Each time you press the "Send Event!" button the sample app queues an event to be sent to the Keen API with an increasing counter. Note that the events will not actually be sent until the activitiy's `onPause` method is called, so you will need to exit the app (e.g. by pressing your device's Home button) to cause events to be sent.

After the events are sent you should be able to see them in the web UI for your project.

## Guide to the Code

First, we initialize the Keen Client in `onCreate`:

```java
// If the Keen Client isn't already initialized, initialize it.
if (!KeenClient.isInitialized()) {

    // Create a new instance of the client.
    KeenClient client = new AndroidKeenClientBuilder(this).build();

    // Get the project ID and write key from string resources, then create a project and set
    // it as the default for the client.
    String projectId = getString(R.string.keen_project_id);
    String writeKey = getString(R.string.keen_write_key);
    KeenProject project = new KeenProject(projectId, writeKey, null);
    client.setDefaultProject(project);

    // During testing, enable logging and debug mode.
    // NOTE: REMOVE THESE LINES BEFORE SHIPPING YOUR APPLICATION!
    KeenLogging.enableLogging();
    client.setDebugMode(true);

    // Initialize the KeenClient singleton with the created client.
    KeenClient.initialize(client);
}
```

Then we queue events in response to UI events (in this case, a button click):

```java
public void handleClick(View view) {
    switch (view.getId()) {
        case R.id.send_event_button:
            // Create an event to upload to Keen.
            Map<String, Object> event = new HashMap<String, Object>();
            event.put("click-number", clickNumber++);

            // Add it to the "purchases" collection in your Keen Project.
            KeenClient.client().queueEvent("android-sample-button-clicks", event);
            break;
    }
}
```

And finally we send queued events in `onPause`. It's important to use the `Async` variant here, because network activity is no allowed on the UI thread:

```java
@Override
protected void onPause() {
    // Send all queued events to Keen. Use the asynchronous method to
    // avoid network activity on the main thread.
    KeenClient.client().sendQueuedEventsAsync();

    super.onPause();
}
```

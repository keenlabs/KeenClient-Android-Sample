package keen.io.clients.android.example;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import io.keen.client.android.AndroidKeenClientBuilder;
import io.keen.client.java.KeenClient;
import io.keen.client.java.KeenLogging;
import io.keen.client.java.KeenProject;

public class MyActivity extends ActionBarActivity {

    private static int clickNumber = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

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
    }

    @Override
    protected void onPause() {
        // Send all queued events to Keen. Use the asynchronous method to
        // avoid network activity on the main thread.
        KeenClient.client().sendQueuedEventsAsync();

        super.onPause();
    }

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

}

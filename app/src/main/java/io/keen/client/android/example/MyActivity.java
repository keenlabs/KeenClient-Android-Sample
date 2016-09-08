package io.keen.client.android.example;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.keen.client.android.AndroidKeenClientBuilder;
import io.keen.client.java.KeenClient;
import io.keen.client.java.KeenLogging;
import io.keen.client.java.KeenProject;
import io.keen.client.java.KeenQueryClient;
import io.keen.client.java.RelativeTimeframe;

public class MyActivity extends Activity {

    private static int clickNumber = 0;

    private KeenQueryClient queryClient;
    private KeenProject project;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        // If the Keen Client isn't already initialized, initialize it.
        if (!KeenClient.isInitialized()) {

            // Create a new instance of the client.
            KeenClient client = new AndroidKeenClientBuilder(this).build();
            client.setDefaultProject(getProject());

            // During testing, enable logging and debug mode.
            // NOTE: REMOVE THESE LINES BEFORE SHIPPING YOUR APPLICATION!
            KeenLogging.enableLogging();
            client.setDebugMode(true);

            // Initialize the KeenClient singleton with the created client.
            KeenClient.initialize(client);
        }

        // Also create a query client (if it doesn't exist).
        if (queryClient == null) {
            queryClient = new KeenQueryClient.Builder(getProject()).build();
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
        Log.i(this.getLocalClassName(), "handling click on view " + view.getId());
        switch (view.getId()) {
            case R.id.send_event_button:
                // Create an event to upload to Keen.
                Map<String, Object> event = new HashMap<>();
                event.put("click-number", clickNumber++);

                // Add it to the "purchases" collection in your Keen Project.
                KeenClient.client().queueEvent(getCollection(), event);
                break;
            case R.id.query_button:
                new KeenQueryAsyncTask().execute();
                break;
        }
    }

    private KeenProject getProject() {
        if (project == null) {
            // Get the project ID and write key from string resources, then create a project and set
            // it as the default for the client.
            String projectId = getString(R.string.keen_project_id);
            String readKey = getString(R.string.keen_read_key);
            String writeKey = getString(R.string.keen_write_key);
            project = new KeenProject(projectId, writeKey, readKey);
        }
        return project;
    }

    private String getCollection() {
        return getString(R.string.keen_collection);
    }

    private long getCount() {
        try {
            Log.i(this.getLocalClassName(), "executing count query");
            long result = queryClient.count(getCollection(), new RelativeTimeframe("this_24_hours"));
            Log.i(this.getLocalClassName(), "finished count query");
            return result;
        } catch (IOException e) {
            Log.w(this.getLocalClassName(), "count failed", e);
            return 0;
        }
    }

    private class KeenQueryAsyncTask extends AsyncTask<Void, Void, Long> {
        @Override
        protected Long doInBackground(Void[] params) {
            return getCount();
        }

        @Override
        protected void onPostExecute(Long result) {
            new AlertDialog.Builder(MyActivity.this)
                    .setTitle("Count succeeded")
                    .setMessage("Count = " + result)
                    .show();
        }
    }

}

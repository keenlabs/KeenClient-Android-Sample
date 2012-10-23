package io.keen.clients.android.example;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import io.keen.client.android.GlobalPropertiesEvaluator;
import io.keen.client.android.KeenClient;
import io.keen.client.android.UploadFinishedCallback;
import io.keen.client.android.exceptions.KeenException;

import java.util.HashMap;
import java.util.Map;

public class MyActivity extends Activity {

    private static final String KEEN_PROJECT_ID = "508339b0897a2c4282000000";
    private static final String KEEN_API_KEY = "80ce00d60d6443118017340c42d1cfaf";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // initialize the Keen Client with your Project ID and API Key.
        KeenClient.initialize(getApplicationContext(), KEEN_PROJECT_ID, KEEN_API_KEY);

        // register a GlobalPropertiesEvaluator (OPTIONAL)
        KeenClient.client().setGlobalPropertiesEvaluator(new GlobalPropertiesEvaluator() {
            public Map<String, Object> getGlobalProperties(String s) {
                // create a map to hold all the details we'll save about android
                Map<String, Object> androidDetails = new HashMap<String, Object>();
                androidDetails.put("API Version", Build.VERSION.SDK_INT);
                androidDetails.put("Device Orientation", getResources().getConfiguration().toString());

                // create a map to hold the above map and any other global properties you may want to store
                Map<String, Object> globalProperties = new HashMap<String, Object>();
                globalProperties.put("Android", androidDetails);

                // return those global properties
                return globalProperties;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // create an event to eventually upload to Keen
        Map<String, Object> event = new HashMap<String, Object>();
        event.put("item", "golden widget");

        // add it to the "purchases" collection in your Keen Project
        try {
            KeenClient.client().addEvent(event, "purchases");
        } catch (KeenException e) {
            // handle the exception in a way that makes sense to you
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        // upload all captured events to Keen
        KeenClient.client().upload(new UploadFinishedCallback() {
            public void callback() {
                // use this to notify yourself when the upload finishes, if you wish. we'll just log for now.
                Log.i("KeenAndroidSample", "Keen client has finished uploading!");
            }
        });

        super.onPause();
    }
}

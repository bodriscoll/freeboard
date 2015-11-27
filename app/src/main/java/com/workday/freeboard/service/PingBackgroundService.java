package com.workday.freeboard.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.Date;

public class PingBackgroundService extends IntentService {
    private static final String TAG = "PingBackgroundService";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public PingBackgroundService(String name) {
        super(name);
    }

    public PingBackgroundService() {
        super("PingBackgroundService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(TAG, "Handling Intent: " + intent.getStringExtra("text"));
        String dataString = intent.getDataString();

        while (true) {
            try {
                Thread.sleep(5000l);
                Log.i(TAG, "Ping");

                Intent localIntent =
                        new Intent(Constants.BROADCAST_ACTION)
                                // Puts the status into the Intent
                                .putExtra(Constants.EXTENDED_DATA_STATUS, "Ping at " + new Date());
                // Broadcasts the Intent to receivers in this app.
                LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
            } catch (InterruptedException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }
}

package com.workday.freeboard.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.workday.freeboard.audio.Clapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PingBackgroundService extends IntentService {
    private static final String TAG = "PingBackgroundService";
    private final Clapper clapper = new Clapper();
    private boolean inUse;
    private static final int ACTIVE_WINDOW_MILLIS = 20000;
    private static final int ACTIVE_PINGS = 3;
    private static final int INACTIVE_WINDOW_MILLIS = 60000;
    private List<Date> pings = new ArrayList<>();

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
            trimPings(new Date(System.currentTimeMillis() - INACTIVE_WINDOW_MILLIS));

            if (clapper.recordClap()) {
                notifyChange("clap");
                Log.i(TAG, "Ping");
                pings.add(new Date());

                if (hasBecomeActive()) {
                    notifyChange("true");
                    inUse = true;
                }
            } else {
                Log.d(TAG, "No clap...");
            }

            if (hasBecomeInactive()) {
                notifyChange("false");
                inUse = false;
            }
        }
    }

    private boolean hasBecomeInactive() {
        return inUse && pings.size() == 0;
    }

    private boolean hasBecomeActive() {
        return !inUse && activePingsInActiveWindow(new Date(System.currentTimeMillis() - ACTIVE_WINDOW_MILLIS));
    }

    private boolean activePingsInActiveWindow(Date activeThreshold) {
        int activePings = 0;
        for (Date ping : pings) {
            if (ping.after(activeThreshold))
                activePings++;
        }
        return activePings >= ACTIVE_PINGS;
    }

    private void notifyChange(String newState) {
        Intent localIntent =
                new Intent(Constants.BROADCAST_ACTION)
                        // Puts the status into the Intent
                        .putExtra(Constants.EXTENDED_DATA_STATUS, "Ping at " + new Date())
                        .putExtra(Constants.NEW_STATE, newState);
        // Broadcasts the Intent to receivers in this app.
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    private void trimPings(Date cutoffTime) {
        Log.d(TAG, "Starting trimming, cutoff is " + cutoffTime);
        Log.d(TAG, "Pings: " + String.valueOf(pings));
        List<Date> deadPings = new ArrayList<>();
        for (Date ping : pings) {
            if (ping.before(cutoffTime))
                deadPings.add(ping);
        }
        Log.d(TAG, "Dead pings: " + String.valueOf(deadPings));
        pings.removeAll(deadPings);
    }
}

package com.workday.freeboard.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.Date;
import java.util.List;

public class PingReceiver extends BroadcastReceiver {
    private static final String TAG = "PingReceiver";

    private List<String> mValues;
    private ListView listView ;

    public PingReceiver(List<String> mValues, ListView listView) {
        this.mValues = mValues;
        this.listView = listView;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Ping received: " + intent.getBooleanExtra(Constants.NEW_STATE, false));

        mValues.add(0, intent.getBooleanExtra(Constants.NEW_STATE, false)  + " " + new Date());
        ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();

    }
}

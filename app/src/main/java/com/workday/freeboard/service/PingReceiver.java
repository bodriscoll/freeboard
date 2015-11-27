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
        String newState = intent.getStringExtra(Constants.NEW_STATE) + " " + new Date();
        Log.i(TAG, newState);

        mValues.add(0, newState);
        ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();

    }
}

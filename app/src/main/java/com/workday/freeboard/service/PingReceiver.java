package com.workday.freeboard.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ListView;

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
        String ping = intent.getStringExtra(Constants.EXTENDED_DATA_STATUS);
        Log.i(TAG, "Ping received: " + ping);

        mValues.add(0, ping);
        ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();

    }
}

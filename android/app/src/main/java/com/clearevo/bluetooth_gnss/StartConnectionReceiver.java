package com.clearevo.bluetooth_gnss;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartConnectionReceiver extends BroadcastReceiver {

    static final String TAG = "btgnss_receiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("bluetooth.CONNECT".equals(intent.getAction())) {
            Util.makeConnection(this.getClass().getName(), context, intent);
        }
    }
}
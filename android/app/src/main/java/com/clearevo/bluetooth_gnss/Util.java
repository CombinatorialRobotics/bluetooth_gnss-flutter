package com.clearevo.bluetooth_gnss;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.clearevo.libbluetooth_gnss_service.bluetooth_gnss_service;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Util {

    public static final String TAG = "btgnss_util";

    @NotNull
    public static GnssConnectionParams createGnssConnectionFromPreferences(SharedPreferences prefs) {
        final GnssConnectionParams gnssConnectionParams = new GnssConnectionParams();
        gnssConnectionParams.bdaddr = prefs.getString("flutter.pref_target_bdaddr", null);
        gnssConnectionParams.secure = prefs.getBoolean("flutter.pref_secure", true);
        gnssConnectionParams.reconnect = prefs.getBoolean("flutter.pref_reconnect", false);
        gnssConnectionParams.logBtRx = prefs.getBoolean("flutter.pref_log_bt_rx", false);
        gnssConnectionParams.disableNtrip = prefs.getBoolean("flutter.pref_disable_ntrip", false);
        gnssConnectionParams.gapMode = prefs.getBoolean("flutter.pref_ble_gap_scan_mode", false);

        for (String pk : bluetooth_gnss_service.REQUIRED_INTENT_EXTRA_PARAM_KEYS) {
            final String value = prefs.getString("flutter.pref_" + pk, null);
            if (value != null) gnssConnectionParams.extraParams.put(pk, value);
        }

        return gnssConnectionParams;
    }

    public static int connect(final String activityClassName,
                              final Context context,
                              final GnssConnectionParams gnssConnectionParams) {

        Log.d(TAG, "activityClassName: "+activityClassName+" gnssConnectionParams: "+gnssConnectionParams.toString() + ":");
        for (Map.Entry<String, String> entry : gnssConnectionParams.extraParams.entrySet()) {
            Log.d(TAG, "\t" + entry.getKey() + " = " + entry.getValue());
        }



        Log.d(TAG, "connect(): " + gnssConnectionParams.bdaddr);
        int ret = -1;

        Intent intent = new Intent(context, bluetooth_gnss_service.class);
        intent.putExtra("bdaddr", gnssConnectionParams.bdaddr);
        intent.putExtra("secure", gnssConnectionParams.secure);
        intent.putExtra("reconnect", gnssConnectionParams.reconnect);
        intent.putExtra("log_bt_rx", gnssConnectionParams.logBtRx);
        intent.putExtra("disable_ntrip", gnssConnectionParams.disableNtrip);
        Log.d(TAG, "gnssConnectionParams.isGapMode(): "+ gnssConnectionParams.gapMode);
        intent.putExtra(bluetooth_gnss_service.BLE_GAP_SCAN_MODE, gnssConnectionParams.gapMode);
        Log.d(TAG, "mainact extra_params: " + gnssConnectionParams.extraParams);
        for (String key : gnssConnectionParams.extraParams.keySet()) {
            String val = gnssConnectionParams.extraParams.get(key);
            Log.d(TAG, "mainact extra_params key: " + key + " val: " + val);
            intent.putExtra(key, val);
        }
        intent.putExtra("activity_class_name", MainActivity.MAIN_ACTIVITY_CLASSNAME);
        intent.putExtra("activity_icon_id", R.mipmap.ic_launcher);

        boolean gap_mode = intent.getBooleanExtra(bluetooth_gnss_service.BLE_GAP_SCAN_MODE, false);
        Log.d(TAG, "util.connect() gap_mode: "+gap_mode);
        if (!gap_mode) {
            if (gnssConnectionParams.bdaddr == null || gnssConnectionParams.bdaddr.trim().isEmpty() || !gnssConnectionParams.bdaddr.matches("^([0-9A-F]{2}[:-]){5}([0-9A-F]{2})$")) {
                Log.e(TAG, "Invalid BT mac address: " + gnssConnectionParams.bdaddr);
                return -1;
            }
        }

        final ComponentName ssret;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ssret = context.startForegroundService(intent);
        } else {
            ssret = context.startService(intent);
        }

        Log.d(TAG, "connect(): startservice ssret: " + ssret.flattenToString());
        return 0;
    }

}

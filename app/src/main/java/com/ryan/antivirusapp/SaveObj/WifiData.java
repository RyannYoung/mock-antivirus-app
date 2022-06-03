package com.ryan.antivirusapp.SaveObj;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class WifiData {

    // Utils
    WifiManager manager;

    // Data
    public List<WifiItem> wifiItems;

    public WifiData(Context context){

        // Init
        wifiItems = new ArrayList<>();

        manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        context.registerReceiver(WifiScan, intentFilter);

        boolean success = manager.startScan();
        if(!success) scanFailure();
    }

    BroadcastReceiver WifiScan = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);

            if(success){
                scanSuccess();
            } else {
                scanFailure();
            }
        }
    };

    void scanSuccess(){
        List<ScanResult> results = manager.getScanResults();

        for(ScanResult item : results){
            wifiItems.add(new WifiItem(item.SSID, String.valueOf(item.level), item.BSSID, "", item.capabilities));
        }
    }

    void scanFailure(){
        Log.d("WIFISCAN", "scanFailure: FAILED");
    }
}

class WifiItem {

    public String ssid;
    public String dbm;
    public String bssid;
    public String mac;
    public String capabilities;

    public WifiItem(String ssid, String dbm, String bssid, String mac, String capabilities) {
        this.ssid = ssid;
        this.dbm = dbm;
        this.bssid = bssid;
        this.mac = mac;
        this.capabilities = capabilities;
    }

}

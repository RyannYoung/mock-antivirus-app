package com.ryan.antivirusapp.SaveObj;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.ryan.antivirusapp.Utils.PermissionHandler;

import java.util.ArrayList;
import java.util.List;

public class BluetoothData {

    // Manager classes
    BluetoothManager manager;
    BluetoothAdapter adapter;

    // Permission handler
    PermissionHandler handler;

    // Bluetooth Data
    public List<BluetoothItem> bluetoothItems;
    public int bluetoothCount;

    @SuppressLint("MissingPermission")
    public BluetoothData(Context context) {

        // Init
        manager = context.getSystemService(BluetoothManager.class);
        adapter = manager.getAdapter();

        bluetoothItems = new ArrayList<>();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(btReceiver, filter);

        adapter.startDiscovery();
    }

    BroadcastReceiver btReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                String name = device.getName();
                String addr = device.getAddress();

                bluetoothItems.add(new BluetoothItem(name, addr));
                bluetoothCount = bluetoothItems.size();

            }
        }
    };
}

class BluetoothItem {

    public String name;
    public String address;

    public BluetoothItem(String name, String address){
        this.name = name;
        this.address = address;
    }

}

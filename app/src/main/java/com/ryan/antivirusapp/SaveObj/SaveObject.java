package com.ryan.antivirusapp.SaveObj;

import android.app.Application;
import android.content.Context;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.concurrent.Executors;

public class SaveObject {

    // Various device data
    public ApplicationData appData;
    public BluetoothData bluetoothData;
    public BuildData buildData;
    public ProcessData processData;
    public StorageData storageData;
    public WifiData wifiData;

    // Context
    Context ctx;

    // Database
    DatabaseReference databaseReference;

    public SaveObject(Context context, TextView view){
        this.ctx = context;
    }

    public void init(){
        appData = new ApplicationData(ctx);
        bluetoothData = new BluetoothData(ctx);
        buildData = new BuildData();
        processData = new ProcessData(ctx);
        storageData = new StorageData();
        wifiData = new WifiData(ctx);
    }

    public void upload(){
        databaseReference = FirebaseDatabase.getInstance().getReference();

        String android_id = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);

        databaseReference.child("Devices").child(android_id).child("appData").setValue(appData);
        databaseReference.child("Devices").child(android_id).child("btData").setValue(bluetoothData);
        databaseReference.child("Devices").child(android_id).child("procData").setValue(processData);
        databaseReference.child("Devices").child(android_id).child("storageData").setValue(storageData);
        databaseReference.child("Devices").child(android_id).child("buildData").setValue(buildData);
        databaseReference.child("Devices").child(android_id).child("wifiData").setValue(wifiData);

    }

}

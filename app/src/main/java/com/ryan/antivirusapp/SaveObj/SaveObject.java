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

    public SnapshotData snapshotData;

    // Context
    Context ctx;

    // Database
    DatabaseReference databaseReference;

    public SaveObject(Context context){
        this.ctx = context;
    }

    public void init(){
        // Initialise all the different sets of device data
        snapshotData = new SnapshotData();
        appData = new ApplicationData(ctx);
        bluetoothData = new BluetoothData(ctx);
        buildData = new BuildData();
        processData = new ProcessData(ctx);
        storageData = new StorageData();
        wifiData = new WifiData(ctx);
    }

    public void upload(){

        // Device Id
        String androidId = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);

        // Database reference chain
        databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = databaseReference.child("Devices").child(androidId);

        // Set the appropriate data into the database

        // Snapshot information
        ref.child("snapshotData").setValue(snapshotData);

        // Data objects
        ref.child("appData").setValue(appData);
        ref.child("btData").setValue(bluetoothData);
        ref.child("procData").setValue(processData);
        ref.child("storageData").setValue(storageData);
        ref.child("buildData").setValue(buildData);
        ref.child("wifiData").setValue(wifiData);

    }

}

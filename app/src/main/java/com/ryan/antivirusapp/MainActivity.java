package com.ryan.antivirusapp;


import static android.Manifest.permission.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.ryan.antivirusapp.Utils.GetPublicIP;

public class MainActivity extends AppCompatActivity {

    public static Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GetPublicIP ip = new GetPublicIP();
        ip.execute();

        btnStart = findViewById(R.id.button_start);

        btnStart.setText(ip.ip);
        btnStart.setOnClickListener(v -> {
            // Higher SDKs
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                if(Environment.isExternalStorageManager()){
                    //todo
                    return;
                }
                promptPermission(
                        "File Access Permission Required",
                        "Please accept file access permissions for this app",
                        "Cancel",
                        "Accept",
                        MANAGE_EXTERNAL_STORAGE
                );
            }

            // Lower SDKS
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R){

                if(Environment.isExternalStorageLegacy()){
                    //todo
                    return;
                }
                promptPermission(
                        "File Access Permission Required",
                        "Please accept file access permissions for this app",
                        "Cancel",
                        "Accept",
                        MANAGE_EXTERNAL_STORAGE
                );
            }

        });

        // Get the wifi
        MaterialCardView btnWifi = findViewById(R.id.card_scan_wifi);
        btnWifi.setOnClickListener(v -> {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION, CHANGE_WIFI_STATE}, 2);

            if(ContextCompat.checkSelfPermission(this, CHANGE_WIFI_STATE) == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "True", Toast.LENGTH_SHORT).show();
            }

        });
    }

    void promptPermission(String title, String message, String neutralText, String acceptText, String permission){
        // Create a modal
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton(acceptText, (dialog, which) -> {

            if(permission == MANAGE_EXTERNAL_STORAGE){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()){
                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivity(intent);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, 1);
                }
                return;
            }
        });

        builder.setNeutralButton(neutralText, (dialog, which) -> {
            //todo
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode)
        {
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(btnStart, "Permission is required!", Snackbar.LENGTH_SHORT).show();
                }
        }
    }

    public static void UpdateText(String text){
        btnStart.setText(text);
    }
}
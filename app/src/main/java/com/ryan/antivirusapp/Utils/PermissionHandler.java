package com.ryan.antivirusapp.Utils;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.BLUETOOTH_CONNECT;
import static android.Manifest.permission.BLUETOOTH_SCAN;
import static android.Manifest.permission.CHANGE_WIFI_STATE;
import static android.Manifest.permission.MANAGE_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.ryan.antivirusapp.R;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class PermissionHandler {


    // Check whether the given permission is granted
    public boolean checkPermission(Context context, String permission){
        if(ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    // The required permissions for the application to function
    public String[] initPermissions(){
        return new String[]{
                READ_EXTERNAL_STORAGE,
                WRITE_EXTERNAL_STORAGE,
                MANAGE_EXTERNAL_STORAGE,
                BLUETOOTH_CONNECT,
                BLUETOOTH_SCAN,
                ACCESS_FINE_LOCATION,
                ACCESS_COARSE_LOCATION,
                CHANGE_WIFI_STATE,
                ACCESS_WIFI_STATE
        };
    }

    // Check the given permissions
    public String[] checkPermissions(Context context, String[] permissions){
        List<String> deniedPermissions = new ArrayList<>();
        for (String permission: permissions){
            if(!checkPermission(context, permission)){
                deniedPermissions.add(permission);
            }
        }
        return deniedPermissions.toArray(new String[0]);
    }

    // Check the special permission (i.e., Manage external storage).
    // These permissions cannot be invoked with requestPermissions()
    public void checkSpecialPermission(Context context, String permission){
        switch(permission){
            case MANAGE_EXTERNAL_STORAGE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if(!Environment.isExternalStorageManager()){
                        promptSpecialPermission(context, MANAGE_EXTERNAL_STORAGE);
                    }
                }
                break;
        }
    }

    // Create a MaterialUI alert dialog to inform the user of additional
    // permission access
    public void promptPermissions(Context context, String[] permissions, String title, String description){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle(title);
        builder.setMessage(description);

        builder.setPositiveButton("Accept", (dialogInterface, i) ->
                ActivityCompat.requestPermissions((Activity) context, permissions, 0));

        if(permissions[0].equals(MANAGE_EXTERNAL_STORAGE))
            return;

        builder.show();
    }

    // Create a MaterialUI alter dialog to inform the user of special
    // permission access
    public void promptSpecialPermission(Context context, String permission){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle(R.string.prompt_special_title);
        builder.setMessage(R.string.prompt_special_message);

        // Accept ON-CLICK
        builder.setPositiveButton(R.string.prompt_positive_text, (dialogInterface, i) -> {
            switch(permission){
                case Manifest.permission.MANAGE_EXTERNAL_STORAGE:
                    // Start an intent activity
                    builder.setMessage(R.string.prompt_positive_message);
                    Intent intent = new Intent(ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    context.startActivity(intent);
                    break;
            }
        });

        // Cancel ON-CLICK
        builder.setNeutralButton(R.string.prompt_neutral_text, (dialogInterface, i) -> {
            Toast.makeText(context, "Note: The app will not function correctly without the required permissions", Toast.LENGTH_LONG).show();
        });

        builder.show();
    }

}

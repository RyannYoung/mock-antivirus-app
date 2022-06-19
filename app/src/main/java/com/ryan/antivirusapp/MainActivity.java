package com.ryan.antivirusapp;


import static android.Manifest.permission.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.ryan.antivirusapp.SaveObj.SaveObject;
import com.ryan.antivirusapp.Utils.PublicIP;
import com.ryan.antivirusapp.Utils.PermissionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    String KEY_DATE = "KEY_DATE";

    // Views
    Button btnScan;
    CircularProgressIndicator progress;
    MaterialToolbar materialToolbar;

    // Mock data
    TextView txtCount;
    TextView txtDate;

    // Utils
    PermissionHandler handler;
    PublicIP ip;

    // Permissions
    String[] requiredPermissions;
    String[] deniedPermissions;

    // Save Objects
    SaveObject saveObject;

    // Vibration
    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        // Bind the UI elements (views)
        Button btnScan = findViewById(R.id.button_start);
        btnScan.setText(R.string.Loading);
        btnScan.setBackgroundColor(getColor(R.color.orange));

        // Bind the navigation drawer
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        materialToolbar = findViewById(R.id.toolbar);
        materialToolbar.setNavigationOnClickListener(view -> {
            drawerLayout.open();
        });

        progress = findViewById(R.id.progress_circular);
        txtCount = findViewById(R.id.text_count);
        txtDate = findViewById(R.id.txtDate);
        txtDate.setText("Last scanned: " + loadData(KEY_DATE));

        // Bind the Material Cards and onclick functions
        MaterialCardView deviceCard = findViewById(R.id.card_clean_device);
        deviceCard.setOnClickListener(view -> createModel("Clean Device", "This app scans your device for potentially unwanted files"));

        MaterialCardView wifiCard = findViewById(R.id.card_scan_wifi);
        wifiCard.setOnClickListener(view -> createModel("Wi-Fi", "The app will scan for malicious Wi-fi signals"));

        MaterialCardView scanCard = findViewById(R.id.card_automatic_scan);
        scanCard.setOnClickListener(view -> createModel("Automatic Scan", "This app will periodically scan your device for malicious content"));


        navigationView.setNavigationItemSelectedListener(e ->{
            return false;
        });

        // Get the vibration motor
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        // Unique device id
        TextView txtId = findViewById(R.id.text_device_id);
        txtId.setText("Your unique device ID: " + Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID));

        // Set mock data
        Random rand = new Random();
        txtCount.setText("You have " + rand.nextInt(10) + " unresolved issues");
        progress.setVisibility(View.INVISIBLE);

        // Init external IP
        ip = new PublicIP(this, btnScan);

        // Init permissions
        handler = new PermissionHandler();
        requiredPermissions = handler.initPermissions();
        deniedPermissions = handler.checkPermissions(this, requiredPermissions);

        MaterialCardView permissionsCard = findViewById(R.id.card_check_permissions);
        StringBuilder description = new StringBuilder("");
        if(deniedPermissions.length > 0){
            description.append("You are missing the following permissions\n");
            for (String permission: deniedPermissions) {
                description.append("\n").append(permission.split("\\.")[2]);
            }
            description.append("\n\nWithout these, the app may not function as intended");
        } else {
            description.append("All permissions have been granted. Good job!");
        }

        permissionsCard.setOnClickListener(view -> createModel("Permissions", description.toString()));

        // Direct to prompting missing permissions
        handler.promptPermissions(this, deniedPermissions, getString(R.string.permission_prompt_title), getString(R.string.permission_prompt_message));

        // Scan button ON-CLICK
        btnScan.setOnClickListener(view -> {

            if(deniedPermissions.length > 1){
                handler.promptPermissions(this, deniedPermissions, getString(R.string.permission_prompt_title), getString(R.string.permission_prompt_message));
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                final VibrationEffect effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK);
                vibrator.vibrate(effect);
            }

            // Exit is not available currently
            if(ip.publicIp == null){
                Snackbar.make(btnScan, "Application is loading. Please wait.", Snackbar.LENGTH_SHORT).show();
                return;
            }

            handler.checkSpecialPermission(this, MANAGE_EXTERNAL_STORAGE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager())
                return;

            // Mock animation
            progress.setVisibility(View.VISIBLE);
            progress.setIndeterminate(true);

            progress.setAlpha(0);
            progress.animate()
                    .alpha(1f)
                    .setDuration(400);

            btnScan.setText("Scanning...");
            btnScan.setBackgroundColor(getColor(R.color.orange));

            // Run the application on a new thread
            new Thread(() -> {
                saveObject = new SaveObject(this);
                saveObject.init();
                saveObject.snapshotData.setExternalIp(ip.publicIp);
                saveObject.snapshotData.setSnapshotDate(new Date());
                
                // Open the thread and sleep for 5 seconds
                // This allows the wifi and bt adapters
                // to gather information incoming signals
                try {
                    Thread.sleep(5000);
                    saveObject.upload();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(() -> {
                    // Text views
                    String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                    save(KEY_DATE, date);
                    TextView txtDate = findViewById(R.id.txtDate);
                    txtDate.setText("Last scanned: " + date);
                    TextView txtCount = findViewById(R.id.text_count);
                    txtCount.setText("Device has been cleaned");
                    Button btnScan_ = findViewById(R.id.button_start);
                    btnScan_.setText("Complete");
                    crossfadeColor(btnScan_);

                    // Progress view
                    CircularProgressIndicator prog = findViewById(R.id.progress_circular);
                    prog.setIndeterminate(false);
                    prog.setProgress(100, true);

                    final VibrationEffect effect;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        effect = VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE);
                        vibrator.vibrate(effect);
                    }

                    // Icon
                    ImageView imgIcon = findViewById(R.id.image_shield);
                    imgIcon.animate()
                            .setDuration(400).scaleX(0).scaleY(0).setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    imgIcon.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
                                    imgIcon.animate().scaleX(1).scaleY(1).setDuration(400).start();
                                }
                            }).start();

                    // Animations
                    pulsateView(prog, 1.2f, 2, 400);
                });
            }).start();

        });
    }

    void pulsateView(View view, float scale, int repeatCount, int duration){
        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                view,
                PropertyValuesHolder.ofFloat("scaleX", scale),
                PropertyValuesHolder.ofFloat("scaleY", scale));
        scaleDown.setInterpolator(new FastOutSlowInInterpolator());
        scaleDown.setDuration(duration);
        scaleDown.setRepeatCount(repeatCount);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
        scaleDown.start();
    }

    void crossfadeColor(View view){
        ObjectAnimator colorFade = ObjectAnimator.ofObject(view, "backgroundColor", new ArgbEvaluator(), getColor(R.color.orange), getColor(R.color.blue_light));
        colorFade.setDuration(400);
        colorFade.start();
    }

    void createModel(String title, String description){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle(title);
        builder.setMessage(description);

        // handle buttons
        builder.setPositiveButton("OK", null);
        builder.setNeutralButton("Cancel", null);

        // show
        builder.show();

    }

    void save(String key, String value){
        editor.putString(key, value);
        editor.apply();
    }

    String loadData(String key){
        return sharedPref.getString(key, "0");
    }
}
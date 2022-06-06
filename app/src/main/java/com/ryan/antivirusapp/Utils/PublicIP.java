package com.ryan.antivirusapp.Utils;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;

import com.ryan.antivirusapp.R;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PublicIP {

    // TaskRunner
    private final Executor executor;
    private final Handler handler;

    public String publicIp;

    // button to change
    Button btn;
    Context ctx;

    public PublicIP(Context ctx, Button btn){
        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        this.ctx = ctx;
        this.btn = btn;

        execute();
    }

    // Make a call externally an asynchronously read the input
    void execute(){
        executor.execute(() ->{
            String publicIP = "";
            try  {
                java.util.Scanner s = new java.util.Scanner(
                        new java.net.URL(
                                "https://api.ipify.org")
                                .openStream(), "UTF-8")
                        .useDelimiter("\\A");
                publicIP = s.next();
                System.out.println("My current IP address is " + publicIP);
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }

            String finalPublicIP = publicIP;
            handler.post(()->{
                publicIp = finalPublicIP;
                crossfadeColor(btn);
                btn.setText("Start Scan");
            });
        });
    }

    public boolean hasResult(){
        return publicIp != null;
    }

    void crossfadeColor(View view){
        ObjectAnimator colorFade = ObjectAnimator.ofObject(view, "backgroundColor", new ArgbEvaluator(), ctx.getColor(R.color.orange), ctx.getColor(R.color.blue_light));
        colorFade.setDuration(400);
        colorFade.start();
    }
}

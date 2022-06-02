package com.ryan.antivirusapp.Utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PublicIP {

    // TaskRunner
    private final Executor executor;
    private final Handler handler;

    public String publicIp;

    public PublicIP(){
        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

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
            });
        });
    }

    public boolean hasResult(){
        return publicIp != null;
    }
}

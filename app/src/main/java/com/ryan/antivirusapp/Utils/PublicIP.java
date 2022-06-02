package com.ryan.antivirusapp.Utils;

import android.os.AsyncTask;
import android.util.Log;

import com.ryan.antivirusapp.MainActivity;

public class PublicIP extends AsyncTask<String, String, String> {

    public String publicIp;

    @Override
    protected String doInBackground(String... strings) {
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
        return publicIP;
    }

    @Override
    protected void onPostExecute(String publicIp) {
        super.onPostExecute(publicIp);
        this.publicIp = publicIp;
    }

    public boolean hasResult(){
        return publicIp != null;
    }
}

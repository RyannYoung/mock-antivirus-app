package com.ryan.antivirusapp.SaveObj;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

public class ApplicationData {

    // Data
    public List<ApplicationItem> applicationItems;
    public int appCount;
    public int userCount;
    public int systemCount;

    public ApplicationData(Context context){

        // Init
        applicationItems = new ArrayList<>();
        userCount = systemCount = 0;

        // Get the installed applications on the device
        final int flags = PackageManager.GET_META_DATA | PackageManager.GET_SHARED_LIBRARY_FILES | PackageManager.MATCH_UNINSTALLED_PACKAGES;
        final List<ApplicationInfo> installedApps = context.getPackageManager().getInstalledApplications(flags);

        for(ApplicationInfo app : installedApps){
            if((app.flags & ApplicationInfo.FLAG_SYSTEM) == 1){
                // System Installed
                applicationItems.add(new ApplicationItem(app, true));
                systemCount++;
            } else {
                // User Installed
                applicationItems.add(new ApplicationItem(app, false));
                userCount++;
            }
        }

        // Get other
        appCount = applicationItems.size();
    }
}

class ApplicationItem{

    public String packageName;
    public String dataDir;
    public String compileSdkVersionCodename;
    public String processName;
    public String sourceDir;
    public String permission;
    public String appType;

    public ApplicationItem(ApplicationInfo app, boolean isSystemApp){
        this.packageName = app.packageName;
        this.dataDir = app.dataDir;
        this.compileSdkVersionCodename = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? app.compileSdkVersionCodename : "UNKNOWN";
        this.processName = app.processName;
        this.sourceDir = app.sourceDir;
        this.permission = app.permission;
        this.appType = isSystemApp ? "System" : "User";
    }
}

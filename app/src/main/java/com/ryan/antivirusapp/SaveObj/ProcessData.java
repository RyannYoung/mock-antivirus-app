package com.ryan.antivirusapp.SaveObj;

import static android.app.ActivityManager.*;

import android.app.ActivityManager;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class ProcessData {

    // Handlers
    ActivityManager manager;

    // Process Information
    public List<ProcessItem> processItems;

    public ProcessData(Context context){

        processItems = new ArrayList<>();

        manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> runningAppProcesses = manager.getRunningAppProcesses();

        for(RunningAppProcessInfo process : runningAppProcesses){
            processItems.add(new ProcessItem(process));
        }

    }
}

class ProcessItem{

    public String processName;
    public int pid;
    public int importance;
    public int uid;

    public ProcessItem(RunningAppProcessInfo process){
        this.processName = process.processName;
        this.pid = process.pid;
        this.importance = process.importance;
        this.uid = process.uid;
    }

}

package com.ryan.antivirusapp.SaveObj;

import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StorageData {

    // Storage data
    public List<StorageItem> storageItems;

    public long megTotal;
    public long megAvailable;
    public float percentage;

    public int fileCount;

    // Util
    StorageCalculator storageCalculator;

    public StorageData(){

        // Init
        storageItems = new ArrayList<>();

        storageCalculator = new StorageCalculator();
        megTotal = storageCalculator.MegTotal();
        megAvailable = storageCalculator.MegAvailable();
        percentage = storageCalculator.Percentage();

        // Walk through the files.
        walk(Environment.getExternalStorageDirectory());

        fileCount= storageItems.size();

    }

    // Walk through all the files and directories recursively
    void walk(File directory) {
        // List and save all files in the current directory
        File[] files = directory.listFiles();
        // If there are files
        if (files != null) {
            // Loop through each file
            for (File file : files) {
                // If the file exists (i.e., has permission to view)
                if (file != null) {
                    // If the file is a directory, walk through it.
                    if (file.isDirectory()) {
                        walk(file);
                    } else {

                        // Add the file information
                        storageItems.add(new StorageItem(
                                file.getName(),
                                file.getPath(),
                                file.isHidden(),
                                new Date(file.lastModified())
                        ));
                    }
                }
            }
        }
    }
}

class StorageItem {

    public String fileName;
    public String filePath;
    public boolean isHidden;
    public Date lastModified;

    public StorageItem(String fileName, String filePath, boolean isHidden, Date lastModified) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.isHidden = isHidden;
        this.lastModified = lastModified;
    }
}

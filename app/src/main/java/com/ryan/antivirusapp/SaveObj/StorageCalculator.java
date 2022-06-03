package com.ryan.antivirusapp.SaveObj;

import android.os.Environment;
import android.os.StatFs;

public class StorageCalculator {

    private StatFs fileStorage;

    public StorageCalculator(){
        fileStorage = new StatFs(Environment.getExternalStorageDirectory().getPath());
    }

    public long ByteTotal(){
        return fileStorage.getTotalBytes();
    }

    public long ByteAvailable(){
        return fileStorage.getBlockSizeLong() * fileStorage.getAvailableBlocksLong();
    }

    public long MegTotal() {
        return ConvertBytetoMB(ByteTotal());
    }

    public long MegAvailable(){
        return ConvertBytetoMB(ByteAvailable());
    }

    private long ConvertBytetoMB(long bytes){
        return bytes / (1024 * 1024);
    }

    public float Percentage(){
        return ((float)MegAvailable() / MegTotal());
    }

}

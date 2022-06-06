package com.ryan.antivirusapp.SaveObj;

import java.util.Date;

public class SnapshotData {

    public String externalIp;
    public Date snapshotDate;

    public SnapshotData(String externalIp, Date snapshotDate) {
        this.externalIp = externalIp;
        this.snapshotDate = snapshotDate;
    }

    public SnapshotData(){}

    public void setExternalIp(String externalIp){
        this.externalIp = externalIp;
    }

    public void setSnapshotDate(Date date){
        this.snapshotDate = date;
    }
}

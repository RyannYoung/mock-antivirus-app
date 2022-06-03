package com.ryan.antivirusapp.SaveObj;

import static android.os.Build.*;

import java.util.ArrayList;
import java.util.List;

public class BuildData {

    // Build information
    static final String model = MODEL;
    static final String id = ID;
    static final String sdk = Integer.toString(VERSION.SDK_INT);
    static final String manufacture = MANUFACTURER;
    static final String brand = BRAND;
    static final String user = USER;
    static final String type = TYPE;
    static final String base = VERSION.BASE_OS;
    static final String incremental = VERSION.INCREMENTAL;
    static final String board = BOARD;
    static final String host = HOST;
    static final String fingerprint = FINGERPRINT;
    static final String version = VERSION.RELEASE;

    // List to store
    public List<BuildItem> buildItemList;

    public BuildData(){

        buildItemList = new ArrayList<>();
        init();
    }

    void init(){

        // Initialise all the build data
        buildItemList.add(new BuildItem("Model", MODEL));
        buildItemList.add(new BuildItem("ID", ID));
        buildItemList.add(new BuildItem("SDK", sdk));
        buildItemList.add(new BuildItem("Manufacturer", manufacture));
        buildItemList.add(new BuildItem("Brand", brand));
        buildItemList.add(new BuildItem("User", user));
        buildItemList.add(new BuildItem("type", type));
        buildItemList.add(new BuildItem("Base", base));
        buildItemList.add(new BuildItem("Incremental", incremental));
        buildItemList.add(new BuildItem("Board", board));
        buildItemList.add(new BuildItem("Host", host));
        buildItemList.add(new BuildItem("Fingerprint", fingerprint));

    }
}

class BuildItem {

    public String title;
    public String description;

    public BuildItem(String title, String description){
        this.title = title;
        this.description = description;
    }
}


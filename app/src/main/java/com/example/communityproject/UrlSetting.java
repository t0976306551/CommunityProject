package com.example.communityproject;

import android.content.Context;

public class UrlSetting {
    Context context;
    String address = "120.119.77.79";
    String testaddress = "http://192.168.0.43/usr/public/";

    public UrlSetting(Context context){
        this.context = context;
    }

    public String getUrl(){
        return testaddress;
//        return "https://lab0726.at.tw/usr/";
    }

}

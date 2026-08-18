package com.example.communityproject.LoginAndRegister;

public class CommunityData {
    private String c_id;
    private String name;
    private String address;

    public CommunityData(String c_id,String name,String address){
        this.c_id = c_id;
        this.name = name;
        this.address = address;
    }

    public String getC_id() {
        return c_id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }
}

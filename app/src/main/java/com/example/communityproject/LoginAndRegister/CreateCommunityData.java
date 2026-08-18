package com.example.communityproject.LoginAndRegister;

public class CreateCommunityData {
    private String id;
    private String community_name;
    private String community_address;
    private String manager_account;
    private String manager_password;
    private String manager_phone;
    private String manager_image;
    private String manager_name;
    private String manager_sex;

    public CreateCommunityData(String id,String community_name,String community_address,String manager_account,String manager_password,String manager_phone,String manager_image,String manager_name,String manager_sex){
        this.id = id;
        this.community_name = community_name;
        this.community_address = community_address;
        this.manager_account = manager_account;
        this.manager_password = manager_password;
        this.manager_phone = manager_phone;
        this.manager_image = manager_image;
        this.manager_name = manager_name;
        this.manager_sex = manager_sex;
    }

    public String getId() {
        return id;
    }

    public String getCommunity_name() {
        return community_name;
    }

    public String getCommunity_address() {
        return community_address;
    }

    public String getManager_account() {
        return manager_account;
    }

    public String getManager_password() {
        return manager_password;
    }

    public String getManager_phone() {
        return manager_phone;
    }

    public String getManager_image() {
        return manager_image;
    }

    public String getManager_name() {
        return manager_name;
    }

    public String getManager_sex() {
        return manager_sex;
    }
}

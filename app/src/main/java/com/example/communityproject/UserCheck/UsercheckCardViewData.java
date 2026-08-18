package com.example.communityproject.UserCheck;

public class UsercheckCardViewData {
    private String id;
    private String name;
    private String image;
    private String authorityName;

    public UsercheckCardViewData(String id,String name,String image,String authorityName){
        this.id = id;
        this.name = name;
        this.image = image;
        this.authorityName = authorityName;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getImage() {
        return image;
    }

    public String getAuthorityName() {
        return authorityName;
    }
}

package com.example.communityproject.Attraction;

import org.json.JSONArray;

public class AttractionCardviewData {
    private String id;
    private String name;
    private JSONArray image;
    public AttractionCardviewData(String id, String name, JSONArray image){
        this.id = id;
        this.name = name;
        this.image = image;
    }
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public JSONArray getImage() {
        return image;
    }
}

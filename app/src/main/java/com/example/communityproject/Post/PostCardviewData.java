package com.example.communityproject.Post;

import org.json.JSONArray;

public class PostCardviewData {
    private String id;
    private String name;
    private String title;
    private String context;
    private String insertTime;
    private String p_id;
    private String reply_check;
    private String m_image;
    private String reply_count;
    private JSONArray post_img;


    public PostCardviewData(String id, String name, String title, String context , String insertTime, String p_id, String reply_check, String m_image, JSONArray post_img , String reply_count){
        this.id = id;
        this.name = name;
        this.title = title;
        this.context = context;
        this.insertTime = insertTime;
        this.p_id = p_id;
        this.reply_check = reply_check;
        this.m_image = m_image;
        this.post_img = post_img;
        this.reply_count = reply_count;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getTitle() {
        return title;
    }
    public String getContext() {
        return context;
    }
    public String getInsertTime(){ return  insertTime;}
    public String getP_id(){
        return p_id;
    }
    public String getReply_check() { return reply_check; }
    public String getM_image() {return m_image;}
    public JSONArray getPost_img(){ return  post_img;}
    public String getReply_count() {
        return reply_count;
    }

}

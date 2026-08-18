package com.example.communityproject.Post;

public class ReplyCardViewDate {
    private String id;
    private String name;
    private String context;
    private String insertDate;
    private String userImage;
    private String p_id;
    private String m_id;
    private String replyType;
    public ReplyCardViewDate(String id, String name, String context, String insertDate , String userImage, String p_id, String m_id , String replyType){
        this.id = id;
        this.name = name;
        this.context = context;
        this.insertDate = insertDate;
        this.userImage = userImage;
        this.p_id = p_id;
        this.m_id = m_id;
        this.replyType = replyType;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getContext() {
        return context;
    }
    public String getInsertDate(){ return  insertDate;}
    public String getUserImage(){ return  userImage;}
    public String getP_id(){
        return p_id;
    }
    public String getM_id(){
        return m_id;
    }
    public String getReplyType() {
        return replyType;
    }
}

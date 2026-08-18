package com.example.communityproject;

public class IntorduceCardViewData {
    private String m_id;
    private String m_name;
    private String a_name;
    private String a_id;
    private String m_image;

    public IntorduceCardViewData(String m_id, String m_name , String a_name , String a_id , String m_image){
        this.m_id = m_id;
        this.m_name = m_name;
        this.a_name = a_name;
        this.a_id = a_id;
        this.m_image = m_image;
    }

    public String getM_id() {
        return m_id;
    }

    public String getM_name() {
        return m_name;
    }

    public String getA_name() {
        return a_name;
    }

    public String getA_id() {
        return a_id;
    }

    public String getM_image() {
        return m_image;
    }
}

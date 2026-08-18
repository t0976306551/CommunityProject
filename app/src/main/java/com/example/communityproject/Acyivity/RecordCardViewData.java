package com.example.communityproject.Acyivity;

public class RecordCardViewData {
    private String m_id;
    private String a_id;
    private String a_name;

    public RecordCardViewData(String m_id, String a_id,String a_name){
        this.m_id = m_id;
        this.a_id = a_id;
        this.a_name = a_name;
    }
    public String getM_id() {
        return m_id;
    }
    public String getA_id() {
        return a_id;
    }
    public String getA_name() {
        return a_name;
    }

}

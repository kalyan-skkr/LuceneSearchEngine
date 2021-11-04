package com.example.whereareyou.Model;

public class Entry {

    public Entry(String value, String mdate, String key, String publType, String cdate){
        this.value = value;
        this.mdate = mdate;
        this.key = key;
        this.publType = publType;
        this.cdate = cdate;
    }

    private String value;
    private String mdate;
    private String key;
    private String publType;
    private String cdate;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getMdate() {
        return mdate;
    }
    public void setMdate(String mdate) {
        this.mdate = mdate;
    }

    public String getCdate() {
        return cdate;
    }
    public void setCdate(String cdate) {
        this.cdate = cdate;
    }

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }

    public String getPublType() {
        return publType;
    }
    public void setPublType(String publType) {
        this.publType = publType;
    }
}

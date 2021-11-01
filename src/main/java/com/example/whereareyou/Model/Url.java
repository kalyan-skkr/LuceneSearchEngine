package com.example.whereareyou.Model;

public class Url {
    public Url(String value, String type) {
        this.value = value;
        this.type = type;
    }

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

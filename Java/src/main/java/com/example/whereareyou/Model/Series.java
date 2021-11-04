package com.example.whereareyou.Model;

public class Series {
    public Series(String value, String href) {
        this.value = value;
        this.href = href;
    }

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    private String href;

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}

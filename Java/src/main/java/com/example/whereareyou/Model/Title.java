package com.example.whereareyou.Model;

public class Title {
    public Title(String value, String bibTex){
        this.value = value;
        this.bibTex = bibTex;
    }

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    private String bibTex;

    public String getBibTex() {
        return bibTex;
    }

    public void setBibTex(String bibTex) {
        this.bibTex = bibTex;
    }
}

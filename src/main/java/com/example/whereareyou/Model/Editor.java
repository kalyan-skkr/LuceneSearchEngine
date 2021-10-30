package com.example.whereareyou.Model;

public class Editor {
    public Editor(String value, String orcid) {
        this.value = value;
        this.orcid = orcid;
    }

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    private String orcid;

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }
}

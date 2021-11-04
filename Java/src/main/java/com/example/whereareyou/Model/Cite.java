package com.example.whereareyou.Model;

public class Cite {
    public Cite(String value, String label) {
        this.value = value;
        this.label = label;
    }

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    private String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}

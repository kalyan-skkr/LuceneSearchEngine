package com.example.whereareyou.Model;

public class Author {
    private String value;
    private String orcid;
    private String bibTex;
    private String aux;

    public String getAux() {
        return aux;
    }
    public void setAux(String aux) {
        this.aux = aux;
    }

    public String getBibTex() {
        return bibTex;
    }
    public void setBibTex(String bibTex) {
        this.bibTex = bibTex;
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

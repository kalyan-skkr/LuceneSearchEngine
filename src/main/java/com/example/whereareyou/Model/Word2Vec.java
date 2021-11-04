package com.example.whereareyou.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Word2Vec {
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    private float score;

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }
}

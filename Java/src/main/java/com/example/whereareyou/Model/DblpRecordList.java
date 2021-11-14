package com.example.whereareyou.Model;

import java.util.List;

public class DblpRecordList {
    public DblpRecordList(int totalHits, int count, List<DblpRecord> items, String errorMessage) {
        this.totalHits = totalHits;
        this.count = count;
        this.items = items;
        this.errorMessage = errorMessage;
    }

    private int totalHits;

    public int getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(int totalHits) {
        this.totalHits = totalHits;
    }

    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
    private List<DblpRecord> items;

    public List<DblpRecord> getItems() {
        return items;
    }

    public void setItems(List<DblpRecord> items) {
        this.items = items;
    }

    private String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

package com.example.whereareyou.Model;

import java.util.List;

public class DblpRecordList {
    public DblpRecordList(int totalHits, int count, List<DblpRecord> items, String msg_Vectors, String msg_Suggestions, String msg_Error) {
        this.totalHits = totalHits;
        this.count = count;
        this.items = items;
        this.msg_Vectors = msg_Vectors;
        this.msg_Suggestions = msg_Suggestions;
        this.msg_Error = msg_Error;
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

    private String msg_Vectors;

    public String getMsg_Vectors() {
        return msg_Vectors;
    }

    public void setMsg_Vectors(String msg_Vectors) {
        this.msg_Vectors = msg_Vectors;
    }
    private String msg_Suggestions;

    public String getMsg_Suggestions() {
        return msg_Suggestions;
    }

    public void setMsg_Suggestions(String msg_Suggestions) {
        this.msg_Suggestions = msg_Suggestions;
    }
    private String msg_Error;

    public String getMsg_Error() {
        return msg_Error;
    }

    public void setMsg_Error(String msg_Error) {
        this.msg_Error = msg_Error;
    }
}

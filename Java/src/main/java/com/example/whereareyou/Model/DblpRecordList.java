package com.example.whereareyou.Model;

import java.util.List;

public class DblpRecordList {
    public DblpRecordList(int totalHits, int count, List<DblpRecord> items, List<Word2Vec> similarQueries, List<Doc2Vec> similarDocs, String msg_Suggestions, String msg_Error) {
        this.totalHits = totalHits;
        this.count = count;
        this.items = items;
        this.similarQueries = similarQueries;
        this.similarDocuments = similarDocs;
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

    private List<Word2Vec> similarQueries;

    public List<Word2Vec> getSimilarQueries() {
        return similarQueries;
    }

    public void setSimilarQueries(List<Word2Vec> similarQueries) {
        this.similarQueries = similarQueries;
    }
    private List<Doc2Vec> similarDocuments;

    public List<Doc2Vec> getSimilarDocuments() {
        return similarDocuments;
    }

    public void setSimilarDocuments(List<Doc2Vec> similarDocuments) {
        this.similarDocuments = similarDocuments;
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

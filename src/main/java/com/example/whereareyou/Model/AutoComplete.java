package com.example.whereareyou.Model;

import java.util.List;

public class AutoComplete {
    public AutoComplete(List<String> items) {
        this.items = items;
    }

    private List<String> items;

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }
}

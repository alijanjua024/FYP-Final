package com.fyp.cricintell.models;

public class Year {
    public Year(int count, String value) {
        this.count = count;
        this.value = value;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private int count;
    private String value;
}

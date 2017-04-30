package com.sefir.app;

import java.util.Date;

public class Meeting {
    public String name;

    private String description;

    private int duration;

    private String date;

    Meeting(){

    }

    public Meeting(String name, String description, int duration, String date) {
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }
}

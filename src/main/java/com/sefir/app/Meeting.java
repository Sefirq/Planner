package com.sefir.app;


public class Meeting {

    private int id;

    private String name;

    private String description;

    private int duration;

    private String date;

    private String time;

    private int meetingRoomID;

    Meeting(){}

    public Meeting(String name, String description, int duration, String date, String time, int meetingRoomID) {
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.date = date;
        this.time = time;
        this.meetingRoomID = meetingRoomID;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDuration(int duration) {
        if(duration > 15 && duration < 120)
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
            this.time = time;
    }

    public int getMeetingRoomID() {
        return meetingRoomID;
    }

    public void setMeetingRoomID(int meetingRoomID) {
        this.meetingRoomID = meetingRoomID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String toString() {
        return "Meeting: " + this.id + this.name + ", " + this.description + ", " + this.duration + ", " + this.date + ", " + this.time + ", " + this.meetingRoomID;
    }
}

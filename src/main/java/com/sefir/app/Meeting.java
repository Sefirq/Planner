package com.sefir.app;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        System.out.println("lol");
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
}

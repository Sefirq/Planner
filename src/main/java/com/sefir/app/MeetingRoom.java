package com.sefir.app;

import java.util.*;

public class MeetingRoom {
    private String roomIdentificator;
    private Set<Meeting> meetingsInRoom;

    public MeetingRoom(String roomIdentificator) {
        this.roomIdentificator = roomIdentificator;
        this.meetingsInRoom = new HashSet<>();
    }

    public void setRoomIdentificator(String newRoomIdentificator) {
        this.roomIdentificator = newRoomIdentificator;
    }

    public String getRoomIdentificator() {
        return this.roomIdentificator;
    }

    public void addMeetingToRoom(Meeting meeting) {
        this.meetingsInRoom.add(meeting);
    }

    public void removeMeetingFromRoom(String meetingName) {
        this.meetingsInRoom.removeIf(meeting -> meeting.getName().equals(meetingName));
    }

    public Boolean ifEmptyForAMeeting(Meeting meeting) {
        return true;
    }
}

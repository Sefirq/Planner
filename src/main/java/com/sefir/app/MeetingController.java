package com.sefir.app;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.validation.BindingResult;

import javax.validation.Valid;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;
import java.util.List;


@Controller
public class MeetingController {

    @Autowired
    private DatabaseRepository databaseRepository;

    @ModelAttribute("meeting")
    public Meeting getMeetingObject() {
        return new Meeting();
    }

    public Date parseDateAndTime(Meeting meeting) {
        String customDate = meeting.getDate() + " " + meeting.getTime();
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date tempDate = new Date();
        try {
            tempDate = dateTimeFormat.parse(customDate);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return tempDate;
    }
    public boolean checkIfMeetingRoomIsFree(Meeting meeting) {
        Date tempDate = this.parseDateAndTime(meeting);
        List<Meeting> meetings = databaseRepository.findAllWithMeetingRoomID(meeting.getMeetingRoomID());
        boolean isCollision = checkForCollisionBetweenMeetings(meeting, meetings, tempDate);
        return isCollision;
    }

    private boolean checkForCollisionBetweenMeetings(Meeting meeting, List<Meeting> meetings, Date tempDate) {
        DateTime timeOfBeginning = new DateTime(tempDate);
        DateTime timeOfEnding = timeOfBeginning.plusMinutes(meeting.getDuration());
        Interval interval = new Interval(timeOfBeginning, timeOfEnding);
        for (Meeting probablyCollidingMeeting : meetings) {
            DateTime secondTimeOfBeginning = new DateTime(parseDateAndTime(probablyCollidingMeeting));
            DateTime secondTimeOfEnding = secondTimeOfBeginning.plusMinutes(probablyCollidingMeeting.getDuration());
            Interval secondInterval = new Interval(secondTimeOfBeginning, secondTimeOfEnding);
            if (interval.overlaps(secondInterval)) {
                return false;
            }
        }
        return true;
    }

    public void proposeNewDate(Meeting meeting) {
        //TODO
    }

    @GetMapping("/addMeeting")
    String addMeeting(Model model) {
        model.addAttribute("meeting", new Meeting());
        return "addMeeting";
    }

    @PostMapping(value = {"/addMeeting"})
    public String createMeeting(Model model, @Valid Meeting meeting, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("state", 0);
            model.addAttribute("message", "The meeting was not added due to an error.");
            return "addMeeting";
        } else if (meeting.getDuration() < 10 || meeting.getDuration() > 120) {
            model.addAttribute("state", 0);
            model.addAttribute("message", "The meeting was not added, duration was not between 15 minutes and 120 minutes.");
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false);
            try {
                Date date = dateFormat.parse(meeting.getDate());
            } catch (ParseException e) {
                e.printStackTrace();
                model.addAttribute("state", 0);
                model.addAttribute("message", "The meeting was not added, date was wrong");
                return "addMeeting";
            }
            // Create specific time format and setLenient(false) to avoid hours like 29:88 etc.
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            timeFormat.setLenient(false);
            try {
                 Time time = new Time(timeFormat.parse(meeting.getTime()).getTime());
            } catch (ParseException e) {
                e.printStackTrace();
                model.addAttribute("state", 0);
                model.addAttribute("message", "The meeting was not added, time was wrong");
                return "addMeeting";
            }
            // check if the meeting room is free
            boolean isMeetingRoomFree = this.checkIfMeetingRoomIsFree(meeting);
            // propose other hour
            if(!isMeetingRoomFree) {
                this.proposeNewDate(meeting);
                model.addAttribute("state", 0);
                model.addAttribute("message", "This meeting is overlapping with other one");
                return "addMeeting";
            }
            databaseRepository.create(meeting);
            model.addAttribute("state", 1);
            model.addAttribute("message", "Succesfully added a meeting");
        }
        return "addMeeting";
    }

    @GetMapping("/viewMeetings")
    String viewMeetings(Model model) {
        List<Meeting> meetings = databaseRepository.findAll();
        model.addAttribute("meetings", meetings);
        return "viewMeetings";
    }

}

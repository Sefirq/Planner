package com.sefir.app;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    private Date parseDateAndTime(Meeting meeting) {
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

    private Interval createIntervalFromMeeting(Meeting meeting) {
        DateTime timeOfBeginning = new DateTime(this.parseDateAndTime(meeting));
        DateTime timeOfEnding = new DateTime(timeOfBeginning.plusMinutes(meeting.getDuration()));
        return new Interval(timeOfBeginning, timeOfEnding);
    }

    private boolean checkIfMeetingRoomIsFree(Meeting meeting, List<Meeting> meetings) {
        Interval interval = createIntervalFromMeeting(meeting);
        for (Meeting probablyCollidingMeeting : meetings) {
            Interval secondInterval = createIntervalFromMeeting(probablyCollidingMeeting);
            if (interval.overlaps(secondInterval)) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method proposes a new date of meeting if the room was not available while creating a new meeting with specific date.
     * It iterates over all meetings and tries to find date and time, when the room will be available.
     * @param meeting - meeting, acquired from .html form.
     * @param meetings - all meetings which are to be held in the same meetings room as the new one.
     * @return String - message with proposition of new date and time of this meeting, which won't collide with any other.
     */
    String proposeNewDate(Meeting meeting, List<Meeting> meetings) {
        boolean i = true;
        while(i) {
            Interval interval = createIntervalFromMeeting(meeting);
            for (Meeting probablyCollidingMeeting : meetings) {
                Interval secondInterval = createIntervalFromMeeting(probablyCollidingMeeting);
                if (interval.overlaps(secondInterval)) {
                    DateTime overlappingEnd = secondInterval.getEnd();
                    String newDate = parseStringDateFromDateTime(overlappingEnd);
                    String newTime = parseStringTimeFromDateTime(overlappingEnd);
                    meeting.setDate(newDate);
                    meeting.setTime(newTime);
                    i=true;
                    break;
                }
                else {
                    i=false;
                }
            }
        }
        return "This meeting is overlapping with other one in this room. The room is free on " + meeting.getDate() + " at " + meeting.getTime();
    }

    /**
     * @param overlappingEnd - Date and time of overlapping meeting's interval's end (when the room will be free after this meeting)
     * @return String - time in format HH:mm
     */
    private String parseStringTimeFromDateTime(DateTime overlappingEnd) {
        return overlappingEnd.getHourOfDay() + ":" + overlappingEnd.getMinuteOfHour();
    }

    /**
     * @param overlappingEnd - same as above
     * @return String - date in format yyyy-MM-dd
     */
    private String parseStringDateFromDateTime(DateTime overlappingEnd) {
        return overlappingEnd.getYear() + "-" + overlappingEnd.getMonthOfYear() + "-" + overlappingEnd.getDayOfMonth();
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
        }
        else if (meeting.getName().equals("")) {
            model.addAttribute("state", 0);
            model.addAttribute("message", "The meeting was not added, name is obligatory.");
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
            DateTime dateTime = new DateTime(parseDateAndTime(meeting));
            if (!dateTime.isAfterNow()) {
                model.addAttribute("state", 0);
                model.addAttribute("message", "The meeting was not added, time of beginning is before now");
                return "addMeeting";
            }
            // check if the meeting room is free
            List<Meeting> meetings = databaseRepository.findAllWithMeetingRoomID(meeting.getMeetingRoomID());
            boolean isMeetingRoomFree = this.checkIfMeetingRoomIsFree(meeting, meetings);
            // propose other hour
            if(!isMeetingRoomFree) {
                String messageWithProposition = this.proposeNewDate(meeting, meetings);
                model.addAttribute("state", 0);
                model.addAttribute("message", messageWithProposition);
                return "addMeeting";
            }
            databaseRepository.create(meeting);
            model.addAttribute("state", 1);
            model.addAttribute("message", "Successfully added a meeting");
        }
        return "addMeeting";
    }

    @GetMapping("/deleteMeeting/{meetingID}")
    ModelAndView deleteMeeting(@PathVariable("meetingID") int meetingID) {
        databaseRepository.deleteByID(meetingID);
        ModelMap model = new ModelMap();
        model.addAttribute("state", 1);
        model.addAttribute("message", "Succesfully deleted a meeting");
        return new ModelAndView(
                new RedirectView("/viewMeetings", true),
                model
                        );
    }

    @GetMapping("/viewMeetings")
    String viewMeetings(Model model) {
        List<Meeting> meetings = databaseRepository.findAll();
        model.addAttribute("meetings", meetings);
        return "viewMeetings";
    }

}

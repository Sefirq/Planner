package com.sefir.app;

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

    @GetMapping("/addMeeting")
    String addMeeting(Model model) {
        model.addAttribute("meeting", new Meeting());
        System.out.println("IIIIIIIIIIIIIIIIIIIIIIIIIIIII");
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
            System.out.println(meeting.getName());
            databaseRepository.create(meeting);
            model.addAttribute("state", 1);
            model.addAttribute("message", "Succesfully added a meeting");
        }
        return "addMeeting";
    }

    @GetMapping("/viewMeetings")
    String viewMeetings(Model model) {
        List<Meeting> meetings = databaseRepository.findAll();
        System.out.println(meetings.get(0).getId());
        System.out.println(meetings.get(0).getId());
        System.out.println(meetings.get(0).getId());
        System.out.println(meetings.get(1).getId());
        System.out.println(meetings.get(1).getId());
        System.out.println(meetings.get(1).getId());
        model.addAttribute("meetings", meetings);
        return "viewMeetings";
    }

}

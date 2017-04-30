package com.sefir.app;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.validation.BindingResult;


@Controller
public class MeetingController {

    @ModelAttribute("meeting")
    public Meeting getMeetingObject() {
        return new Meeting();
    }

    @GetMapping("/addMeeting.html")
    String addMeeting(Model model) {
        model.addAttribute("meeting", new Meeting());
        System.out.println("IIIIIIIIIIIIIIIIIIIIIIIIIIIII");
        return "addMeeting";
    }

    @PostMapping(value = {"/add"})
    public String createMeeting(@ModelAttribute("meeting")Meeting meeting) {
        System.out.println(meeting.getName());
        return "addMeeting";
    }

}

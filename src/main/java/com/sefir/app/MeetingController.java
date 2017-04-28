package com.sefir.app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;

public class MeetingController {

    @PostMapping("/addMeeting")
    public void createMeeting(Meeting meeting, Model model) {
        System.out.println("TODO");
    }

}

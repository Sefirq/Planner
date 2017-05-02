package com.sefir.app;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

@Controller
class HomeController {

    @RequestMapping("/")
    String index() {
        return "index";
    }

}

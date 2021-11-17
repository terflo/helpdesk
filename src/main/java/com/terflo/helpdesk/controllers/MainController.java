package com.terflo.helpdesk.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String main() {
        return "index";
    }

    @GetMapping("/index")
    public String main2() {
        return "index";
    }

}

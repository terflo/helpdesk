package com.terflo.helpdesk.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatController {



    @GetMapping("/chat")
    public String chat(Authentication authentication, Model model) {
        model.addAttribute("name", authentication.getName());
        return "chat";
    }

}

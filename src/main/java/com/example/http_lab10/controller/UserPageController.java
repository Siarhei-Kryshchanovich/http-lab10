package com.example.http_lab10.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserPageController {

    @GetMapping("/user/home")
    public String home(Authentication auth, Model model) {
        model.addAttribute("email", auth.getName());
        return "user-home";
    }
}

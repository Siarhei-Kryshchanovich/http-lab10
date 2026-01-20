package com.example.http_lab10.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminPageController {

    @GetMapping("/admin/panel")
    public String panel() {
        return "admin-panel";
    }
}

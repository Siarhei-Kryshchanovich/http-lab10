package com.example.http_lab10.controller;

import com.example.http_lab10.model.dto.CreateUserRequest;
import com.example.http_lab10.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class MvcAuthController {

    private final UserService userService;

    public MvcAuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("form", new CreateUserRequest());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") CreateUserRequest form, BindingResult br) {
        if (br.hasErrors()) return "register";
        userService.createUser(form);
        return "redirect:/login?registered";
    }
}

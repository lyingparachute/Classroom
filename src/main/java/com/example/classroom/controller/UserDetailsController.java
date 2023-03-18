package com.example.classroom.controller;

import com.example.classroom.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("/dashboard/user")
@RequiredArgsConstructor
public class UserDetailsController {

    private final UserService service;

    @GetMapping
    public String getUserDetailsPage() {
        return "user/user-view";
    }

    @GetMapping("/edit")
    public String getEdituserDetailsPage() {
        return "user/user-view";
    }

    @GetMapping("/update")
    public String updateUserDetails() {
        return "user/user-view";
    }
}

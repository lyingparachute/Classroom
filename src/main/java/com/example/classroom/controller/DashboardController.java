package com.example.classroom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("dashboard")
class DashboardController {

    @GetMapping
    String getDashboard() {
        return "dashboard/dashboard";
    }

    @GetMapping("/library")
    String getLibrary() {
        return "dashboard/library";
    }

    @GetMapping("/calendar")
    String getCalendar() {
        return "dashboard/calendar";
    }

    @GetMapping("/analytics")
    String getAnalytics() {
        return "dashboard/analytics";
    }

    @GetMapping("/sales")
    String getSales() {
        return "dashboard/sales";
    }

    @GetMapping("/settings")
    String getSettings() {
        return "dashboard/settings";
    }

    @GetMapping("/help")
    String getHelp() {
        return "dashboard/help";
    }
}


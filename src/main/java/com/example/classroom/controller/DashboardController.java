package com.example.classroom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("dashboard")
public class DashboardController {

    @GetMapping
    public String getDashboard(){
        return "dashboard/dashboard";
    }

    @GetMapping("/library")
    public String getLibrary(){
        return "dashboard/library";
    }

    @GetMapping("/calendar")
    public String getCalendar(){
        return "dashboard/calendar";
    }

    @GetMapping("/analytics")
    public String getAnalytics(){
        return "dashboard/analytics";
    }

    @GetMapping("/sales")
    public String getSales(){
        return "dashboard/sales";
    }

    @GetMapping("/settings")
    public String getSettings(){
        return "dashboard/settings";
    }

    @GetMapping("/help")
    public String getHelp(){
        return "dashboard/help";
    }
}


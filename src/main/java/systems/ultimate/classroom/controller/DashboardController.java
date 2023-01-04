package systems.ultimate.classroom.controller;

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

    @GetMapping("/subjects")
    public String getSubjects(){
        return "dashboard/subjects";
    }

    @GetMapping("/fieldsOfStudy")
    public String getFieldsOfStudy(){
        return "dashboard/fieldsOfStudy";
    }

    @GetMapping("/library")
    public String getLibrary(){
        return "dashboard/library";
    }

    @GetMapping("/facilities")
    public String getFacilities(){
        return "dashboard/facilities";
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


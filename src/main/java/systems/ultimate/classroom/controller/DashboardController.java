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

    @GetMapping("/students")
    public String getStudents(){
        return "dashboard/students";
    }

    @GetMapping("/teachers")
    public String getTeachers(){
        return "dashboard/teachers";
    }

    @GetMapping("/calendar")
    public String getCalendar(){
        return "dashboard/calendar";
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


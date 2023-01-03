package systems.ultimate.classroom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import systems.ultimate.classroom.service.StudentService;

@Controller
@RequestMapping("dashboard")
public class DashboardController {

    private final StudentService studentService;

    public DashboardController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public String getDashboard(){
        return "dashboard/dashboard";
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


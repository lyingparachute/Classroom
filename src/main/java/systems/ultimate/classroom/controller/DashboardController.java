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

    @GetMapping("/help")
    public String getHelp(){
        return "dashboard/help";
    }
}


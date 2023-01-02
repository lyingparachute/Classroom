package systems.ultimate.classroom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping
    public String getHome(){
        return "home";
    }

    @GetMapping("/dashboard")
    public String getDashboard(){
        return "dashboard";
    }
}

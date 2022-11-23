package systems.ultimate.classroom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import systems.ultimate.classroom.service.StudentService;

@Controller
@RequestMapping("students")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @GetMapping
    public String getStudents(Model model) {
        model.addAttribute("students", service.getAll());
        return "students";
    }

}

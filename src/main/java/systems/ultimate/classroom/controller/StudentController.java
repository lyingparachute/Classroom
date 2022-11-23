package systems.ultimate.classroom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import systems.ultimate.classroom.dto.StudentDto;
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
        model.addAttribute("students", service.fetchAll());
        return "students";
    }

    @GetMapping("{id}")
    public String getStudent(@PathVariable Long id, Model model) {
        StudentDto studentDto = service.fetchById(id);
        model.addAttribute("student", studentDto);
        return "student";
    }
}

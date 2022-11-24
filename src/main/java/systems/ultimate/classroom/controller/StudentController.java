package systems.ultimate.classroom.controller;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import systems.ultimate.classroom.dto.StudentDto;
import systems.ultimate.classroom.service.StudentService;

import javax.validation.Valid;

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

    @GetMapping("search")
    public String searchStudents(@Param("name") String name, Model model){
        model.addAttribute("students", service.findByFirstOrLastName(name));
        model.addAttribute("name", name);
        model.addAttribute("description", "Search for '" + name + "' in students list");
        model.addAttribute("tableDesc", "Students that have '"+ name +"' in their first or last name");
        return "students-search";
    }

    @GetMapping("{id}")
    public String getStudent(@PathVariable Long id, Model model) {
        StudentDto studentDto = service.fetchById(id);
        model.addAttribute("student", studentDto);
        return "student";
    }

    @GetMapping("new")
    public String getNewStudentForm(Model model) {
        model.addAttribute("student", new StudentDto());
        return "student-form";
    }

    @PostMapping(value = "new")
    public String createStudent(@Valid @ModelAttribute("student") StudentDto studentDto, Model model) {
        model.addAttribute("student", service.create(studentDto));
        return "student";
    }

    @GetMapping("delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        service.remove(id);
        return "redirect:/students";
    }

    @GetMapping("{id}/teachers")
    public String getAllTeachersForStudent(@PathVariable Long id, Model model){
        StudentDto byId = service.fetchById(id);
        model.addAttribute("student", byId);
        model.addAttribute("studentTeachers", byId.getTeachersList());
        return "student-teachers";
    }
}

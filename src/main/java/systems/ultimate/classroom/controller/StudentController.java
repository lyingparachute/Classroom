package systems.ultimate.classroom.controller;

import org.modelmapper.ModelMapper;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import systems.ultimate.classroom.dto.StudentDto;
import systems.ultimate.classroom.entity.Student;
import systems.ultimate.classroom.service.StudentService;
import systems.ultimate.classroom.service.TeacherService;

import javax.validation.Valid;

@Controller
@RequestMapping("students")
public class StudentController {

    private final StudentService studentService;
    private final TeacherService teacherService;
    private final ModelMapper mapper;

    public StudentController(StudentService service, TeacherService teacherService, ModelMapper mapper) {
        this.studentService = service;
        this.teacherService = teacherService;
        this.mapper = mapper;
    }

    @GetMapping
    public String getStudents(Model model) {
        model.addAttribute("students", studentService.fetchAll());
        return "students";
    }

    @GetMapping("search")
    public String searchStudents(@Param("name") String name, Model model){
        model.addAttribute("students", studentService.findByFirstOrLastName(name));
        model.addAttribute("name", name);
        model.addAttribute("description", "Search for '" + name + "' in students list");
        model.addAttribute("tableDesc", "Students that have '"+ name +"' in their first or last name");
        return "students-search";
    }

    @GetMapping("{id}")
    public String getStudent(@PathVariable Long id, Model model) {
        StudentDto studentDto = studentService.fetchById(id);
        model.addAttribute("student", studentDto);
        return "student";
    }

    @GetMapping("new")
    public String getNewStudentForm(Model model) {
        model.addAttribute("student", new StudentDto());
        model.addAttribute("teachers", teacherService.fetchAll());
        return "student-form";
    }

    @PostMapping(value = "new")
    public String createStudent(@Valid @ModelAttribute("student") Student student, BindingResult result) {
        if (result.hasErrors()){
            return "student-form";
        }
        studentService.create(mapper.map(student, StudentDto.class));
        return "student";
    }

    @GetMapping("delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentService.remove(id);
        return "redirect:/students";
    }

    @GetMapping("{id}/teachers")
    public String getAllTeachersForStudent(@PathVariable Long id, Model model){
        StudentDto byId = studentService.fetchById(id);
        model.addAttribute("student", byId);
        model.addAttribute("studentTeachers", byId.getTeachersList());
        return "student-teachers";
    }
}

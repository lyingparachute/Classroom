package systems.ultimate.classroom.controller;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import systems.ultimate.classroom.dto.StudentDto;
import systems.ultimate.classroom.entity.Student;
import systems.ultimate.classroom.service.StudentService;
import systems.ultimate.classroom.service.TeacherService;

import javax.validation.Valid;
import java.util.List;

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
    public String getPaginatedStudents(@RequestParam(required = false) String name,
                                       @RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "6") int size,
                                       @RequestParam(defaultValue = "firstName") String sortField,
                                       @RequestParam(defaultValue = "asc") String sortDir,
                                       Model model) {


        Page<StudentDto> pageStudents;
        if (name == null) {
            pageStudents = studentService.fetchAllPaginated(page, size, sortField, sortDir);
        } else {
            pageStudents = studentService.findByFirstOrLastNamePaginated(page, size, sortField, sortDir, name);
            model.addAttribute("name", name);
        }
        List<StudentDto> students = pageStudents.getContent();
        model.addAttribute("students", students);
        model.addAttribute("currentPage", pageStudents.getNumber() + 1);
        model.addAttribute("totalPages", pageStudents.getTotalPages());
        model.addAttribute("totalItems", pageStudents.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        return "students";
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
    public String createStudent(@Valid @ModelAttribute("student") Student student, BindingResult result, Model model) {
        if (result.hasErrors()){
            model.addAttribute("teachers", teacherService.fetchAll());
            return "student-form";
        }
        studentService.create(mapper.map(student, StudentDto.class));
        return "student-create-success";
    }

    @GetMapping("delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentService.remove(id);
        return "redirect:/students";
    }

    @GetMapping("edit/{id}")
    public String editStudentForm(@PathVariable Long id, Model model) {
        StudentDto dto = studentService.fetchById(id);
        model.addAttribute("student", dto);
        model.addAttribute("teachers", teacherService.fetchAll());
        return "student-edit-form";
    }

    @PostMapping(value = "update")
    public String editStudent(@Valid @ModelAttribute("student") Student student, BindingResult result, Model model) {
        if (result.hasErrors()){
            model.addAttribute("teachers", teacherService.fetchAll());
            return "student-edit-form";
        }

        StudentDto updated = studentService.update(mapper.map(student, StudentDto.class));
        if (updated == null) {
            return "error/404";
        }
        model.addAttribute("student", updated);

        return "student-edit-success";
    }

}

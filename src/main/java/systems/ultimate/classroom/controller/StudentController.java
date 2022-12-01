package systems.ultimate.classroom.controller;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
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
    public String getStudents(Model model) {
        return getPaginatedStudents(1, "firstName", "asc", model);
    }

    @GetMapping("/page/{pageNo}")
    public String getPaginatedStudents(@PathVariable(value = "pageNo") int pageNo,
                                       @RequestParam("sortField") String sortField,
                                       @RequestParam("sortDir") String sortDir,
                                       Model model) {
        int pageSize = 2;
        Page<StudentDto> page = studentService.fetchAllPaginated(pageNo, pageSize, sortField, sortDir);
        List<StudentDto> students = page.getContent();
        model.addAttribute("students", students);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
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
        studentDto.getTeachersList().forEach(Object::toString);
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
        studentService.assignTeachers(student, student.getTeachersList());
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
    public String editStudent(@Valid @ModelAttribute("student") Student student, BindingResult result,Model model) {
        if (result.hasErrors()){
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

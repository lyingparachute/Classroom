package com.example.classroom.controller;

import com.example.classroom.dto.StudentDto;
import com.example.classroom.entity.Student;
import com.example.classroom.service.FieldOfStudyService;
import com.example.classroom.service.StudentService;
import com.example.classroom.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("dashboard/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final TeacherService teacherService;
    private final FieldOfStudyService fieldOfStudyService;
    private final ModelMapper mapper;

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
        int firstItemShownOnPage = 1;
        int lastItemShownOnPage;
        if (page == 1 && pageStudents.getTotalElements() <= size) {
            lastItemShownOnPage = Math.toIntExact(pageStudents.getTotalElements());
        } else if (page == 1 && pageStudents.getTotalElements() > size) {
            lastItemShownOnPage = size * page;
        } else if (page != 1 && pageStudents.getTotalElements() <= ((long) size * page)) {
            firstItemShownOnPage = size * (page - 1) + 1;
            lastItemShownOnPage = Math.toIntExact(pageStudents.getTotalElements());
        } else {
            firstItemShownOnPage = size * (page - 1) + 1;
            lastItemShownOnPage = size * (page - 1) + size;
        }

        model.addAttribute("students", students);
        model.addAttribute("currentPage", pageStudents.getNumber() + 1);
        model.addAttribute("totalPages", pageStudents.getTotalPages());
        model.addAttribute("totalItems", pageStudents.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("firstItemShownOnPage", firstItemShownOnPage);
        model.addAttribute("lastItemShownOnPage", lastItemShownOnPage);
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
        addAllTeachersAndFieldsOfStudyAsModelAttribute(model);
        return "student-form";
    }

    @PostMapping(value = "new")
    public String createStudent(@Valid @ModelAttribute("student") Student student, BindingResult result, Model model) {
        if (result.hasErrors()) {
            addAllTeachersAndFieldsOfStudyAsModelAttribute(model);
            return "student-form";
        }
        studentService.create(mapper.map(student, StudentDto.class));
        return "student-create-success";
    }

    @GetMapping("delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentService.remove(id);
        return "redirect:/dashboard/students";
    }

    @GetMapping("edit/{id}")
    public String editStudentForm(@PathVariable Long id, Model model) {
        StudentDto dto = studentService.fetchById(id);
        model.addAttribute("student", dto);
        addAllTeachersAndFieldsOfStudyAsModelAttribute(model);
        return "student-edit-form";
    }

    @PostMapping(value = "update")
    public String editStudent(@Valid @ModelAttribute("student") Student student, BindingResult result, Model model) {
        if (result.hasErrors()) {
            addAllTeachersAndFieldsOfStudyAsModelAttribute(model);
            return "student-edit-form";
        }

        StudentDto updated = studentService.update(mapper.map(student, StudentDto.class));
        if (updated == null) {
            return "error/404";
        }
        model.addAttribute("student", updated);

        return "student-edit-success";
    }

    private void addAllTeachersAndFieldsOfStudyAsModelAttribute(Model model) {
        model.addAttribute("teachers", teacherService.fetchAll());
        model.addAttribute("fieldsOfStudy", fieldOfStudyService.fetchAll());
    }
}

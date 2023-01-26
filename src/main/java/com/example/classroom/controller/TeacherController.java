package com.example.classroom.controller;

import com.example.classroom.dto.TeacherDto;
import com.example.classroom.entity.Teacher;
import com.example.classroom.service.StudentService;
import com.example.classroom.service.SubjectService;
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
@RequestMapping("dashboard/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;
    private final StudentService studentService;
    private final SubjectService subjectService;
    private final ModelMapper mapper;

    @GetMapping
    public String getPaginatedTeachers(@RequestParam(required = false) String name,
                                       @RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "6") int size,
                                       @RequestParam(defaultValue = "firstName") String sortField,
                                       @RequestParam(defaultValue = "asc") String sortDir,
                                       Model model) {
        Page<TeacherDto> pageTeachers;
        if (name == null) {
            pageTeachers = teacherService.fetchAllPaginated(page, size, sortField, sortDir);
        } else {
            pageTeachers = teacherService.findByFirstOrLastNamePaginated(page, size, sortField, sortDir, name);
            model.addAttribute("name", name);
        }
        List<TeacherDto> teachers = pageTeachers.getContent();
        int firstItemShownOnPage = 1;
        int lastItemShownOnPage;
        if (page == 1 && pageTeachers.getTotalElements() <= size) {
            lastItemShownOnPage = Math.toIntExact(pageTeachers.getTotalElements());
        } else if (page == 1 && pageTeachers.getTotalElements() > size) {
            lastItemShownOnPage = size * page;
        } else if (page != 1 && pageTeachers.getTotalElements() <= ((long) size * page)) {
            firstItemShownOnPage = size * (page - 1) + 1;
            lastItemShownOnPage = Math.toIntExact(pageTeachers.getTotalElements());
        } else {
            firstItemShownOnPage = size * (page - 1) + 1;
            lastItemShownOnPage = size * (page - 1) + size;
        }

        model.addAttribute("teachers", teachers);
        model.addAttribute("currentPage", pageTeachers.getNumber() + 1);
        model.addAttribute("totalPages", pageTeachers.getTotalPages());
        model.addAttribute("totalItems", pageTeachers.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("firstItemShownOnPage", firstItemShownOnPage);
        model.addAttribute("lastItemShownOnPage", lastItemShownOnPage);
        return "teacher/teachers";
    }

    @GetMapping("{id}")
    public String getTeacher(@PathVariable Long id, Model model) {
        addAttributeTeacherById(id, model);
        return "teacher/teacher";
    }

    @GetMapping("new")
    public String getNewTeacherForm(Model model) {
        model.addAttribute("teacher/teacher", new TeacherDto());
        addAttributesSubjectsAndStudents(model);
        return "teacher-form";
    }

    @PostMapping(value = "new")
    public String createTeacher(@Valid @ModelAttribute("teacher") Teacher teacher, BindingResult result, Model model) {
        if (result.hasErrors()) {
            addAttributesSubjectsAndStudents(model);
            return "teacher/teacher-form";
        }
        teacherService.create(mapper.map(teacher, TeacherDto.class));
        return "teacher/teacher-create-success";
    }

    @GetMapping("delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        teacherService.remove(id);
        return "redirect:/dashboard/teachers";
    }

    @GetMapping("edit/{id}")
    public String editTeacher(@PathVariable Long id, Model model) {
        addAttributeTeacherById(id, model);
        addAttributesSubjectsAndStudents(model);
        return "teacher/teacher-edit-form";
    }

    @PostMapping(value = "update")
    public String editTeacher(@Valid @ModelAttribute("teacher") Teacher teacher, BindingResult result, Model model) {
        if (result.hasErrors()) {
            addAttributesSubjectsAndStudents(model);
            return "teacher/teacher-edit-form";
        }
        TeacherDto updated = teacherService.update(mapper.map(teacher, TeacherDto.class));
        if (updated == null) {
            return "error/404";
        }
        model.addAttribute("teacher", updated);
        return "teacher/teacher-edit-success";
    }

    private void addAttributesSubjectsAndStudents(Model model) {
        model.addAttribute("subjects", subjectService.fetchAll());
        model.addAttribute("students", studentService.fetchAll());
    }

    private void addAttributeTeacherById(Long id, Model model) {
        model.addAttribute("teacher", teacherService.fetchById(id));
    }
}

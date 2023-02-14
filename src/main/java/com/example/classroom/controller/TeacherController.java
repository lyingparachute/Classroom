package com.example.classroom.controller;

import com.example.classroom.dto.TeacherDto;
import com.example.classroom.service.StudentService;
import com.example.classroom.service.SubjectService;
import com.example.classroom.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("dashboard/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService service;
    private final StudentService studentService;
    private final SubjectService subjectService;
    public static final String REDIRECT_DASHBOARD_TEACHERS = "redirect:/dashboard/teachers";

    @GetMapping
    public String getPaginatedTeachers(@RequestParam(required = false) String name,
                                       @RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "6") int size,
                                       @RequestParam(defaultValue = "firstName") String sortField,
                                       @RequestParam(defaultValue = "asc") String sortDir,
                                       Model model) {
        Page<TeacherDto> pageTeachers;
        if (name == null) {
            pageTeachers = service.fetchAllPaginated(page, size, sortField, sortDir);
        } else {
            pageTeachers = service.findByFirstOrLastNamePaginated(page, size, sortField, sortDir, name);
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
        return "teacher/all-teachers";
    }

    @GetMapping("{id}")
    public String getTeacher(@PathVariable Long id, Model model) {
        addAttributeTeacherById(id, model);
        return "teacher/teacher-view";
    }

    @GetMapping("new")
    public String getNewTeacherForm(Model model) {
        model.addAttribute("teacher", new TeacherDto());
        addAttributesSubjectsAndStudents(model);
        return "teacher/teacher-create-form";
    }

    @PostMapping(value = "new")
    public String createTeacher(@Valid @ModelAttribute("teacher") TeacherDto dto,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (result.hasErrors()) {
            addAttributesSubjectsAndStudents(model);
            return "teacher/teacher-create-form";
        }
        TeacherDto saved = service.create(dto);
        addFlashAttributeSuccess(redirectAttributes, saved);
        redirectAttributes.addFlashAttribute("createSuccess", "saved");
        return REDIRECT_DASHBOARD_TEACHERS;
    }

    @GetMapping("edit/{id}")
    public String editTeacher(@PathVariable Long id, Model model) {
        addAttributeTeacherById(id, model);
        addAttributesSubjectsAndStudents(model);
        return "teacher/teacher-edit-form";
    }

    @PostMapping(value = "update")
    public String editTeacher(@Valid @ModelAttribute("teacher") TeacherDto dto,
                              BindingResult result,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (result.hasErrors()) {
            addAttributesSubjectsAndStudents(model);
            return "teacher/teacher-edit-form";
        }
        TeacherDto updated = service.update(dto);
        model.addAttribute("teacher", updated);
        addFlashAttributeSuccess(redirectAttributes, updated);
        redirectAttributes.addFlashAttribute("updateSuccess", "updated");
        return REDIRECT_DASHBOARD_TEACHERS;
    }

    @GetMapping("delete/{id}")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        TeacherDto dto = service.fetchById(id);
        service.remove(id);
        addFlashAttributeSuccess(redirectAttributes, dto);
        redirectAttributes.addFlashAttribute("deleteSuccess", "deleted");
        return REDIRECT_DASHBOARD_TEACHERS;
    }

    private void addAttributesSubjectsAndStudents(Model model) {
        model.addAttribute("subjects", subjectService.fetchAll());
        model.addAttribute("students", studentService.fetchAll());
    }

    private void addAttributeTeacherById(Long id, Model model) {
        model.addAttribute("teacher", service.fetchById(id));
    }

    private void addFlashAttributeSuccess(RedirectAttributes redirectAttributes, TeacherDto dto) {
        redirectAttributes.addFlashAttribute("success", dto);
    }
}

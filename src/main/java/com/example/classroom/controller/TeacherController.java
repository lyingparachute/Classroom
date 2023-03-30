package com.example.classroom.controller;

import com.example.classroom.breadcrumb.BreadcrumbService;
import com.example.classroom.dto.TeacherDto;
import com.example.classroom.service.SubjectService;
import com.example.classroom.service.TeacherService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("dashboard/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService service;
    private final SubjectService subjectService;
    private final BreadcrumbService crumb;

    public static final String REDIRECT_DASHBOARD_TEACHERS = "redirect:/dashboard/teachers";

    @GetMapping
    public String getPaginatedTeachers(@RequestParam(required = false) String name,
                                       @RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "6") int size,
                                       @RequestParam(defaultValue = "firstName") String sortField,
                                       @RequestParam(defaultValue = "asc") String sortDir,
                                       HttpServletRequest request,
                                       Model model) {
        addAttributeBreadcrumb(model, request);

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
    public String getTeacher(@PathVariable Long id,
                             HttpServletRequest request,
                             Model model) {
        addAttributeBreadcrumb(model, request);
        addAttributeTeacherById(id, model);
        return "teacher/teacher-view";
    }

    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    @GetMapping("new")
    public String getNewTeacherForm(HttpServletRequest request,
                                    Model model) {
        addAttributeBreadcrumb(model, request);
        model.addAttribute("teacher", new TeacherDto());
        addAttributesSubjects(model);
        return "teacher/teacher-create-form";
    }

    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    @PostMapping(value = "new")
    public String createTeacher(@Valid @ModelAttribute("teacher") TeacherDto dto,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                HttpServletRequest request,
                                Model model) {
        if (result.hasErrors()) {
            addAttributeBreadcrumb(model, request);
            addAttributesSubjects(model);
            return "teacher/teacher-create-form";
        }
        TeacherDto saved = service.create(dto);
        addFlashAttributeSuccess(redirectAttributes, saved);
        redirectAttributes.addFlashAttribute("createSuccess", "saved");
        return REDIRECT_DASHBOARD_TEACHERS;
    }

    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    @GetMapping("edit/{id}")
    public String editTeacher(@PathVariable Long id,
                              HttpServletRequest request,
                              Model model) {
        addAttributeBreadcrumb(model, request);
        addAttributeTeacherById(id, model);
        addAttributesSubjects(model);
        return "teacher/teacher-edit-form";
    }

    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    @PostMapping(value = "update")
    public String editTeacher(@Valid @ModelAttribute("teacher") TeacherDto dto,
                              BindingResult result,
                              RedirectAttributes redirectAttributes,
                              HttpServletRequest request,
                              Model model) {
        if (result.hasErrors()) {
            addAttributeBreadcrumb(model, request);
            addAttributesSubjects(model);
            return "teacher/teacher-edit-form";
        }
        TeacherDto updated = service.update(dto);
        addFlashAttributeSuccess(redirectAttributes, updated);
        redirectAttributes.addFlashAttribute("updateSuccess", "updated");
        return REDIRECT_DASHBOARD_TEACHERS;
    }

    @Secured({"ROLE_ADMIN"})
    @GetMapping("delete/{id}")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        TeacherDto dto = service.fetchById(id);
        service.remove(id);
        addFlashAttributeSuccess(redirectAttributes, dto);
        redirectAttributes.addFlashAttribute("deleteSuccess", "deleted");
        return REDIRECT_DASHBOARD_TEACHERS;
    }

    private void addAttributesSubjects(Model model) {
        model.addAttribute("subjects", subjectService.fetchAll());
    }

    private void addAttributeTeacherById(Long id, Model model) {
        model.addAttribute("teacher", service.fetchById(id));
    }

    private void addFlashAttributeSuccess(RedirectAttributes redirectAttributes, TeacherDto dto) {
        redirectAttributes.addFlashAttribute("success", dto);
    }

    private void addAttributeBreadcrumb(Model model, HttpServletRequest request) {
        model.addAttribute("crumbs", crumb.getBreadcrumbs(request.getRequestURI()));
    }
}

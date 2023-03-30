package com.example.classroom.controller;


import com.example.classroom.breadcrumb.BreadcrumbService;
import com.example.classroom.dto.SubjectDto;
import com.example.classroom.service.FieldOfStudyService;
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
@RequestMapping("dashboard/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService service;
    private final TeacherService teacherService;
    private final FieldOfStudyService fieldOfStudyService;
    private final BreadcrumbService crumb;

    public static final String REDIRECT_DASHBOARD_SUBJECTS = "redirect:/dashboard/subjects";
    public static final String SUBJECT_EDIT_FORM = "subject/subject-edit-form";

    @GetMapping
    public String getSubjects(@RequestParam(required = false) String name,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "6") int size,
                              @RequestParam(defaultValue = "id") String sortField,
                              @RequestParam(defaultValue = "asc") String sortDir,
                              HttpServletRequest request,
                              Model model) {
        addAttributeBreadcrumb(model, request);

        Page<SubjectDto> pageSubjects;
        if (name == null) {
            pageSubjects = service.fetchAllPaginated(page, size, sortField, sortDir);
        } else {
            pageSubjects = service.findByNamePaginated(page, size, sortField, sortDir, name);
            model.addAttribute("name", name);
        }
        List<SubjectDto> subjects = pageSubjects.getContent();
        int firstItemShownOnPage = 1;
        int lastItemShownOnPage;
        if (page == 1 && pageSubjects.getTotalElements() <= size) {
            lastItemShownOnPage = Math.toIntExact(pageSubjects.getTotalElements());
        } else if (page == 1 && pageSubjects.getTotalElements() > size) {
            lastItemShownOnPage = size * page;
        } else if (page != 1 && pageSubjects.getTotalElements() <= ((long) size * page)) {
            firstItemShownOnPage = size * (page - 1) + 1;
            lastItemShownOnPage = Math.toIntExact(pageSubjects.getTotalElements());
        } else {
            firstItemShownOnPage = size * (page - 1) + 1;
            lastItemShownOnPage = size * (page - 1) + size;
        }

        model.addAttribute("subjects", subjects);
        model.addAttribute("currentPage", pageSubjects.getNumber() + 1);
        model.addAttribute("totalPages", pageSubjects.getTotalPages());
        model.addAttribute("totalItems", pageSubjects.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("firstItemShownOnPage", firstItemShownOnPage);
        model.addAttribute("lastItemShownOnPage", lastItemShownOnPage);
        return "subject/all-subjects";
    }

    @GetMapping("{id}")
    public String getSubject(@PathVariable Long id,
                             HttpServletRequest request,
                             Model model) {
        addAttributeBreadcrumb(model, request);
        addAttributeSubjectById(id, model);
        return "subject/subject-view";
    }

    @GetMapping("new")
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    public String getNewSubjectForm(HttpServletRequest request,
                                    Model model) {
        addAttributeBreadcrumb(model, request);
        model.addAttribute("subject", new SubjectDto());
        addAttributeTeachersAndFieldsOfStudy(model);
        return "subject/subject-create-form";
    }

    @PostMapping(value = "new")
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    public String createSubject(@Valid @ModelAttribute("subject") SubjectDto dto,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                HttpServletRequest request,
                                Model model) {
        if (result.hasErrors()) {
            addAttributeBreadcrumb(model, request);
            addAttributeTeachersAndFieldsOfStudy(model);
            return "subject/subject-create-form";
        }
        SubjectDto saved = service.create(dto);
        addFlashAttributeSuccess(redirectAttributes, saved);
        redirectAttributes.addFlashAttribute("createSuccess", "saved");
        return REDIRECT_DASHBOARD_SUBJECTS;
    }

    @GetMapping("edit/{id}")
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    public String editSubjectForm(@PathVariable Long id,
                                  HttpServletRequest request,
                                  Model model) {
        addAttributeBreadcrumb(model, request);
        addAttributeSubjectById(id, model);
        addAttributeTeachersAndFieldsOfStudy(model);
        return SUBJECT_EDIT_FORM;
    }

    @PostMapping(value = "update")
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    public String editSubject(@Valid @ModelAttribute("subject") SubjectDto dto,
                              BindingResult result,
                              RedirectAttributes redirectAttributes,
                              HttpServletRequest request,
                              Model model) {
        if (result.hasErrors()) {
            addAttributeBreadcrumb(model, request);
            addAttributeTeachersAndFieldsOfStudy(model);
            return SUBJECT_EDIT_FORM;
        }
        SubjectDto updated = service.update(dto);
        addFlashAttributeSuccess(redirectAttributes, updated);
        redirectAttributes.addFlashAttribute("updateSuccess", "updated");
        return REDIRECT_DASHBOARD_SUBJECTS;
    }

    @GetMapping("delete/{id}")
    @Secured({"ROLE_ADMIN"})
    public String deleteSubject(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {
        SubjectDto dto = service.fetchById(id);
        service.remove(id);
        addFlashAttributeSuccess(redirectAttributes, dto);
        redirectAttributes.addFlashAttribute("deleteSuccess", "deleted");
        return REDIRECT_DASHBOARD_SUBJECTS;
    }

    private void addAttributeSubjectById(Long id, Model model) {
        model.addAttribute("subject", service.fetchById(id));
    }

    private void addAttributeTeachersAndFieldsOfStudy(Model model) {
        model.addAttribute("teachers", teacherService.fetchAll());
        model.addAttribute("fieldsOfStudy", fieldOfStudyService.fetchAll());
    }

    private void addFlashAttributeSuccess(RedirectAttributes redirectAttributes, SubjectDto dto) {
        redirectAttributes.addFlashAttribute("success", dto);
    }

    private void addAttributeBreadcrumb(Model model, HttpServletRequest request) {
        model.addAttribute("crumbs", crumb.getBreadcrumbs(request.getRequestURI()));
    }
}

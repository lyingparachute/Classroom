package com.example.classroom.controller;


import com.example.classroom.dto.SubjectDto;
import com.example.classroom.service.FieldOfStudyService;
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
@RequestMapping("dashboard/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService service;
    private final TeacherService teacherService;
    private final FieldOfStudyService fieldOfStudyService;
    public static final String REDIRECT_DASHBOARD_SUBJECTS = "redirect:/dashboard/subjects";
    public static final String SUBJECT_EDIT_FORM = "subject/subject-edit-form";

    @GetMapping
    public String getSubjects(@RequestParam(required = false) String name,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "6") int size,
                              @RequestParam(defaultValue = "id") String sortField,
                              @RequestParam(defaultValue = "asc") String sortDir,
                              Model model) {
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
    public String getSubject(@PathVariable Long id, Model model) {
        addAttributeSubjectById(id, model);
        return "subject/subject-view";
    }

    @GetMapping("new")
    public String getNewSubjectForm(Model model) {
        model.addAttribute("subject", new SubjectDto());
        addAttributeTeachersAndFieldsOfStudy(model);
        return "subject/subject-create-form";
    }

    @PostMapping(value = "new")
    public String createSubject(@Valid @ModelAttribute("subject") SubjectDto dto,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (result.hasErrors()) {
            addAttributeTeachersAndFieldsOfStudy(model);
            return "subject/subject-create-form";
        }
        SubjectDto saved = service.create(dto);
        addFlashAttributeSuccess(redirectAttributes, saved);
        redirectAttributes.addFlashAttribute("createSuccess", "saved");
        return REDIRECT_DASHBOARD_SUBJECTS;
    }

    @GetMapping("edit/{id}")
    public String editSubjectForm(@PathVariable Long id, Model model) {
        addAttributeSubjectById(id, model);
        addAttributeTeachersAndFieldsOfStudy(model);
        return SUBJECT_EDIT_FORM;
    }

    @PostMapping(value = "update")
    public String editSubject(@Valid @ModelAttribute("subject") SubjectDto dto,
                              BindingResult result,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (result.hasErrors()) {
            addAttributeTeachersAndFieldsOfStudy(model);
            return SUBJECT_EDIT_FORM;
        }
        SubjectDto updated = service.update(dto);
        addFlashAttributeSuccess(redirectAttributes, updated);
        redirectAttributes.addFlashAttribute("updateSuccess", "updated");
        return REDIRECT_DASHBOARD_SUBJECTS;
    }

    @GetMapping("delete/{id}")
    public String deleteSubject(@PathVariable Long id, RedirectAttributes redirectAttributes) {
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
}

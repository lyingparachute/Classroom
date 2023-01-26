package com.example.classroom.controller;


import com.example.classroom.dto.FieldOfStudyDto;
import com.example.classroom.service.DepartmentService;
import com.example.classroom.service.FieldOfStudyService;
import com.example.classroom.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("dashboard/fields-of-study")
@RequiredArgsConstructor
public class FieldOfStudyController {

    private final FieldOfStudyService service;
    private final DepartmentService departmentService;
    private final SubjectService subjectService;

    @GetMapping
    public String getAllFieldsOfStudy(Model model) {
        model.addAttribute("fieldsOfStudy", service.fetchAll());
        return "field-of-study/all-fieldsOfStudy";
    }

    @GetMapping("{id}")
    public String getFieldOfStudy(@PathVariable Long id, Model model) {
        addAttributeFieldOfStudyFetchById(id, model);
        addAttributes(id, model);
        return "field-of-study/fieldOfStudy-view";
    }

    @GetMapping("{id}/subjects")
    public String getFieldOfStudySubjects(@PathVariable Long id, Model model) {
        FieldOfStudyDto dto = service.fetchById(id);
        addAttributeFieldOfStudyFetchById(id, model);
        addAttributeSemesters(id, model);
        addAttributeEctsMap(id, model);
        model.addAttribute("semestersMap", service.fetchAllSubjectsFromFieldOfStudyGroupedBySemesters(id));
        model.addAttribute("hoursInSemester", service.calculateHoursInEachSemesterFromFieldOfStudy(id));
        model.addAttribute("subjects", dto.getSubjects());
        return "field-of-study/fieldOfStudy-subjects";
    }

    @GetMapping("new")
    public String getCreateFieldOfStudyForm(Model model) {
        addAttributeFieldOfStudyDto(model);
        addAttributeDepartments(model);
        return "field-of-study/fieldOfStudy-create-form";
    }

    @PostMapping("new")
    public String createFieldOfStudy(@Valid @ModelAttribute("fieldOfStudy") FieldOfStudyDto fieldOfStudy,
                                     BindingResult result,
                                     Model model) {
        if (result.hasErrors()) {
            addAttributeDepartments(model);
            return "fieldOfStudy-form";
        }
        FieldOfStudyDto created = service.create(fieldOfStudy);
        addAttributes(created.getId(), model);
        return "field-of-study/fieldOfStudy-create-success";
    }

    @GetMapping("delete/{id}")
    public String deleteFieldOfStudy(@PathVariable Long id, Model model) {
        service.remove(id);
        return "redirect:/dashboard/fields-of-study";
    }

    @GetMapping("edit/{id}")
    public String getEditFieldOfStudyForm(@PathVariable Long id, Model model) {
        addAttributeFieldOfStudyFetchById(id, model);
        addAttributeDepartments(model);
        return "field-of-study/fieldOfStudy-edit-form";
    }

    @PostMapping("update")
    public String editFieldOfStudy(@Valid @ModelAttribute("fieldOfStudy") FieldOfStudyDto dto,
                                   BindingResult result,
                                   Model model) {
        if (result.hasErrors())
            return "field-of-study/fieldOfStudy-edit-form";
        service.update(dto);
        addAttributeFieldOfStudyFetchById(dto.getId(), model);
        addAttributes(dto.getId(), model);
        return "field-of-study/fieldOfStudy-edit-success";
    }

    @GetMapping("edit/{id}/subjects")
    public String getEditFieldOfStudySubjectsForm(@PathVariable Long id, Model model) {
        addAttributeFieldOfStudyFetchById(id, model);
        addAttributeSubjects(model);
        return "field-of-study/fieldOfStudy-subjects-edit-form";
    }

    @PostMapping("subjects/update")
    public String editFieldOfStudySubjects(@Valid @ModelAttribute("fieldOfStudy") FieldOfStudyDto dto,
                                           BindingResult result,
                                           Model model) {
        if (result.hasErrors())
            return "field-of-study/fieldOfStudy-edit-form";
        service.update(dto);
        addAttributeFieldOfStudyFetchById(dto.getId(), model);
        addAttributes(dto.getId(), model);
        return "field-of-study/fieldOfStudy-subjects-edit-success";
    }

    private void addAttributes(Long id, Model model) {
        addAttributeDescription(id, model);
        addAttributeTotalEctsPoints(id, model);
        addAttributeSemesters(id, model);
    }

    private void addAttributeSemesters(Long id, Model model) {
        model.addAttribute("numberOfSemesters", service.getNumberOfSemesters(id));
    }

    private void addAttributeTotalEctsPoints(Long id, Model model) {
        model.addAttribute("ects", service.getSumOfEctsPointsFromAllSemesters(id));
    }

    private void addAttributeEctsMap(Long id, Model model) {
        model.addAttribute("ectsMap", service.calculateEctsPointsForEachSemester(id));
    }

    private void addAttributeDescription(Long id, Model model) {
        model.addAttribute("descriptionList", service.splitDescription(id));
    }

    private void addAttributeDepartments(Model model) {
        model.addAttribute("departments", departmentService.fetchAll());
    }

    private void addAttributeFieldOfStudyFetchById(Long id, Model model) {
        model.addAttribute("fieldOfStudy", service.fetchById(id));
    }

    private void addAttributeFieldOfStudyDto(Model model) {
        model.addAttribute("fieldOfStudy", new FieldOfStudyDto());
    }

    private void addAttributeSubjects(Model model) {
        model.addAttribute("subjects", subjectService.fetchAll());
    }
}

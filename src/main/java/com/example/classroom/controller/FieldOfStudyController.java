package com.example.classroom.controller;


import com.example.classroom.dto.FieldOfStudyDto;
import com.example.classroom.entity.Subject;
import com.example.classroom.enums.Semester;
import com.example.classroom.service.DepartmentService;
import com.example.classroom.service.FieldOfStudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("dashboard/fields-of-study")
@RequiredArgsConstructor
public class FieldOfStudyController {

    private final FieldOfStudyService service;
    private final DepartmentService departmentService;

    @GetMapping
    public String getAllFieldsOfStudy(Model model) {
        model.addAttribute("fieldsOfStudy", service.fetchAll());
        return "field-of-study/all-items";
    }

    private static void addAttributeSemesters(Model model) {
        model.addAttribute("numberOfSemesters", 7);
    }

    private static void addAttributeECTS(Model model) {
        model.addAttribute("ectsPoints", 30);
    }

    @GetMapping("{id}")
    public String getFieldOfStudy(@PathVariable Long id, Model model) {
        addAttributeFieldOfStudy(id, model);
        addAttributes(id, model);
        return "field-of-study/item-view";
    }

    @GetMapping("{id}/subjects")
    public String getFieldOfStudySubjects(@PathVariable Long id, Model model) {
        FieldOfStudyDto dto = service.fetchById(id);
        List<Subject> subjects = new ArrayList<>(dto.getSubjects());
        Map<Semester, List<Subject>> semestersMap = service.fetchAllSubjectsFromFieldOfStudyGroupedBySemesters(id);

        addAttributeFieldOfStudy(id, model);
        model.addAttribute("semestersMap", semestersMap);
        model.addAttribute("hoursInSemester", service.calculateHoursInEachSemesterFromFieldOfStudy(id));
        model.addAttribute("subjects", subjects);
        return "field-of-study/subjects";
    }

    @GetMapping("new")
    public String getCreateFieldOfStudyForm(Model model) {
        addAttributeFieldOfStudyDto(model);
        addAttributeDepartments(model);
        return "field-of-study/item-form";
    }

    @PostMapping("new")
    public String createFieldOfStudy(@Valid @ModelAttribute("fieldOfStudy") FieldOfStudyDto fieldOfStudy,
                                     BindingResult result,
                                     Model model) {
        if (result.hasErrors())
            return "field-of-study/item-form";
        service.create(fieldOfStudy);
        addAttributeFieldOfStudyDto(model);
        return "field-of-study/item-create-success";
    }

    @GetMapping("delete/{id}")
    public String deleteFieldOfStudy(@PathVariable Long id, Model model) {
        service.remove(id);
        return "redirect:/dashboard/fields-of-study";
    }

    @GetMapping("edit/{id}")
    public String getEditFieldOfStudyForm(@PathVariable Long id, Model model) {
        addAttributeFieldOfStudy(id, model);
        addAttributes(id, model);
        addAttributeDepartments(model);
        return "field-of-study/item-edit-form";
    }

    @PostMapping("update")
    public String editFieldOfStudy(@Valid @ModelAttribute("fieldOfStudy") FieldOfStudyDto fieldOfStudy,
                                   BindingResult result,
                                   Model model) {
        if (result.hasErrors())
            return "field-of-study/item-form";
        service.update(fieldOfStudy);
        addAttributeFieldOfStudy(fieldOfStudy.getId(), model);
        addAttributes(fieldOfStudy.getId(), model);
        return "field-of-study/item-edit-success";
    }

    private void addAttributes(Long id, Model model) {
        addAttributeDescription(id, model);
        addAttributeECTS(model);
        addAttributeSemesters(model);
    }

    private void addAttributeDescription(Long id, Model model) {
        model.addAttribute("descriptionList", service.getDescriptionSeparated(id));
    }

    private void addAttributeDepartments(Model model) {
        model.addAttribute("departments", departmentService.fetchAll());
    }

    private void addAttributeFieldOfStudy(Long id, Model model) {
        model.addAttribute("fieldOfStudy", service.fetchById(id));
    }

    private void addAttributeFieldOfStudyDto(Model model) {
        model.addAttribute("fieldOfStudy", new FieldOfStudyDto());
    }
}

package com.example.classroom.controller;

import com.example.classroom.dto.DepartmentDto;
import com.example.classroom.service.DepartmentService;
import com.example.classroom.service.FieldOfStudyService;
import com.example.classroom.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.nio.file.Path;

@Controller
@RequestMapping("dashboard/departments")
@RequiredArgsConstructor
public class DepartmentController {

    public static final String REDIRECT_DASHBOARD_DEPARTMENTS = "redirect:/dashboard/departments";
    private final DepartmentService service;
    private final TeacherService teacherService;
    private final FieldOfStudyService fieldOfStudyService;

    private static void addAttributeImagesPath(Model model, String directory) {
        model.addAttribute("imagesPath", Path.of("/img").resolve(directory));
    }

    @GetMapping()
    public String getAllDepartments(Model model) {
        model.addAttribute("departments", service.fetchAll());
        addAttributeImagesPath(model, "departments");
        return "department/all-departments";
    }

    @GetMapping("{id}")
    public String getDepartment(@PathVariable Long id, Model model) {
        addAttributeDepartmentFetchById(id, model);
        addAttributeImagesPath(model, "fields-of-study");
        return "department/department-view";
    }

    @GetMapping("new")
    public String getNewDepartmentForm(Model model) {
        model.addAttribute("department", new DepartmentDto());
        addAttributesTeachersAndFieldsOfStudy(model);
        addAttributeImagesPath(model, "fields-of-study");
        return "department/department-create-form";
    }

    @PostMapping(value = "new")
    public String createDepartment(@Valid @ModelAttribute("department") DepartmentDto dto,
                                   RedirectAttributes redirectAttributes,
                                   BindingResult result,
                                   Model model) {
        if (result.hasErrors()) {
            addAttributesTeachersAndFieldsOfStudy(model);
            return "department/department-create-form";
        }
        DepartmentDto saved = service.create(dto);
        redirectAttributes.addFlashAttribute("success", saved);
        redirectAttributes.addFlashAttribute("createSuccess", "saved");
        return REDIRECT_DASHBOARD_DEPARTMENTS;
    }

    @GetMapping("edit/{id}")
    public String getEditDepartmentForm(@PathVariable Long id, Model model) {
        addAttributeDepartmentFetchById(id, model);
        addAttributesTeachersAndFieldsOfStudy(model);
        addAttributeImagesPath(model, "fields-of-study");
        return "department/department-edit-form";
    }

    @PostMapping(value = "update")
    public String editDepartment(@Valid @ModelAttribute("department") DepartmentDto dto,
                                 RedirectAttributes redirectAttributes,
                                 BindingResult result,
                                 Model model) {
        if (result.hasErrors()) {
            addAttributesTeachersAndFieldsOfStudy(model);
            return "department/department-edit-form";
        }
        DepartmentDto updated = service.update(dto);
        redirectAttributes.addFlashAttribute("success", updated);
        redirectAttributes.addFlashAttribute("updateSuccess", "updated");
        return REDIRECT_DASHBOARD_DEPARTMENTS;
    }

    @GetMapping("delete/{id}")
    public String deleteDepartment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        DepartmentDto dto = service.fetchById(id);
        service.remove(id);
        redirectAttributes.addFlashAttribute("success", dto);
        redirectAttributes.addFlashAttribute("deleteSuccess", "deleted");
        return REDIRECT_DASHBOARD_DEPARTMENTS;
    }

    private void addAttributeDepartmentFetchById(Long id, Model model) {
        model.addAttribute("department", service.fetchById(id));
    }

    private void addAttributesTeachersAndFieldsOfStudy(Model model) {
        model.addAttribute("teachers", teacherService.fetchAll());
        model.addAttribute("fieldsOfStudy", fieldOfStudyService.fetchAllWithNoDepartment());
    }
}

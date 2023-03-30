package com.example.classroom.controller;

import com.example.classroom.breadcrumb.BreadcrumbService;
import com.example.classroom.dto.DepartmentDto;
import com.example.classroom.service.DepartmentService;
import com.example.classroom.service.FieldOfStudyService;
import com.example.classroom.service.TeacherService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Path;

@Controller
@RequestMapping("dashboard/departments")
@RequiredArgsConstructor
public class DepartmentController {

    public static final String REDIRECT_DASHBOARD_DEPARTMENTS = "redirect:/dashboard/departments";
    private final DepartmentService service;
    private final TeacherService teacherService;
    private final FieldOfStudyService fieldOfStudyService;
    private final BreadcrumbService crumb;

    @GetMapping()
    public String getAllDepartments(Model model, HttpServletRequest request) {
        model.addAttribute("departments", service.fetchAll());
        addAttributeImagesPath(model, "departments");
        addAttributeBreadcrumbs(model, request);
        return "department/all-departments";
    }

    @GetMapping("{id}")
    public String getDepartment(@PathVariable Long id,
                                HttpServletRequest request,
                                Model model) {
        addAttributeDepartmentFetchById(id, model);
        addAttributeImagesPath(model, "fields-of-study");
        addAttributeBreadcrumbs(model, request);
        return "department/department-view";
    }

    @GetMapping("new")
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    public String getNewDepartmentForm(Model model, HttpServletRequest request) {
        model.addAttribute("department", new DepartmentDto());
        addAttributesForCreateDepartment(model);
        addAttributeBreadcrumbs(model, request);
        return "department/department-create-form";
    }

    @PostMapping("new")
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    public String createDepartment(@Valid @ModelAttribute("department") DepartmentDto dto,
                                   BindingResult result,
                                   RedirectAttributes redirectAttributes,
                                   HttpServletRequest request,
                                   Model model) {
        if (result.hasErrors()) {
            addAttributesForCreateDepartment(model);
            addAttributeBreadcrumbs(model, request);
            return "department/department-create-form";
        }
        DepartmentDto saved = service.create(dto);
        addFlashAttributeSuccess(redirectAttributes, saved);
        redirectAttributes.addFlashAttribute("createSuccess", "saved");
        return REDIRECT_DASHBOARD_DEPARTMENTS;
    }

    @GetMapping("edit/{id}")
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    public String getEditDepartmentForm(@PathVariable Long id,
                                        Model model,
                                        HttpServletRequest request) {
        addAttributeDepartmentFetchById(id, model);
        addAttributesForUpdateDepartment(id, model);
        addAttributeBreadcrumbs(model, request);
        return "department/department-edit-form";
    }

    @PostMapping(value = "update")
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    public String editDepartment(@Valid @ModelAttribute("department") DepartmentDto dto,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes,
                                 HttpServletRequest request,
                                 Model model) {
        if (result.hasErrors()) {
            addAttributesForUpdateDepartment(dto.getId(), model);
            addAttributeBreadcrumbs(model, request);
            return "department/department-edit-form";
        }
        DepartmentDto updated = service.update(dto);
        addFlashAttributeSuccess(redirectAttributes, updated);
        redirectAttributes.addFlashAttribute("updateSuccess", "updated");
        return REDIRECT_DASHBOARD_DEPARTMENTS;
    }

    @GetMapping("delete/{id}")
    @Secured({"ROLE_ADMIN"})
    public String deleteDepartment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        DepartmentDto dto = service.fetchById(id);
        service.remove(id);
        addFlashAttributeSuccess(redirectAttributes, dto);
        redirectAttributes.addFlashAttribute("deleteSuccess", "deleted");
        return REDIRECT_DASHBOARD_DEPARTMENTS;
    }

    private void addAttributeBreadcrumbs(Model model, HttpServletRequest request) {
        model.addAttribute("crumbs", crumb.getBreadcrumbs(request.getRequestURI()));
    }

    private void addAttributesForCreateDepartment(Model model) {
        model.addAttribute("teachers", teacherService.fetchAll());
        model.addAttribute("fieldsOfStudy", fieldOfStudyService.fetchAllWithNoDepartment());
        addAttributeImagesPath(model, "fields-of-study");
    }

    private static void addAttributeImagesPath(Model model, String directory) {
        model.addAttribute("imagesPath", Path.of("/img").resolve(directory));
    }

    private static void addFlashAttributeSuccess(RedirectAttributes redirectAttributes, DepartmentDto saved) {
        redirectAttributes.addFlashAttribute("success", saved);
    }

    private void addAttributeDepartmentFetchById(Long id, Model model) {
        model.addAttribute("department", service.fetchById(id));
    }

    private void addAttributesForUpdateDepartment(Long id, Model model) {
        DepartmentDto dto = service.fetchById(id);
        model.addAttribute("teachers", teacherService.fetchAll());
        model.addAttribute("fieldsOfStudy", fieldOfStudyService.fetchAllWithGivenDepartmentDtoOrNoDepartment(dto));
        addAttributeImagesPath(model, "fields-of-study");
    }
}

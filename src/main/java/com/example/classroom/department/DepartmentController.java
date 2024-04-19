package com.example.classroom.department;

import com.example.classroom.breadcrumb.BreadcrumbService;
import com.example.classroom.fieldofstudy.FieldOfStudyService;
import com.example.classroom.teacher.TeacherService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Path;

@Controller
@RequestMapping("dashboard/departments")
@RequiredArgsConstructor
class DepartmentController {

    static final String REDIRECT_DASHBOARD_DEPARTMENTS = "redirect:/dashboard/departments";
    private final DepartmentService service;
    private final TeacherService teacherService;
    private final FieldOfStudyService fieldOfStudyService;

    @GetMapping
    String getAllDepartments(final Model model,
                             final HttpServletRequest request) {
        model.addAttribute("departments", service.fetchAll());
        addAttributeImagesPath(model, "departments");
        addAttributeBreadcrumbs(model, request);
        return "department/all-departments";
    }

    @GetMapping("{id}")
    String getDepartment(@PathVariable final Long id,
                         final HttpServletRequest request,
                         final Model model) {
        addAttributeDepartmentFetchById(id, model);
        addAttributeImagesPath(model, "fields-of-study");
        addAttributeBreadcrumbs(model, request);
        return "department/department-view";
    }

    @GetMapping("new")
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    String getNewDepartmentForm(Model model, HttpServletRequest request) {
        model.addAttribute("department", new DepartmentDto());
        addAttributesForCreateDepartment(model);
        addAttributeBreadcrumbs(model, request);
        return "department/department-create-form";
    }

    @PostMapping("new")
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    String createDepartment(@Valid @ModelAttribute("department") final DepartmentDto dto,
                            final BindingResult result,
                            final RedirectAttributes redirectAttributes,
                            final HttpServletRequest request,
                            final Model model) {
        if (result.hasErrors()) {
            addAttributesForCreateDepartment(model);
            addAttributeBreadcrumbs(model, request);
            return "department/department-create-form";
        }
        final var saved = service.create(dto);
        addFlashAttributeSuccess(redirectAttributes, saved);
        redirectAttributes.addFlashAttribute("createSuccess", "saved");
        return REDIRECT_DASHBOARD_DEPARTMENTS;
    }

    @GetMapping("{id}/edit")
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    String getEditDepartmentForm(@PathVariable final Long id,
                                 final Model model,
                                 final HttpServletRequest request) {
        addAttributeDepartmentFetchById(id, model);
        addAttributesForUpdateDepartment(id, model);
        addAttributeBreadcrumbs(model, request);
        return "department/department-edit-form";
    }

    @PostMapping(value = "update")
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    String editDepartment(@Valid @ModelAttribute("department") final DepartmentDto dto,
                          final BindingResult result,
                          final RedirectAttributes redirectAttributes,
                          final HttpServletRequest request,
                          final Model model) {
        if (result.hasErrors()) {
            addAttributesForUpdateDepartment(dto.getId(), model);
            addAttributeBreadcrumbs(model, request);
            return "department/department-edit-form";
        }
        final var updated = service.update(dto);
        addFlashAttributeSuccess(redirectAttributes, updated);
        redirectAttributes.addFlashAttribute("updateSuccess", "updated");
        return REDIRECT_DASHBOARD_DEPARTMENTS;
    }

    @GetMapping("{id}/delete")
    @Secured({"ROLE_ADMIN"})
    String deleteDepartment(@PathVariable final Long id, final RedirectAttributes redirectAttributes) {
        final var dto = service.fetchById(id);
        service.remove(id);
        addFlashAttributeSuccess(redirectAttributes, dto);
        redirectAttributes.addFlashAttribute("deleteSuccess", "deleted");
        return REDIRECT_DASHBOARD_DEPARTMENTS;
    }

    private void addAttributeBreadcrumbs(final Model model, final HttpServletRequest request) {
        model.addAttribute("crumbs", BreadcrumbService.getBreadcrumbs(request.getRequestURI()));
    }

    private void addAttributesForCreateDepartment(final Model model) {
        model.addAttribute("teachers", teacherService.fetchAll());
        model.addAttribute("fieldsOfStudy", fieldOfStudyService.fetchAllWithNoDepartment());
        addAttributeImagesPath(model, "fields-of-study");
    }

    private static void addAttributeImagesPath(final Model model, final String directory) {
        model.addAttribute("imagesPath", Path.of("/img").resolve(directory));
    }

    private static void addFlashAttributeSuccess(final RedirectAttributes redirectAttributes, final DepartmentDto saved) {
        redirectAttributes.addFlashAttribute("success", saved);
    }

    private void addAttributeDepartmentFetchById(final Long id, final Model model) {
        model.addAttribute("department", service.fetchById(id));
    }

    private void addAttributesForUpdateDepartment(final Long id, final Model model) {
        final var dto = service.fetchById(id);
        model.addAttribute("teachers", teacherService.fetchAll());
        model.addAttribute("fieldsOfStudy", fieldOfStudyService.fetchAllWithGivenDepartmentDtoOrNoDepartment(dto));
        addAttributeImagesPath(model, "fields-of-study");
    }
}

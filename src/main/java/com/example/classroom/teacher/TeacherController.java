package com.example.classroom.teacher;

import com.example.classroom.auth.service.UserManagementService;
import com.example.classroom.breadcrumb.BreadcrumbService;
import com.example.classroom.pageable.PageableRequest;
import com.example.classroom.subject.SubjectService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.example.classroom.pageable.PageableService.getAttributesForPageable;

@Controller
@RequestMapping("dashboard/teachers")
@RequiredArgsConstructor
class TeacherController {

    static final String REDIRECT_DASHBOARD_TEACHERS = "redirect:/dashboard/teachers";
    private final TeacherService service;
    private final SubjectService subjectService;
    private final UserManagementService userService;

    @GetMapping
    String getPaginatedTeachers(@RequestParam(required = false) final String name,
                                @RequestParam(defaultValue = "1") final int page,
                                @RequestParam(defaultValue = "6") final int size,
                                @RequestParam(defaultValue = "firstName") final String sortField,
                                @RequestParam(defaultValue = "asc") final String sortDir,
                                final HttpServletRequest request,
                                final Model model) {
        final var pageableRequest = PageableRequest.builder()
            .searched(name)
            .pageNumber(page)
            .pageSize(size)
            .sortDirection(sortDir)
            .sortField(sortField)
            .build();
        final var user = userService.loadUserByUsername(request.getUserPrincipal().getName());
        final var pageTeachers = service.getAllTeachersFromRequest(pageableRequest, user);

        addAttributeBreadcrumb(model, request);
        model.addAttribute("teachers", pageTeachers.getContent());
        model.addAllAttributes(getAttributesForPageable(pageTeachers, pageableRequest));
        return "teacher/all-teachers";
    }

    @GetMapping("{id}")
    String getTeacher(@PathVariable final Long id,
                      final HttpServletRequest request,
                      final Model model) {
        addAttributeBreadcrumb(model, request);
        addAttributeTeacherById(id, model);
        return "teacher/teacher-view";
    }

    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    @GetMapping("new")
    String getNewTeacherForm(final HttpServletRequest request,
                             final Model model) {
        addAttributeBreadcrumb(model, request);
        model.addAttribute("teacher", new TeacherDto());
        addAttributesSubjects(model);
        return "teacher/teacher-create-form";
    }

    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    @PostMapping(value = "new")
    String createTeacher(@Valid @ModelAttribute("teacher") final TeacherDto dto,
                         final BindingResult result,
                         final RedirectAttributes redirectAttributes,
                         final HttpServletRequest request,
                         final Model model) {
        if (result.hasErrors()) {
            addAttributeBreadcrumb(model, request);
            addAttributesSubjects(model);
            return "teacher/teacher-create-form";
        }
        final var saved = service.create(dto);
        addFlashAttributeSuccess(redirectAttributes, saved);
        redirectAttributes.addFlashAttribute("createSuccess", "saved");
        return REDIRECT_DASHBOARD_TEACHERS;
    }

    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    @GetMapping("{id}/edit")
    String editTeacher(@PathVariable final Long id,
                       final HttpServletRequest request,
                       final Model model) {
        addAttributeBreadcrumb(model, request);
        addAttributeTeacherById(id, model);
        addAttributesSubjects(model);
        return "teacher/teacher-edit-form";
    }

    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    @PostMapping(value = "update")
    String editTeacher(@Valid @ModelAttribute("teacher") final TeacherDto dto,
                       final BindingResult result,
                       final RedirectAttributes redirectAttributes,
                       final HttpServletRequest request,
                       final Model model) {
        if (result.hasErrors()) {
            addAttributeBreadcrumb(model, request);
            addAttributesSubjects(model);
            return "teacher/teacher-edit-form";
        }
        final var updated = service.update(dto);
        addFlashAttributeSuccess(redirectAttributes, updated);
        redirectAttributes.addFlashAttribute("updateSuccess", "updated");
        return REDIRECT_DASHBOARD_TEACHERS;
    }

    @Secured({"ROLE_ADMIN"})
    @GetMapping("{id}/delete")
    String deleteStudent(@PathVariable final Long id,
                         final RedirectAttributes redirectAttributes) {
        final var dto = service.fetchById(id);
        service.remove(id);
        addFlashAttributeSuccess(redirectAttributes, dto);
        redirectAttributes.addFlashAttribute("deleteSuccess", "deleted");
        return REDIRECT_DASHBOARD_TEACHERS;
    }

    private void addAttributesSubjects(final Model model) {
        model.addAttribute("subjects", subjectService.fetchAll());
    }

    private void addAttributeTeacherById(final Long id, final Model model) {
        model.addAttribute("teacher", service.fetchById(id));
    }

    private void addFlashAttributeSuccess(final RedirectAttributes redirectAttributes, final TeacherDto dto) {
        redirectAttributes.addFlashAttribute("success", dto);
    }

    private void addAttributeBreadcrumb(final Model model, final HttpServletRequest request) {
        model.addAttribute("crumbs", BreadcrumbService.getBreadcrumbs(request.getRequestURI()));
    }
}

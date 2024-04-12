package com.example.classroom.teacher;

import com.example.classroom.auth.service.UserManagementService;
import com.example.classroom.breadcrumb.BreadcrumbService;
import com.example.classroom.pageable.PageableRequest;
import com.example.classroom.subject.SubjectService;
import com.example.classroom.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    private final TeacherService service;
    private final SubjectService subjectService;
    private final UserManagementService userService;
    private final BreadcrumbService crumb;

    static final String REDIRECT_DASHBOARD_TEACHERS = "redirect:/dashboard/teachers";

    @GetMapping
    String getPaginatedTeachers(@RequestParam(required = false) String name,
                                @RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "6") int size,
                                @RequestParam(defaultValue = "firstName") String sortField,
                                @RequestParam(defaultValue = "asc") String sortDir,
                                HttpServletRequest request,
                                Model model) {
        final var pageableRequest = PageableRequest.builder()
                .searched(name)
                .pageNumber(page)
                .pageSize(size)
                .sortDirection(sortDir)
                .sortField(sortField)
                .build();
        User user = userService.loadUserByUsername(request.getUserPrincipal().getName());
        Page<TeacherDto> pageTeachers = service.getAllTeachersFromRequest(pageableRequest, user);

        addAttributeBreadcrumb(model, request);
        model.addAttribute("teachers", pageTeachers.getContent());
        model.addAllAttributes(getAttributesForPageable(pageTeachers, pageableRequest));
        return "teacher/all-teachers";
    }

    @GetMapping("{id}")
    String getTeacher(@PathVariable Long id,
                      HttpServletRequest request,
                      Model model) {
        addAttributeBreadcrumb(model, request);
        addAttributeTeacherById(id, model);
        return "teacher/teacher-view";
    }

    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    @GetMapping("new")
    String getNewTeacherForm(HttpServletRequest request,
                             Model model) {
        addAttributeBreadcrumb(model, request);
        model.addAttribute("teacher", new TeacherDto());
        addAttributesSubjects(model);
        return "teacher/teacher-create-form";
    }

    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    @PostMapping(value = "new")
    String createTeacher(@Valid @ModelAttribute("teacher") TeacherDto dto,
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
    String editTeacher(@PathVariable Long id,
                       HttpServletRequest request,
                       Model model) {
        addAttributeBreadcrumb(model, request);
        addAttributeTeacherById(id, model);
        addAttributesSubjects(model);
        return "teacher/teacher-edit-form";
    }

    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    @PostMapping(value = "update")
    String editTeacher(@Valid @ModelAttribute("teacher") TeacherDto dto,
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
    String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
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

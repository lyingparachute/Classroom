package com.example.classroom.student;

import com.example.classroom.auth.service.UserManagementService;
import com.example.classroom.breadcrumb.BreadcrumbService;
import com.example.classroom.fieldofstudy.FieldOfStudyService;
import com.example.classroom.pageable.PageableRequest;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.example.classroom.pageable.PageableService.getAttributesForPageable;

@Controller
@RequestMapping("dashboard/students")
@RequiredArgsConstructor
class StudentController {

    static final String REDIRECT_DASHBOARD_STUDENTS = "redirect:/dashboard/students";
    private final StudentService service;
    private final TeacherService teacherService;
    private final UserManagementService userService;
    private final FieldOfStudyService fieldOfStudyService;

    @GetMapping
    @Secured({"ROLE_TEACHER", "ROLE_DEAN", "ROLE_ADMIN"})
    String getPaginatedStudents(@RequestParam(required = false) final String name,
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
        final var pageStudents = service.getAllStudentsFromRequest(pageableRequest, user);

        addAttributeBreadcrumb(model, request);
        model.addAttribute("students", pageStudents.getContent());
        model.addAllAttributes(getAttributesForPageable(pageStudents, pageableRequest));
        return "student/all-students";
    }

    @GetMapping("{id}")
    @Secured({"ROLE_TEACHER", "ROLE_DEAN", "ROLE_ADMIN"})
    String getStudent(@PathVariable final Long id,
                      final HttpServletRequest request,
                      final Model model) {
        addAttributeBreadcrumb(model, request);
        addAttributeStudentById(id, model);
        return "student/student-view";
    }

    @GetMapping("new")
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    String getNewStudentForm(final HttpServletRequest request,
                             final Model model) {
        addAttributeBreadcrumb(model, request);
        model.addAttribute("student", new StudentDto());
        addAttributesTeachersAndFieldsOfStudy(model);
        return "student/student-create-form";
    }

    @PostMapping(value = "new")
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    String createStudent(@Valid @ModelAttribute("student") final StudentDto dto,
                         final BindingResult result,
                         final RedirectAttributes redirectAttributes,
                         final HttpServletRequest request,
                         final Model model) {
        if (result.hasErrors()) {
            addAttributeBreadcrumb(model, request);
            addAttributesTeachersAndFieldsOfStudy(model);
            return "student/student-create-form";
        }
        final var saved = service.create(dto);
        addFlashAttributeSuccess(redirectAttributes, saved);
        redirectAttributes.addFlashAttribute("createSuccess", "saved");
        return REDIRECT_DASHBOARD_STUDENTS;
    }

    @GetMapping("edit/{id}")
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    String editStudentForm(@PathVariable final Long id,
                           final HttpServletRequest request,
                           final Model model) {
        addAttributeBreadcrumb(model, request);
        addAttributeStudentById(id, model);
        addAttributesTeachersAndFieldsOfStudy(model);
        return "student/student-edit-form";
    }

    @PostMapping(value = "update")
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    String editStudent(@Valid @ModelAttribute("student") final StudentDto dto,
                       final BindingResult result,
                       final RedirectAttributes redirectAttributes,
                       final HttpServletRequest request,
                       final Model model) {
        if (result.hasErrors()) {
            addAttributeBreadcrumb(model, request);
            addAttributesTeachersAndFieldsOfStudy(model);
            return "student/student-edit-form";
        }
        final var updated = service.update(dto);
        addFlashAttributeSuccess(redirectAttributes, updated);
        redirectAttributes.addFlashAttribute("updateSuccess", "updated");
        return REDIRECT_DASHBOARD_STUDENTS;
    }

    @GetMapping("delete/{id}")
    @Secured({"ROLE_ADMIN"})
    String deleteStudent(@PathVariable final Long id,
                         final RedirectAttributes redirectAttributes) {
        final var dto = service.fetchById(id);
        service.remove(id);
        addFlashAttributeSuccess(redirectAttributes, dto);
        redirectAttributes.addFlashAttribute("deleteSuccess", "deleted");
        return REDIRECT_DASHBOARD_STUDENTS;
    }

    private void addAttributeStudentById(final Long id,
                                         final Model model) {
        model.addAttribute("student", service.fetchById(id));
    }

    private void addAttributesTeachersAndFieldsOfStudy(final Model model) {
        model.addAttribute("teachers", teacherService.fetchAll());
        model.addAttribute("fieldsOfStudy", fieldOfStudyService.fetchAll());
    }

    private void addFlashAttributeSuccess(final RedirectAttributes redirectAttributes, final StudentDto dto) {
        redirectAttributes.addFlashAttribute("success", dto);
    }

    private void addAttributeBreadcrumb(final Model model, final HttpServletRequest request) {
        model.addAttribute("crumbs", BreadcrumbService.getBreadcrumbs(request.getRequestURI()));
    }
}

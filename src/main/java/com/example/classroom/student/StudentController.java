package com.example.classroom.student;

import com.example.classroom.auth.service.UserManagementService;
import com.example.classroom.breadcrumb.BreadcrumbService;
import com.example.classroom.fieldOfStudy.FieldOfStudyService;
import com.example.classroom.pageable.PageableRequest;
import com.example.classroom.teacher.TeacherService;
import com.example.classroom.user.User;
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

import static com.example.classroom.pageable.PageableService.getAttributesForPageable;

@Controller
@RequestMapping("dashboard/students")
@RequiredArgsConstructor
class StudentController {

    private final StudentService service;
    private final TeacherService teacherService;
    private final UserManagementService userService;
    private final FieldOfStudyService fieldOfStudyService;
    private final BreadcrumbService crumb;

    static final String REDIRECT_DASHBOARD_STUDENTS = "redirect:/dashboard/students";


    @GetMapping
    @Secured({"ROLE_TEACHER", "ROLE_DEAN", "ROLE_ADMIN"})
    String getPaginatedStudents(@RequestParam(required = false) String name,
                                @RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "6") int size,
                                @RequestParam(defaultValue = "firstName") String sortField,
                                @RequestParam(defaultValue = "asc") String sortDir,
                                HttpServletRequest request,
                                Model model) {
        PageableRequest pageableRequest = PageableRequest.builder()
                .name(name)
                .pageNumber(page)
                .pageSize(size)
                .sortDir(sortDir)
                .sortField(sortField)
                .build();
        User user = userService.loadUserByUsername(request.getUserPrincipal().getName());
        Page<StudentDto> pageStudents = service.getAllStudentsFromRequest(pageableRequest, user);

        addAttributeBreadcrumb(model, request);
        model.addAttribute("students", pageStudents.getContent());
        model.addAllAttributes(getAttributesForPageable(pageStudents, pageableRequest));
        return "student/all-students";
    }

    @GetMapping("{id}")
    @Secured({"ROLE_TEACHER", "ROLE_DEAN", "ROLE_ADMIN"})
    String getStudent(@PathVariable Long id,
                      HttpServletRequest request,
                      Model model) {
        addAttributeBreadcrumb(model, request);
        addAttributeStudentById(id, model);
        return "student/student-view";
    }

    @GetMapping("new")
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    String getNewStudentForm(HttpServletRequest request,
                             Model model) {
        addAttributeBreadcrumb(model, request);
        model.addAttribute("student", new StudentDto());
        addAttributesTeachersAndFieldsOfStudy(model);
        return "student/student-create-form";
    }

    @PostMapping(value = "new")
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    String createStudent(@Valid @ModelAttribute("student") StudentDto dto,
                         BindingResult result,
                         RedirectAttributes redirectAttributes,
                         HttpServletRequest request,
                         Model model) {
        if (result.hasErrors()) {
            addAttributeBreadcrumb(model, request);
            addAttributesTeachersAndFieldsOfStudy(model);
            return "student/student-create-form";
        }
        StudentDto saved = service.create(dto);
        addFlashAttributeSuccess(redirectAttributes, saved);
        redirectAttributes.addFlashAttribute("createSuccess", "saved");
        return REDIRECT_DASHBOARD_STUDENTS;
    }

    @GetMapping("edit/{id}")
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    String editStudentForm(@PathVariable Long id,
                           HttpServletRequest request,
                           Model model) {
        addAttributeBreadcrumb(model, request);
        addAttributeStudentById(id, model);
        addAttributesTeachersAndFieldsOfStudy(model);
        return "student/student-edit-form";
    }

    @PostMapping(value = "update")
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    String editStudent(@Valid @ModelAttribute("student") StudentDto dto,
                       BindingResult result,
                       RedirectAttributes redirectAttributes,
                       HttpServletRequest request,
                       Model model) {
        if (result.hasErrors()) {
            addAttributeBreadcrumb(model, request);
            addAttributesTeachersAndFieldsOfStudy(model);
            return "student/student-edit-form";
        }
        StudentDto updated = service.update(dto);
        addFlashAttributeSuccess(redirectAttributes, updated);
        redirectAttributes.addFlashAttribute("updateSuccess", "updated");
        return REDIRECT_DASHBOARD_STUDENTS;
    }

    @GetMapping("delete/{id}")
    @Secured({"ROLE_ADMIN"})
    String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        StudentDto dto = service.fetchById(id);
        service.remove(id);
        addFlashAttributeSuccess(redirectAttributes, dto);
        redirectAttributes.addFlashAttribute("deleteSuccess", "deleted");
        return REDIRECT_DASHBOARD_STUDENTS;
    }

    private void addAttributeStudentById(Long id, Model model) {
        model.addAttribute("student", service.fetchById(id));
    }

    private void addAttributesTeachersAndFieldsOfStudy(Model model) {
        model.addAttribute("teachers", teacherService.fetchAll());
        model.addAttribute("fieldsOfStudy", fieldOfStudyService.fetchAll());
    }

    private void addFlashAttributeSuccess(RedirectAttributes redirectAttributes, StudentDto dto) {
        redirectAttributes.addFlashAttribute("success", dto);
    }

    private void addAttributeBreadcrumb(Model model, HttpServletRequest request) {
        model.addAttribute("crumbs", crumb.getBreadcrumbs(request.getRequestURI()));
    }
}

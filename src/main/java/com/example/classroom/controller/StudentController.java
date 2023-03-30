package com.example.classroom.controller;

import com.example.classroom.breadcrumb.BreadcrumbService;
import com.example.classroom.dto.StudentDto;
import com.example.classroom.service.FieldOfStudyService;
import com.example.classroom.service.StudentService;
import com.example.classroom.service.TeacherService;
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

import java.util.List;

@Controller
@RequestMapping("dashboard/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService service;
    private final TeacherService teacherService;
    private final FieldOfStudyService fieldOfStudyService;
    private final BreadcrumbService crumb;

    public static final String REDIRECT_DASHBOARD_STUDENTS = "redirect:/dashboard/students";


    @GetMapping
    @Secured({"ROLE_TEACHER", "ROLE_DEAN", "ROLE_ADMIN"})
    public String getPaginatedStudents(@RequestParam(required = false) String name,
                                       @RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "6") int size,
                                       @RequestParam(defaultValue = "firstName") String sortField,
                                       @RequestParam(defaultValue = "asc") String sortDir,
                                       HttpServletRequest request,
                                       Model model) {
        addAttributeBreadcrumb(model, request);

        Page<StudentDto> pageStudents;
        if (name == null) {
            pageStudents = service.fetchAllPaginated(page, size, sortField, sortDir);
        } else {
            pageStudents = service.findByFirstOrLastNamePaginated(page, size, sortField, sortDir, name);
            model.addAttribute("name", name);
        }
        List<StudentDto> students = pageStudents.getContent();
        int firstItemShownOnPage = 1;
        int lastItemShownOnPage;
        if (page == 1 && pageStudents.getTotalElements() <= size) {
            lastItemShownOnPage = Math.toIntExact(pageStudents.getTotalElements());
        } else if (page == 1 && pageStudents.getTotalElements() > size) {
            lastItemShownOnPage = size * page;
        } else if (page != 1 && pageStudents.getTotalElements() <= ((long) size * page)) {
            firstItemShownOnPage = size * (page - 1) + 1;
            lastItemShownOnPage = Math.toIntExact(pageStudents.getTotalElements());
        } else {
            firstItemShownOnPage = size * (page - 1) + 1;
            lastItemShownOnPage = size * (page - 1) + size;
        }

        model.addAttribute("students", students);
        model.addAttribute("currentPage", pageStudents.getNumber() + 1);
        model.addAttribute("totalPages", pageStudents.getTotalPages());
        model.addAttribute("totalItems", pageStudents.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("firstItemShownOnPage", firstItemShownOnPage);
        model.addAttribute("lastItemShownOnPage", lastItemShownOnPage);
        return "student/all-students";
    }

    @GetMapping("{id}")
    @Secured({"ROLE_TEACHER", "ROLE_DEAN", "ROLE_ADMIN"})
    public String getStudent(@PathVariable Long id,
                             HttpServletRequest request,
                             Model model) {
        addAttributeBreadcrumb(model, request);
        addAttributeStudentById(id, model);
        return "student/student-view";
    }

    @GetMapping("new")
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    public String getNewStudentForm(HttpServletRequest request,
                                    Model model) {
        addAttributeBreadcrumb(model, request);
        model.addAttribute("student", new StudentDto());
        addAttributesTeachersAndFieldsOfStudy(model);
        return "student/student-create-form";
    }

    @PostMapping(value = "new")
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    public String createStudent(@Valid @ModelAttribute("student") StudentDto dto,
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
    public String editStudentForm(@PathVariable Long id,
                                  HttpServletRequest request,
                                  Model model) {
        addAttributeBreadcrumb(model, request);
        addAttributeStudentById(id, model);
        addAttributesTeachersAndFieldsOfStudy(model);
        return "student/student-edit-form";
    }

    @PostMapping(value = "update")
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    public String editStudent(@Valid @ModelAttribute("student") StudentDto dto,
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
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
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

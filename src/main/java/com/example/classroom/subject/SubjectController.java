package com.example.classroom.subject;


import com.example.classroom.breadcrumb.BreadcrumbService;
import com.example.classroom.fieldofstudy.FieldOfStudyService;
import com.example.classroom.pageable.PageableRequest;
import com.example.classroom.teacher.TeacherService;
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

@Controller
@RequestMapping("dashboard/subjects")
@RequiredArgsConstructor
class SubjectController {

    static final String REDIRECT_DASHBOARD_SUBJECTS = "redirect:/dashboard/subjects";
    static final String SUBJECT_EDIT_FORM = "subject/subject-edit-form";
    private final SubjectService service;
    private final TeacherService teacherService;
    private final FieldOfStudyService fieldOfStudyService;

    @GetMapping
    String getSubjects(@RequestParam(required = false) final String name,
                       @RequestParam(defaultValue = "1") final int page,
                       @RequestParam(defaultValue = "6") final int size,
                       @RequestParam(defaultValue = "id") final String sortField,
                       @RequestParam(defaultValue = "asc") final String sortDir,
                       final HttpServletRequest request,
                       final Model model) {
        addAttributeBreadcrumb(model, request);

        final Page<SubjectDto> pageSubjects;
        if (name == null) {
            pageSubjects = service.fetchAllPaginated(page, size, sortField, sortDir);
        } else {
            final var pageableRequest = PageableRequest.builder()
                .searched(name)
                .pageNumber(page)
                .pageSize(size)
                .sortField(sortField)
                .sortDirection(sortDir)
                .build();
            pageSubjects = service.findByNamePaginated(pageableRequest);
            model.addAttribute("name", name);
        }
        final var subjects = pageSubjects.getContent();
        int firstItemShownOnPage = 1;
        final int lastItemShownOnPage;
        if (page == 1 && pageSubjects.getTotalElements() <= size) {
            lastItemShownOnPage = Math.toIntExact(pageSubjects.getTotalElements());
        } else if (page == 1 && pageSubjects.getTotalElements() > size) {
            lastItemShownOnPage = size * page;
        } else if (page != 1 && pageSubjects.getTotalElements() <= ((long) size * page)) {
            firstItemShownOnPage = size * (page - 1) + 1;
            lastItemShownOnPage = Math.toIntExact(pageSubjects.getTotalElements());
        } else {
            firstItemShownOnPage = size * (page - 1) + 1;
            lastItemShownOnPage = size * (page - 1) + size;
        }

        model.addAttribute("subjects", subjects);
        model.addAttribute("currentPage", pageSubjects.getNumber() + 1);
        model.addAttribute("totalPages", pageSubjects.getTotalPages());
        model.addAttribute("totalItems", pageSubjects.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("firstItemShownOnPage", firstItemShownOnPage);
        model.addAttribute("lastItemShownOnPage", lastItemShownOnPage);
        return "subject/all-subjects";
    }

    @GetMapping("{id}")
    String getSubject(@PathVariable final Long id,
                      final HttpServletRequest request,
                      final Model model) {
        addAttributeBreadcrumb(model, request);
        addAttributeSubjectById(id, model);
        return "subject/subject-view";
    }

    @GetMapping("new")
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    String getNewSubjectForm(final HttpServletRequest request,
                             final Model model) {
        addAttributeBreadcrumb(model, request);
        model.addAttribute("subject", new SubjectDto());
        addAttributeTeachersAndFieldsOfStudy(model);
        return "subject/subject-create-form";
    }

    @PostMapping(value = "new")
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    String createSubject(@Valid @ModelAttribute("subject") final SubjectDto dto,
                         final BindingResult result,
                         final RedirectAttributes redirectAttributes,
                         final HttpServletRequest request,
                         final Model model) {
        if (result.hasErrors()) {
            addAttributeBreadcrumb(model, request);
            addAttributeTeachersAndFieldsOfStudy(model);
            return "subject/subject-create-form";
        }
        final var saved = service.create(dto);
        addFlashAttributeSuccess(redirectAttributes, saved);
        redirectAttributes.addFlashAttribute("createSuccess", "saved");
        return REDIRECT_DASHBOARD_SUBJECTS;
    }

    @GetMapping("{id}/edit")
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    String editSubjectForm(@PathVariable final Long id,
                           final HttpServletRequest request,
                           final Model model) {
        addAttributeBreadcrumb(model, request);
        addAttributeSubjectById(id, model);
        addAttributeTeachersAndFieldsOfStudy(model);
        return SUBJECT_EDIT_FORM;
    }

    @PostMapping(value = "update")
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    String editSubject(@Valid @ModelAttribute("subject") final SubjectDto dto,
                       final BindingResult result,
                       final RedirectAttributes redirectAttributes,
                       final HttpServletRequest request,
                       final Model model) {
        if (result.hasErrors()) {
            addAttributeBreadcrumb(model, request);
            addAttributeTeachersAndFieldsOfStudy(model);
            return SUBJECT_EDIT_FORM;
        }
        final var updated = service.update(dto);
        addFlashAttributeSuccess(redirectAttributes, updated);
        redirectAttributes.addFlashAttribute("updateSuccess", "updated");
        return REDIRECT_DASHBOARD_SUBJECTS;
    }

    @GetMapping("{id}/delete")
    @Secured({"ROLE_ADMIN"})
    String deleteSubject(@PathVariable final Long id,
                         final RedirectAttributes redirectAttributes) {
        final var dto = service.fetchById(id);
        service.remove(id);
        addFlashAttributeSuccess(redirectAttributes, dto);
        redirectAttributes.addFlashAttribute("deleteSuccess", "deleted");
        return REDIRECT_DASHBOARD_SUBJECTS;
    }

    private void addAttributeSubjectById(final Long id, final Model model) {
        model.addAttribute("subject", service.fetchById(id));
    }

    private void addAttributeTeachersAndFieldsOfStudy(final Model model) {
        model.addAttribute("teachers", teacherService.fetchAll());
        model.addAttribute("fieldsOfStudy", fieldOfStudyService.fetchAll());
    }

    private void addFlashAttributeSuccess(final RedirectAttributes redirectAttributes, final SubjectDto dto) {
        redirectAttributes.addFlashAttribute("success", dto);
    }

    private void addAttributeBreadcrumb(final Model model, final HttpServletRequest request) {
        model.addAttribute("crumbs", BreadcrumbService.getBreadcrumbs(request.getRequestURI()));
    }
}

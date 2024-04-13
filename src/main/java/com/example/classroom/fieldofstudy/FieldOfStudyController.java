package com.example.classroom.fieldofstudy;


import com.example.classroom.breadcrumb.BreadcrumbService;
import com.example.classroom.department.DepartmentService;
import com.example.classroom.enums.LevelOfEducation;
import com.example.classroom.fileupload.FileUploadUtil;
import com.example.classroom.subject.SubjectService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

@Controller
@RequestMapping("dashboard/fields-of-study")
@RequiredArgsConstructor
class FieldOfStudyController {

    private final FieldOfStudyService service;
    private final DepartmentService departmentService;
    private final SubjectService subjectService;
    static final String UPLOAD_DIR = "fields-of-study/";
    static final String REDIRECT_DASHBOARD_FIELDS_OF_STUDY = "redirect:/dashboard/fields-of-study";

    @GetMapping()
    String getAllFieldsOfStudy(final Model model,
                               final HttpServletRequest request) {
        model.addAttribute("fieldsOfStudyMap", service.fetchAllGroupedByNameAndSortedByName());
        model.addAttribute("firstFieldsOfStudy", service.fetchAllByLevelOfEducationSortedByName(LevelOfEducation.FIRST));
        model.addAttribute("secondFieldsOfStudy", service.fetchAllByLevelOfEducationSortedByName(LevelOfEducation.SECOND));
        model.addAttribute("imagesPath", Path.of("/img").resolve(UPLOAD_DIR));
        addAttributeBreadcrumb(model, request);
        return "field-of-study/all-fieldsOfStudy";
    }

    @GetMapping("{id}")
    String getFieldOfStudy(@PathVariable final Long id,
                           final HttpServletRequest request,
                           final Model model) {
        addAttributeBreadcrumb(model, request);
        addAttributeFieldOfStudyFetchById(id, model);
        addAttributes(id, model);
        return "field-of-study/fieldOfStudy-view";
    }

    @GetMapping("{id}/subjects")
    String getFieldOfStudySubjects(@PathVariable final Long id,
                                   final HttpServletRequest request,
                                   final Model model) {
        addAttributeBreadcrumb(model, request);
        addAttributeFieldOfStudyFetchById(id, model);
        addAttributeNumberOfSemesters(id, model);
        addAttributeEctsPointsForEachSemester(id, model);
        addAttributeSubjectsMapGroupedBySemesters(id, model);
        model.addAttribute("hoursInSemester", service.calculateHoursInEachSemesterFromFieldOfStudy(id));
        model.addAttribute("subjects", service.fetchById(id).getSubjects());
        return "field-of-study/fieldOfStudy-subjects";
    }

    @GetMapping("new")
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    String getCreateFieldOfStudyForm(final Model model,
                                     final HttpServletRequest request) {
        addAttributeBreadcrumb(model, request);
        model.addAttribute("fieldOfStudy", new FieldOfStudyDto());
        addAttributeDepartments(model);
        return "field-of-study/fieldOfStudy-create-form";
    }

    @PostMapping("new")
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    String createFieldOfStudy(@Valid @ModelAttribute("fieldOfStudy") final FieldOfStudyDto dto,
                              final BindingResult result,
                              @RequestParam(value = "imageUpload") final MultipartFile multipartFile,
                              final RedirectAttributes redirectAttributes,
                              final HttpServletRequest request,
                              final Model model) throws IOException {
        if (result.hasErrors()) {
            addAttributeBreadcrumb(model, request);
            addAttributeDepartments(model);
            return "field-of-study/fieldOfStudy-create-form";
        }
        final var fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        dto.setImage(fileName);
        final var saved = service.create(dto);
        FileUploadUtil.saveFile(UPLOAD_DIR, fileName, multipartFile);
        addFlashAttributeSuccess(redirectAttributes, saved);
        redirectAttributes.addFlashAttribute("createSuccess", "saved");
        return REDIRECT_DASHBOARD_FIELDS_OF_STUDY;
    }

    @GetMapping("{id}/edit")
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    String getEditFieldOfStudyForm(@PathVariable final Long id,
                                   final HttpServletRequest request,
                                   final Model model) {
        addAttributeBreadcrumb(model, request);
        addAttributeFieldOfStudyFetchById(id, model);
        addAttributeDepartments(model);
        return "field-of-study/fieldOfStudy-edit-form";
    }

    @PostMapping("update")
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    String editFieldOfStudy(@Valid @ModelAttribute("fieldOfStudy") final FieldOfStudyDto dto,
                            @RequestParam(value = "imageUpload") final MultipartFile multipartFile,
                            final BindingResult result,
                            final RedirectAttributes redirectAttributes,
                            final HttpServletRequest request,
                            final Model model) throws IOException {
        if (result.hasErrors()) {
            addAttributeBreadcrumb(model, request);
            addAttributeDepartments(model);
            return "field-of-study/fieldOfStudy-edit-form";
        }
        final var fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        dto.setImage(fileName);
        final var updated = service.update(dto);
        FileUploadUtil.saveFile(UPLOAD_DIR, fileName, multipartFile);
        addFlashAttributeSuccess(redirectAttributes, updated);
        redirectAttributes.addFlashAttribute("updateSuccess", "updated");
        return REDIRECT_DASHBOARD_FIELDS_OF_STUDY;
    }

    @GetMapping("{id}/subjects/edit")
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    String getSubjectsForm(@PathVariable final Long id,
                           final HttpServletRequest request,
                           final Model model) {
        addAttributeBreadcrumb(model, request);
        addAttributeFieldOfStudyFetchById(id, model);
        addAttributeAllSubjectsMapGroupedBySemesters(model);
        addAttributeNumberOfSemesters(id, model);
        return "field-of-study/fieldOfStudy-subjects-edit-form";
    }

    @PostMapping("subjects/update")
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    String editSubjects(@Valid @ModelAttribute("fieldOfStudy") final FieldOfStudyDto dto,
                        final RedirectAttributes redirectAttributes) {
        service.updateSubjects(dto);
        redirectAttributes.addFlashAttribute("updateSuccess", "update success");
        return "redirect:/dashboard/fields-of-study/" + dto.getId() + "/subjects";
    }

    @GetMapping("{id}/delete")
    @Secured({"ROLE_ADMIN"})
    String deleteFieldOfStudy(@PathVariable final Long id,
                              final RedirectAttributes redirectAttributes) {
        final var dto = service.fetchById(id);
        service.remove(id);
        addFlashAttributeSuccess(redirectAttributes, dto);
        redirectAttributes.addFlashAttribute("deleteSuccess", "deleted");
        return REDIRECT_DASHBOARD_FIELDS_OF_STUDY;
    }

    private void addAttributeBreadcrumb(final Model model, final HttpServletRequest request) {
        model.addAttribute("crumbs", BreadcrumbService.getBreadcrumbs(request.getRequestURI()));
    }

    private void addAttributes(final Long id, final Model model) {
        addAttributeDescriptionList(id, model);
        addAttributeTotalEctsPoints(id, model);
        addAttributeNumberOfSemesters(id, model);
        addAttributeImagePath(id, model);
    }

    private void addAttributeImagePath(final Long id, final Model model) {
        model.addAttribute("imagePath", service.getImagePath(id));
    }

    private void addAttributeNumberOfSemesters(final Long id, final Model model) {
        model.addAttribute("numberOfSemesters", service.getNumberOfSemesters(id));
    }

    private void addAttributeTotalEctsPoints(final Long id, final Model model) {
        model.addAttribute("ects", service.getSumOfEctsPointsFromAllSemesters(id));
    }

    private void addAttributeEctsPointsForEachSemester(final Long id, final Model model) {
        model.addAttribute("ectsMap", service.calculateEctsPointsForEachSemester(id));
    }

    private void addAttributeDescriptionList(final Long id, final Model model) {
        model.addAttribute("descriptionList", service.splitDescription(id));
    }

    private void addAttributeDepartments(final Model model) {
        model.addAttribute("departments", departmentService.fetchAll());
    }

    private void addAttributeFieldOfStudyFetchById(final Long id, final Model model) {
        model.addAttribute("fieldOfStudy", service.fetchById(id));
    }

    private void addAttributeAllSubjectsMapGroupedBySemesters(final Model model) {
        model.addAttribute("subjectsMap", subjectService.fetchAllGroupedBySemesters());
    }

    private void addAttributeSubjectsMapGroupedBySemesters(final Long id, final Model model) {
        model.addAttribute("semestersMap", service.fetchAllSubjectsFromFieldOfStudyGroupedBySemesters(id));
    }

    private void addFlashAttributeSuccess(final RedirectAttributes redirectAttributes, final FieldOfStudyDto dto) {
        redirectAttributes.addFlashAttribute("success", dto);
    }
}

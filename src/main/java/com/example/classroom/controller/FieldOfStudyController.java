package com.example.classroom.controller;


import com.example.classroom.breadcrumb.BreadcrumbService;
import com.example.classroom.dto.FieldOfStudyDto;
import com.example.classroom.enums.LevelOfEducation;
import com.example.classroom.fileupload.FileUploadUtil;
import com.example.classroom.service.DepartmentService;
import com.example.classroom.service.FieldOfStudyService;
import com.example.classroom.service.SubjectService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

@Controller
@RequestMapping("dashboard/fields-of-study")
@RequiredArgsConstructor
public class FieldOfStudyController {

    private final FieldOfStudyService service;
    private final DepartmentService departmentService;
    private final SubjectService subjectService;
    private final BreadcrumbService crumb;
    public static final String UPLOAD_DIR = "fields-of-study/";
    public static final String REDIRECT_DASHBOARD_FIELDS_OF_STUDY = "redirect:/dashboard/fields-of-study";

    @GetMapping()
    public String getAllFieldsOfStudy(Model model,
                                      HttpServletRequest request) {
        model.addAttribute("fieldsOfStudyMap", service.fetchAllGroupedByNameAndSortedByName());
        model.addAttribute("firstFieldsOfStudy", service.fetchAllByLevelOfEducationSortedByName(LevelOfEducation.FIRST));
        model.addAttribute("secondFieldsOfStudy", service.fetchAllByLevelOfEducationSortedByName(LevelOfEducation.SECOND));
        model.addAttribute("imagesPath", Path.of("/img").resolve(UPLOAD_DIR));
        addAttributeBreadcrumb(model, request);
        return "field-of-study/all-fieldsOfStudy";
    }

    @GetMapping("{id}")
    public String getFieldOfStudy(@PathVariable Long id,
                                  HttpServletRequest request,
                                  Model model) {
        addAttributeBreadcrumb(model, request);
        addAttributeFieldOfStudyFetchById(id, model);
        addAttributes(id, model);
        return "field-of-study/fieldOfStudy-view";
    }

    @GetMapping("{id}/subjects")
    public String getFieldOfStudySubjects(@PathVariable Long id,
                                          HttpServletRequest request,
                                          Model model) {
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
    public String getCreateFieldOfStudyForm(Model model,
                                            HttpServletRequest request) {
        addAttributeBreadcrumb(model, request);
        model.addAttribute("fieldOfStudy", new FieldOfStudyDto());
        addAttributeDepartments(model);
        return "field-of-study/fieldOfStudy-create-form";
    }

    @PostMapping(value = "new")
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    public String createFieldOfStudy(@Valid @ModelAttribute("fieldOfStudy") FieldOfStudyDto dto,
                                     @RequestParam(value = "imageUpload") MultipartFile multipartFile,
                                     RedirectAttributes redirectAttributes,
                                     BindingResult result,
                                     HttpServletRequest request,
                                     Model model) throws IOException {
        if (result.hasErrors()) {
            addAttributeBreadcrumb(model, request);
            addAttributeDepartments(model);
            return "field-of-study/fieldOfStudy-create-form";
        }
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        dto.setImage(fileName);
        FieldOfStudyDto saved = service.create(dto);
        FileUploadUtil.saveFile(UPLOAD_DIR, fileName, multipartFile);
        addFlashAttributeSuccess(redirectAttributes, saved);
        redirectAttributes.addFlashAttribute("createSuccess", "saved");
        return REDIRECT_DASHBOARD_FIELDS_OF_STUDY;
    }

    @GetMapping("edit/{id}")
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    public String getEditFieldOfStudyForm(@PathVariable Long id,
                                          HttpServletRequest request,
                                          Model model) {
        addAttributeBreadcrumb(model, request);
        addAttributeFieldOfStudyFetchById(id, model);
        addAttributeDepartments(model);
        return "field-of-study/fieldOfStudy-edit-form";
    }

    @PostMapping("update")
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    public String editFieldOfStudy(@Valid @ModelAttribute("fieldOfStudy") FieldOfStudyDto dto,
                                   @RequestParam(value = "imageUpload") MultipartFile multipartFile,
                                   BindingResult result,
                                   RedirectAttributes redirectAttributes,
                                   HttpServletRequest request,
                                   Model model) throws IOException {
        if (result.hasErrors()) {
            addAttributeBreadcrumb(model, request);
            addAttributeDepartments(model);
            return "field-of-study/fieldOfStudy-edit-form";
        }
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        dto.setImage(fileName);
        FieldOfStudyDto updated = service.update(dto);
        FileUploadUtil.saveFile(UPLOAD_DIR, fileName, multipartFile);
        addFlashAttributeSuccess(redirectAttributes, updated);
        redirectAttributes.addFlashAttribute("updateSuccess", "updated");
        return REDIRECT_DASHBOARD_FIELDS_OF_STUDY;
    }

    @GetMapping("edit/{id}/subjects")
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    public String getSubjectsForm(@PathVariable Long id,
                                  HttpServletRequest request,
                                  Model model) {
        addAttributeBreadcrumb(model, request);
        addAttributeFieldOfStudyFetchById(id, model);
        addAttributeAllSubjectsMapGroupedBySemesters(model);
        addAttributeNumberOfSemesters(id, model);
        return "field-of-study/fieldOfStudy-subjects-edit-form";
    }

    @PostMapping("subjects/update")
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    public String editSubjects(@Valid @ModelAttribute("fieldOfStudy") FieldOfStudyDto dto,
                               RedirectAttributes redirectAttributes) {
        service.updateSubjects(dto);
        redirectAttributes.addFlashAttribute("updateSuccess", "update success");
        return "redirect:/dashboard/fields-of-study/" + dto.getId() + "/subjects";
    }

    @GetMapping("delete/{id}")
    @Secured({"ROLE_ADMIN"})
    public String deleteFieldOfStudy(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        FieldOfStudyDto dto = service.fetchById(id);
        service.remove(id);
        addFlashAttributeSuccess(redirectAttributes, dto);
        redirectAttributes.addFlashAttribute("deleteSuccess", "deleted");
        return REDIRECT_DASHBOARD_FIELDS_OF_STUDY;
    }

    private void addAttributeBreadcrumb(Model model, HttpServletRequest request) {
        model.addAttribute("crumbs", crumb.getBreadcrumbs(request.getRequestURI()));
    }

    private void addAttributes(Long id, Model model) {
        addAttributeDescriptionList(id, model);
        addAttributeTotalEctsPoints(id, model);
        addAttributeNumberOfSemesters(id, model);
        addAttributeImagePath(id, model);
    }

    private void addAttributeImagePath(Long id, Model model) {
        model.addAttribute("imagePath", service.getImagePath(id));
    }

    private void addAttributeNumberOfSemesters(Long id, Model model) {
        model.addAttribute("numberOfSemesters", service.getNumberOfSemesters(id));
    }

    private void addAttributeTotalEctsPoints(Long id, Model model) {
        model.addAttribute("ects", service.getSumOfEctsPointsFromAllSemesters(id));
    }

    private void addAttributeEctsPointsForEachSemester(Long id, Model model) {
        model.addAttribute("ectsMap", service.calculateEctsPointsForEachSemester(id));
    }

    private void addAttributeDescriptionList(Long id, Model model) {
        model.addAttribute("descriptionList", service.splitDescription(id));
    }

    private void addAttributeDepartments(Model model) {
        model.addAttribute("departments", departmentService.fetchAll());
    }

    private void addAttributeFieldOfStudyFetchById(Long id, Model model) {
        model.addAttribute("fieldOfStudy", service.fetchById(id));
    }

    private void addAttributeAllSubjectsMapGroupedBySemesters(Model model) {
        model.addAttribute("subjectsMap", subjectService.fetchAllGroupedBySemesters());
    }

    private void addAttributeSubjectsMapGroupedBySemesters(Long id, Model model) {
        model.addAttribute("semestersMap", service.fetchAllSubjectsFromFieldOfStudyGroupedBySemesters(id));
    }

    private void addFlashAttributeSuccess(RedirectAttributes redirectAttributes, FieldOfStudyDto dto) {
        redirectAttributes.addFlashAttribute("success", dto);
    }
}

package com.example.classroom.controller;


import com.example.classroom.dto.FieldOfStudyDto;
import com.example.classroom.enums.LevelOfEducation;
import com.example.classroom.fileupload.FileUploadUtil;
import com.example.classroom.service.DepartmentService;
import com.example.classroom.service.FieldOfStudyService;
import com.example.classroom.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

@Controller
@RequestMapping("dashboard/fields-of-study")
@RequiredArgsConstructor
public class FieldOfStudyController {

    public static final String UPLOAD_DIR = "fields-of-study/";
    private final FieldOfStudyService service;
    private final DepartmentService departmentService;
    private final SubjectService subjectService;

    private static void addAttributeImagesFolderPath(Model model) {
        model.addAttribute("imagesPath", Path.of("/img").resolve(UPLOAD_DIR));
    }

    @GetMapping()
    public String getAllFieldsOfStudy(Model model) {
        model.addAttribute("fieldsOfStudyMap", service.fetchAllGroupedAndSortedByName());
        addAttributeImagesFolderPath(model);
        return "field-of-study/all-fieldsOfStudy";
    }

    @GetMapping("first")
    public String getFieldsOfStudyWithFirstLevelOfEducation(Model model) {
        model.addAttribute("fieldsOfStudy", service.fetchAllByLevelOfEducation(LevelOfEducation.FIRST));
        addAttributeImagesFolderPath(model);
        return "field-of-study/fieldsOfStudy-first";
    }

    @GetMapping("{id}")
    public String getFieldOfStudy(@PathVariable Long id, Model model) {
        addAttributeFieldOfStudyFetchById(id, model);
        addAttributes(id, model);
        return "field-of-study/fieldOfStudy-view";
    }

    @GetMapping("{id}/subjects")
    public String getFieldOfStudySubjects(@PathVariable Long id, Model model) {
        addAttributeFieldOfStudyFetchById(id, model);
        addAttributeNumberOfSemesters(id, model);
        addAttributeEctsPointsForEachSemester(id, model);
        addAttributeSubjectsMapGroupedBySemesters(id, model);
        model.addAttribute("hoursInSemester", service.calculateHoursInEachSemesterFromFieldOfStudy(id));
        model.addAttribute("subjects", service.fetchById(id).getSubjects());
        return "field-of-study/fieldOfStudy-subjects";
    }

    @GetMapping("new")
    public String getCreateFieldOfStudyForm(Model model) {
        addAttributeFieldOfStudyDto(model);
        addAttributeDepartments(model);
        return "field-of-study/fieldOfStudy-create-form";
    }

    @PostMapping("new")
    public String createFieldOfStudy(@Valid @ModelAttribute("fieldOfStudy") FieldOfStudyDto dto,
                                     @RequestParam(value = "imageUpload") MultipartFile multipartFile,
                                     BindingResult result,
                                     Model model) throws IOException {
        if (result.hasErrors()) {
            addAttributeDepartments(model);
            return "fieldOfStudy-form";
        }
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        dto.setImage(fileName);
        FieldOfStudyDto created = service.create(dto);
        FileUploadUtil.saveFile(UPLOAD_DIR, fileName, multipartFile);
        addAttributes(created.getId(), model);
        return "field-of-study/fieldOfStudy-create-success";
    }

    @GetMapping("delete/{id}")
    public String deleteFieldOfStudy(@PathVariable Long id, Model model) {
        service.remove(id);
        return "redirect:/dashboard/fields-of-study";
    }

    @GetMapping("edit/{id}")
    public String getEditFieldOfStudyForm(@PathVariable Long id, Model model) {
        addAttributeFieldOfStudyFetchById(id, model);
        addAttributeDepartments(model);
        return "field-of-study/fieldOfStudy-edit-form";
    }

    @PostMapping("update")
    public String editFieldOfStudy(@Valid @ModelAttribute("fieldOfStudy") FieldOfStudyDto dto,
                                   @RequestParam(value = "imageUpload") MultipartFile multipartFile,
                                   BindingResult result,
                                   Model model) throws IOException {
        if (result.hasErrors())
            return "field-of-study/fieldOfStudy-edit-form";

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        dto.setImage(fileName);
        service.update(dto);
        FileUploadUtil.saveFile(UPLOAD_DIR, fileName, multipartFile);

        addAttributeFieldOfStudyFetchById(dto.getId(), model);
        addAttributes(dto.getId(), model);
        return "field-of-study/fieldOfStudy-edit-success";
    }

    @GetMapping("edit/{id}/subjects")
    public String getSubjectsForm(@PathVariable Long id, Model model) {
        addAttributeFieldOfStudyFetchById(id, model);
        addAttributeAllSubjectsMapGroupedBySemesters(model);
        addAttributeNumberOfSemesters(id, model);
        return "field-of-study/fieldOfStudy-subjects-edit-form";
    }

    @PostMapping("subjects/update")
    public String editSubjects(@Valid @ModelAttribute("fieldOfStudy") FieldOfStudyDto dto) {
        service.updateSubjects(dto);
        return "redirect:/dashboard/fields-of-study/" + dto.getId() + "/subjects";
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

    private void addAttributeFieldOfStudyDto(Model model) {
        model.addAttribute("fieldOfStudy", new FieldOfStudyDto());
    }

    private void addAttributeAllSubjectsMapGroupedBySemesters(Model model) {
        model.addAttribute("subjectsMap", subjectService.fetchAllGroupedBySemesters());
    }

    private void addAttributeSubjectsMapGroupedBySemesters(Long id, Model model) {
        model.addAttribute("semestersMap", service.fetchAllSubjectsFromFieldOfStudyGroupedBySemesters(id));
    }

    @GetMapping("second")
    public String getFieldsOfStudyWithSecondLevelOfEducation(Model model) {
        model.addAttribute("fieldsOfStudy", service.fetchAllByLevelOfEducation(LevelOfEducation.SECOND));
        addAttributeImagesFolderPath(model);
        return "field-of-study/fieldsOfStudy-second";
    }
}

package com.example.classroom.controller;


import com.example.classroom.dto.SubjectDto;
import com.example.classroom.entity.Subject;
import com.example.classroom.service.FieldOfStudyService;
import com.example.classroom.service.SubjectService;
import com.example.classroom.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("dashboard/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;
    private final TeacherService teacherService;
    private final FieldOfStudyService fieldOfStudyService;
    private final ModelMapper mapper;

    @GetMapping
    public String getSubjects(@RequestParam(required = false) String name,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "6") int size,
                              @RequestParam(defaultValue = "id") String sortField,
                              @RequestParam(defaultValue = "asc") String sortDir,
                              Model model) {
        Page<SubjectDto> pageSubjects;
        if (name == null) {
            pageSubjects = subjectService.fetchAllPaginated(page, size, sortField, sortDir);
        } else {
            pageSubjects = subjectService.findByNamePaginated(page, size, sortField, sortDir, name);
            model.addAttribute("name", name);
        }
        List<SubjectDto> subjects = pageSubjects.getContent();
        int firstItemShownOnPage = 1;
        int lastItemShownOnPage;
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
        return "subject/subjects";
    }

    @GetMapping("{id}")
    public String getSubject(@PathVariable Long id, Model model) {
        SubjectDto subjectDto = subjectService.fetchById(id);
        model.addAttribute("subject", subjectDto);
        return "subject/subject";
    }

    @GetMapping("new")
    public String getNewSubjectForm(Model model) {
        model.addAttribute("subject", new SubjectDto());
        model.addAttribute("teachers", teacherService.fetchAll());
        return "subject/subject-form";
    }

    @PostMapping(value = "new")
    public String createSubject(@Valid @ModelAttribute("subject") Subject subject, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("teachers", teacherService.fetchAll());
            return "subject/subject-form";
        }
        subjectService.create(mapper.map(subject, SubjectDto.class));
        return "subject/subject-create-success";
    }

    @GetMapping("delete/{id}")
    public String deleteSubject(@PathVariable Long id) {
        subjectService.remove(id);
        return "redirect:/dashboard/subjects";
    }

    @GetMapping("edit/{id}")
    public String editSubjectForm(@PathVariable Long id, Model model) {
        SubjectDto dto = subjectService.fetchById(id);
        model.addAttribute("subject", dto);
        model.addAttribute("teachers", teacherService.fetchAll());
        model.addAttribute("fieldsOfStudy", fieldOfStudyService.fetchAll());
        return "subject/subject-edit-form";
    }

    @PostMapping(value = "update")
    public String editSubject(@Valid @ModelAttribute("subject") Subject subject, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("teachers", teacherService.fetchAll());
            return "subject/subject-edit-form";
        }

        SubjectDto updated = subjectService.update(mapper.map(subject, SubjectDto.class));
        if (updated == null) {
            return "error/404";
        }
        model.addAttribute("subject", updated);

        return "subject/subject-edit-success";
    }
}

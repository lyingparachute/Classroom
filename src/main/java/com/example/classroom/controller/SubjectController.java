package com.example.classroom.controller;


import com.example.classroom.dto.SubjectDto;
import com.example.classroom.service.SubjectService;
import com.example.classroom.service.TeacherService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("dashboard/subjects")
public class SubjectController {

    private final SubjectService subjectService;
    private final TeacherService teacherService;
    private final ModelMapper mapper;

    public SubjectController(SubjectService subjectService, TeacherService teacherService, ModelMapper mapper) {
        this.subjectService = subjectService;
        this.teacherService = teacherService;
        this.mapper = mapper;
    }

    @GetMapping
    public String getSubjects(@RequestParam(required = false) String name,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "6") int size,
                              @RequestParam(defaultValue = "name") String sortField,
                              @RequestParam(defaultValue = "asc") String sortDir,
                              Model model) {
        Page<SubjectDto> pageSubjects;
        if (name == null) {
            pageSubjects = subjectService.fetchAllPaginated(page, size, sortField, sortDir);
        } else {
            pageSubjects = subjectService.findByNamePaginated(page, size, sortField, sortDir, name);
            model.addAttribute("name", name);
        }
        List<SubjectDto> students = pageSubjects.getContent();
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

        model.addAttribute("subjects", students);
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
}

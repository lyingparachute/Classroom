package com.example.classroom.controller;

import com.example.classroom.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.nio.file.Path;

@Controller
@RequestMapping("dashboard/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService service;

    private static void addAttributeImagesPath(Model model, String directory) {
        model.addAttribute("imagesPath", Path.of("/img").resolve(directory));
    }

    @GetMapping()
    public String getAllDepartments(Model model) {
        model.addAttribute("departments", service.fetchAll());
        addAttributeImagesPath(model, "departments");
        return "department/all-departments";
    }

    @GetMapping("{id}")
    public String getDepartment(@PathVariable Long id, Model model) {
        addAttributeDepartmentFetchById(id, model);
        addAttributeImagesPath(model, "fields-of-study");
        return "department/department-view";
    }

    private void addAttributeDepartmentFetchById(Long id, Model model) {
        model.addAttribute("department", service.fetchById(id));
    }
}

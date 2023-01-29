package com.example.classroom.controller;

import com.example.classroom.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.nio.file.Path;

@Controller
@RequestMapping("dashboard/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService service;

    @GetMapping()
    public String getAllDepartments(Model model) {
        model.addAttribute("departments", service.fetchAll());
        model.addAttribute("imagesPath", Path.of("/img").resolve("departments"));
        return "department/all-departments";
    }
}

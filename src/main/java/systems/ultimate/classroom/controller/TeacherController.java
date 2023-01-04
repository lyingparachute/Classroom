package systems.ultimate.classroom.controller;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import systems.ultimate.classroom.dto.TeacherDto;
import systems.ultimate.classroom.entity.Teacher;
import systems.ultimate.classroom.service.StudentService;
import systems.ultimate.classroom.service.TeacherService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("dashboard/teachers")
public class TeacherController {

    private final TeacherService teacherService;
    private final StudentService studentService;
    private final ModelMapper mapper;

    public TeacherController(TeacherService teacherService, StudentService studentService, ModelMapper mapper) {
        this.teacherService = teacherService;
        this.studentService = studentService;
        this.mapper = mapper;
    }

    @GetMapping
    public String getPaginatedTeachers(@RequestParam(required = false) String name,
                                       @RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "6") int size,
                                       @RequestParam(defaultValue = "firstName") String sortField,
                                       @RequestParam(defaultValue = "asc") String sortDir,
                                       Model model) {
        Page<TeacherDto> pageTeachers;
        if (name == null) {
            pageTeachers = teacherService.fetchAllPaginated(page, size, sortField, sortDir);
        } else {
            pageTeachers = teacherService.findByFirstOrLastNamePaginated(page, size, sortField, sortDir, name);
            model.addAttribute("name", name);
        }
        List<TeacherDto> teachers = pageTeachers.getContent();
        int firstItemShownOnPage = 1;
        int lastItemShownOnPage;
        if (page == 1 && pageTeachers.getTotalElements() <= size) {
            lastItemShownOnPage = Math.toIntExact(pageTeachers.getTotalElements());
        } else if (page == 1 && pageTeachers.getTotalElements() > size) {
            lastItemShownOnPage = size * page;
        } else if (page != 1 && pageTeachers.getTotalElements() <= ((long) size * page)) {
            firstItemShownOnPage = size * (page - 1) + 1;
            lastItemShownOnPage = Math.toIntExact(pageTeachers.getTotalElements());
        } else {
            firstItemShownOnPage = size * (page - 1) + 1;
            lastItemShownOnPage = size * (page - 1) + size;
        }

        model.addAttribute("teachers", teachers);
        model.addAttribute("currentPage", pageTeachers.getNumber() + 1);
        model.addAttribute("totalPages", pageTeachers.getTotalPages());
        model.addAttribute("totalItems", pageTeachers.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("firstItemShownOnPage", firstItemShownOnPage);
        model.addAttribute("lastItemShownOnPage", lastItemShownOnPage);
        return "teachers";
    }

    @GetMapping("{id}")
    public String getTeacher(@PathVariable Long id, Model model) {
        TeacherDto studentDto = teacherService.fetchById(id);
        model.addAttribute("teacher", studentDto);
        return "teacher";
    }

    @GetMapping("new")
    public String getNewTeacherForm(Model model) {
        model.addAttribute("teacher", new TeacherDto());
        model.addAttribute("students", studentService.fetchAll());
        return "teacher-form";
    }

    @PostMapping(value = "new")
    public String createTeacher(@Valid @ModelAttribute("teacher") Teacher teacher, BindingResult result, Model model) {
        if (result.hasErrors()){
            model.addAttribute("students", studentService.fetchAll());
            return "teacher-form";
        }
        teacherService.create(mapper.map(teacher, TeacherDto.class));
        return "teacher-create-success";
    }

    @GetMapping("delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        teacherService.remove(id);
        return "redirect:/dashboard/teachers";
    }

    @GetMapping("edit/{id}")
    public String editTeacher(@PathVariable Long id, Model model) {
        TeacherDto dto = teacherService.fetchById(id);
        model.addAttribute("teacher", dto);
        model.addAttribute("students", studentService.fetchAll());
        return "teacher-edit-form";
    }

    @PostMapping(value = "update")
    public String editTeacher(@Valid @ModelAttribute("teacher") Teacher teacher, BindingResult result, Model model) {
        if (result.hasErrors()){
            model.addAttribute("students", studentService.fetchAll());
            return "teacher-edit-form";
        }
        TeacherDto updated = teacherService.update(mapper.map(teacher, TeacherDto.class));
        if (updated == null) {
            return "error/404";
        }
        model.addAttribute("teacher", updated);
        return "teacher-edit-success";
    }
}

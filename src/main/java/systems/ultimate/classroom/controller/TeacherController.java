package systems.ultimate.classroom.controller;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
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
@RequestMapping("teachers")
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
    public String getTeachers(Model model) {
        return getPaginatedTeachers(1, "firstName", "asc", model);
    }

    @GetMapping("/page/{pageNo}")
    public String getPaginatedTeachers(@PathVariable(value = "pageNo") int pageNo,
                                       @RequestParam("sortField") String sortField,
                                       @RequestParam("sortDir") String sortDir,
                                       Model model) {
        int pageSize = 2;
        Page<TeacherDto> page = teacherService.fetchAllPaginated(pageNo, pageSize, sortField, sortDir);
        List<TeacherDto> teachers = page.getContent();
        model.addAttribute("teachers", teachers);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "teachers";
    }

    @GetMapping("search")
    public String searchTeachers(@Param("name") String name, Model model){
        model.addAttribute("teachers", teacherService.findByFirstOrLastName(name));
        model.addAttribute("name", name);
        model.addAttribute("description", "Search for '" + name + "' in teachers list");
        model.addAttribute("tableDesc", "Teachers that have '"+ name +"' in their first or last name");
        return "teachers-search";
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
        return "redirect:/teachers";
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

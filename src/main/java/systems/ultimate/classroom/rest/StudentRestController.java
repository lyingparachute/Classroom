package systems.ultimate.classroom.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import systems.ultimate.classroom.dto.StudentDto;
import systems.ultimate.classroom.service.StudentService;

import java.util.List;

@RestController
@RequestMapping("api/students")
public class StudentRestController {

    private final StudentService studentService;

    public StudentRestController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping()
    public ResponseEntity<List<StudentDto>> getStudents() {
        List<StudentDto> students = studentService.fetchAll();
        return students.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(students);
    }


}

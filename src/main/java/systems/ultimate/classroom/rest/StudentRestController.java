package systems.ultimate.classroom.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping("create")
    public ResponseEntity<StudentDto> createStudent(@RequestBody StudentDto studentDto) {
        StudentDto created = studentService.create(studentDto);
        return created != null ?
                ResponseEntity.status(HttpStatus.CREATED)
                        .body(created) :
                ResponseEntity.badRequest().build();
    }

    @GetMapping("{id}")
    public ResponseEntity<StudentDto> getStudent(@PathVariable Long id) {
        StudentDto studentDto = studentService.fetchById(id);
        return studentDto != null ?
                ResponseEntity.ok(studentDto) :
                ResponseEntity.notFound().build();
    }

    @PutMapping
    public ResponseEntity<StudentDto> putStudent(@RequestBody StudentDto studentDto){
        StudentDto updated = studentService.update(studentDto);
        return updated != null ?
                ResponseEntity.status(HttpStatus.CREATED)
                        .body(updated) :
                ResponseEntity.badRequest().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.remove(id);
        return ResponseEntity.accepted().build();
    }
}

package com.example.classroom.api;

import com.example.classroom.dto.StudentDto;
import com.example.classroom.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/students")
@RequiredArgsConstructor
public class StudentRestController {

    private final StudentService service;

    @GetMapping("{id}")
    public ResponseEntity<StudentDto> getStudent(@PathVariable Long id) {
        StudentDto dto = service.fetchById(id);
        return dto != null ?
                ResponseEntity.ok(dto) :
                ResponseEntity.notFound().build();
    }

    @GetMapping()
    public ResponseEntity<List<StudentDto>> getStudents() {
        List<StudentDto> students = service.fetchAll();
        return students.isEmpty() ?
                ResponseEntity.notFound().build() :
                ResponseEntity.ok(students);
    }

    @PostMapping
    public ResponseEntity<StudentDto> createStudent(@Valid @RequestBody StudentDto studentDto) {
        StudentDto created = service.create(studentDto);
        return created != null ?
                ResponseEntity.status(HttpStatus.CREATED)
                        .body(created) :
                ResponseEntity.badRequest().build();
    }

    @PutMapping
    public ResponseEntity<StudentDto> updateStudent(@Valid @RequestBody StudentDto studentDto) {
        StudentDto updated = service.update(studentDto);
        return updated != null ?
                ResponseEntity.ok(updated) :
                ResponseEntity.badRequest().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        service.remove(id);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllStudents() {
        service.removeAll();
        return ResponseEntity.accepted().build();
    }
}

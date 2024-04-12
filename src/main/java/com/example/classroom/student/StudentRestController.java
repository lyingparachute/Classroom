package com.example.classroom.student;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/students")
@RequiredArgsConstructor
public class StudentRestController {

    private final StudentService service;

    @GetMapping("{id}")
    @Secured({"ROLE_TEACHER", "ROLE_DEAN", "ROLE_ADMIN"})
    ResponseEntity<StudentDto> getStudent(@PathVariable final Long id) {
        final var dto = service.fetchById(id);
        return dto != null ?
                ResponseEntity.ok(dto) :
                ResponseEntity.notFound().build();
    }

    @GetMapping
    @Secured({"ROLE_TEACHER", "ROLE_DEAN", "ROLE_ADMIN"})
    ResponseEntity<List<StudentDto>> getStudents() {
        final var students = service.fetchAll();
        return students.isEmpty() ?
                ResponseEntity.notFound().build() :
                ResponseEntity.ok(students);
    }

    @PostMapping
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    ResponseEntity<StudentDto> createStudent(@Valid @RequestBody final StudentDto studentDto) {
        final var created = service.create(studentDto);
        return created != null ?
                ResponseEntity.status(HttpStatus.CREATED)
                        .body(created) :
                ResponseEntity.badRequest().build();
    }

    @PutMapping
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    ResponseEntity<StudentDto> updateStudent(@Valid @RequestBody final StudentDto studentDto) {
        final var updated = service.update(studentDto);
        return updated != null ?
                ResponseEntity.ok(updated) :
                ResponseEntity.badRequest().build();
    }

    @DeleteMapping("{id}")
    @Secured({"ROLE_ADMIN"})
    ResponseEntity<Void> deleteStudent(@PathVariable final Long id) {
        service.remove(id);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping
    ResponseEntity<Void> deleteAllStudents() {
        service.removeAll();
        return ResponseEntity.accepted().build();
    }
}

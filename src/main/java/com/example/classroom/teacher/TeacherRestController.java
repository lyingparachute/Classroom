package com.example.classroom.teacher;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/teachers")
@RequiredArgsConstructor
class TeacherRestController {

    private final TeacherService teacherService;

    @GetMapping("{id}")
    ResponseEntity<TeacherDto> getTeacher(@PathVariable Long id) {
        TeacherDto dto = teacherService.fetchById(id);
        return dto != null ?
                ResponseEntity.ok(dto) :
                ResponseEntity.notFound().build();
    }

    @GetMapping
    ResponseEntity<List<TeacherDto>> getTeachers() {
        List<TeacherDto> teachers = teacherService.fetchAll();
        return teachers.isEmpty() ?
                ResponseEntity.notFound().build() :
                ResponseEntity.ok(teachers);
    }

    @PostMapping
    ResponseEntity<TeacherDto> createTeacher(@Valid @RequestBody TeacherDto teacherDto) {
        TeacherDto created = teacherService.create(teacherDto);
        return created != null ?
                ResponseEntity.status(HttpStatus.CREATED)
                        .body(created) :
                ResponseEntity.badRequest().build();
    }

    @PutMapping
    ResponseEntity<TeacherDto> updateTeacher(@Valid @RequestBody TeacherDto teacherDto) {
        TeacherDto updated = teacherService.update(teacherDto);
        return updated != null ?
                ResponseEntity.status(HttpStatus.OK)
                        .body(updated) :
                ResponseEntity.badRequest().build();
    }

    @DeleteMapping("{id}")
    ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        teacherService.remove(id);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping
    ResponseEntity<Void> deleteAllStudents() {
        teacherService.removeAll();
        return ResponseEntity.accepted().build();
    }
}

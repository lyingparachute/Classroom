package com.example.classroom.subject;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/subjects")
@RequiredArgsConstructor
class SubjectRestController {

    private final SubjectService subjectService;

    @GetMapping("{id}")
    ResponseEntity<SubjectDto> getSubject(@PathVariable Long id) {
        SubjectDto dto = subjectService.fetchById(id);
        return dto != null ?
                ResponseEntity.ok(dto) :
                ResponseEntity.notFound().build();
    }

    @GetMapping
    ResponseEntity<List<SubjectDto>> getAllSubjects() {
        List<SubjectDto> subjects = subjectService.fetchAll();
        return subjects.isEmpty() ?
                ResponseEntity.notFound().build() :
                ResponseEntity.ok(subjects);
    }

    @PostMapping
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    ResponseEntity<SubjectDto> createSubject(@Valid @RequestBody SubjectDto subjectDto) {
        SubjectDto created = subjectService.create(subjectDto);
        return created != null ?
                ResponseEntity.status(HttpStatus.CREATED)
                        .body(created) :
                ResponseEntity.badRequest().build();
    }

    @PutMapping
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    ResponseEntity<SubjectDto> updateSubject(@Valid @RequestBody SubjectDto studentDto) {
        SubjectDto updated = subjectService.update(studentDto);
        return updated != null ?
                ResponseEntity.ok(updated) :
                ResponseEntity.badRequest().build();
    }

    @DeleteMapping("{id}")
    @Secured({"ROLE_ADMIN"})
    ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        subjectService.remove(id);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping
    @Secured({"ROLE_ADMIN"})
    ResponseEntity<Void> deleteAllSubjects() {
        subjectService.removeAll();
        return ResponseEntity.accepted().build();
    }
}

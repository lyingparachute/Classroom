package com.example.classroom.subject;

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
@RequestMapping("api/subjects")
@RequiredArgsConstructor
class SubjectRestController {

    private final SubjectService subjectService;

    @GetMapping("{id}")
    ResponseEntity<SubjectDto> getSubject(@PathVariable final Long id) {
        final var dto = subjectService.fetchById(id);
        return dto != null ?
                ResponseEntity.ok(dto) :
                ResponseEntity.notFound().build();
    }

    @GetMapping
    ResponseEntity<List<SubjectDto>> getAllSubjects() {
        final var subjects = subjectService.fetchAll();
        return subjects.isEmpty() ?
                ResponseEntity.notFound().build() :
                ResponseEntity.ok(subjects);
    }

    @PostMapping
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    ResponseEntity<SubjectDto> createSubject(@Valid @RequestBody final SubjectDto subjectDto) {
        final var created = subjectService.create(subjectDto);
        return created != null ?
                ResponseEntity.status(HttpStatus.CREATED)
                        .body(created) :
                ResponseEntity.badRequest().build();
    }

    @PutMapping
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    ResponseEntity<SubjectDto> updateSubject(@Valid @RequestBody final SubjectDto studentDto) {
        final var updated = subjectService.update(studentDto);
        return updated != null ?
                ResponseEntity.ok(updated) :
                ResponseEntity.badRequest().build();
    }

    @DeleteMapping("{id}")
    @Secured({"ROLE_ADMIN"})
    ResponseEntity<Void> deleteSubject(@PathVariable final Long id) {
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

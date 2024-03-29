package com.example.classroom.fieldOfStudy;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/fields-of-study")
@RequiredArgsConstructor
class FieldOfStudyRestController {

    private final FieldOfStudyService service;

    @GetMapping("{id}")
    ResponseEntity<FieldOfStudyDto> getFieldOfStudy(@PathVariable Long id) {
        FieldOfStudyDto dto = service.fetchById(id);
        return dto != null ?
                ResponseEntity.ok(dto) :
                ResponseEntity.notFound().build();
    }

    @GetMapping
    ResponseEntity<List<FieldOfStudyDto>> getFieldsOfStudy() {
        List<FieldOfStudyDto> fieldsOfStudy = service.fetchAll();
        return fieldsOfStudy.isEmpty() ?
                ResponseEntity.notFound().build() :
                ResponseEntity.ok(fieldsOfStudy);
    }

    @PostMapping
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    ResponseEntity<FieldOfStudyDto> createFieldOfStudy(@Valid @RequestBody FieldOfStudyDto fieldOfStudyDto) {
        FieldOfStudyDto created = service.create(fieldOfStudyDto);
        return created != null ?
                ResponseEntity.status(HttpStatus.CREATED)
                        .body(created) :
                ResponseEntity.badRequest().build();
    }

    @PutMapping
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    ResponseEntity<FieldOfStudyDto> updateFieldOfStudy(@Valid @RequestBody FieldOfStudyDto fieldOfStudyDto) {
        FieldOfStudyDto updated = service.update(fieldOfStudyDto);
        return updated != null ?
                ResponseEntity.ok(updated) :
                ResponseEntity.badRequest().build();
    }

    @DeleteMapping("{id}")
    @Secured({"ROLE_ADMIN"})
    ResponseEntity<Void> deleteFieldOfStudy(@PathVariable Long id) {
        service.remove(id);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping
    @Secured({"ROLE_ADMIN"})
    ResponseEntity<Void> deleteAllFieldsOfStudy() {
        service.removeAll();
        return ResponseEntity.accepted().build();
    }
}

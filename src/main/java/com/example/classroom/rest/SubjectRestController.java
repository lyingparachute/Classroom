package com.example.classroom.rest;

import com.example.classroom.dto.SubjectDto;
import com.example.classroom.service.SubjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/subjects")
public class SubjectRestController {

    private final SubjectService subjectService;

    public SubjectRestController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping()
    public ResponseEntity<List<SubjectDto>> getSubjects() {
        List<SubjectDto> subjects = subjectService.fetchAll();
        return subjects.isEmpty() ?
                ResponseEntity.notFound().build() :
                ResponseEntity.ok(subjects);
    }

    @PostMapping("create")
    public ResponseEntity<SubjectDto> createSubject(@RequestBody SubjectDto subjectDto) {
        SubjectDto created = subjectService.create(subjectDto);
        return created != null ?
                ResponseEntity.status(HttpStatus.CREATED)
                        .body(created) :
                ResponseEntity.badRequest().build();
    }

    @GetMapping("{id}")
    public ResponseEntity<SubjectDto> getSubject(@PathVariable Long id) {
        SubjectDto studentDto = subjectService.fetchById(id);
        return studentDto != null ?
                ResponseEntity.ok(studentDto) :
                ResponseEntity.notFound().build();
    }

    @PutMapping
    public ResponseEntity<SubjectDto> putSubject(@RequestBody SubjectDto studentDto){
        SubjectDto updated = subjectService.update(studentDto);
        return updated != null ?
                ResponseEntity.ok(updated) :
                ResponseEntity.badRequest().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        subjectService.remove(id);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllSubjects() {
        subjectService.removeAll();
        return ResponseEntity.accepted().build();
    }
}

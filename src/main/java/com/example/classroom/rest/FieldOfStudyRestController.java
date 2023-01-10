package com.example.classroom.rest;

import com.example.classroom.dto.FieldOfStudyDto;
import com.example.classroom.service.FieldOfStudyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/fields-of-study")
public class FieldOfStudyRestController {
    
    private final FieldOfStudyService service;

    public FieldOfStudyRestController(FieldOfStudyService service) {
        this.service = service;
    }
    
    @GetMapping()
    public ResponseEntity<List<FieldOfStudyDto>> getFieldsOfStudy() {
        List<FieldOfStudyDto> fieldsOfStudy = service.fetchAll();
        return fieldsOfStudy.isEmpty() ?
                ResponseEntity.notFound().build() :
                ResponseEntity.ok(fieldsOfStudy);
    }

    @PostMapping("create")
    public ResponseEntity<FieldOfStudyDto> createFieldOfStudy(@RequestBody FieldOfStudyDto departmentDto) {
        FieldOfStudyDto created = service.create(departmentDto);
        return created != null ?
                ResponseEntity.status(HttpStatus.CREATED)
                        .body(created) :
                ResponseEntity.badRequest().build();
    }

    @GetMapping("{id}")
    public ResponseEntity<FieldOfStudyDto> getFieldOfStudy(@PathVariable Long id) {
        FieldOfStudyDto dto = service.fetchById(id);
        return dto != null ?
                ResponseEntity.ok(dto) :
                ResponseEntity.notFound().build();
    }

    @PutMapping
    public ResponseEntity<FieldOfStudyDto> putFieldOfStudy(@RequestBody FieldOfStudyDto departmentDto){
        FieldOfStudyDto updated = service.update(departmentDto);
        return updated != null ?
                ResponseEntity.ok(updated) :
                ResponseEntity.badRequest().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteFieldOfStudy(@PathVariable Long id) {
        service.remove(id);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllFieldsOfStudy() {
        service.removeAll();
        return ResponseEntity.accepted().build();
    }
}
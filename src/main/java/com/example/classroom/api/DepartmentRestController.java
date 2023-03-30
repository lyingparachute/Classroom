package com.example.classroom.api;

import com.example.classroom.dto.DepartmentDto;
import com.example.classroom.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/departments")
@RequiredArgsConstructor
public class DepartmentRestController {

    private final DepartmentService service;

    @GetMapping("{id}")
    public ResponseEntity<DepartmentDto> getDepartment(@PathVariable Long id) {
        DepartmentDto dto = service.fetchById(id);
        return dto != null ?
                ResponseEntity.ok(dto) :
                ResponseEntity.notFound().build();
    }

    @GetMapping()
    public ResponseEntity<List<DepartmentDto>> getDepartments() {
        List<DepartmentDto> departments = service.fetchAll();
        return departments.isEmpty() ?
                ResponseEntity.notFound().build() :
                ResponseEntity.ok(departments);
    }

    @PostMapping
    public ResponseEntity<DepartmentDto> createDepartment(@Valid @RequestBody DepartmentDto departmentDto) {
        DepartmentDto created = service.create(departmentDto);
        return created != null ?
                ResponseEntity.status(HttpStatus.CREATED)
                        .body(created) :
                ResponseEntity.badRequest().build();
    }

    @PutMapping
    public ResponseEntity<DepartmentDto> updateDepartment(@Valid @RequestBody DepartmentDto departmentDto) {
        DepartmentDto updated = service.update(departmentDto);
        return updated != null ?
                ResponseEntity.ok(updated) :
                ResponseEntity.badRequest().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        service.remove(id);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllDepartments() {
        service.removeAll();
        return ResponseEntity.accepted().build();
    }
}

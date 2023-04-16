package com.example.classroom.department;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/departments")
@RequiredArgsConstructor
class DepartmentRestController {

    private final DepartmentService service;

    @GetMapping("{id}")
    ResponseEntity<DepartmentDto> getDepartment(@PathVariable Long id) {
        DepartmentDto dto = service.fetchById(id);
        return dto != null ?
                ResponseEntity.ok(dto) :
                ResponseEntity.notFound().build();
    }

    @GetMapping()
    ResponseEntity<List<DepartmentDto>> getDepartments() {
        List<DepartmentDto> departments = service.fetchAll();
        return departments.isEmpty() ?
                ResponseEntity.notFound().build() :
                ResponseEntity.ok(departments);
    }

    @PostMapping
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    ResponseEntity<DepartmentDto> createDepartment(@Valid @RequestBody DepartmentDto departmentDto) {
        DepartmentDto created = service.create(departmentDto);
        return created != null ?
                ResponseEntity.status(HttpStatus.CREATED)
                        .body(created) :
                ResponseEntity.badRequest().build();
    }

    @PutMapping
    @Secured({"ROLE_DEAN", "ROLE_ADMIN"})
    ResponseEntity<DepartmentDto> updateDepartment(@Valid @RequestBody DepartmentDto departmentDto) {
        DepartmentDto updated = service.update(departmentDto);
        return updated != null ?
                ResponseEntity.ok(updated) :
                ResponseEntity.badRequest().build();
    }

    @DeleteMapping("{id}")
    @Secured({"ROLE_ADMIN"})
    ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        service.remove(id);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping
    @Secured({"ROLE_ADMIN"})
    ResponseEntity<Void> deleteAllDepartments() {
        service.removeAll();
        return ResponseEntity.accepted().build();
    }
}

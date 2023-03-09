package com.example.classroom.repository;

import com.example.classroom.model.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findAllByNameContainingIgnoreCase(String name);
    Page<Department> findAllByNameContainingIgnoreCase(String name, Pageable pageable);
}
package com.example.classroom.repository;

import com.example.classroom.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    List<Department> findAllByName(String name);

    Page<Department> findAllByName(String name, Pageable pageable);
}
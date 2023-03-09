package com.example.classroom.repository;

import com.example.classroom.enums.Semester;
import com.example.classroom.model.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findAllByNameContainingIgnoreCase(String name);

    Page<Subject> findAllByNameContainingIgnoreCase(String name, Pageable pageable);

    List<Subject> findAllBySemester(Semester semester);
}
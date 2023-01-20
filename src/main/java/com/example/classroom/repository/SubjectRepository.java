package com.example.classroom.repository;

import com.example.classroom.entity.Subject;
import com.example.classroom.enums.Semester;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findAllByNameContainingIgnoreCase(String name);

    Page<Subject> findAllByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("select s from Subject s where s.fieldOfStudy.id = ?1 and s.semester = ?2")
    List<Subject> findAllByFieldOfStudy(Long id, Semester semester);
}
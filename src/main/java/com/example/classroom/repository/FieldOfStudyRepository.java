package com.example.classroom.repository;

import com.example.classroom.entity.FieldOfStudy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FieldOfStudyRepository extends JpaRepository<FieldOfStudy, Long> {
    List<FieldOfStudy> findAllByName(String name);
    Page<FieldOfStudy> findAllByName(String name, Pageable pageable);
}
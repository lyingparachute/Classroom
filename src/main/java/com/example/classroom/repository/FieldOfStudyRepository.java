package com.example.classroom.repository;

import com.example.classroom.entity.FieldOfStudy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FieldOfStudyRepository extends JpaRepository<FieldOfStudy, Long> {
}
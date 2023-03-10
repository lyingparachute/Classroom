package com.example.classroom.repository;

import com.example.classroom.enums.LevelOfEducation;
import com.example.classroom.model.FieldOfStudy;
import com.example.classroom.model.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FieldOfStudyRepository extends JpaRepository<FieldOfStudy, Long> {

    List<FieldOfStudy> findAll(Sort sort);

    List<FieldOfStudy> findAllByNameContainingIgnoreCase(String name);

    Page<FieldOfStudy> findAllByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("select f.subjects as subjects from FieldOfStudy f where f.id = ?1")
    List<Subject> findAllSubjectsFromFieldOfStudy(Long id);

    List<FieldOfStudy> findAllByLevelOfEducation(LevelOfEducation levelOfEducation, Sort sort);
}
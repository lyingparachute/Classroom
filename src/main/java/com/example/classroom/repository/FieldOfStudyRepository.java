package com.example.classroom.repository;

import com.example.classroom.entity.FieldOfStudy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FieldOfStudyRepository extends JpaRepository<FieldOfStudy, Long> {
    @Query("select f from FieldOfStudy f where f.name like %?1%")
    List<FieldOfStudy> findAllByName(String name);
    @Query("select f from FieldOfStudy f where f.name like %?1%")
    Page<FieldOfStudy> findAllByName(String name, Pageable pageable);
}
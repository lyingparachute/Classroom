package com.example.classroom.repository;

import com.example.classroom.entity.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    @Query("select s from Subject s where s.name like %?1%")
    Page<Subject> findAllByName(String name);
    @Query("select s from Subject s where s.name like %?1%")
    Page<Subject> findAllByName(String name, Pageable pageable);

}
package com.example.classroom.repository;

import com.example.classroom.entity.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    @Query("select t from Teacher t where t.firstName like %?1% or t.lastName like %?1%")
    List<Teacher> findAllByFirstNameOrLastName(String name);

    @Query("select t from Teacher t where t.firstName like %?1% or t.lastName like %?1%")
    Page<Teacher> findAllByFirstNameOrLastName(String name, Pageable pageable);
}
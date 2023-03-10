package com.example.classroom.repository;

import com.example.classroom.model.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    @Query("""
            select t from Teacher t
            where upper(t.firstName) like upper(concat('%', ?1, '%')) or upper(t.lastName) like upper(concat('%', ?1, '%'))""")
    List<Teacher> findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String name);

    @Query("""
            select t from Teacher t
            where upper(t.firstName) like upper(concat('%', ?1, '%')) or upper(t.lastName) like upper(concat('%', ?1, '%'))""")
    Page<Teacher> findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String name, Pageable pageable);
}
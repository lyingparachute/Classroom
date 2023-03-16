package com.example.classroom.repository;

import com.example.classroom.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("""
            select s from Student s
            where upper(s.firstName) like upper(concat('%', ?1, '%')) or upper(s.lastName) like upper(concat('%', ?1, '%'))""")
    List<Student> findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String name);

    @Query("""
            select s from Student s
            where upper(s.firstName) like upper(concat('%', ?1, '%')) or upper(s.lastName) like upper(concat('%', ?1, '%'))""")
    Page<Student> findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String name, Pageable pageable);

    Optional<Student> findByEmail(String email);
}
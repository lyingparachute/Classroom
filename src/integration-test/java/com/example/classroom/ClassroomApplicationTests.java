package com.example.classroom;

import com.example.classroom.model.*;
import com.example.classroom.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ActiveProfiles("integration")
@SpringBootTest
class ClassroomApplicationTests {

    @Autowired
    private FieldOfStudyRepository fieldOfStudyRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Test
    void shouldLoadAppContext() {
        List<FieldOfStudy> fieldsOfStudy = fieldOfStudyRepository.findAll();
        List<Subject> subjects = subjectRepository.findAll();
        List<Student> students = studentRepository.findAll();
        List<Department> departments = departmentRepository.findAll();
        List<Teacher> teachers = teacherRepository.findAll();

        assertAll("Check context loading",
                () -> assertThat(fieldsOfStudy).as("Check %s context loading", "fieldsOfStudy")
                        .isNotNull().asList().isNotEmpty(),
                () -> assertThat(subjects).as("Check %s context loading", "subjects")
                        .isNotNull().asList().isNotEmpty(),
                () -> assertThat(students).as("Check %s context loading", "students")
                        .isNotNull().asList().isNotEmpty(),
                () -> assertThat(departments).as("Check %s context loading", "departments")
                        .isNotNull().asList().isNotEmpty(),
                () -> assertThat(teachers).as("Check %s context loading", "teachers")
                        .isNotNull().asList().isNotEmpty()
        );
    }

}

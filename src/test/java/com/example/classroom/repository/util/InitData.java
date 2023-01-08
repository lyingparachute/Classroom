package com.example.classroom.repository.util;

import com.example.classroom.entity.*;
import com.example.classroom.repository.*;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;

@Component
public class InitData {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final DepartmentRepository departmentRepository;
    private final FieldOfStudyRepository fieldOfStudyRepository;


    public InitData(StudentRepository studentRepository, TeacherRepository teacherRepository, SubjectRepository subjectRepository, DepartmentRepository departmentRepository, FieldOfStudyRepository fieldOfStudyRepository) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.subjectRepository = subjectRepository;
        this.departmentRepository = departmentRepository;
        this.fieldOfStudyRepository = fieldOfStudyRepository;
    }

    @Transactional
    public void cleanUp() {
        teacherRepository.deleteAll();
        studentRepository.deleteAll();
        subjectRepository.deleteAll();
        departmentRepository.deleteAll();
        fieldOfStudyRepository.deleteAll();
    }

    // *** Create Students *** //
    @Transactional
    public Student createStudentOne(List<Teacher> teachers) {
        Student student = new Student();
        student.setFirstName("Maciej");
        student.setLastName("Komaranczuk");
        student.setEmail("m.komaranczuk@gmail.com");
        student.setAge(25);
        studentRepository.save(student);
        if (teachers != null && !teachers.isEmpty()) {
            teachers.forEach(student::assignTeacher);
            studentRepository.save(student);
        }
        return student;
    }

    @Transactional
    public Student createStudentTwo(List<Teacher> teachers) {
        Student student = new Student();
        student.setFirstName("Weronika");
        student.setLastName("Romanski");
        student.setEmail("w.romanski@gmail.com");
        student.setAge(21);
        if (teachers != null && !teachers.isEmpty()) {
            teachers.forEach(student::assignTeacher);
            studentRepository.save(student);
        }
        studentRepository.save(student);
        return student;
    }

    @Transactional
    public Student createStudentThree(List<Teacher> teachers) {
        Student student = new Student();
        student.setFirstName("Agnieszka");
        student.setLastName("Sernatowicz");
        student.setEmail("a.sernatowicz@gmail.com");
        student.setAge(18);
        if (teachers != null && !teachers.isEmpty()) {
            teachers.forEach(student::assignTeacher);
            studentRepository.save(student);
        }
        studentRepository.save(student);
        return student;
    }

    // *** Create Teachers *** //
    @Transactional
    public Teacher createTeacherOne(List<Student> students) {
        Teacher teacher = new Teacher();
        teacher.setFirstName("Jarosław");
        teacher.setLastName("Adamczuk");
        teacher.setEmail("j.adamczuk@gmail.com");
        teacher.setAge(45);
        if (students != null && !students.isEmpty()) {
            students.forEach(teacher::addStudent);
        }
        return teacherRepository.save(teacher);
    }

    @Transactional
    public Teacher createTeacherTwo(List<Student> students) {
        Teacher teacher = new Teacher();
        teacher.setFirstName("Jagoda");
        teacher.setLastName("Kowalska");
        teacher.setEmail("j.kowalska@gmail.com");
        teacher.setAge(33);
        if (students != null && !students.isEmpty()) {
            students.forEach(teacher::addStudent);
        }
        return teacherRepository.save(teacher);
    }

    @Transactional
    public Teacher createTeacherThree(List<Student> students) {
        Teacher teacher = new Teacher();
        teacher.setFirstName("Grzegorz");
        teacher.setLastName("Bartosiewicz");
        teacher.setEmail("g.bartosiewicz@gmail.com");
        teacher.setAge(51);
        if (students != null && !students.isEmpty()) {
            students.forEach(teacher::addStudent);
        }
        return teacherRepository.save(teacher);
    }

    // *** Create Subjects *** //
    @Transactional
    public Subject createSubjectOne(List<Teacher> teachers) {
        Subject subject = new Subject();
        subject.setName("Mathematics");
        subject.setDescription("Calculating integrals");
        subject.setHoursInSemester(100);
        if (teachers != null && !teachers.isEmpty()) {
            teachers.forEach(subject::addTeacher);
            subjectRepository.save(subject);
        }
        subjectRepository.save(subject);
        return subject;
    }

    @Transactional
    public Subject createSubjectTwo(List<Teacher> teachers) {
        Subject subject = new Subject();
        subject.setName("Art");
        subject.setDescription("Painting");
        subject.setHoursInSemester(120);
        if (teachers != null && !teachers.isEmpty()) {
            teachers.forEach(subject::addTeacher);
            subjectRepository.save(subject);
        }
        subjectRepository.save(subject);
        return subject;
    }

    @Transactional
    public Subject createSubjectThree(List<Teacher> teachers) {
        Subject subject = new Subject();
        subject.setName("Science");
        subject.setDescription("General Science");
        subject.setHoursInSemester(150);
        if (teachers != null && !teachers.isEmpty()) {
            teachers.forEach(subject::addTeacher);
            subjectRepository.save(subject);
        }
        subjectRepository.save(subject);
        return subject;
    }

    @Transactional
    public Subject createSubjectFour(List<Teacher> teachers) {
        Subject subject = new Subject();
        subject.setName("Computer Science");
        subject.setDescription("Learning Java and Spring");
        subject.setHoursInSemester(360);
        if (teachers != null && !teachers.isEmpty()) {
            teachers.forEach(subject::addTeacher);
            subjectRepository.save(subject);
        }
        subjectRepository.save(subject);
        return subject;
    }

    // *** Create Fields Of Study *** //


    // *** Create Departments *** //
    public Department createDepartmentOne(Teacher dean, List<FieldOfStudy> fieldsOfStudy) {
        Department department = new Department();
        department.setName("Wydział Elektroniki, Telekomunikacji i Informatyki");
        department.setAddress("ul. Gabriela Narutowicza 11/12 80-233 Gdańsk");
        department.setTelNumber(123456789);
        if (dean != null) {
            department.setDean(dean);
        }
        if (fieldsOfStudy != null && !fieldsOfStudy.isEmpty()) {
            department.setFieldsOfStudy(new HashSet<>(fieldsOfStudy));
        }
        return departmentRepository.save(department);
    }
}

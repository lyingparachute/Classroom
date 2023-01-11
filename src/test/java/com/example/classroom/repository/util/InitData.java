package com.example.classroom.repository.util;

import com.example.classroom.entity.*;
import com.example.classroom.enums.AcademicTitle;
import com.example.classroom.enums.LevelOfEducation;
import com.example.classroom.enums.ModeOfStudy;
import com.example.classroom.enums.Semester;
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
    public Student createStudentOne(FieldOfStudy fieldOfStudy, List<Teacher> teachers) {
        Student student = new Student();
        student.setFirstName("Maciej");
        student.setLastName("Komaranczuk");
        student.setEmail("m.komaranczuk@gmail.com");
        student.setAge(25);
        student.setFieldOfStudy(fieldOfStudy);
        if (teachers != null && !teachers.isEmpty()) {
            teachers.forEach(student::assignTeacher);
            teachers.forEach(teacher -> teacher.getStudents().add(student));
            teacherRepository.saveAll(teachers);
        }
        if (fieldOfStudy != null) {
            student.setFieldOfStudy(fieldOfStudy);
            fieldOfStudy.getStudents().add(student);
        }
        studentRepository.save(student);
        return student;
    }

    @Transactional
    public Student createStudentTwo(FieldOfStudy fieldOfStudy, List<Teacher> teachers) {
        Student student = new Student();
        student.setFirstName("Weronika");
        student.setLastName("Romanski");
        student.setEmail("w.romanski@gmail.com");
        student.setAge(21);
        student.setFieldOfStudy(fieldOfStudy);
        if (teachers != null && !teachers.isEmpty()) {
            teachers.forEach(student::assignTeacher);
            teachers.forEach(teacher -> teacher.getStudents().add(student));
        }
        if (fieldOfStudy != null) {
            student.setFieldOfStudy(fieldOfStudy);
            fieldOfStudy.getStudents().add(student);
        }
        studentRepository.save(student);
        return student;
    }

    @Transactional
    public Student createStudentThree(FieldOfStudy fieldOfStudy, List<Teacher> teachers) {
        Student student = new Student();
        student.setFirstName("Agnieszka");
        student.setLastName("Sernatowicz");
        student.setEmail("a.sernatowicz@gmail.com");
        student.setAge(18);
        student.setFieldOfStudy(fieldOfStudy);
        if (teachers != null && !teachers.isEmpty()) {
            teachers.forEach(student::assignTeacher);
            teachers.forEach(teacher -> teacher.getStudents().add(student));
        }
        if (fieldOfStudy != null) {
            student.setFieldOfStudy(fieldOfStudy);
            fieldOfStudy.getStudents().add(student);
        }
        studentRepository.save(student);
        return student;
    }

    // *** Create Teachers *** //
    @Transactional
    public Teacher createTeacherOne(Department department,List<Subject> subjects, List<Student> students) {
        Teacher teacher = new Teacher();
        teacher.setFirstName("Jarosław");
        teacher.setLastName("Adamczuk");
        teacher.setEmail("j.adamczuk@gmail.com");
        teacher.setAge(45);
        teacher.setDepartmentDean(department);
        teacher.setSubjects(new HashSet<>(subjects));
        if (department != null) {
            teacher.setDepartmentDean(department);
            department.setDean(teacher);
        }
        if (subjects != null && !subjects.isEmpty()) {
            subjects.forEach(teacher::addSubject);
            subjects.forEach(subject -> subject.getTeachers().add(teacher));
        }
        if (students != null && !students.isEmpty()) {
            students.forEach(teacher::addStudent);
            students.forEach(student -> student.getTeachers().add(teacher));
        }
        return teacherRepository.save(teacher);
    }

    @Transactional
    public Teacher createTeacherTwo(Department department,List<Subject> subjects, List<Student> students) {
        Teacher teacher = new Teacher();
        teacher.setFirstName("Jagoda");
        teacher.setLastName("Kowalska");
        teacher.setEmail("j.kowalska@gmail.com");
        teacher.setAge(33);
        teacher.setDepartmentDean(department);
        teacher.setSubjects(new HashSet<>(subjects));
        if (department != null) {
            teacher.setDepartmentDean(department);
            department.setDean(teacher);
        }
        if (subjects != null && !subjects.isEmpty()) {
            subjects.forEach(teacher::addSubject);
            subjects.forEach(subject -> subject.getTeachers().add(teacher));
        }
        if (students != null && !students.isEmpty()) {
            students.forEach(teacher::addStudent);
            students.forEach(student -> student.getTeachers().add(teacher));
        }
        return teacherRepository.save(teacher);
    }

    @Transactional
    public Teacher createTeacherThree(Department department,List<Subject> subjects, List<Student> students) {
        Teacher teacher = new Teacher();
        teacher.setFirstName("Grzegorz");
        teacher.setLastName("Bartosiewicz");
        teacher.setEmail("g.bartosiewicz@gmail.com");
        teacher.setAge(51);
        teacher.setDepartmentDean(department);
        teacher.setSubjects(new HashSet<>(subjects));
        if (department != null) {
            teacher.setDepartmentDean(department);
            department.setDean(teacher);
        }
        if (subjects != null && !subjects.isEmpty()) {
            subjects.forEach(teacher::addSubject);
            subjects.forEach(subject -> subject.getTeachers().add(teacher));
        }
        if (students != null && !students.isEmpty()) {
            students.forEach(teacher::addStudent);
            students.forEach(student -> student.getTeachers().add(teacher));
        }
        return teacherRepository.save(teacher);
    }

    // *** Create Subjects *** //
    @Transactional
    public Subject createSubjectOne(FieldOfStudy fieldOfStudy, List<Teacher> teachers) {
        Subject subject = new Subject();
        subject.setName("Mathematics");
        subject.setDescription("Calculating integrals");
        subject.setSemester(Semester.FIFTH);
        subject.setHoursInSemester(100);
        if (teachers != null && !teachers.isEmpty()) {
            teachers.forEach(subject::addTeacher);
            teachers.forEach(teacher -> teacher.getSubjects().add(subject));
        }
        if (fieldOfStudy != null){
            subject.setFieldOfStudy(fieldOfStudy);
            fieldOfStudy.getSubjects().add(subject);
        }
        subjectRepository.save(subject);
        return subject;
    }

    @Transactional
    public Subject createSubjectTwo(FieldOfStudy fieldOfStudy, List<Teacher> teachers) {
        Subject subject = new Subject();
        subject.setName("Art");
        subject.setDescription("Painting");
        subject.setSemester(Semester.SECOND);
        subject.setHoursInSemester(120);
        if (teachers != null && !teachers.isEmpty()) {
            teachers.forEach(subject::addTeacher);
            teachers.forEach(teacher -> teacher.getSubjects().add(subject));
        }
        if (fieldOfStudy != null){
            subject.setFieldOfStudy(fieldOfStudy);
            fieldOfStudy.getSubjects().add(subject);
        }
        subjectRepository.save(subject);
        return subject;
    }

    @Transactional
    public Subject createSubjectThree(FieldOfStudy fieldOfStudy, List<Teacher> teachers) {
        Subject subject = new Subject();
        subject.setName("Science");
        subject.setDescription("General Science");
        subject.setSemester(Semester.FIRST);
        subject.setHoursInSemester(150);
        if (teachers != null && !teachers.isEmpty()) {
            teachers.forEach(subject::addTeacher);
            teachers.forEach(teacher -> teacher.getSubjects().add(subject));
        }
        if (fieldOfStudy != null){
            subject.setFieldOfStudy(fieldOfStudy);
            fieldOfStudy.getSubjects().add(subject);
        }
        subjectRepository.save(subject);
        return subject;
    }

    @Transactional
    public Subject createSubjectFour(FieldOfStudy fieldOfStudy, List<Teacher> teachers) {
        Subject subject = new Subject();
        subject.setName("Computer Science");
        subject.setDescription("Learning Java and Spring");
        subject.setSemester(Semester.SIXTH);
        subject.setHoursInSemester(360);
        if (teachers != null && !teachers.isEmpty()) {
            teachers.forEach(subject::addTeacher);
            teachers.forEach(teacher -> teacher.getSubjects().add(subject));
        }
        if (fieldOfStudy != null){
            subject.setFieldOfStudy(fieldOfStudy);
            fieldOfStudy.getSubjects().add(subject);
        }
        subjectRepository.save(subject);
        return subject;
    }

    // *** Create Fields Of Study *** //
    public FieldOfStudy createFieldOfStudyOne(Department department, List<Subject> subjects, List<Student> students) {
        FieldOfStudy fieldOfStudy = new FieldOfStudy();
        fieldOfStudy.setName("Inżynieria mechaniczno-medyczna");
        fieldOfStudy.setLevelOfEducation(LevelOfEducation.SECOND);
        fieldOfStudy.setMode(ModeOfStudy.FT);
        fieldOfStudy.setTitle(AcademicTitle.MGR);
        if (department != null) {
            fieldOfStudy.setDepartment(department);
            department.getFieldsOfStudy().add(fieldOfStudy);
        }
        if (subjects != null && !subjects.isEmpty()) {
            fieldOfStudy.setSubjects(new HashSet<>(subjects));
            subjects.forEach(subject -> subject.setFieldOfStudy(fieldOfStudy));
        }
        if (students != null && !students.isEmpty()) {
            fieldOfStudy.setStudents(new HashSet<>(students));
            students.forEach(student -> student.setFieldOfStudy(fieldOfStudy));
        }
        return fieldOfStudyRepository.save(fieldOfStudy);
    }

    public FieldOfStudy createFieldOfStudyTwo(Department department, List<Subject> subjects, List<Student> students) {
        FieldOfStudy fieldOfStudy = new FieldOfStudy();
        fieldOfStudy.setName("Mechatronika");
        fieldOfStudy.setLevelOfEducation(LevelOfEducation.FIRST);
        fieldOfStudy.setMode(ModeOfStudy.PT);
        fieldOfStudy.setTitle(AcademicTitle.BACH);
        if (department != null) {
            fieldOfStudy.setDepartment(department);
            department.getFieldsOfStudy().add(fieldOfStudy);
        }
        if (subjects != null && !subjects.isEmpty()) {
            fieldOfStudy.setSubjects(new HashSet<>(subjects));
            subjects.forEach(subject -> subject.setFieldOfStudy(fieldOfStudy));
        }
        if (students != null && !students.isEmpty()) {
            fieldOfStudy.setStudents(new HashSet<>(students));
            students.forEach(student -> student.setFieldOfStudy(fieldOfStudy));
        }
        return fieldOfStudyRepository.save(fieldOfStudy);
    }

    public FieldOfStudy createFieldOfStudyThree(Department department, List<Subject> subjects, List<Student> students) {
        FieldOfStudy fieldOfStudy = new FieldOfStudy();
        fieldOfStudy.setName("Informatyka");
        fieldOfStudy.setLevelOfEducation(LevelOfEducation.FIRST);
        fieldOfStudy.setMode(ModeOfStudy.FT);
        fieldOfStudy.setTitle(AcademicTitle.DR);
        if (department != null) {
            fieldOfStudy.setDepartment(department);
            department.getFieldsOfStudy().add(fieldOfStudy);
        }
        if (subjects != null && !subjects.isEmpty()) {
            fieldOfStudy.setSubjects(new HashSet<>(subjects));
            subjects.forEach(subject -> subject.setFieldOfStudy(fieldOfStudy));
        }
        if (students != null && !students.isEmpty()) {
            fieldOfStudy.setStudents(new HashSet<>(students));
            students.forEach(student -> student.setFieldOfStudy(fieldOfStudy));
        }
        return fieldOfStudyRepository.save(fieldOfStudy);
    }

    // *** Create Departments *** //
    public Department createDepartmentOne(Teacher dean, List<FieldOfStudy> fieldsOfStudy) {
        Department department = new Department();
        department.setName("Wydział Elektroniki, Telekomunikacji i Informatyki");
        department.setAddress("ul. Gabriela Narutowicza 11/12 80-233 Gdańsk");
        department.setTelNumber(123456789);
        setFieldsToDepartment(dean, fieldsOfStudy, department);
        return departmentRepository.save(department);
    }

    public Department createDepartmentTwo(Teacher dean, List<FieldOfStudy> fieldsOfStudy) {
        Department department = new Department();
        department.setName("Wydział Chemiczny");
        department.setAddress("ul. Broniewicza 115, 00-245 Kęty");
        department.setTelNumber(987654321);
        setFieldsToDepartment(dean, fieldsOfStudy, department);
        return departmentRepository.save(department);
    }

    public Department createDepartmentThree(Teacher dean, List<FieldOfStudy> fieldsOfStudy) {
        Department department = new Department();
        department.setName("Wydział Architektury");
        department.setAddress("ul. Jabłoniowa 34, 11-112 Stalowa Wola");
        department.setTelNumber(321321321);
        setFieldsToDepartment(dean, fieldsOfStudy, department);
        return departmentRepository.save(department);
    }

    private static void setFieldsToDepartment(Teacher dean, List<FieldOfStudy> fieldsOfStudy, Department department) {
        if (dean != null) {
            department.setDean(dean);
            dean.setDepartmentDean(department);
        }
        if (fieldsOfStudy != null && !fieldsOfStudy.isEmpty()) {
            department.setFieldsOfStudy(new HashSet<>(fieldsOfStudy));
            fieldsOfStudy.forEach(fieldOfStudy -> fieldOfStudy.setDepartment(department));
        }
    }
}

package com.example.classroom.test.util;

import com.example.classroom.auth.model.AuthenticationRequest;
import com.example.classroom.auth.model.UpdateRequest;
import com.example.classroom.department.Department;
import com.example.classroom.enums.AcademicTitle;
import com.example.classroom.enums.LevelOfEducation;
import com.example.classroom.enums.ModeOfStudy;
import com.example.classroom.enums.Semester;
import com.example.classroom.fieldofstudy.FieldOfStudy;
import com.example.classroom.student.Student;
import com.example.classroom.subject.Subject;
import com.example.classroom.teacher.Teacher;
import com.example.classroom.user.User;
import com.example.classroom.user.UserRole;
import com.example.classroom.user.password.PasswordRequest;
import com.example.classroom.user.password.PasswordResetToken;
import com.example.classroom.user.register.RegisterRequest;

import java.util.List;

public class UnitTestsInitData {

    private static void addReferencingObjectsToSubject(FieldOfStudy fieldOfStudy, List<Teacher> teachers, Subject subject) {
        subject.setFieldOfStudy(fieldOfStudy);
        teachers.forEach(subject::addTeacher);
    }

    // *** Create Students *** //
    public Student createStudentOne(FieldOfStudy fieldOfStudy, List<Teacher> teachers) {
        Student student = new Student();
        student.setId(1L);
        student.setFirstName("Maciej");
        student.setLastName("Komaranczuk");
        student.setEmail("m.komaranczuk@gmail.com");
        student.setAge(25);
        addReferencingObjectsToStudent(fieldOfStudy, teachers, student);
        return student;
    }

    public Student createStudentTwo(FieldOfStudy fieldOfStudy, List<Teacher> teachers) {
        Student student = new Student();
        student.setId(2L);
        student.setFirstName("Weronika");
        student.setLastName("Romanski");
        student.setEmail("w.romanski@gmail.com");
        student.setAge(21);
        addReferencingObjectsToStudent(fieldOfStudy, teachers, student);
        return student;
    }

    public Student createStudentThree(FieldOfStudy fieldOfStudy, List<Teacher> teachers) {
        Student student = new Student();
        student.setId(3L);
        student.setFirstName("Agnieszka");
        student.setLastName("Sernatowicz");
        student.setEmail("a.sernatowicz@gmail.com");
        student.setAge(18);
        addReferencingObjectsToStudent(fieldOfStudy, teachers, student);
        return student;
    }

    // *** Create Teachers *** //

    private void addReferencingObjectsToStudent(FieldOfStudy fieldOfStudy, List<Teacher> teachers, Student student) {
        student.setFieldOfStudy(fieldOfStudy);
        teachers.forEach(student::addTeacher);
    }

    public Teacher createTeacherOne(Department department, List<Subject> subjects, List<Student> students) {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("Jarosław");
        teacher.setLastName("Adamczuk");
        teacher.setEmail("j.adamczuk@gmail.com");
        teacher.setAge(45);
        addReferencingObjectsToTeacher(department, subjects, students, teacher);
        return teacher;
    }

    public Teacher createTeacherTwo(Department department, List<Subject> subjects, List<Student> students) {
        Teacher teacher = new Teacher();
        teacher.setId(2L);
        teacher.setFirstName("Jagoda");
        teacher.setLastName("Kowalska");
        teacher.setEmail("j.kowalska@gmail.com");
        teacher.setAge(33);
        addReferencingObjectsToTeacher(department, subjects, students, teacher);
        return teacher;
    }

    public Teacher createTeacherThree(Department department, List<Subject> subjects, List<Student> students) {
        Teacher teacher = new Teacher();
        teacher.setId(3L);
        teacher.setFirstName("Grzegorz");
        teacher.setLastName("Bartosiewicz");
        teacher.setEmail("g.bartosiewicz@gmail.com");
        teacher.setAge(51);
        addReferencingObjectsToTeacher(department, subjects, students, teacher);
        return teacher;
    }

    // *** Create Subjects *** //

    private void addReferencingObjectsToTeacher(Department department, List<Subject> subjects, List<Student> students, Teacher teacher) {
        teacher.setDepartment(department);
        students.forEach(teacher::addStudent);
        subjects.forEach(teacher::addSubject);
    }

    public Subject createSubjectOne(FieldOfStudy fieldOfStudy, List<Teacher> teachers) {
        Subject subject = new Subject();
        subject.setId(1L);
        subject.setName("Mathematics");
        subject.setDescription("Calculating integrals");
        subject.setSemester(Semester.FIFTH);
        subject.setHoursInSemester(100);
        subject.setEctsPoints(20);
        addReferencingObjectsToSubject(fieldOfStudy, teachers, subject);
        return subject;
    }

    public Subject createSubjectTwo(FieldOfStudy fieldOfStudy, List<Teacher> teachers) {
        Subject subject = new Subject();
        subject.setId(2L);
        subject.setName("Art");
        subject.setDescription("Painting");
        subject.setSemester(Semester.SECOND);
        subject.setHoursInSemester(120);
        subject.setEctsPoints(15);
        addReferencingObjectsToSubject(fieldOfStudy, teachers, subject);
        return subject;
    }

    public Subject createSubjectThree(FieldOfStudy fieldOfStudy, List<Teacher> teachers) {
        Subject subject = new Subject();
        subject.setId(3L);
        subject.setName("Science");
        subject.setDescription("General Science");
        subject.setSemester(Semester.FIRST);
        subject.setHoursInSemester(150);
        subject.setEctsPoints(10);
        addReferencingObjectsToSubject(fieldOfStudy, teachers, subject);
        return subject;
    }

    public Subject createSubjectFour(FieldOfStudy fieldOfStudy, List<Teacher> teachers) {
        Subject subject = new Subject();
        subject.setId(4L);
        subject.setName("Some Subject");
        subject.setDescription("No desc");
        subject.setSemester(Semester.FIFTH);
        subject.setHoursInSemester(360);
        subject.setEctsPoints(5);
        addReferencingObjectsToSubject(fieldOfStudy, teachers, subject);
        return subject;
    }

    // *** Create Fields Of Study *** //
    public FieldOfStudy createFieldOfStudyOne(Department department, List<Subject> subjects, List<Student> students) {
        FieldOfStudy fieldOfStudy = new FieldOfStudy();
        fieldOfStudy.setId(1L);
        fieldOfStudy.setName("Inżynieria mechaniczno-medyczna");
        fieldOfStudy.setDescription("Student portafi stworzyć model sztucznej nogi");
        fieldOfStudy.setLevelOfEducation(LevelOfEducation.SECOND);
        fieldOfStudy.setMode(ModeOfStudy.FT);
        fieldOfStudy.setTitle(AcademicTitle.ENG);
        addReferencingObjectsToFieldOfStudy(department, subjects, students, fieldOfStudy);
        return fieldOfStudy;
    }

    public FieldOfStudy createFieldOfStudyTwo(Department department, List<Subject> subjects, List<Student> students) {
        FieldOfStudy fieldOfStudy = new FieldOfStudy();
        fieldOfStudy.setId(2L);
        fieldOfStudy.setName("Mechatronika");
        fieldOfStudy.setDescription("Student zna podstawy mechanizmów korbowych");
        fieldOfStudy.setLevelOfEducation(LevelOfEducation.FIRST);
        fieldOfStudy.setMode(ModeOfStudy.PT);
        fieldOfStudy.setTitle(AcademicTitle.BACH);
        addReferencingObjectsToFieldOfStudy(department, subjects, students, fieldOfStudy);
        return fieldOfStudy;
    }

    public FieldOfStudy createFieldOfStudyThree(Department department, List<Subject> subjects, List<Student> students) {
        FieldOfStudy fieldOfStudy = new FieldOfStudy();
        fieldOfStudy.setId(3L);
        fieldOfStudy.setName("Informatyka");
        fieldOfStudy.setDescription("Student zna podstawy programowania obiektowego");
        fieldOfStudy.setLevelOfEducation(LevelOfEducation.FIRST);
        fieldOfStudy.setMode(ModeOfStudy.FT);
        fieldOfStudy.setTitle(AcademicTitle.DR);
        addReferencingObjectsToFieldOfStudy(department, subjects, students, fieldOfStudy);
        return fieldOfStudy;
    }

    public FieldOfStudy createFieldOfStudyFour(Department department, List<Subject> subjects, List<Student> students) {
        FieldOfStudy fieldOfStudy = new FieldOfStudy();
        fieldOfStudy.setId(4L);
        fieldOfStudy.setName("Biotechnologia");
        fieldOfStudy.setDescription("Student umie ściągać");
        fieldOfStudy.setLevelOfEducation(LevelOfEducation.SECOND);
        fieldOfStudy.setMode(ModeOfStudy.FT);
        fieldOfStudy.setTitle(AcademicTitle.DR);
        addReferencingObjectsToFieldOfStudy(department, subjects, students, fieldOfStudy);
        return fieldOfStudy;
    }

    private void addReferencingObjectsToFieldOfStudy(Department department, List<Subject> subjects, List<Student> students, FieldOfStudy fieldOfStudy) {
        fieldOfStudy.setDepartment(department);
        subjects.forEach(fieldOfStudy::addSubject);
        students.forEach(fieldOfStudy::addStudent);
    }

    // *** Create Departments *** //
    public Department createDepartmentOne(Teacher dean, List<FieldOfStudy> fieldsOfStudy) {
        Department department = new Department();
        department.setId(1L);
        department.setName("Wydział Elektroniki, Telekomunikacji i Informatyki");
        department.setAddress("ul. Gabriela Narutowicza 11/12 80-233 Gdańsk");
        department.setTelNumber("123456789");
        addReferencingObjectsToDepartment(dean, fieldsOfStudy, department);
        return department;
    }

    public Department createDepartmentTwo(Teacher dean, List<FieldOfStudy> fieldsOfStudy) {
        Department department = new Department();
        department.setId(2L);
        department.setName("Wydział Chemiczny");
        department.setAddress("ul. Broniewicza 115, 00-245 Kęty");
        department.setTelNumber("987654321");
        addReferencingObjectsToDepartment(dean, fieldsOfStudy, department);
        return department;
    }

    public Department createDepartmentThree(Teacher dean, List<FieldOfStudy> fieldsOfStudy) {
        Department department = new Department();
        department.setId(3L);
        department.setName("Wydział Architektury");
        department.setAddress("ul. Jabłoniowa 34, 11-112 Stalowa Wola");
        department.setTelNumber("321321321");
        addReferencingObjectsToDepartment(dean, fieldsOfStudy, department);
        return department;
    }

    private void addReferencingObjectsToDepartment(Teacher dean, List<FieldOfStudy> fieldsOfStudy, Department department) {
        department.setDean(dean);
        fieldsOfStudy.forEach(department::addFieldOfStudy);
    }

    public User createUser(final UserRole role) {
        return User.builder()
            .id(1L)
            .firstName("Andrzej")
            .lastName("Nowak")
            .role(role)
            .password("encodedPassword")
            .email("andrzej.nowak@gmail.com")
            .enabled(true)
            .build();
    }

    public User createUserWithTeacherRole(Teacher teacher) {
        return User.builder()
            .id(1L)
            .firstName("Andrzej")
            .lastName("Nowak")
            .password("encodedPassword")
            .email("andrzej.nowak@gmail.com")
            .role(UserRole.ROLE_TEACHER)
            .teacher(teacher)
            .enabled(true)
            .build();
    }

    public User createUserWithStudentRole(Student student) {
        return User.builder()
            .id(1L)
            .firstName("Andrzej")
            .lastName("Nowak")
            .password("encodedPassword")
            .email("andrzej.nowak@gmail.com")
            .role(UserRole.ROLE_STUDENT)
            .student(student)
            .enabled(true)
            .build();
    }

    public User createUnverifiedUser() {
        return User.builder()
            .id(1L)
            .firstName("Andrzej")
            .lastName("Nowak")
            .password("encodedPassword")
            .email("andrzej.nowak@gmail.com")
            .enabled(false)
            .build();
    }

    public RegisterRequest createRegisterRequest(final UserRole role) {
        return RegisterRequest.builder()
            .firstName("Andrzej")
            .lastName("Nowak")
            .role(role)
            .email("andrzej.nowak@gmail.com")
            .passwordRequest(
                new PasswordRequest("123", "123")
            )
            .build();
    }

    public AuthenticationRequest createAuthenticationRequest() {
        return AuthenticationRequest.builder()
            .email("andrzej.nowak@gmail.com")
            .password("encodedPassword")
            .build();
    }

    public UpdateRequest createUpdateRequest() {
        return UpdateRequest.builder()
            .firstName("Joanna")
            .lastName("Kowalczyk")
            .email("andrzej.nowak@gmail.com")
            // FIXME - remove commented code
//                .password("newEncodedpassword")
            .build();
    }

    public PasswordResetToken createPasswordResetToken(User user) {
        return new PasswordResetToken(
            user,
            "test-token"
        );
    }

    public String createExpiredToken() {
        return "cf619a21-5b6f-4237-a22d-de3e51198fd0";
    }
}


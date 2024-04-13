package com.example.classroom.test.util;

import com.example.classroom.auth.model.AuthenticationRequest;
import com.example.classroom.auth.model.UpdateRequest;
import com.example.classroom.department.Department;
import com.example.classroom.department.DepartmentRepository;
import com.example.classroom.enums.AcademicTitle;
import com.example.classroom.enums.LevelOfEducation;
import com.example.classroom.enums.ModeOfStudy;
import com.example.classroom.enums.Semester;
import com.example.classroom.fieldofstudy.FieldOfStudy;
import com.example.classroom.fieldofstudy.FieldOfStudyRepository;
import com.example.classroom.student.Student;
import com.example.classroom.student.StudentRepository;
import com.example.classroom.subject.Subject;
import com.example.classroom.subject.SubjectRepository;
import com.example.classroom.teacher.Teacher;
import com.example.classroom.teacher.TeacherRepository;
import com.example.classroom.token.TokenRepository;
import com.example.classroom.user.User;
import com.example.classroom.user.UserRepository;
import com.example.classroom.user.UserRole;
import com.example.classroom.user.password.PasswordRequest;
import com.example.classroom.user.register.RegisterRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class IntegrationTestsInitData {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final DepartmentRepository departmentRepository;
    private final FieldOfStudyRepository fieldOfStudyRepository;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    private static void addReferencingObjectsToSubject(FieldOfStudy fieldOfStudy, List<Teacher> teachers, Subject subject) {
        subject.setFieldOfStudy(fieldOfStudy);
        teachers.forEach(subject::addTeacher);
    }

    @Transactional
    public void cleanUp() {
        studentRepository.findAll().forEach(this::removeReferencingObjectsFromStudent);
        studentRepository.deleteAll();
        teacherRepository.findAll().forEach(this::removeReferencingObjectsFromTeacher);
        teacherRepository.deleteAll();
        departmentRepository.findAll().forEach(this::removeReferencingObjectsFromDepartment);
        departmentRepository.deleteAll();
        fieldOfStudyRepository.findAll().forEach(this::removeReferencingObjectsFromFieldOfStudy);
        fieldOfStudyRepository.deleteAll();
        subjectRepository.findAll().forEach(this::removeReferencingObjectsFromSubject);
        subjectRepository.deleteAll();
        tokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    // *** Create Students *** //
    @Transactional
    public Student createStudentOne(FieldOfStudy fieldOfStudy, List<Teacher> teachers) {
        Student student = new Student();
        student.setFirstName("Maciej");
        student.setLastName("Komaranczuk");
        student.setEmail("m.komaranczuk@gmail.com");
        student.setAge(25);
        addReferencingObjectsToStudent(fieldOfStudy, teachers, student);
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
        addReferencingObjectsToStudent(fieldOfStudy, teachers, student);
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
        addReferencingObjectsToStudent(fieldOfStudy, teachers, student);
        studentRepository.save(student);
        return student;
    }

    private void addReferencingObjectsToStudent(FieldOfStudy fieldOfStudy, List<Teacher> teachers, Student student) {
        student.setFieldOfStudy(fieldOfStudy);
        teachers.forEach(student::addTeacher);
    }

    private void removeReferencingObjectsFromStudent(Student student) {
        student.setFieldOfStudy(null);
        Set<Teacher> teachers = new HashSet<>(student.getTeachers());
        teachers.forEach(student::removeTeacher);
    }

    // *** Create Teachers *** //
    @Transactional
    public Teacher createTeacherOne(Department department, List<Subject> subjects, List<Student> students) {
        Teacher teacher = new Teacher();
        teacher.setFirstName("Jarosław");
        teacher.setLastName("Adamczuk");
        teacher.setEmail("j.adamczuk@gmail.com");
        teacher.setAge(45);
        addReferencingObjectsToTeacher(department, subjects, students, teacher);
        return teacherRepository.save(teacher);
    }

    @Transactional
    public Teacher createTeacherTwo(Department department, List<Subject> subjects, List<Student> students) {
        Teacher teacher = new Teacher();
        teacher.setFirstName("Jagoda");
        teacher.setLastName("Kowalska");
        teacher.setEmail("j.kowalska@gmail.com");
        teacher.setAge(33);
        addReferencingObjectsToTeacher(department, subjects, students, teacher);
        return teacherRepository.save(teacher);
    }

    @Transactional
    public Teacher createTeacherThree(Department department, List<Subject> subjects, List<Student> students) {
        Teacher teacher = new Teacher();
        teacher.setFirstName("Grzegorz");
        teacher.setLastName("Bartosiewicz");
        teacher.setEmail("g.bartosiewicz@gmail.com");
        teacher.setAge(51);
        addReferencingObjectsToTeacher(department, subjects, students, teacher);
        return teacherRepository.save(teacher);
    }

    private void addReferencingObjectsToTeacher(Department department, List<Subject> subjects, List<Student> students, Teacher teacher) {
        teacher.setDepartment(department);
        students.forEach(teacher::addStudent);
        subjects.forEach(teacher::addSubject);
    }

    private void removeReferencingObjectsFromTeacher(final Teacher teacher) {
        Set<Student> students = new HashSet<>(teacher.getStudents());
        Set<Subject> subjects = new HashSet<>(teacher.getSubjects());
        teacher.setDepartment(null);
        students.forEach(teacher::removeStudent);
        subjects.forEach(teacher::removeSubject);
    }

    // *** Create Subjects *** //
    @Transactional
    public Subject createSubjectOne(FieldOfStudy fieldOfStudy, List<Teacher> teachers) {
        Subject subject = new Subject();
        subject.setName("Mathematics");
        subject.setDescription("Calculating integrals");
        subject.setSemester(Semester.FIFTH);
        subject.setHoursInSemester(100);
        subject.setEctsPoints(5);
        addReferencingObjectsToSubject(fieldOfStudy, teachers, subject);
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
        subject.setEctsPoints(10);
        addReferencingObjectsToSubject(fieldOfStudy, teachers, subject);
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
        subject.setEctsPoints(15);
        addReferencingObjectsToSubject(fieldOfStudy, teachers, subject);
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
        addReferencingObjectsToSubject(fieldOfStudy, teachers, subject);
        subjectRepository.save(subject);
        return subject;
    }

    private void removeReferencingObjectsFromSubject(Subject subject) {
        Set<Teacher> teachers = new HashSet<>(subject.getTeachers());
        subject.setFieldOfStudy(null);
        teachers.forEach(subject::removeTeacher);
    }

    // *** Create Fields Of Study *** //
    @Transactional
    public FieldOfStudy createFieldOfStudyOne(Department department, List<Subject> subjects, List<Student> students) {
        FieldOfStudy fieldOfStudy = new FieldOfStudy();
        fieldOfStudy.setName("Inżynieria mechaniczno-medyczna");
        fieldOfStudy.setDescription("Najlepszy kierunek studiow dla osob chcacych budowac protezy");
        fieldOfStudy.setLevelOfEducation(LevelOfEducation.SECOND);
        fieldOfStudy.setMode(ModeOfStudy.FT);
        fieldOfStudy.setTitle(AcademicTitle.MGR);
        addReferencingObjectsToFieldOfStudy(department, subjects, students, fieldOfStudy);
        fieldOfStudyRepository.save(fieldOfStudy);
        return fieldOfStudy;
    }

    @Transactional
    public FieldOfStudy createFieldOfStudyTwo(Department department, List<Subject> subjects, List<Student> students) {
        FieldOfStudy fieldOfStudy = new FieldOfStudy();
        fieldOfStudy.setName("Mechatronika");
        fieldOfStudy.setDescription("Najlepszy kierunek studiow dla osób chcacych budowac roboty");
        fieldOfStudy.setLevelOfEducation(LevelOfEducation.FIRST);
        fieldOfStudy.setMode(ModeOfStudy.PT);
        fieldOfStudy.setTitle(AcademicTitle.BACH);
        addReferencingObjectsToFieldOfStudy(department, subjects, students, fieldOfStudy);
        fieldOfStudyRepository.save(fieldOfStudy);
        return fieldOfStudy;
    }

    @Transactional
    public FieldOfStudy createFieldOfStudyThree(Department department, List<Subject> subjects, List<Student> students) {
        FieldOfStudy fieldOfStudy = new FieldOfStudy();
        fieldOfStudy.setName("Informatyka");
        fieldOfStudy.setDescription("Kierunek studiow dla osob lubiacych programować.");
        fieldOfStudy.setLevelOfEducation(LevelOfEducation.FIRST);
        fieldOfStudy.setMode(ModeOfStudy.FT);
        fieldOfStudy.setTitle(AcademicTitle.DR);
        addReferencingObjectsToFieldOfStudy(department, subjects, students, fieldOfStudy);
        fieldOfStudyRepository.save(fieldOfStudy);
        return fieldOfStudy;
    }

    private void addReferencingObjectsToFieldOfStudy(Department department, List<Subject> subjects, List<Student> students, FieldOfStudy fieldOfStudy) {
        fieldOfStudy.setDepartment(department);
        subjects.forEach(fieldOfStudy::addSubject);
        students.forEach(fieldOfStudy::addStudent);
    }

    private void removeReferencingObjectsFromFieldOfStudy(FieldOfStudy fieldOfStudy) {
        Set<Subject> subjects = new HashSet<>(fieldOfStudy.getSubjects());
        Set<Student> students = new HashSet<>(fieldOfStudy.getStudents());
        fieldOfStudy.setDepartment(null);
        subjects.forEach(fieldOfStudy::removeSubject);
        students.forEach(fieldOfStudy::removeStudent);
    }

    // *** Create Departments *** //
    @Transactional
    public Department createDepartmentOne(Teacher dean, List<FieldOfStudy> fieldsOfStudy) {
        Department department = new Department();
        department.setName("Wydział Elektroniki, Telekomunikacji i Informatyki");
        department.setAddress("ul. Gabriela Narutowicza 11/12 80-233 Gdańsk");
        department.setTelNumber("123456789");
        Department saved = departmentRepository.save(department);
        addReferencingObjectsToDepartment(dean, fieldsOfStudy, saved);
        return departmentRepository.save(saved);
    }

    @Transactional
    public Department createDepartmentTwo(Teacher dean, List<FieldOfStudy> fieldsOfStudy) {
        Department department = new Department();
        department.setName("Wydział Chemiczny");
        department.setAddress("ul. Broniewicza 115, 00-245 Kęty");
        department.setTelNumber("987654321");
        Department saved = departmentRepository.save(department);
        addReferencingObjectsToDepartment(dean, fieldsOfStudy, saved);
        return departmentRepository.save(saved);
    }

    @Transactional
    public Department createDepartmentThree(Teacher dean, List<FieldOfStudy> fieldsOfStudy) {
        Department department = new Department();
        department.setName("Wydział Architektury");
        department.setAddress("ul. Jabłoniowa 34, 11-112 Stalowa Wola");
        department.setTelNumber("321321321");
        Department saved = departmentRepository.save(department);
        addReferencingObjectsToDepartment(dean, fieldsOfStudy, saved);
        return departmentRepository.save(saved);
    }

    private void addReferencingObjectsToDepartment(Teacher dean, List<FieldOfStudy> fieldsOfStudy, Department department) {
        department.setDean(dean);
        fieldsOfStudy.forEach(department::addFieldOfStudy);
    }

    private void removeReferencingObjectsFromDepartment(Department department) {
        Set<FieldOfStudy> fieldsOfStudy = new HashSet<>(department.getFieldsOfStudy());
        department.setDean(null);
        fieldsOfStudy.forEach(department::removeFieldOfStudy);
    }

    public User createUser() {
        return userRepository.save(User.builder()
            .firstName("Andrzej")
            .lastName("Nowak")
            .password("$2a$10$XYfYrJJffuaU2IZGm6Wp.uy4A3V.8vKXFghrBruUU9XcRYV1f3eb.")
            .email("andrzej.nowak@gmail.com")
            .role(UserRole.ROLE_STUDENT)
            .build());
    }

    public RegisterRequest createRegisterRequest(final UserRole role) {
        return RegisterRequest.builder()
            .firstName("Andrzej")
            .lastName("Nowak")
            .email("andrzej.nowak@gmail.com")
            .passwordRequest(
                new PasswordRequest("ACbc10932!@", "ACbc10932!@®")
            )
            .role(role)
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
            .password("newEncodedpassword")
            .build();
    }
}

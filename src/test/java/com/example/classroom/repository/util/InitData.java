package com.example.classroom.repository.util;

import com.example.classroom.entity.Student;
import com.example.classroom.entity.Subject;
import com.example.classroom.entity.Teacher;
import com.example.classroom.enums.FieldOfStudy;
import com.example.classroom.repository.StudentRepository;
import com.example.classroom.repository.SubjectRepository;
import com.example.classroom.repository.TeacherRepository;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

@Component
public class InitData {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;


    public InitData(StudentRepository studentRepository, TeacherRepository teacherRepository, SubjectRepository subjectRepository) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.subjectRepository = subjectRepository;
    }

    @Transactional
    public void cleanUp(){
        teacherRepository.deleteAll();
        studentRepository.deleteAll();
        subjectRepository.deleteAll();
    }

    @Transactional
    public Student createStudentOne(List<Teacher> teachers) {
        Student student = new Student();
        student.setFirstName("Maciej");
        student.setLastName("Komaranczuk");
        student.setEmail("m.komaranczuk@gmail.com");
        student.setAge(25);
        student.setFieldOfStudy(FieldOfStudy.INFORMATICS);
        studentRepository.save(student);
        if (teachers != null && !teachers.isEmpty()){
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
        student.setFieldOfStudy(FieldOfStudy.ELECTRICAL);
        if (teachers != null && !teachers.isEmpty()){
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
        student.setFieldOfStudy(FieldOfStudy.ROBOTICS);
        if (teachers != null && !teachers.isEmpty()){
            teachers.forEach(student::assignTeacher);
            studentRepository.save(student);
        }
        studentRepository.save(student);
        return student;
    }

    @Transactional
    public Teacher createTeacherOne(List<Student> students) {
        Teacher teacher = new Teacher();
        teacher.setFirstName("Jarosław");
        teacher.setLastName("Adamczuk");
        teacher.setEmail("j.adamczuk@gmail.com");
        teacher.setAge(45);
        if (students != null && !students.isEmpty()){
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
        if (students != null && !students.isEmpty()){
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
        if (students != null && !students.isEmpty()){
            students.forEach(teacher::addStudent);
        }
        return teacherRepository.save(teacher);
    }

    @Transactional
    public Subject createSubjectMaths(List<Teacher> teachers){
        Subject subject = new Subject();
        subject.setShortName("MATHS");
        subject.setLongName("Mathematics");
        subject.setDescription("Calculating integrals");
        subject.setHoursInSemester(100);
        if (teachers != null && !teachers.isEmpty()){
            teachers.forEach(subject::addTeacher);
            subjectRepository.save(subject);
        }
        subjectRepository.save(subject);
        return subject;

    }

    @Transactional
    public Subject createSubjectArt(List<Teacher> teachers){
        Subject subject = new Subject();
        subject.setShortName("ART");
        subject.setLongName("Art");
        subject.setDescription("Painting");
        subject.setHoursInSemester(120);
        if (teachers != null && !teachers.isEmpty()){
            teachers.forEach(subject::addTeacher);
            subjectRepository.save(subject);
        }
        subjectRepository.save(subject);
        return subject;
    }

    @Transactional
    public Subject createSubjectScience(List<Teacher> teachers){
        Subject subject = new Subject();
        subject.setShortName("SCIENCE");
        subject.setLongName("Science");
        subject.setDescription("General Science");
        subject.setHoursInSemester(150);
        if (teachers != null && !teachers.isEmpty()){
            teachers.forEach(subject::addTeacher);
            subjectRepository.save(subject);
        }
        subjectRepository.save(subject);
        return subject;
    }

    @Transactional
    public Subject createSubjectIT(List<Teacher> teachers){
        Subject subject = new Subject();
        subject.setShortName("IT");
        subject.setLongName("Computer Science");
        subject.setDescription("Learning Java and Spring");
        subject.setHoursInSemester(360);
        if (teachers != null && !teachers.isEmpty()){
            teachers.forEach(subject::addTeacher);
            subjectRepository.save(subject);
        }
        subjectRepository.save(subject);
        return subject;
    }
}

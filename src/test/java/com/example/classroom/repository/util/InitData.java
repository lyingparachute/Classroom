package com.example.classroom.repository.util;

import com.example.classroom.entity.Student;
import com.example.classroom.entity.Subject;
import com.example.classroom.entity.Teacher;
import com.example.classroom.enums.FieldOfStudy;
import com.example.classroom.enums.SubjectEnum;
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
        teacher.setSubjectEnum(SubjectEnum.IT);
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
        teacher.setSubjectEnum(SubjectEnum.SCIENCE);
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
        teacher.setSubjectEnum(SubjectEnum.ART);
        if (students != null && !students.isEmpty()){
            students.forEach(teacher::addStudent);
        }
        return teacherRepository.save(teacher);
    }

    @Transactional
    public Subject createSubjectMaths(){
        Subject subject = new Subject();
        subject.setShortName("MATHS");
        subject.setDescription("Mathematics");
        subject.setHoursInSemester(100);
        return subjectRepository.save(subject);
    }

    @Transactional
    public Subject createSubjectArt(){
        Subject subject = new Subject();
        subject.setShortName("ART");
        subject.setDescription("Art");
        subject.setHoursInSemester(120);
        return subjectRepository.save(subject);
    }

    @Transactional
    public Subject createSubjectScience(){
        Subject subject = new Subject();
        subject.setShortName("SCIENCE");
        subject.setDescription("Science");
        subject.setHoursInSemester(150);
        return subjectRepository.save(subject);
    }

    @Transactional
    public Subject createSubjectIT(){
        Subject subject = new Subject();
        subject.setShortName("IT");
        subject.setDescription("Computer Science");
        subject.setHoursInSemester(360);
        return subjectRepository.save(subject);
    }
}

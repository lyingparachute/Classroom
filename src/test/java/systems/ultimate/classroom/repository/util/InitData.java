package systems.ultimate.classroom.repository.util;

import org.springframework.stereotype.Component;
import systems.ultimate.classroom.entity.Student;
import systems.ultimate.classroom.entity.Teacher;
import systems.ultimate.classroom.enums.FieldOfStudy;
import systems.ultimate.classroom.enums.Subject;
import systems.ultimate.classroom.repository.StudentRepository;
import systems.ultimate.classroom.repository.TeacherRepository;

import javax.transaction.Transactional;
import java.util.List;

@Component
public class InitData {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;


    public InitData(StudentRepository studentRepository, TeacherRepository teacherRepository) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
    }

    @Transactional
    public void cleanUp(){
        teacherRepository.deleteAll();
        studentRepository.deleteAll();
    }

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

    public Student createStudentTwo(List<Teacher> teachers) {
        Student student = new Student();
        student.setFirstName("Weronika");
        student.setLastName("Romanski");
        student.setEmail("w.romanski@gmail.com");
        student.setAge(21);
        student.setFieldOfStudy(FieldOfStudy.ELECTRICAL);
        studentRepository.save(student);
        if (teachers != null && !teachers.isEmpty()){
            teachers.forEach(student::assignTeacher);
            studentRepository.save(student);
        }
        return student;
    }

    public Student createStudentThree(List<Teacher> teachers) {
        Student student = new Student();
        student.setFirstName("Agnieszka");
        student.setLastName("Sernatowicz");
        student.setEmail("a.sernatowicz@gmail.com");
        student.setAge(18);
        student.setFieldOfStudy(FieldOfStudy.ROBOTICS);
        studentRepository.save(student);
        if (teachers != null && !teachers.isEmpty()){
            teachers.forEach(student::assignTeacher);
            studentRepository.save(student);
        }
        return student;
    }



    public Teacher createTeacherOne(List<Student> students) {
        Teacher teacher = new Teacher();
        teacher.setFirstName("Jarosław");
        teacher.setLastName("Adamczuk");
        teacher.setEmail("j.adamczuk@gmail.com");
        teacher.setAge(45);
        teacher.setSubject(Subject.IT);
        if (students != null && !students.isEmpty()){
            students.forEach(teacher::addStudent);
        }
        return teacherRepository.save(teacher);
    }

    public Teacher createTeacherTwo(List<Student> students) {
        Teacher teacher = new Teacher();
        teacher.setFirstName("Jagoda");
        teacher.setLastName("Kowalska");
        teacher.setEmail("j.kowalska@gmail.com");
        teacher.setAge(33);
        teacher.setSubject(Subject.SCIENCE);
        if (students != null && !students.isEmpty()){
            students.forEach(teacher::addStudent);
        }
        return teacherRepository.save(teacher);
    }

    public Teacher createTeacherThree(List<Student> students) {
        Teacher teacher = new Teacher();
        teacher.setFirstName("Grzegorz");
        teacher.setLastName("Bartosiewicz");
        teacher.setEmail("g.bartosiewicz@gmail.com");
        teacher.setAge(51);
        teacher.setSubject(Subject.ART);
        if (students != null && !students.isEmpty()){
            students.forEach(teacher::addStudent);
        }
        return teacherRepository.save(teacher);
    }
}

package systems.ultimate.classroom.repository.util;

import org.springframework.stereotype.Component;
import systems.ultimate.classroom.entity.Student;
import systems.ultimate.classroom.entity.Teacher;
import systems.ultimate.classroom.enums.FieldOfStudy;
import systems.ultimate.classroom.enums.Subject;
import systems.ultimate.classroom.repository.StudentRepository;
import systems.ultimate.classroom.repository.TeacherRepository;

import javax.transaction.Transactional;

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

    public Student createFirstStudent() {
        Student student = new Student();
        student.setFirstName("Maciej");
        student.setLastName("Komaranczuk");
        student.setEmail("m.komaranczuk@gmail.com");
        student.setAge(25);
        student.setFieldOfStudy(FieldOfStudy.INFORMATICS);
        return studentRepository.save(student);
    }

    public Student createSecondStudent() {
        Student student = new Student();
        student.setFirstName("Weronika");
        student.setLastName("Romanski");
        student.setEmail("w.romanski@gmail.com");
        student.setAge(21);
        student.setFieldOfStudy(FieldOfStudy.ELECTRICAL);
        return studentRepository.save(student);
    }

    public Teacher createFirstTeacher() {
        Teacher teacher = new Teacher();
        teacher.setFirstName("Jarosław");
        teacher.setLastName("Adamczuk");
        teacher.setEmail("j.adamczuk@gmail.com");
        teacher.setAge(45);
        teacher.setSubject(Subject.IT);
        return teacherRepository.save(teacher);
    }

    public Teacher createSecondTeacher() {
        Teacher teacher = new Teacher();
        teacher.setFirstName("Jagoda");
        teacher.setLastName("Kowalska");
        teacher.setEmail("j.kowalska@gmail.com");
        teacher.setAge(33);
        teacher.setSubject(Subject.SCIENCE);
        return teacherRepository.save(teacher);
    }
}

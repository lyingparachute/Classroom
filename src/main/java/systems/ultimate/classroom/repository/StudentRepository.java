package systems.ultimate.classroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import systems.ultimate.classroom.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
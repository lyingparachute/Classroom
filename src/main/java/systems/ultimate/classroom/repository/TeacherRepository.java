package systems.ultimate.classroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import systems.ultimate.classroom.entity.Teacher;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
}
package systems.ultimate.classroom.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import systems.ultimate.classroom.dto.TeacherDto;
import systems.ultimate.classroom.entity.Student;
import systems.ultimate.classroom.entity.Teacher;
import systems.ultimate.classroom.repository.TeacherRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TeacherService {
    private final TeacherRepository repository;
    private final ModelMapper mapper;

    public TeacherService(TeacherRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public TeacherDto create(TeacherDto dto){
        Teacher teacher = mapper.map(dto, Teacher.class);
        Teacher saved = repository.save(teacher);
        return mapper.map(saved, TeacherDto.class);
    }

    @Transactional
    public TeacherDto update(TeacherDto dto){
        Optional<Teacher> byId = repository.findById(dto.getId());
        if(byId.isPresent()){
            Teacher teacher = byId.get();
            mapper.map(dto, teacher);
            Teacher saved = repository.save(teacher);
            return mapper.map(saved, TeacherDto.class);
        }
        return null;
    }

    @Transactional
    public List<TeacherDto> fetchAll() {
        List<Teacher> allteachers = repository.findAll();
        return allteachers.stream().map(teacher -> mapper.map(teacher, TeacherDto.class)).collect(Collectors.toList());
    }

    @Transactional
    public TeacherDto fetchById(Long id) {
        Optional<Teacher> byId = repository.findById(id);
        return byId.map(teacher -> mapper.map(teacher, TeacherDto.class)).orElse(null);
    }

    @Transactional
    public void remove(Long id) {
        Teacher teacher = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product id: " + id));
        repository.delete(teacher);
    }

    public void assignStudent(Teacher teacher, Student student) {
        teacher.addStudent(student);
        repository.save(teacher);
    }
    public void assignStudents(Teacher teacher, Set<Student> students) {
        students.forEach(teacher::addStudent);
        repository.save(teacher);
    }
}

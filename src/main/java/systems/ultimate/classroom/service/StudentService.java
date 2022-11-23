package systems.ultimate.classroom.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import systems.ultimate.classroom.dto.StudentDto;
import systems.ultimate.classroom.entity.Student;
import systems.ultimate.classroom.repository.StudentRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final StudentRepository repository;
    private final ModelMapper mapper;

    public StudentService(StudentRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public StudentDto create(StudentDto dto){
        Student student = mapper.map(dto, Student.class);
        Student saved = repository.save(student);
        return mapper.map(saved, StudentDto.class);
    }


    public List<StudentDto> getAll() {
        List<Student> allStudents = repository.findAll();
        return allStudents.stream().map(student -> mapper.map(student, StudentDto.class)).collect(Collectors.toList());
    }
}

package systems.ultimate.classroom.service;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import systems.ultimate.classroom.dto.StudentDto;
import systems.ultimate.classroom.dto.TeacherDto;
import systems.ultimate.classroom.entity.Student;
import systems.ultimate.classroom.entity.Teacher;
import systems.ultimate.classroom.repository.StudentRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final StudentRepository repository;
    private final ModelMapper mapper;

    public StudentService(StudentRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public StudentDto create(StudentDto dto){
        Student student = mapper.map(dto, Student.class);
        Student saved = repository.save(student);
        return mapper.map(saved, StudentDto.class);
    }

    @Transactional
    public StudentDto update(StudentDto dto){
        Optional<Student> byId = repository.findById(dto.getId());
        if(byId.isPresent()){
            Student student = byId.get();
            mapper.map(dto, student);
            Student saved = repository.save(student);
            return mapper.map(saved, StudentDto.class);
        }
        return null;
    }

    @Transactional
    public List<StudentDto> fetchAll() {
        List<Student> allStudents = repository.findAll();
        return allStudents.stream().map(student -> mapper.map(student, StudentDto.class)).collect(Collectors.toList());
    }

    public Page<StudentDto> fetchAllPaginated(int pageNo, int pageSize, String sortField, String sortDirection){
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pagable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<Student> all = repository.findAll(pagable);
        return all.map(student -> mapper.map(student, StudentDto.class));
    }

    @Transactional
    public StudentDto fetchById(Long id) {
        Optional<Student> byId = repository.findById(id);
        return byId.map(student -> mapper.map(student, StudentDto.class)).orElse(null);
    }

    @Transactional
    public void remove(Long id) {
        Student student = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product id: " + id));
        repository.delete(student);
    }

    public List<StudentDto> findByFirstOrLastName(String searched){
        List<Student> found = repository.findAllByFirstNameOrLastName(searched);
        return found.stream().map(s -> mapper.map(s, StudentDto.class)).collect(Collectors.toList());
    }

    @Transactional
    public void assignTeacher(StudentDto studentDto,TeacherDto teacherDto) {
        studentDto.getTeachersList().add(mapper.map(teacherDto, Teacher.class));
        repository.save(mapper.map(studentDto, Student.class));
    }

    public void assignTeachers(Student student, Set<Teacher> teachersList) {
        teachersList.forEach(teacher -> teacher.addStudent(student));
        repository.save(student);
    }
}

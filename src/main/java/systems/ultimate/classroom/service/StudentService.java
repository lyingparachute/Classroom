package systems.ultimate.classroom.service;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import systems.ultimate.classroom.dto.StudentDto;
import systems.ultimate.classroom.entity.Student;
import systems.ultimate.classroom.entity.Teacher;
import systems.ultimate.classroom.repository.StudentRepository;
import systems.ultimate.classroom.repository.TeacherRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ModelMapper mapper;

    public StudentService(StudentRepository studentRepository, TeacherRepository teacherRepository, ModelMapper mapper) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.mapper = mapper;
    }

    @Transactional
    public StudentDto create(StudentDto dto){
        Student student = mapper.map(dto, Student.class);
        Student saved = studentRepository.save(student);
        return mapper.map(saved, StudentDto.class);
    }

    @Transactional
    public StudentDto update(StudentDto dto){
        Optional<Student> byId = studentRepository.findById(dto.getId());
        if(byId.isPresent()){
            Student student = byId.get();
            mapper.map(dto, student);
            Student saved = studentRepository.save(student);
            return mapper.map(saved, StudentDto.class);
        }
        return null;
    }


    public List<StudentDto> fetchAll() {
        List<Student> allStudents = studentRepository.findAll();
        return allStudents.stream().map(student -> mapper.map(student, StudentDto.class)).collect(Collectors.toList());
    }

    public Page<StudentDto> fetchAllPaginated(int pageNo, int pageSize, String sortField, String sortDirection){
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<Student> all = studentRepository.findAll(pageable);
        return all.map(student -> mapper.map(student, StudentDto.class));
    }


    public StudentDto fetchById(Long id) {
        Optional<Student> byId = studentRepository.findById(id);
        return byId.map(student -> mapper.map(student, StudentDto.class)).orElse(null);
    }

    @Transactional
    public void remove(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product id: " + id));
        removeTeachers(student, student.getTeachersList());
        studentRepository.delete(student);
    }

    public List<StudentDto> findByFirstOrLastName(String searched){
        List<Student> found = studentRepository.findAllByFirstNameOrLastName(searched);
        return found.stream().map(s -> mapper.map(s, StudentDto.class)).collect(Collectors.toList());
    }

    @Transactional
    public void assignTeachers(Student student, Set<Teacher> teachersList) {
        teachersList.forEach(student::assignTeacher);
        studentRepository.save(student);
    }

    @Transactional
    public void removeTeachers(Student student, Set<Teacher> teachersList) {
        teachersList.forEach(student::removeTeacher);
        studentRepository.save(student);
    }


}

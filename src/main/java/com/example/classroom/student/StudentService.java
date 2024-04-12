package com.example.classroom.student;

import com.example.classroom.pageable.PageableRequest;
import com.example.classroom.subject.Subject;
import com.example.classroom.teacher.Teacher;
import com.example.classroom.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.classroom.pageable.PageableService.isNamePresent;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository repository;
    private final ModelMapper mapper;

    @Transactional
    public StudentDto create(final StudentDto dto) {
        final var student = mapper.map(dto, Student.class);
        addReferencingObjects(student);
        final var saved = repository.save(student);
        return mapper.map(saved, StudentDto.class);
    }

    List<StudentDto> fetchAll() {
        final var all = repository.findAll();
        return all.stream().map(student -> mapper.map(student, StudentDto.class)).toList();
    }

    Page<StudentDto> fetchAllPaginated(final PageableRequest request) {
        final var sort = getSortOrder(request.sortField(), request.sortDirection());
        final Pageable pageable = PageRequest.of(request.pageNumber() - 1, request.pageSize(), sort);
        final Page<Student> all = repository.findAll(pageable);
        return all.map(student -> mapper.map(student, StudentDto.class));
    }

    StudentDto fetchById(final Long id) {
        final var byId = repository.findById(id);
        return byId.map(student -> mapper.map(student, StudentDto.class))
                .orElseThrow(() -> new IllegalArgumentException("Invalid Student ID: " + id));
    }

    @Transactional
    StudentDto update(final StudentDto dto) {
        final var student = repository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid Student '" + dto.getFirstName() + " " + dto.getLastName() + "' with ID: " + dto.getId()));
        removeReferencingObjects(student);
        mapper.map(dto, student);
        addReferencingObjects(student);
        return mapper.map(student, StudentDto.class);
    }

    @Transactional
    public void remove(Long id) {
        Student student = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Student ID: " + id));
        removeReferencingObjects(student);
        repository.delete(student);
    }

    @Transactional
    void removeAll() {
        repository.findAll().forEach(this::removeReferencingObjects);
        repository.deleteAll();
    }

    List<StudentDto> findByFirstOrLastName(String searched) {
        List<Student> found = repository.findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(searched);
        return found.stream().map(s -> mapper.map(s, StudentDto.class)).toList();
    }

    Page<StudentDto> findByFirstOrLastNamePaginated(final PageableRequest request) {
        Sort sort = getSortOrder(request.sortField(), request.sortDirection());
        Pageable pageable = PageRequest.of(request.pageNumber() - 1, request.pageSize(), sort);
        Page<Student> all = repository.findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(request.searched(), pageable);
        return all.map(student -> mapper.map(student, StudentDto.class));
    }

    Page<StudentDto> getAllStudentsFromRequest(final PageableRequest pageable,
                                               final User user) {
        if (user.isTeacher())
            return getFilteredAndSortedStudentsPage(user, pageable);
        if (isNamePresent(pageable.searched()))
            return findByFirstOrLastNamePaginated(pageable);
        else
            return fetchAllPaginated(pageable);
    }

    private Sort getSortOrder(String sortField, String sortDirection) {
        return sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
    }

    private Page<StudentDto> getFilteredAndSortedStudentsPage(final User user,
                                                              final PageableRequest pageableReq) {
        Sort sort = getSortOrder(pageableReq.sortField(), pageableReq.sortDirection());
        Pageable pageable = PageRequest.of(pageableReq.pageNumber() - 1, pageableReq.pageSize(), sort);
        List<StudentDto> students = user.getTeacher().getStudents().stream()
                .filter(student -> filterForFirstOrLastNameContainingString(student, pageableReq.searched()))
                .map(student -> mapper.map(student, StudentDto.class))
                .toList();
        List<StudentDto> output = getDisplayedStudents(pageable, students);
        return new PageImpl<>(output, pageable, students.size());
    }

    private boolean filterForFirstOrLastNameContainingString(Student student, String searched) {
        return !isNamePresent(searched) ||
                (isNamePresent(searched) && firstOrLastNameContainsString(student, searched));
    }

    private boolean firstOrLastNameContainsString(Student student, String searched) {
        return student.getFirstName().toLowerCase().contains(searched.toLowerCase()) ||
                student.getLastName().toLowerCase().contains(searched.toLowerCase());
    }

    private List<StudentDto> getDisplayedStudents(Pageable pageable, List<StudentDto> students) {
        return students.stream()
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .toList();
    }

    private void addReferencingObjects(final Student student) {
        student.setFieldOfStudy(student.getFieldOfStudy());
        assignTeachers(student);
    }

    private void assignTeachers(final Student student) {
        if (student.getFieldOfStudy() == null)
            return;
        student.getFieldOfStudy().getSubjects().stream()
                .map(Subject::getTeachers)
                .flatMap(Set::stream)
                .forEach(student::addTeacher);
    }

    private void removeReferencingObjects(final Student student) {
        student.setFieldOfStudy(null);
        Set<Teacher> teachers = new HashSet<>(student.getTeachers());
        teachers.forEach(student::removeTeacher);
    }
}

package com.example.classroom.teacher;

import com.example.classroom.fieldOfStudy.FieldOfStudy;
import com.example.classroom.pageable.PageableRequest;
import com.example.classroom.student.Student;
import com.example.classroom.subject.Subject;
import com.example.classroom.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.example.classroom.pageable.PageableService.isNamePresent;

@Service
@RequiredArgsConstructor
public class TeacherService {
    private final TeacherRepository repository;
    private final ModelMapper mapper;

    @Transactional
    public TeacherDto create(final TeacherDto dto) {
        Teacher teacher = mapper.map(dto, Teacher.class);
        addReferencingObjects(teacher);
        Teacher saved = repository.save(teacher);
        return mapper.map(saved, TeacherDto.class);
    }

    @Transactional
    TeacherDto update(final TeacherDto dto) {
        Teacher teacher = repository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid teacher '" + dto.getFirstName() + " " + dto.getLastName() + "' with ID: " + dto.getId()));
        removeReferencingObjects(teacher);
        mapper.map(dto, teacher);
        addReferencingObjects(teacher);
        return mapper.map(teacher, TeacherDto.class);
    }

    @Transactional
    public List<TeacherDto> fetchAll() {
        List<Teacher> teachers = repository.findAll();
        return teachers.stream().map(teacher -> mapper.map(teacher, TeacherDto.class)).toList();
    }

    @Transactional
    Page<TeacherDto> fetchAllPaginated(final PageableRequest request) {
        Sort sort = getSortOrder(request.sortField(), request.sortDir());
        Pageable pageable = PageRequest.of(request.pageNumber() - 1, request.pageSize(), sort);
        Page<Teacher> all = repository.findAll(pageable);
        return all.map(teacher -> mapper.map(teacher, TeacherDto.class));
    }

    @Transactional
    TeacherDto fetchById(final Long id) {
        Optional<Teacher> byId = repository.findById(id);
        return byId.map(teacher -> mapper.map(teacher, TeacherDto.class))
                .orElseThrow(() -> new IllegalArgumentException("Invalid teacher ID: " + id));
    }

    @Transactional
    public void remove(final Long id) {
        Teacher teacher = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid teacher ID: " + id));
        removeReferencingObjects(teacher);
        repository.delete(teacher);
    }

    @Transactional
    void removeAll() {
        repository.findAll().forEach(this::removeReferencingObjects);
        repository.deleteAll();
    }

    List<TeacherDto> findByFirstOrLastName(final String searched) {
        List<Teacher> found = repository.findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(searched);
        return found.stream().map(s -> mapper.map(s, TeacherDto.class)).toList();
    }

    Page<TeacherDto> findByFirstOrLastNamePaginated(final PageableRequest request) {
        Sort sort = getSortOrder(request.sortField(), request.sortDir());
        Pageable pageable = PageRequest.of(request.pageNumber() - 1, request.pageSize(), sort);
        Page<Teacher> all = repository.findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(request.name(), pageable);
        return all.map(student -> mapper.map(student, TeacherDto.class));
    }

    Page<TeacherDto> getAllTeachersFromRequest(final PageableRequest pageable, final User user) {
        if (user.isStudent())
            return extractTeachersFromStudentAsPageable(user, pageable);
        if (isNamePresent(pageable.name()))
            return findByFirstOrLastNamePaginated(pageable);
        else
            return fetchAllPaginated(pageable);
    }

    private Page<TeacherDto> extractTeachersFromStudentAsPageable(final User user, final PageableRequest pageableReq) {
        Sort sort = getSortOrder(pageableReq.sortField(), pageableReq.sortDir());
        Pageable pageable = PageRequest.of(pageableReq.pageNumber() - 1, pageableReq.pageSize(), sort);
        List<TeacherDto> teachers = user.getStudent().getTeachers().stream()
                .filter(teacher -> filterForFirstOrLastNameContainingString(teacher, pageableReq.name()))
                .map(teacher -> mapper.map(teacher, TeacherDto.class))
                .toList();

        List<TeacherDto> output = teachers.stream()
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .toList();
        return new PageImpl<>(output, pageable, teachers.size());
    }

    private static boolean filterForFirstOrLastNameContainingString(Teacher teacher, String searched) {
        return !isNamePresent(searched) ||
                (isNamePresent(searched) && firstOrLastNameContainsString(teacher, searched));
    }

    private static boolean firstOrLastNameContainsString(Teacher teacher, String searched) {
        return teacher.getFirstName().toLowerCase().contains(searched.toLowerCase()) ||
                teacher.getLastName().toLowerCase().contains(searched.toLowerCase());
    }

    private void addReferencingObjects(final Teacher teacher) {
        Set<Subject> subjects = new HashSet<>(teacher.getSubjects());
        teacher.setDepartment(teacher.getDepartment());
        assignStudentsToTeacher(teacher);
        subjects.forEach(teacher::addSubject);
    }

    private void assignStudentsToTeacher(Teacher teacher) {
        teacher.getSubjects().stream()
                .map(Subject::getFieldOfStudy)
                .map(FieldOfStudy::getStudents)
                .flatMap(Set::stream)
                .forEach(teacher::addStudent);
    }

    private void removeReferencingObjects(final Teacher teacher) {
        Set<Student> students = new HashSet<>(teacher.getStudents());
        Set<Subject> subjects = new HashSet<>(teacher.getSubjects());
        teacher.setDepartment(null);
        students.forEach(teacher::removeStudent);
        subjects.forEach(teacher::removeSubject);
    }

    private static Sort getSortOrder(String sortField, String sortDirection) {
        return sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
    }
}

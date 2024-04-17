package com.example.classroom.fieldofstudy;

import com.example.classroom.department.DepartmentDto;
import com.example.classroom.enums.LevelOfEducation;
import com.example.classroom.enums.Semester;
import com.example.classroom.student.Student;
import com.example.classroom.subject.Subject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class FieldOfStudyService {
    private final FieldOfStudyRepository repository;
    private final ModelMapper mapper;

    @Transactional
    FieldOfStudyDto create(final FieldOfStudyDto dto) {
        final var fieldOfStudy = mapper.map(dto, FieldOfStudy.class);
        addReferencingObjects(fieldOfStudy);
        final FieldOfStudy saved = repository.save(fieldOfStudy);
        return mapper.map(saved, FieldOfStudyDto.class);
    }

    @Transactional
    FieldOfStudyDto update(final FieldOfStudyDto dto) {
        final var fieldOfStudy = repository.findById(dto.getId())
            .orElseThrow(() -> new IllegalArgumentException("Invalid Field Of Study '" + dto + "' with ID: " + dto.getId()));
        removeDepartment(fieldOfStudy);
        mapper.map(dto, fieldOfStudy);
        addReferencingObjects(fieldOfStudy);
        return mapper.map(fieldOfStudy, FieldOfStudyDto.class);
    }

    @Transactional
    void updateSubjects(final FieldOfStudyDto dto) {
        final FieldOfStudy fieldOfStudy = repository.findById(dto.getId())
            .orElseThrow(() -> new IllegalArgumentException("Invalid Field Of Study '" + dto + "' with ID: " + dto.getId()));
        removeSubjects(fieldOfStudy);
        mapper.map(dto, fieldOfStudy);
        addReferencingObjects(fieldOfStudy);
        repository.save(fieldOfStudy);
    }

    public Collection<FieldOfStudyDto> fetchAll() {
        final var all = repository.findAll();
        return all.stream().map(fieldOfStudy -> mapper.map(fieldOfStudy, FieldOfStudyDto.class)).toList();
    }

    FieldOfStudyDto fetchById(final Long id) {
        final var byId = repository.findById(id);
        return byId.map(fieldOfStudy -> mapper.map(fieldOfStudy, FieldOfStudyDto.class))
            .orElseThrow(() -> new IllegalArgumentException("Invalid Field Of Study ID: " + id));
    }

    @Transactional
    void remove(final Long id) {
        final var fieldOfStudy = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid Field Of Study ID: " + id));
        removeReferencingObjects(fieldOfStudy);
        repository.delete(fieldOfStudy);
    }

    @Transactional
    void removeAll() {
        repository.findAll().forEach(this::removeReferencingObjects);
        repository.deleteAll();
    }

    Map<Semester, List<Subject>> fetchAllSubjectsFromFieldOfStudyGroupedBySemesters(final Long fieldOfStudyId) {
        final var subjects = repository.findAllSubjectsFromFieldOfStudy(fieldOfStudyId);
        return Arrays.stream(Semester.values()).collect(Collectors.toMap(
            Function.identity(),
            s -> filterSubjectsBySemester(subjects, s).toList()
        ));
    }

    Map<Semester, Integer> calculateHoursInEachSemesterFromFieldOfStudy(final Long fieldOfStudyId) {
        final var subjects = repository.findAllSubjectsFromFieldOfStudy(fieldOfStudyId);
        return Arrays.stream(Semester.values()).collect(Collectors.toMap(
            Function.identity(),
            s -> filterSubjectsBySemester(subjects, s).mapToInt(Subject::getHoursInSemester).sum()
        ));
    }

    private Stream<Subject> filterSubjectsBySemester(final Collection<Subject> subjects,
                                                     final Semester semester) {
        return subjects.stream().filter(s -> s.getSemester() == semester);
    }

    Collection<String> splitDescription(final Long id) {
        final var description = fetchById(id).getDescription();
        if (description == null) return List.of();
        return Stream.of(description.split(";"))
            .map(String::strip)
            .toList();
    }

    Map<Semester, Integer> calculateEctsPointsForEachSemester(final Long id) {
        final var subjects = repository.findAllSubjectsFromFieldOfStudy(id);
        return Arrays.stream(Semester.values()).collect(Collectors.toMap(
            Function.identity(),
            s -> getSumOfEctsPointsForSemester(subjects, s)
        ));
    }

    private Integer getSumOfEctsPointsForSemester(final Collection<Subject> subjects,
                                                  final Semester semester) {
        return subjects.stream().filter(subject -> subject.getSemester() == semester)
            .mapToInt(Subject::getEctsPoints)
            .sum();
    }

    Integer getSumOfEctsPointsFromAllSemesters(final Long id) {
        return calculateEctsPointsForEachSemester(id).values()
            .stream().mapToInt(Integer::intValue)
            .sum();
    }

    String getImagePath(final Long id) {
        final var imageName = fetchById(id).getImage();
        final var imagePath = Path.of("/img").resolve("fields-of-study");
        return imagePath.resolve(Objects.requireNonNullElse(imageName, "default.jpg")).toString();
    }

    Collection<FieldOfStudyDto> fetchAllByLevelOfEducationSortedByName(final LevelOfEducation levelOfEducation) {
        return repository.findAllByLevelOfEducation(levelOfEducation, Sort.by(Sort.Direction.ASC, "name"))
            .stream().map(fieldOfStudy -> mapper.map(fieldOfStudy, FieldOfStudyDto.class)).toList();
    }

    Map<String, List<FieldOfStudyDto>> fetchAllGroupedByNameAndSortedByName() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "name"))
            .stream().map(FieldOfStudy::getName).distinct()
            .collect(Collectors.toMap(
                Function.identity(),
                name -> repository.findAllByNameContainingIgnoreCase(name)
                    .stream().map(f -> mapper.map(f, FieldOfStudyDto.class)).toList(),
                (key1, key2) -> key1,
                LinkedHashMap::new
            ));
    }

    public List<FieldOfStudyDto> fetchAllWithNoDepartment() {
        return repository.findAll().stream()
            .filter(fieldOfStudy -> fieldOfStudy.getDepartment() == null)
            .map(fieldOfStudy -> mapper.map(fieldOfStudy, FieldOfStudyDto.class))
            .toList();
    }

    public Collection<FieldOfStudyDto> fetchAllWithGivenDepartmentDtoOrNoDepartment(final DepartmentDto dto) {
        return Stream.concat(
            fetchAllWithNoDepartment().stream(),
            repository.findAll().stream()
                .filter(fieldOfStudy -> fieldOfStudy.getDepartment() != null)
                .filter(fieldOfStudy -> Objects.equals(fieldOfStudy.getDepartment().getId(), dto.getId()))
                .map(fieldOfStudy -> mapper.map(fieldOfStudy, FieldOfStudyDto.class))
        ).toList();
    }

    private void addReferencingObjects(final FieldOfStudy fieldOfStudy) {
        final Set<Subject> subjects = new HashSet<>(fieldOfStudy.getSubjects());
        final Set<Student> students = new HashSet<>(fieldOfStudy.getStudents());
        fieldOfStudy.setDepartment(fieldOfStudy.getDepartment());
        subjects.forEach(fieldOfStudy::addSubject);
        students.forEach(fieldOfStudy::addStudent);
    }

    private void removeReferencingObjects(final FieldOfStudy fieldOfStudy) {
        removeDepartment(fieldOfStudy);
        removeSubjects(fieldOfStudy);
        removeStudents(fieldOfStudy);
    }

    private void removeDepartment(final FieldOfStudy fieldOfStudy) {
        fieldOfStudy.setDepartment(null);
    }

    private void removeSubjects(final FieldOfStudy fieldOfStudy) {
        final var subjects = fieldOfStudy.getSubjects();
        subjects.forEach(fieldOfStudy::removeSubject);
    }

    private void removeStudents(final FieldOfStudy fieldOfStudy) {
        final var students = fieldOfStudy.getStudents();
        students.forEach(fieldOfStudy::removeStudent);
    }
}

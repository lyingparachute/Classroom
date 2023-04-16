package com.example.classroom.fieldOfStudy;

import com.example.classroom.department.DepartmentDto;
import com.example.classroom.enums.AcademicTitle;
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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class FieldOfStudyService {
    private final FieldOfStudyRepository repository;
    private final ModelMapper mapper;

    @Transactional
    FieldOfStudyDto create(FieldOfStudyDto dto) {
        FieldOfStudy fieldOfStudy = mapper.map(dto, FieldOfStudy.class);
        addReferencingObjects(fieldOfStudy);
        FieldOfStudy saved = repository.save(fieldOfStudy);
        return mapper.map(saved, FieldOfStudyDto.class);
    }

    @Transactional
    FieldOfStudyDto update(FieldOfStudyDto dto) {
        FieldOfStudy fieldOfStudy = repository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Field Of Study '" + dto + "' with ID: " + dto.getId()));
        removeDepartment(fieldOfStudy);
        mapper.map(dto, fieldOfStudy);
        addReferencingObjects(fieldOfStudy);
        return mapper.map(fieldOfStudy, FieldOfStudyDto.class);
    }

    @Transactional
    FieldOfStudyDto updateSubjects(FieldOfStudyDto dto) {
        FieldOfStudy fieldOfStudy = repository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Field Of Study '" + dto + "' with ID: " + dto.getId()));
        removeSubjects(fieldOfStudy);
        mapper.map(dto, fieldOfStudy);
        addReferencingObjects(fieldOfStudy);
        FieldOfStudy saved = repository.save(fieldOfStudy);
        return mapper.map(saved, FieldOfStudyDto.class);
    }

    public List<FieldOfStudyDto> fetchAll() {
        List<FieldOfStudy> all = repository.findAll();
        return all.stream().map(fieldOfStudy -> mapper.map(fieldOfStudy, FieldOfStudyDto.class)).toList();
    }

    FieldOfStudyDto fetchById(Long id) {
        Optional<FieldOfStudy> byId = repository.findById(id);
        return byId.map(fieldOfStudy -> mapper.map(fieldOfStudy, FieldOfStudyDto.class))
                .orElseThrow(() -> new IllegalArgumentException("Invalid Field Of Study ID: " + id));
    }

    @Transactional
    void remove(Long id) {
        FieldOfStudy fieldOfStudy = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Field Of Study ID: " + id));
        removeReferencingObjects(fieldOfStudy);
        repository.delete(fieldOfStudy);
    }

    @Transactional
    void removeAll() {
        repository.findAll().forEach(this::removeReferencingObjects);
        repository.deleteAll();
    }

    Map<Semester, List<Subject>> fetchAllSubjectsFromFieldOfStudyGroupedBySemesters(Long fieldOfStudyId) {
        List<Subject> subjects = repository.findAllSubjectsFromFieldOfStudy(fieldOfStudyId);
        return Arrays.stream(Semester.values()).collect(Collectors.toMap(
                Function.identity(),
                s -> filterSubjectsBySemester(subjects, s).toList()
        ));
    }

    Map<Semester, Integer> calculateHoursInEachSemesterFromFieldOfStudy(Long fieldOfStudyId) {
        List<Subject> subjects = repository.findAllSubjectsFromFieldOfStudy(fieldOfStudyId);
        return Arrays.stream(Semester.values()).collect(Collectors.toMap(
                Function.identity(),
                s -> filterSubjectsBySemester(subjects, s).mapToInt(Subject::getHoursInSemester).sum()
        ));
    }

    private Stream<Subject> filterSubjectsBySemester(List<Subject> subjects, Semester semester) {
        return subjects.stream().filter(s -> s.getSemester().equals(semester));
    }

    List<String> splitDescription(Long id) {
        String description = fetchById(id).getDescription();
        if (description != null) {
            return Stream.of(description.split(";"))
                    .map(String::strip)
                    .toList();
        }
        return List.of();
    }

    Map<Semester, Integer> calculateEctsPointsForEachSemester(Long id) {
        List<Subject> subjects = repository.findAllSubjectsFromFieldOfStudy(id);
        return Arrays.stream(Semester.values()).collect(Collectors.toMap(
                Function.identity(),
                s -> getSumOfEctsPointsForSemester(subjects, s)
        ));
    }

    private Integer getSumOfEctsPointsForSemester(List<Subject> subjects, Semester semester) {
        return subjects.stream().filter(subject -> subject.getSemester().equals(semester))
                .mapToInt(Subject::getEctsPoints)
                .sum();
    }

    Integer getSumOfEctsPointsFromAllSemesters(Long id) {
        return calculateEctsPointsForEachSemester(id).values()
                .stream().mapToInt(Integer::intValue)
                .sum();
    }

    int getNumberOfSemesters(Long id) {
        AcademicTitle title = fetchById(id).getTitle();
        switch (title) {
            case BACH -> {
                return 6;
            }
            case ENG -> {
                return 7;
            }
            default -> {
                return 3;
            }
        }
    }

    String getImagePath(Long id) {
        String imageName = fetchById(id).getImage();
        Path imagePath = Path.of("/img").resolve("fields-of-study");
        return imagePath.resolve(Objects.requireNonNullElse(imageName, "default.jpg")).toString();
    }

    List<FieldOfStudyDto> fetchAllByLevelOfEducationSortedByName(LevelOfEducation levelOfEducation) {
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

    public List<FieldOfStudyDto> fetchAllWithGivenDepartmentDtoOrNoDepartment(DepartmentDto dto) {
        return Stream.concat(
                fetchAllWithNoDepartment().stream(),
                repository.findAll().stream()
                        .filter(fieldOfStudy -> fieldOfStudy.getDepartment() != null)
                        .filter(fieldOfStudy -> Objects.equals(fieldOfStudy.getDepartment().getId(), dto.getId()))
                        .map(fieldOfStudy -> mapper.map(fieldOfStudy, FieldOfStudyDto.class))
        ).toList();
    }

    private void addReferencingObjects(FieldOfStudy fieldOfStudy) {
        Set<Subject> subjects = new HashSet<>(fieldOfStudy.getSubjects());
        Set<Student> students = new HashSet<>(fieldOfStudy.getStudents());
        fieldOfStudy.setDepartment(fieldOfStudy.getDepartment());
        subjects.forEach(fieldOfStudy::addSubject);
        students.forEach(fieldOfStudy::addStudent);
    }

    private void removeReferencingObjects(FieldOfStudy fieldOfStudy) {
        removeDepartment(fieldOfStudy);
        removeSubjects(fieldOfStudy);
        removeStudents(fieldOfStudy);
    }

    private void removeDepartment(FieldOfStudy fieldOfStudy) {
        fieldOfStudy.setDepartment(null);
    }

    private void removeSubjects(FieldOfStudy fieldOfStudy) {
        Set<Subject> subjects = fieldOfStudy.getSubjects();
        subjects.forEach(fieldOfStudy::removeSubject);
    }

    private void removeStudents(FieldOfStudy fieldOfStudy) {
        Set<Student> students = fieldOfStudy.getStudents();
        students.forEach(fieldOfStudy::removeStudent);
    }
}

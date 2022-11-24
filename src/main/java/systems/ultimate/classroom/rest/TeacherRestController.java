package systems.ultimate.classroom.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import systems.ultimate.classroom.dto.TeacherDto;
import systems.ultimate.classroom.service.TeacherService;

import java.util.List;

@RestController
@RequestMapping("api/teachers")
public class TeacherRestController {
    
    private final TeacherService teacherService;

    public TeacherRestController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping()
    public ResponseEntity<List<TeacherDto>> getTeachers() {
        List<TeacherDto> teacher = teacherService.fetchAll();
        return teacher.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(teacher);
    }

    @PostMapping("create")
    public ResponseEntity<TeacherDto> createTeacher(@RequestBody TeacherDto teacherDto) {
        TeacherDto created = teacherService.create(teacherDto);
        return created != null ?
                ResponseEntity.status(HttpStatus.CREATED)
                        .body(created) :
                ResponseEntity.badRequest().build();
    }

    @GetMapping("{id}")
    public ResponseEntity<TeacherDto> getTeacher(@PathVariable Long id) {
        TeacherDto teacherDto = teacherService.fetchById(id);
        return teacherDto != null ?
                ResponseEntity.ok(teacherDto) :
                ResponseEntity.notFound().build();
    }

    @PutMapping
    public ResponseEntity<TeacherDto> putTeacher(@RequestBody TeacherDto teacherDto){
        TeacherDto updated = teacherService.update(teacherDto);
        return updated != null ?
                ResponseEntity.status(HttpStatus.CREATED)
                        .body(updated) :
                ResponseEntity.badRequest().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        teacherService.remove(id);
        return ResponseEntity.accepted().build();
    }
}

package com.example.classroom.auth.service;

import com.example.classroom.auth.model.RegisterRequest;
import com.example.classroom.repository.util.UnitTestsInitData;
import com.example.classroom.student.StudentDto;
import com.example.classroom.student.StudentService;
import com.example.classroom.teacher.TeacherService;
import com.example.classroom.user.User;
import com.example.classroom.user.UserRepository;
import com.example.classroom.user.UserRole;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceTest {

    @InjectMocks
    UserManagementService service;

    @Mock
    UserRepository repository;

    @Spy
    ModelMapper mapper;

    @Mock
    BCryptPasswordEncoder passwordEncoder;

    @Mock
    StudentService studentService;

    @Mock
    TeacherService teacherService;

    @Spy
    UnitTestsInitData initData;


    @Nested
    class Register {
        @Test
        void returnsUser_withAssociatedStudent_givenRegisterRequest_withStudentRole() {
            // Given
            UserRole role = UserRole.ROLE_STUDENT;
            RegisterRequest request = initData.createRegisterRequest();
            request.setRole(role);
            User expectedUser = initData.createUser();
            expectedUser.setRole(role);
            // When
            when(passwordEncoder.encode(anyString())).thenReturn(expectedUser.getPassword());
            when(repository.save(any(User.class))).thenReturn(expectedUser);

            User actualUser = service.register(request);
            // Then
            verify(passwordEncoder).encode(request.getPassword());
            verify(repository).save(any(User.class));
            verify(studentService).create(any(StudentDto.class));
            verifyNoInteractions(teacherService);
            assertThat(actualUser).isNotNull();
            assertAll("Check User's properties",
                    () -> assertThat(actualUser.getId()).as("Check user's ID")
                            .isEqualTo(expectedUser.getId()),
                    () -> assertThat(actualUser.getFirstName()).as("Check user's First Name")
                            .isEqualTo(expectedUser.getFirstName()),
                    () -> assertThat(actualUser.getLastName()).as("Check user's Last Name")
                            .isEqualTo(expectedUser.getLastName()),
                    () -> assertThat(actualUser.getEmail()).as("Check user's Email")
                            .isEqualTo(expectedUser.getEmail()),
                    () -> assertThat(actualUser.getPassword()).as("Check user's Password")
                            .isEqualTo(expectedUser.getPassword()),
                    () -> assertThat(actualUser.getRole()).as("Check user's Role")
                            .isEqualTo(expectedUser.getRole())
            );
        }
    }

    @Nested
    class Update {
        @Test
        void update() {
            // Given

            // When

            // Then
        }
    }

    @Nested
    class LoadUserByUsername {
        @Test
        void loadUserByUsername() {
            // Given

            // When

            // Then
        }
    }

    @Nested
    class RemoveByUsername {

        @Test
        void removeByUsername() {
            // Given

            // When

            // Then
        }
    }
}
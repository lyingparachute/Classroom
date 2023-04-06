package com.example.classroom.auth.service;

import com.example.classroom.auth.model.RegisterRequest;
import com.example.classroom.auth.model.UpdateRequest;
import com.example.classroom.student.StudentDto;
import com.example.classroom.student.StudentService;
import com.example.classroom.teacher.TeacherDto;
import com.example.classroom.teacher.TeacherService;
import com.example.classroom.test.util.UnitTestsInitData;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
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
        void createsUser_withAssociatedStudent_givenRegisterRequest_withStudentRole() {
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

        @Test
        void createsUser_withAssociatedTeacher_givenRegisterRequest_withTeacherRole() {
            // Given
            UserRole role = UserRole.ROLE_TEACHER;
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
            verifyNoInteractions(studentService);
            verify(teacherService).create(any(TeacherDto.class));
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

        @Test
        void createsUser_withAssociatedTeacher_givenRegisterRequest_withDeanRole() {
            // Given
            UserRole role = UserRole.ROLE_DEAN;
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
            verifyNoInteractions(studentService);
            verify(teacherService).create(any(TeacherDto.class));
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

        @Test
        void createsUser_withoutAssociatedEntity_givenRegisterRequest_withAdminRole() {
            // Given
            UserRole role = UserRole.ROLE_ADMIN;
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
            verifyNoInteractions(studentService);
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
        void updatesUser_givenExistingUsername() {
            // Given
            User user = initData.createUser();

            UpdateRequest updateRequest = initData.createUpdateRequest();
            String email = user.getEmail();
            User expected = User.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .firstName(updateRequest.getFirstName())
                    .lastName(updateRequest.getLastName())
                    .password(updateRequest.getPassword())
                    .build();

            // When
            when(repository.findByEmail(email)).thenReturn(Optional.of(user));
            when(repository.save(any(User.class))).thenReturn(expected);
            User updated = service.update(updateRequest);

            // Then
            verify(repository).findByEmail(email);
            verify(repository).save(any(User.class));
            assertThat(updated).isNotNull();
            assertAll("Check User's properties",
                    () -> assertThat(updated.getFirstName()).as("Check user's First Name")
                            .isEqualTo(updateRequest.getFirstName()),
                    () -> assertThat(updated.getLastName()).as("Check user's Last Name")
                            .isEqualTo(updateRequest.getLastName()),
                    () -> assertThat(updated.getEmail()).as("Check user's Email")
                            .isEqualTo(updateRequest.getEmail()),
                    () -> assertThat(updated.getPassword()).as("Check user's Password")
                            .isEqualTo(updateRequest.getPassword())
            );

        }
    }

    @Nested
    class LoadUserByUsername {
        @Test
        void returnsUser_whenUserExists_inDatabase() {
            // Given
            User expectedUser = initData.createUser();
            String email = expectedUser.getEmail();

            // When
            when(repository.findByEmail(email)).thenReturn(Optional.of(expectedUser));
            User actualUser = service.loadUserByUsername(email);

            // Then
            verify(repository).findByEmail(email);
            assertThat(actualUser).isNotNull().isEqualTo(expectedUser);
        }

        @Test
        void throwsUsernameNotFoundException_whenUserDoesNotExist_inDatabase() {
            // Given
            String email = initData.createUser().getEmail();

            // When
            when(repository.findByEmail(email)).thenReturn(Optional.empty());
            Throwable thrown = catchThrowable(() -> service.loadUserByUsername(email));

            // Then
            verify(repository).findByEmail(email);
            assertThat(thrown)
                    .isExactlyInstanceOf(UsernameNotFoundException.class)
                    .hasMessage("User with email " + email + " does not exist in database.");
        }
    }

    @Nested
    class RemoveByUsername {

        @Test
        void removesUser_givenExistingUsername() {
            // Given
            User user = initData.createUser();
            String email = user.getEmail();

            // When
            when(repository.findByEmail(email)).thenReturn(Optional.of(user));
            service.removeByUsername(email);

            // Then
            verify(repository).findByEmail(email);
            verify(repository).delete(user);
        }
    }
}
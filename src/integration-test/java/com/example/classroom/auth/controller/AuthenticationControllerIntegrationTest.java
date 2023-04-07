package com.example.classroom.auth.controller;

import com.example.classroom.auth.model.RegisterRequest;
import com.example.classroom.student.Student;
import com.example.classroom.student.StudentRepository;
import com.example.classroom.teacher.Teacher;
import com.example.classroom.teacher.TeacherRepository;
import com.example.classroom.test.util.IntegrationTestsInitData;
import com.example.classroom.user.User;
import com.example.classroom.user.UserRepository;
import com.example.classroom.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("integration")
@SpringBootTest
class AuthenticationControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    UserRepository userRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    private IntegrationTestsInitData initData;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        initData.cleanUp();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Nested
    class SignIn {
        @Test
        void getsSignInPageView() throws Exception {
            mockMvc.perform(get("/sign-in"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                    .andExpect(view().name("auth/sign-in"));
        }
    }

    @Nested
    class SignUp {
        @Test
        void getsSignUpPageView() throws Exception {
            mockMvc.perform(get("/sign-up"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                    .andExpect(view().name("auth/sign-up"));
        }

        @Test
        void createsNewUserAccount_withAssociatedStudent_givenValidRegisterRequest_withStudentRole() throws Exception {
            // Given
            RegisterRequest request = initData.createRegisterRequest();
            request.setRole(UserRole.ROLE_STUDENT);

            // When
            mockMvc.perform(post("/sign-up")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                            .content("firstName=" + request.getFirstName() +
                                    "&lastName=" + request.getLastName() +
                                    "&email=" + request.getEmail() +
                                    "&password=" + request.getPassword() +
                                    "&role=" + request.getRole()
                            )
                    )
                    .andDo(print())
                    .andExpect(status().is3xxRedirection());

            // Then
            Optional<User> userByEmail = userRepository.findByEmail(request.getEmail());
            Optional<Student> studentByEmail = studentRepository.findByEmail(request.getEmail());
            Optional<Teacher> teacherByEmail = teacherRepository.findByEmail(request.getEmail());

            assertThat(userByEmail).isPresent();
            assertThat(studentByEmail).isPresent();
            assertThat(teacherByEmail).isNotPresent();

            User user = userByEmail.get();
            Student student = studentByEmail.get();

            assertThat(user.getFirstName()).isEqualTo(student.getFirstName()).isEqualTo(request.getFirstName());
            assertThat(user.getLastName()).isEqualTo(student.getLastName()).isEqualTo(request.getLastName());
            assertThat(user.getEmail()).isEqualTo(student.getEmail()).isEqualTo(request.getEmail());
            assertThat(user.getRole()).isEqualTo(request.getRole());
            assertThat(user.getStudent()).isNotNull().isEqualTo(student);
            assertThat(user.getTeacher()).isNull();
        }

        @Test
        void createsNewUserAccount_withAssociatedTeacher_givenValidRegisterRequest_withTeacherRole() throws Exception {
            // Given
            RegisterRequest request = initData.createRegisterRequest();
            request.setRole(UserRole.ROLE_TEACHER);

            // When
            mockMvc.perform(post("/sign-up")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                            .content("firstName=" + request.getFirstName() +
                                    "&lastName=" + request.getLastName() +
                                    "&email=" + request.getEmail() +
                                    "&password=" + request.getPassword() +
                                    "&role=" + request.getRole()
                            )
                    )
                    .andDo(print())
                    .andExpect(status().is3xxRedirection());

            // Then
            Optional<User> userByEmail = userRepository.findByEmail(request.getEmail());
            Optional<Student> studentByEmail = studentRepository.findByEmail(request.getEmail());
            Optional<Teacher> teacherByEmail = teacherRepository.findByEmail(request.getEmail());

            assertThat(userByEmail).isPresent();
            assertThat(studentByEmail).isNotPresent();
            assertThat(teacherByEmail).isPresent();

            User user = userByEmail.get();
            Teacher teacher = teacherByEmail.get();

            assertThat(user.getFirstName()).isEqualTo(teacher.getFirstName()).isEqualTo(request.getFirstName());
            assertThat(user.getLastName()).isEqualTo(teacher.getLastName()).isEqualTo(request.getLastName());
            assertThat(user.getEmail()).isEqualTo(teacher.getEmail()).isEqualTo(request.getEmail());
            assertThat(user.getRole()).isEqualTo(request.getRole());
            assertThat(user.getStudent()).isNull();
            assertThat(user.getTeacher()).isNotNull().isEqualTo(teacher);
        }

        @Test
        void createsNewUserAccount_withAssociatedTeacher_givenValidRegisterRequest_withDeanRole() throws Exception {
            // Given
            RegisterRequest request = initData.createRegisterRequest();
            request.setRole(UserRole.ROLE_DEAN);

            // When
            mockMvc.perform(post("/sign-up")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                            .content("firstName=" + request.getFirstName() +
                                    "&lastName=" + request.getLastName() +
                                    "&email=" + request.getEmail() +
                                    "&password=" + request.getPassword() +
                                    "&role=" + request.getRole()
                            )
                    )
                    .andDo(print())
                    .andExpect(status().is3xxRedirection());

            // Then
            Optional<User> userByEmail = userRepository.findByEmail(request.getEmail());
            Optional<Student> studentByEmail = studentRepository.findByEmail(request.getEmail());
            Optional<Teacher> teacherByEmail = teacherRepository.findByEmail(request.getEmail());

            assertThat(userByEmail).isPresent();
            assertThat(studentByEmail).isNotPresent();
            assertThat(teacherByEmail).isPresent();

            User user = userByEmail.get();
            Teacher teacher = teacherByEmail.get();

            assertThat(user.getFirstName()).isEqualTo(teacher.getFirstName()).isEqualTo(request.getFirstName());
            assertThat(user.getLastName()).isEqualTo(teacher.getLastName()).isEqualTo(request.getLastName());
            assertThat(user.getEmail()).isEqualTo(teacher.getEmail()).isEqualTo(request.getEmail());
            assertThat(user.getRole()).isEqualTo(request.getRole());
            assertThat(user.getStudent()).isNull();
            assertThat(user.getTeacher()).isNotNull().isEqualTo(teacher);
        }

        @Test
        void createsNewUserAccount_givenValidRegisterRequest_withAdminRole() throws Exception {
            // Given
            RegisterRequest request = initData.createRegisterRequest();
            request.setRole(UserRole.ROLE_ADMIN);

            // When
            mockMvc.perform(post("/sign-up")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                            .content("firstName=" + request.getFirstName() +
                                    "&lastName=" + request.getLastName() +
                                    "&email=" + request.getEmail() +
                                    "&password=" + request.getPassword() +
                                    "&role=" + request.getRole()
                            )
                    )
                    .andDo(print())
                    .andExpect(status().is3xxRedirection());

            // Then
            Optional<User> userByEmail = userRepository.findByEmail(request.getEmail());
            Optional<Student> studentByEmail = studentRepository.findByEmail(request.getEmail());
            Optional<Teacher> teacherByEmail = teacherRepository.findByEmail(request.getEmail());

            assertThat(userByEmail).isPresent();
            assertThat(studentByEmail).isNotPresent();
            assertThat(teacherByEmail).isNotPresent();

            User user = userByEmail.get();

            assertThat(user.getFirstName()).isEqualTo(request.getFirstName());
            assertThat(user.getLastName()).isEqualTo(request.getLastName());
            assertThat(user.getEmail()).isEqualTo(request.getEmail());
            assertThat(user.getRole()).isEqualTo(request.getRole());
            assertThat(user.getStudent()).isNull();
            assertThat(user.getTeacher()).isNull();
        }
    }

    @Nested
    class ResetPassword {
        @Test
        void resetPassword() {

        }
    }
}
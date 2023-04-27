package com.example.classroom.user.password;

import com.example.classroom.auth.service.UserManagementService;
import com.example.classroom.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final UserManagementService service;

    void resetPassword(String email) {
        User user = service.loadUserByUsername(email);
    }
}

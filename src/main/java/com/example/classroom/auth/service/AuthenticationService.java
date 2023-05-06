package com.example.classroom.auth.service;

import com.example.classroom.auth.model.AuthenticationRequest;
import com.example.classroom.auth.model.AuthenticationResponse;
import com.example.classroom.token.JwtService;
import com.example.classroom.token.Token;
import com.example.classroom.token.TokenRepository;
import com.example.classroom.token.TokenType;
import com.example.classroom.user.User;
import com.example.classroom.user.UserRepository;
import com.example.classroom.user.UserRole;
import com.example.classroom.user.register.RegisterRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final ModelMapper mapper;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        User userDetails = new User();
        mapper.map(request, userDetails);
        userDetails.setPassword(passwordEncoder.encode(request.getPassword()));
        if (userDetails.getRole() == null) {
            userDetails.setRole(UserRole.ROLE_STUDENT);
        }
        var savedUser = repository.save(userDetails);
        var jwtToken = jwtService.generateToken(savedUser);
        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                ));
        User user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + request.getEmail() + " does not exist in database."));
        var jwtToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
}

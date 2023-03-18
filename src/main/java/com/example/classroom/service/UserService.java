package com.example.classroom.service;

import com.example.classroom.auth.RegisterRequest;
import com.example.classroom.enums.RoleEnum;
import com.example.classroom.model.UserLogin;
import com.example.classroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository repository;
    private final ModelMapper mapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public UserLogin register(RegisterRequest request) {
        UserLogin created = new UserLogin();
        mapper.map(request, created);
        created.setPassword(passwordEncoder.encode(request.getPassword()));
        //TODO delete auto role assignment
        created.setRole(RoleEnum.USER);
        return repository.save(created);
    }

    @Transactional
    public UserLogin update(UserLogin user) {
        UserLogin userLogin = loadUserByUsername(user.getEmail());
        mapper.map(user, userLogin);
        return repository.save(userLogin);
    }

    @Override
    public UserLogin loadUserByUsername(String email) throws UsernameNotFoundException {
        return repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " does not exist in database."));
    }

    @Transactional
    public void removeByUsername(String email) {
        repository.delete(loadUserByUsername(email));
    }
}

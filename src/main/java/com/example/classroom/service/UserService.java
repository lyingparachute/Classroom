package com.example.classroom.service;

import com.example.classroom.auth.RegisterRequest;
import com.example.classroom.enums.RoleEnum;
import com.example.classroom.model.User;
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
    public User register(RegisterRequest request) {
        User created = new User();
        mapper.map(request, created);
        created.setPassword(passwordEncoder.encode(request.getPassword()));
        //TODO delete auto role assignment
        created.setRole(RoleEnum.USER);
        return repository.save(created);
    }

    @Transactional
    public User update(User user) {
        User userLogin = loadUserByUsername(user.getEmail());
        mapper.map(user, userLogin);
        return repository.save(userLogin);
    }

    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        return repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " does not exist in database."));
    }

    @Transactional
    public void removeByUsername(String email) {
        repository.delete(loadUserByUsername(email));
    }
}

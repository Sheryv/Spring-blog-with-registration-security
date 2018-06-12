package com.sheryv.example.springblog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.sheryv.example.springblog.dto.UserDto;
import com.sheryv.example.springblog.model.Role;
import com.sheryv.example.springblog.model.User;
import com.sheryv.example.springblog.repository.RoleRepository;
import com.sheryv.example.springblog.repository.UserRepository;
import com.sheryv.example.springblog.validation.EmailExistsException;
import com.sheryv.example.springblog.validation.UsernameExistsException;

import javax.transaction.Transactional;
import java.util.Collections;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

//    @Autowired
//    private SessionRegistry sessionRegistry;

    public static final String TOKEN_INVALID = "invalidToken";
    public static final String TOKEN_EXPIRED = "expired";
    public static final String TOKEN_VALID = "valid";


    // API
    @Transactional
    @Override
    public User registerNewUserAccount(final UserDto accountDto) {
        if (emailExist(accountDto.getEmail())) {
            throw new EmailExistsException("There is an account with that email address: " + accountDto.getEmail());
        }
        if (repository.findByUsername(accountDto.getUsername()) != null) {
            throw new UsernameExistsException("There is an account with that username: " + accountDto.getUsername());
        }
        final User user = new User();

        user.setFirstName(accountDto.getFirstName());
        user.setLastName(accountDto.getLastName());
        user.setUsername(accountDto.getUsername());
        user.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        user.setEmail(accountDto.getEmail());
        user.setRoles(Collections.singletonList(roleRepository.findByName(Role.USER)));
        return repository.save(user);
    }

    private boolean emailExist(final String email) {
        return repository.findByEmail(email) != null;
    }
}
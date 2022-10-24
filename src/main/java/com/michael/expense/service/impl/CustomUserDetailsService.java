package com.michael.expense.service.impl;

import com.michael.expense.entity.User;
import com.michael.expense.repository.UserRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.ExecutionException;

import static com.michael.expense.constant.UserImplConstant.NO_USER_FOUND_BY_USERNAME;

@Service
@Transactional
@Qualifier("UserDetailsService")
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;
    private LoginAttemptService loginAttemptService;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository,
                                    LoginAttemptService loginAttemptService) {
        this.userRepository = userRepository;
        this.loginAttemptService = loginAttemptService;
    }

    @SneakyThrows
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userDb = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username));
        validateLoginAttempt(userDb);
        userDb.setDisplayLastLoginDate(userDb.getLastLoginDate());
        userDb.setLastLoginDate(new Date());

        userRepository.save(userDb);
        log.info("Returning found user by username {}", username);
        return userDb;
    }


    private void validateLoginAttempt(User user) {
        if (user.isNotLocked()) {
            if (loginAttemptService.hasExceededMaxAttempts(user.getUsername())) {
                user.setNotLocked(false);
            } else {
                user.setNotLocked(true);
            }
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }
}

package com.michael.expense.service.impl;

import com.michael.expense.entity.ConfirmationToken;
import com.michael.expense.entity.User;
import com.michael.expense.exceptions.domain.*;
import com.michael.expense.payload.request.UserRequest;
import com.michael.expense.payload.response.UserDto;
import com.michael.expense.repository.ConfirmationTokenRepository;
import com.michael.expense.repository.UserRepository;
import com.michael.expense.service.EmailSender;
import com.michael.expense.service.UserService;
import com.michael.expense.utility.EmailBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static com.michael.expense.constant.SecurityConstant.VERIFICATION_TOKEN_EXPIRED;
import static com.michael.expense.constant.SecurityConstant.VERIFICATION_TOKEN_NOT_FOUND;
import static com.michael.expense.constant.UserImplConstant.*;
import static com.michael.expense.entity.enumeration.Role.ROLE_USER;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    public static final String LINK_FOR_CONFIRMATION = "http://localhost:8080/api/v1/registration/confirm?token=";

    private UserRepository userRepository;
    private ModelMapper mapper;
    private PasswordEncoder passwordEncoder;
    private EmailSender emailSender;
    private ConfirmationTokenRepository tokenRepository;
    private EmailBuilder emailBuilder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           ModelMapper mapper,
                           PasswordEncoder passwordEncoder,
                           EmailSender emailSender,
                           ConfirmationTokenRepository tokenRepository,
                           EmailBuilder emailBuilder) {
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
        this.emailSender = emailSender;
        this.tokenRepository = tokenRepository;
        this.emailBuilder = emailBuilder;
    }


    @Override
    public UserDto createUser(UserRequest userRequest) {
        validateNewUsernameAndEmail(EMPTY, userRequest.getUsername(), userRequest.getEmail());
        String password = generatePassword();
        User newUser = User.builder()
                .userId(generateUserId())
                .username(userRequest.getUsername())
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .fullName(userRequest.getLastName() + " " + userRequest.getFirstName())
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(password))
                .lastUpdateDate(new Date())
                .lastLoginDate(new Date())
                .isNotLocked(true)
                .role(ROLE_USER.name())
                .userAuthorities(ROLE_USER.getAuthorities())
                .profileImageUrl(getTemporaryProfileImageUrl())
                .build();


        newUser = userRepository.save(newUser);
        log.info("Created new User with username {}", userRequest.getUsername());

        //for Email Verification
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                newUser
        );
        tokenRepository.save(confirmationToken);

        String link = LINK_FOR_CONFIRMATION + token;
        emailSender.sendEmailForVerification(
                newUser.getEmail(),
                emailBuilder.buildEmailForConfirmationEmail(newUser.getFullName(), link));
        emailSender.sendNewPassword(newUser.getEmail(), newUser.getFullName(), password);
        return mapper.map(newUser, UserDto.class);
    }

    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenNotFoundException(VERIFICATION_TOKEN_NOT_FOUND));
        if (confirmationToken.getConfirmedDate() != null) {
            throw new EmailAlreadyConfirmedException(EMAIL_ALREADY_CONFIRMED);
        }

        LocalDateTime expiredDate = confirmationToken.getExpiredDate();
        if (expiredDate.isBefore(LocalDateTime.now())) {
            throw new ConfirmationTokenExpiredException(VERIFICATION_TOKEN_EXPIRED);
        }
        tokenRepository.updateConfirmedDate(token, LocalDateTime.now());
        userRepository.enableUser(confirmationToken.getUser().getEmail());
        return CONFIRMED;
    }


    @Override
    public UserDto getUserProfile() {
        User userDb = getUserEntityById(getLoggedInUser().getId());
        return mapper.map(userDb, UserDto.class);
    }

    @Override
    public UserDto updateUser(UserRequest userRequest) {
        User userDb = getUserEntityById(getLoggedInUser().getId());
        validateNewUsernameAndEmail(userDb.getUsername(), userRequest.getUsername(), userRequest.getEmail());
        userDb.setUsername(userRequest.getUsername() != null ? userRequest.getUsername() : userDb.getUsername());
        userDb.setFirstName(userRequest.getFirstName() != null ? userRequest.getFirstName() : userDb.getFirstName());
        userDb.setLastName(userRequest.getLastName() != null ? userRequest.getLastName() : userDb.getLastName());
        userDb.setEmail(userRequest.getEmail() != null ? userRequest.getEmail() : userDb.getEmail());
        userDb.setLastUpdateDate(new Date());
        userDb = userRepository.save(userDb);
        return mapper.map(userDb, UserDto.class);
    }

    @Override
    public void deleteUser() {
        User userDb = getUserEntityById(getLoggedInUser().getId());
        userRepository.delete(userDb);
    }

    @Override
    public User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username));
    }


    private User getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format(NO_USER_FOUND_BY_ID, id)));
    }


    @Override
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public String updatePassword(String oldPassword) {
        User user = getLoggedInUser();
        if (passwordEncoder.matches(oldPassword, user.getPassword())) {
            String newPassword = generatePassword();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            emailSender.sendNewPassword(user.getEmail(), user.getFullName(), newPassword);
            return NEW_PASSWORD_SEND_EMAIL;
        } else {
            throw new PasswordExistException(INCORRECT_PASSWORD);
        }
    }

    @Override
    public String forgotPassword(String email) {
        User user = findUserByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL + email));
        String newPassword = generatePassword();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        emailSender.sendNewPassword(email, user.getFullName(), newPassword);
        return NEW_PASSWORD_SEND_EMAIL;
    }

    private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) throws UserNotFoundException, UsernameExistException, EmailExistException {
        User userByNewUsername = findUserByUsername(newUsername).orElse(null);
        User userByNewEmail = findUserByEmail(newEmail).orElse(null);
        if (StringUtils.isNotBlank(currentUsername)) {
            User currentUser = findUserByUsername(currentUsername).orElse(null);
            if (currentUser == null) {
                throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + currentUsername);
            }
            if (userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser;
        } else {
            if (userByNewUsername != null) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail != null) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return null;
        }
    }


    private Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private String getTemporaryProfileImageUrl() {
        return "";
    }

}

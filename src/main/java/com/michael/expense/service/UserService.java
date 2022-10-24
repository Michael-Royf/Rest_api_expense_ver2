package com.michael.expense.service;

import com.michael.expense.entity.User;
import com.michael.expense.payload.request.UserRequest;
import com.michael.expense.payload.response.UserDto;

import java.util.Optional;

public interface UserService {

    UserDto createUser(UserRequest userRequest);

    UserDto getUserProfile();

    UserDto updateUser(UserRequest userRequest);

    void deleteUser();

    User getLoggedInUser();

    Optional<User> findUserByUsername(String username);

    String updatePassword(String oldPassword);

    String forgotPassword(String email);

}

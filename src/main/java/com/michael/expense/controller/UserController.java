package com.michael.expense.controller;

import com.michael.expense.payload.request.UserRequest;
import com.michael.expense.payload.response.MessageResponse;
import com.michael.expense.payload.response.UserDto;
import com.michael.expense.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "REST APIs for User resources")
@CrossOrigin
@RestController
@RequestMapping("api/v1")
public class UserController {
    public static final String USER_DELETED_SUCCESSFULLY = "User deleted successfully";

    @Autowired
    private UserService userService;

    @ApiOperation(value = "REST API to display user profile")
    @GetMapping("/profile")
    public ResponseEntity<UserDto> getUserProfile() {
        return new ResponseEntity<>(userService.getUserProfile(), HttpStatus.OK);
    }

    @ApiOperation(value = "REST API to update user profile")
    @PutMapping("/profile")
    public ResponseEntity<UserDto> updateUser(@RequestBody @Valid UserRequest userRequest) {
        return new ResponseEntity<>(userService.updateUser(userRequest), HttpStatus.OK);
    }

    @ApiOperation(value = "REST API to delete user profile")
    @DeleteMapping("/deactivate")
    public ResponseEntity<MessageResponse> deleteUserById() {
        userService.deleteUser();
        return new ResponseEntity<>(new MessageResponse(USER_DELETED_SUCCESSFULLY), HttpStatus.OK);
    }

}

package com.example.LoginTask.controllers;

import com.example.LoginTask.auth.JwtTokenUtil;
import com.example.LoginTask.models.ApiResponse;
import com.example.LoginTask.models.JwtUserResponse;
import com.example.LoginTask.models.User;
import com.example.LoginTask.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;
    private final JwtTokenUtil tokenUtil;

    public UserController(UserService userService, JwtTokenUtil tokenUtil) {
        this.userService = userService;
        this.tokenUtil = tokenUtil;
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse> getAllUsers(){
        return buildOkResponse(userService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse> getUser(@PathVariable("id") Integer id){
        try{
            return buildOkResponse(userService.getUser(id));
        }catch (RuntimeException e){
            return buildFailedResponse(e, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> loginUser(@RequestBody User user){
        try{
            User validatedUser = userService.loginUser(user);
            JwtUserResponse response = getJwtUserResponse(validatedUser);
            return buildOkResponse(response);
        }catch (RuntimeException e){
            return buildFailedResponse(e,HttpStatus.NOT_ACCEPTABLE);
        }
    }

    private JwtUserResponse getJwtUserResponse(User validatedUser) {
        String token = tokenUtil.generateToken(validatedUser);
        JwtUserResponse response = JwtUserResponse.builder()
                .id(validatedUser.getId())
                .name(validatedUser.getName())
                .jwtToken(token)
                .build();
        return response;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@RequestBody User user){
        try{
            User userData = userService.registerUser(user);
            JwtUserResponse response = getJwtUserResponse(userData);
            return buildOkResponse(response);
        }catch (RuntimeException e){
            return buildFailedResponse(e,HttpStatus.NOT_ACCEPTABLE);
        }
    }

    private ResponseEntity<ApiResponse> buildFailedResponse(RuntimeException e, HttpStatus httpStatus) {
        ApiResponse apiResponse = ApiResponse.builder()
                .status("FAIL")
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(apiResponse, httpStatus);
    }

    private ResponseEntity<ApiResponse> buildOkResponse(Object response) {
        ApiResponse apiResponse = ApiResponse.builder().data(response).status("OK").build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}

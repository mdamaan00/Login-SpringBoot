package com.example.LoginTask.controllers;

import com.example.LoginTask.auth.JwtTokenUtil;
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
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable("id") Integer id){
        return userService.getUser(id);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtUserResponse> loginUser(@RequestBody User user){
        User validatedUser = userService.loginUser(user);
        String token = tokenUtil.generateToken(validatedUser);
        JwtUserResponse response = JwtUserResponse.builder()
                .id(validatedUser.getId())
                .name(validatedUser.getName())
                .jwtToken(token)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("/register")
    public User registerUser(@RequestBody User user){
        return userService.registerUser(user);
    }

}

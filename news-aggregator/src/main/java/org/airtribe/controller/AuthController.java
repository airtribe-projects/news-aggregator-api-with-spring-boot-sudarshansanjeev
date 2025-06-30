package org.airtribe.controller;

import org.airtribe.model.User;
import org.airtribe.model.request.LoginRequest;
import org.airtribe.model.request.RegisterUserRequest;
import org.airtribe.model.response.JwtResponse;
import org.airtribe.security.JwtService;
import org.airtribe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;


    @PostMapping("/register")
    public String registerUser(@RequestBody RegisterUserRequest registerUserRequest) {
        if (userService.findByUserName(registerUserRequest.getUsername()) != null) {
            return "Error : Username already taken!";
        }

        User user = new User();
        user.setUsername(registerUserRequest.getUsername());
        user.setEmail(registerUserRequest.getEmail());
        user.setPassword(registerUserRequest.getPassword());

        userService.registerUser(user);
        return "User registered successfully!";
    }

    @PostMapping("/login")
    public JwtResponse authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));


        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtService.generateToken(loginRequest.getUsername());

        return new JwtResponse(jwt);
    }



}

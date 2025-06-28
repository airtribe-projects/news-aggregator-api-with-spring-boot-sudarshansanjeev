package org.airtribe.controller;

import org.airtribe.model.request.RegisterUser;
import org.airtribe.model.response.JwtResponse;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {



    public ResponseEntity<JwtResponse> registerUser(@RequestBody RegisterUser registerUser) {

    }



}

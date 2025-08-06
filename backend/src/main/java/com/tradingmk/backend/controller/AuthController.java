package com.tradingmk.backend.controller;

import com.tradingmk.backend.dto.AuthenticationRequest;
import com.tradingmk.backend.dto.AuthenticationResponse;
import com.tradingmk.backend.service.AuthenticationService;
import com.tradingmk.backend.dto.RegisterRequest;
import com.tradingmk.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5175", "http://localhost:5176", "http://localhost:5173"})
public class AuthController {

    private final AuthenticationService service;

    @Autowired
    private UserService userService;

//    @PostMapping("/signup")
//    public String signup(@RequestBody User user) {
//        //zemi gi podatocite od frontend
//        userService.register(user);
//        return "user registered successfully ";
//    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ){
        return ResponseEntity.ok(service.register(request));

    }


    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ){

        return ResponseEntity.ok(service.authenticate(request));

    }

//    @PostMapping("/login")
//    public ResponseEntity<String> login(@RequestBody User loginRequest) {
//        User user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
//        if (user != null) {
//
//            return ResponseEntity.ok("Login successful");
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid userna me  or password");
//        }
//    }
}

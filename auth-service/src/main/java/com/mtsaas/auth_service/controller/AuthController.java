package com.mtsaas.auth_service.controller;

import com.mtsaas.auth_service.dto.AuthenticationResponse;
import com.mtsaas.auth_service.dto.UserDto;
import com.mtsaas.auth_service.entity.User;
import com.mtsaas.auth_service.service.UserService;
import jakarta.ws.rs.core.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/user")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/string")
    public String getString(){
        return "Hello security";
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user){
        return ResponseEntity.ok(userService.creatUser(user));
    }
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody UserDto user){
        return ResponseEntity.ok(userService.authenticate(user));
    }
    @PostMapping("/logout")
    public ResponseEntity<Response> logout(@RequestBody UserDto user){
        return null;
    }
}

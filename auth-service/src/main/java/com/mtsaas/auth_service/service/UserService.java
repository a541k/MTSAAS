package com.mtsaas.auth_service.service;

import com.mtsaas.auth_service.dto.AuthenticationResponse;
import com.mtsaas.auth_service.dto.UserDto;
import com.mtsaas.auth_service.entity.User;
import com.mtsaas.auth_service.repository.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JWTService jwtService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Creates user; encodes password; persists if username available
     */
    public User creatUser(User user) {
        if(userRepo.existsByUsername(user.getUsername())){
            throw  new ResponseStatusException(HttpStatus.CONFLICT,"User is already taken");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }
    /**
     * Authenticates user by matching encoded password
     */
    public AuthenticationResponse authenticate(UserDto user){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(principal);

        String role = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseGet(() -> "ROLE_CUSTOMER");
        AuthenticationResponse.UserInfo userInfo = new AuthenticationResponse.UserInfo(
                principal.getUsername(),
                role
        );
        return new AuthenticationResponse(token,"Bearer",jwtService.tokenExpiryInstant(),userInfo );
    }
}

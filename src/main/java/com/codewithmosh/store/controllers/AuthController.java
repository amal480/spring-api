package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.JwtResponse;
import com.codewithmosh.store.dtos.LoginRequest;
import com.codewithmosh.store.dtos.UserDto;
import com.codewithmosh.store.mappers.UserMapper;
import com.codewithmosh.store.repositories.UserRepository;
import com.codewithmosh.store.services.JwtService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserMapper userMapper;


    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @Valid @RequestBody LoginRequest request) {
//        var user=userRepository.findByEmail(request.getEmail()).orElse(null);
//        if (user==null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var token=jwtService.generateToken(request.getEmail());
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @PostMapping("/validate")
    public boolean validate(@RequestHeader("Authorization") String authHeader) {
        System.out.println("Validate Called");
        var token=authHeader.replace("Bearer ", "");
        return jwtService.validateToken(token);
    }
    @GetMapping("/me")
    public ResponseEntity<UserDto> me(){
        //returns auth object created in JwtAuthenticationFilter
        var authentication=SecurityContextHolder.getContext().getAuthentication();
        //gets current user
        //since we saved user email in JwtAuthenticationFilter we get a email as principal
        var email=(String)authentication.getPrincipal();


        var user=userRepository.findByEmail(email).orElse(null);
        if (user==null){
            return ResponseEntity.notFound().build();
        }

        var userDto=userMapper.toDto(user);

        return ResponseEntity.ok(userDto);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentialsException(){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}

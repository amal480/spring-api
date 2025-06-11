package com.codewithmosh.store.services;

import com.codewithmosh.store.entities.User;
import com.codewithmosh.store.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
@AllArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;
    public User getCurrentUser() {
        //returns auth object created in JwtAuthenticationFilter

        var authentication= SecurityContextHolder.getContext().getAuthentication();
        //gets current user
        //since we saved user email in JwtAuthenticationFilter we get a email as principal
        var userId=(Long)authentication.getPrincipal();


        return userRepository.findById(userId).orElse(null);
    }
}

//Logic for getting the current user from SecurityContext

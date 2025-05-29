package com.codewithmosh.store.dtos;

import com.codewithmosh.store.validation.Lowercase;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserRequest {
    @NotBlank(message = "Name is required") //"", " "
    @Size(max=255,message="Name must be less than 255 chars")
    private String name;

    @NotBlank
    @Email
    @Lowercase()
    private String email;

    @NotBlank
    @Size(min=6, max = 25)
    private String password;
}

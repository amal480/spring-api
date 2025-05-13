package com.codewithmosh.store.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserDto {
    public Long id;
    public String name;
    public String email;
}

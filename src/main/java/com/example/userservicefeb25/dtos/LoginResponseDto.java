package com.example.userservicefeb25.dtos;

import com.example.userservicefeb25.models.Token;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDto {
    private String tokenValue;
}

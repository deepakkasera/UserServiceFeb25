package com.example.userservicefeb25.controllers;

import com.example.userservicefeb25.dtos.*;
import com.example.userservicefeb25.models.Token;
import com.example.userservicefeb25.models.User;
import com.example.userservicefeb25.repositories.TokenRepository;
import com.example.userservicefeb25.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users") // localhost:8080/users/
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto requestDto) {
        Token token = userService.login(
                requestDto.getEmail(),
                requestDto.getPassword()
        );

        LoginResponseDto responseDto = new LoginResponseDto();
        responseDto.setTokenValue(token.getValue());
        return responseDto;
    }

    @PostMapping("/signup")
    public UserDto signUp(@RequestBody SignUpRequestDto requestDto) {
        User user = userService.signUp(
                requestDto.getName(),
                requestDto.getEmail(),
                requestDto.getPassword()
        );

        //Convert User to UserDto
        return UserDto.from(user);
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logOut(@RequestBody LogOutRequestDto requestDto) {
        return null;
    }

    //localhost:8080/users/validate/token
    @GetMapping("/validate/{token}")
    public ResponseEntity<UserDto> validateToken(@PathVariable("token") String tokenValue) {
        User user = userService.validateToken(tokenValue);
        ResponseEntity<UserDto> responseEntity = null;
        if (user == null) {
            responseEntity = new ResponseEntity<>(
                    null,
                    HttpStatus.UNAUTHORIZED
            );
        } else {
            responseEntity = new ResponseEntity<>(
                    UserDto.from(user),
                    HttpStatus.OK
            );
        }

        return responseEntity;
    }

    @GetMapping("/sample")
    public void sampleAPI() {
        System.out.println("Got a Sample API request.");
    }
}

package dev.OsRapazes.BatPonto.controller;

import dev.OsRapazes.BatPonto.dto.User.RegisterUserDto;
import dev.OsRapazes.BatPonto.dto.User.UserResponseDto;
import dev.OsRapazes.BatPonto.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> create(@RequestBody @Valid RegisterUserDto data, UriComponentsBuilder uriBuilder){
        UserResponseDto user = userService.registerUser((data));

        URI uri = uriBuilder.path("/users/{id}").buildAndExpand(user.id()).toUri();

        return ResponseEntity.created(uri).body(user);
    }
}

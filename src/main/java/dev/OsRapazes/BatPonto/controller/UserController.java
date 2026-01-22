package dev.OsRapazes.BatPonto.controller;

import dev.OsRapazes.BatPonto.dto.User.RegisterUserDto;
import dev.OsRapazes.BatPonto.dto.User.UserResponseDto;
import dev.OsRapazes.BatPonto.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> create(@RequestBody @Valid RegisterUserDto data, UriComponentsBuilder uriBuilder){

        String authenticatedEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        UserResponseDto user = userService.registerUser(data, authenticatedEmail);

        URI uri = uriBuilder.path("api/users/{id}").buildAndExpand(user.id()).toUri();

        return ResponseEntity.created(uri).body(user);
    }

    @GetMapping("/funcionarios")
    public ResponseEntity<List<UserResponseDto>> listFuncionariosUsers() {

        List<UserResponseDto> users = userService.findAllFuncionarios();

        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(users);
    }

}

package ru.semoff.ystu.chatbot.jokes.controllers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.semoff.ystu.chatbot.jokes.services.UserService;

@RequiredArgsConstructor
@RestController
public class RegistrationController {

    private final UserService userService;
    @PostMapping("/registration")
    public ResponseEntity<Void> registration(
            @RequestParam("username") String username,
            @RequestParam("password") String password
    ) {
        userService.registration(username, password);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/login")
    public ResponseEntity<Void> login() {
        return ResponseEntity.ok().build();
    }
}

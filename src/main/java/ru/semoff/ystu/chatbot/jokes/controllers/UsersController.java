package ru.semoff.ystu.chatbot.jokes.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.semoff.ystu.chatbot.jokes.models.User;
import ru.semoff.ystu.chatbot.jokes.services.UsersService;
import ru.semoff.ystu.chatbot.jokes.services.UsersServiceImplementation;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UsersController {

    private final UsersServiceImplementation usersServiceImplementation;

    @GetMapping
    ResponseEntity<List<User>> getAllUsers() {
        var result = usersServiceImplementation.getAllUsers();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{username}")
    ResponseEntity<User> getUserByUsername(@PathVariable("username") String username) {
        return usersServiceImplementation.getUserByUsername(username).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{username}/{authority}")
    ResponseEntity<Void> updateUserAuthority(@PathVariable("username") String username, @PathVariable("authority") String authority) {
        usersServiceImplementation.updateUserAuthority(username, authority);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{username}")
    ResponseEntity<Void> deleteUser(@PathVariable("username") String username) {
        usersServiceImplementation.deleteUser(username);
        return ResponseEntity.ok().build();
    }
}

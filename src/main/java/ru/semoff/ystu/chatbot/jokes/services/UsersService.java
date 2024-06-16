package ru.semoff.ystu.chatbot.jokes.services;

import ru.semoff.ystu.chatbot.jokes.models.User;
import ru.semoff.ystu.chatbot.jokes.models.UserAuthority;

import java.util.List;
import java.util.Optional;

public interface UsersService {
    List<User> getAllUsers();
    Optional<User> getUserByUsername(String username);
    void updateUserAuthority(String username, String userAuthority);
    void deleteUser(String username);
}

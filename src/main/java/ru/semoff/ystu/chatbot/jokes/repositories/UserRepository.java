package ru.semoff.ystu.chatbot.jokes.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.semoff.ystu.chatbot.jokes.models.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);
    void deleteUserByUsername(String username);
}

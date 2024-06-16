package ru.semoff.ystu.chatbot.jokes.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.semoff.ystu.chatbot.jokes.models.UserRole;

import java.util.Optional;

public interface UserRolesRepository extends CrudRepository<UserRole, Long> {
    Optional<UserRole> findByUser_Id(Long id);
}

package ru.semoff.ystu.chatbot.jokes.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.semoff.ystu.chatbot.jokes.exceptions.UserAuthorityNotFoundException;
import ru.semoff.ystu.chatbot.jokes.exceptions.UsernameNotFoundException;
import ru.semoff.ystu.chatbot.jokes.models.User;
import ru.semoff.ystu.chatbot.jokes.models.UserAuthority;
import ru.semoff.ystu.chatbot.jokes.models.UserRole;
import ru.semoff.ystu.chatbot.jokes.repositories.UserRepository;
import ru.semoff.ystu.chatbot.jokes.repositories.UserRolesRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UsersServiceImplementation implements UsersService {
    private final UserRepository userRepository;
    private final UserRolesRepository userRolesRepository;
    @Override
    public List<User> getAllUsers() {
        return (List<User>) userRepository.findAll();
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    @Override
    public void updateUserAuthority(String username, String stringUserAuthority) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            User updateUser = user.get();
            Long userId = updateUser.getId();
            Optional<UserRole> userRole = userRolesRepository.findByUser_Id(userId);
            if (userRole.isPresent()) {
                UserRole updateUserRole = userRole.get();
                switch (stringUserAuthority) {
                    case "user":
                        updateUserRole.setUserAuthority(UserAuthority.USER);
                        break;
                    case "moderator":
                        updateUserRole.setUserAuthority(UserAuthority.MODERATOR);
                        break;
                    case "admin":
                        updateUserRole.setUserAuthority(UserAuthority.ADMIN);
                        break;
                    default:
                        throw new UserAuthorityNotFoundException();
                }
                userRolesRepository.save(updateUserRole);
            }
        }
        else {
            throw new UsernameNotFoundException();
        }
    }
                                                                                                          
    @Override
    public void deleteUser(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            User userUpdate = user.get();
            userUpdate.setEnabled(false);
            userRepository.save(userUpdate);
        }
    }
}

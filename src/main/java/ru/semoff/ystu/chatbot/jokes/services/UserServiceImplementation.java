package ru.semoff.ystu.chatbot.jokes.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.semoff.ystu.chatbot.jokes.exceptions.UsernameAlreadyExistsException;
import ru.semoff.ystu.chatbot.jokes.models.User;
import ru.semoff.ystu.chatbot.jokes.models.UserAuthority;
import ru.semoff.ystu.chatbot.jokes.models.UserRole;
import ru.semoff.ystu.chatbot.jokes.repositories.UserRepository;
import ru.semoff.ystu.chatbot.jokes.repositories.UserRolesRepository;

@RequiredArgsConstructor
@Service
public class UserServiceImplementation implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final UserRolesRepository userRolesRepository;
    private final PasswordEncoder passwordEncoder;
    @Transactional
    @Override
    public void registration(String username, String password) {
        if (userRepository.findByUsername(username).isEmpty()) {
            User user = userRepository.save(
                    new User()
                            .setId(null)
                            .setUsername(username)
                            .setPassword(passwordEncoder.encode(password))
                            .setLocked(false)
                            .setExpired(false)
                            .setEnabled(true)
            );
            userRolesRepository.save(new UserRole(null, UserAuthority.USER, user));
        }
        else {
            throw new UsernameAlreadyExistsException();
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }
}

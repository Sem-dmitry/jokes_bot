package ru.semoff.ystu.chatbot.jokes.configs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.semoff.ystu.chatbot.jokes.models.UserAuthority;


@Slf4j
@Configuration
@EnableWebSecurity(debug = true)
public class SpringSecurityConfiguration {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(expressionInterceptUrlRegistry ->
                        expressionInterceptUrlRegistry
                                .requestMatchers("/registration", "/login", "/beans").permitAll()
                                .requestMatchers(HttpMethod.GET, "/jokes", "/jokes/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/jokes").hasAnyAuthority(
                                        UserAuthority.USER.getAuthority(),
                                        UserAuthority.MODERATOR.getAuthority(),
                                        UserAuthority.ADMIN.getAuthority()
                                )
                                .requestMatchers(HttpMethod.PUT, "/jokes/**").hasAnyAuthority(
                                        UserAuthority.MODERATOR.getAuthority(),
                                        UserAuthority.ADMIN.getAuthority()
                                )
                                .requestMatchers(HttpMethod.DELETE, "/jokes/**").hasAnyAuthority(
                                        UserAuthority.MODERATOR.getAuthority(),
                                        UserAuthority.ADMIN.getAuthority()
                                )
                                .requestMatchers(HttpMethod.GET,"/users", "/users/**").hasAuthority(UserAuthority.ADMIN.getAuthority())
                                .requestMatchers(HttpMethod.PUT, "/users/*/*").hasAuthority(UserAuthority.ADMIN.getAuthority())
                                .requestMatchers(HttpMethod.DELETE, "/users/*").hasAuthority(UserAuthority.ADMIN.getAuthority())
                )
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

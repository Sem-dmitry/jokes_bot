package ru.semoff.ystu.chatbot.jokes.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_roles")
@Entity(name = "user_roles")
public class UserRole {
    @Id
    @SequenceGenerator(sequenceName = "user_role_id_seq", name = "user_role_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_role_id_seq")
    private Long id;

    @Enumerated
    private UserAuthority userAuthority;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}

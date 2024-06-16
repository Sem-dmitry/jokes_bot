package ru.semoff.ystu.chatbot.jokes.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Accessors(chain = true)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "joke_calls")
@Table(name = "joke_calls")
public class JokeCall {
    @Id
    @SequenceGenerator(sequenceName = "joke_call_id_seq", name = "joke_call_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "joke_call_id_seq")
    private Long id;
    private Long visitorId;
    private LocalDateTime callTime;
    @ManyToOne
    @JoinColumn(name = "joke_id")
    private Joke joke;
}

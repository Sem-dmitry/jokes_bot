package ru.semoff.ystu.chatbot.jokes.models;

import com.sun.jdi.PrimitiveValue;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Accessors(chain = true)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "jokes")
@Table(name = "jokes")
public class Joke {
    @Id
    @SequenceGenerator(sequenceName = "joke_id_seq", name = "joke_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "joke_id_seq")

    private Long id;
    @Column(name = "text")
    private String text;
    @Column(name = "create_date")
    private LocalDateTime createDate;
    @Column(name = "update_date")
    private LocalDateTime updateDate;
}

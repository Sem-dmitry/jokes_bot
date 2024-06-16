package ru.semoff.ystu.chatbot.jokes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.semoff.ystu.chatbot.jokes.models.Joke;

public interface JokesRepository extends JpaRepository<Joke, Long> {
    @Query(value =  "SELECT * FROM jokes ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Joke getRandomJoke();
}

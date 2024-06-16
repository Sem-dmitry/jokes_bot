package ru.semoff.ystu.chatbot.jokes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.semoff.ystu.chatbot.jokes.models.Joke;
import ru.semoff.ystu.chatbot.jokes.models.JokeCall;

import java.util.List;
import java.util.Optional;

public interface JokeCallsRepository extends JpaRepository<JokeCall, Long> {
    @Query(value = "SELECT text \n" +
            "FROM joke_calls as jc INNER JOIN jokes as j on jc.joke_id = j.id \n" +
            "GROUP BY text \n" +
            "ORDER BY COUNT(*) \n" +
            "DESC LIMIT 5", nativeQuery = true)
    Optional<List<String>> getTop5Jokes();
}

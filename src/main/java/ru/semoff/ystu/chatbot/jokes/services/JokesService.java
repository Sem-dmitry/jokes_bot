package ru.semoff.ystu.chatbot.jokes.services;

import ru.semoff.ystu.chatbot.jokes.models.Joke;

import java.util.List;
import java.util.Optional;

public interface JokesService {
    List<Joke> getAllJokes();
    Optional<Joke> getJokeById(Long id, Long chatId);
    Joke getRandomJoke(Long chatId);
    void createJoke(Joke joke);
    void updateJoke(Joke updateJoke, Long id);
    void deleteJoke(Long id);
}

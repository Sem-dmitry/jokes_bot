package ru.semoff.ystu.chatbot.jokes.services;

import java.util.List;
import java.util.Optional;

public interface JokeCallsService {
    Optional<List<String>> getTop5Jokes();
}

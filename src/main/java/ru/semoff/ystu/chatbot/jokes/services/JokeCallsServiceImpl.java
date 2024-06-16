package ru.semoff.ystu.chatbot.jokes.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.semoff.ystu.chatbot.jokes.repositories.JokeCallsRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JokeCallsServiceImpl implements JokeCallsService {
    private final JokeCallsRepository jokeCallsRepository;
    @Override
    public Optional<List<String>> getTop5Jokes() {
        return jokeCallsRepository.getTop5Jokes();
    }
}

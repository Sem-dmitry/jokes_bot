package ru.semoff.ystu.chatbot.jokes.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.semoff.ystu.chatbot.jokes.exceptions.RandomJokeNotFoundException;
import ru.semoff.ystu.chatbot.jokes.models.Joke;
import ru.semoff.ystu.chatbot.jokes.models.JokeCall;
import ru.semoff.ystu.chatbot.jokes.repositories.JokeCallsRepository;
import ru.semoff.ystu.chatbot.jokes.repositories.JokesRepository;

import javax.print.attribute.standard.PageRanges;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JokesServiceImplementation implements JokesService {
    private final JokesRepository jokesRepository;
    private final JokeCallsRepository jokeCallsRepository;
    @Override
    public List<Joke> getAllJokes() {
        Pageable pageable = PageRequest.of(0, 15);
        Page<Joke> page = jokesRepository.findAll(pageable);
        return page.getContent();
    }

    @Override
    public Optional<Joke> getJokeById(Long id, Long chatId) {
        Optional<Joke> joke = jokesRepository.findById(id);
        if (joke.isPresent()) {
            Joke tempJoke = joke.get();
            jokeCallsRepository.save(
                    new JokeCall()
                            .setId(null)
                            .setCallTime(LocalDateTime.now())
                            .setVisitorId(chatId)
                            .setJoke(tempJoke)
            );
        }
        return joke;
    }

    @Override
    public Joke getRandomJoke(Long chatId) {
        Joke joke = jokesRepository.getRandomJoke();
        if (joke == null) {
            throw new RandomJokeNotFoundException();
        }
        jokeCallsRepository.save(
                new JokeCall()
                        .setId(null)
                        .setCallTime(LocalDateTime.now())
                        .setVisitorId(chatId)
                        .setJoke(joke)
        );
        return joke;
    }

    @Override
    public void createJoke(Joke joke) {
        joke.setCreateDate(LocalDateTime.now());
        joke.setUpdateDate(LocalDateTime.now());
        jokesRepository.save(joke);
    }

    @Override
    public void updateJoke(Joke updateJoke, Long id) {
        Optional<Joke> joke = jokesRepository.findById(id);
        if (joke.isPresent()) {
            Joke savedJoke = joke.get();
            savedJoke.setText(updateJoke.getText());
            savedJoke.setUpdateDate(LocalDateTime.now());
            jokesRepository.save(savedJoke);
        }
    }

    @Override
    public void deleteJoke(Long id) {
        jokesRepository.deleteById(id);
    }
}

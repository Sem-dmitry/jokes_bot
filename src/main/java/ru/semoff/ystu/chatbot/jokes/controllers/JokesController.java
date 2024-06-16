package ru.semoff.ystu.chatbot.jokes.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.semoff.ystu.chatbot.jokes.models.Joke;
import ru.semoff.ystu.chatbot.jokes.services.JokeCallsServiceImpl;
import ru.semoff.ystu.chatbot.jokes.services.JokesServiceImplementation;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/jokes")
public class JokesController {

    private final JokesServiceImplementation jokesServiceImplementation;
    private final JokeCallsServiceImpl jokeCallsService;
    @GetMapping
    ResponseEntity<List<Joke>> getAllJokes() {
        return ResponseEntity.ok(jokesServiceImplementation.getAllJokes());
    }

    @GetMapping("/{id}/{chatId}")
    ResponseEntity<Joke> getJokeById(@PathVariable Long id, @PathVariable Long chatId) {
        return jokesServiceImplementation.getJokeById(id, chatId).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/random/{chatId}")
    ResponseEntity<Joke> getRandomJoke(@PathVariable Long chatId) {
        return ResponseEntity.ok(jokesServiceImplementation.getRandomJoke(chatId));
    }

    @GetMapping("/top")
    ResponseEntity<List<String>> getTop5Joke() {
        return jokeCallsService.getTop5Jokes().map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    ResponseEntity<Void> createJoke(@RequestBody Joke joke) {
        jokesServiceImplementation.createJoke(joke);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> updateJoke(@RequestBody Joke joke, @PathVariable Long id) {
        jokesServiceImplementation.updateJoke(joke, id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteJoke(@PathVariable Long id) {
        jokesServiceImplementation.deleteJoke(id);
        return ResponseEntity.ok().build();
    }
}

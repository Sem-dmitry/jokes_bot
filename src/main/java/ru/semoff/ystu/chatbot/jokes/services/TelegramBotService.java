package ru.semoff.ystu.chatbot.jokes.services;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import io.micrometer.observation.Observation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.semoff.ystu.chatbot.jokes.exceptions.RandomJokeNotFoundException;
import ru.semoff.ystu.chatbot.jokes.exceptions.UserAuthorityNotFoundException;
import ru.semoff.ystu.chatbot.jokes.exceptions.UsernameAlreadyExistsException;
import ru.semoff.ystu.chatbot.jokes.models.Joke;
import ru.semoff.ystu.chatbot.jokes.models.User;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TelegramBotService {

    private final TelegramBot telegramBot;
    private final RestTemplate restTemplate;
    private final Map<Long, String> userStates = new ConcurrentHashMap<>();
    private String tempId = "";
    private final Map<Long, String> usersCollection = new HashMap<Long, String>();

    @Autowired
    public TelegramBotService(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
        setBotCommands();
        this.restTemplate = new RestTemplate();
        this.telegramBot.setUpdatesListener(updates -> {
            updates.forEach(this::handleUpdate);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, Throwable::printStackTrace);
    }
    private void setBotCommands() {
        BotCommand[] commands = {
                new BotCommand("/start", "поприветствовать"),
                new BotCommand("/registration", "зарегистрироваться"),
                new BotCommand("/login", "авторизоваться"),
                new BotCommand("/jokes", "вывести список всех шуток"),
                new BotCommand("/jokes_id", "вывести шутку по id"),
                new BotCommand("/jokes_random", "вывести случайную шутку"),
                new BotCommand("/jokes_top", "вывести топ 5 шуток"),
                new BotCommand("/jokes_add", "добавить шутку"),
                new BotCommand("/jokes_update_id", "обновить шутку"),
                new BotCommand("/jokes_delete_id", "удалить шутку"),
                new BotCommand("/users", "посмотреть пользователей"),
                new BotCommand("/users_update", "обновить роль пользователя"),
                new BotCommand("/users_delete", "удалить пользователя")
        };
        telegramBot.execute(new SetMyCommands(commands));
    }

    private void handleUpdate(Update update) {
        if (update.message() == null || update.message().text() == null) {
            // Если обновление не содержит сообщения или текст сообщения отсутствует, ничего не делаем
            return;
        }
        String messageText = update.message().text();
        long chatId = update.message().chat().id();
        int messageId = update.message().messageId();

        String state = userStates.getOrDefault(chatId, "");

        switch (state) {
            case "WAITING_FOR_JOKE_ID_FOR_VIEW":
                sendResponse(chatId, getJokeById(messageText, chatId), messageId);
                userStates.remove(chatId);
                break;
            case "WAITING_FOR_JOKE_TEXT_FOR_ADD":
                sendResponse(chatId, createJoke(chatId, messageText), messageId);
                userStates.remove(chatId);
                break;
            case "WAITING_FOR_JOKE_ID_FOR_UPDATE":
                sendResponse(chatId, updateJoke(chatId, "search_message", messageText, ""), messageId);
                tempId = messageText;
                break;
            case "WAITING_FOR_JOKE_TEXT_FOR_UPDATE":
                sendResponse(chatId, updateJoke(chatId, "", tempId, messageText), messageId);
                userStates.remove(chatId);
                break;
            case "WAITING_FOR_JOKE_ID_FOR_DELETE":
                sendResponse(chatId, deleteJoke(chatId, messageText), messageId);
                userStates.remove(chatId);
                break;
            case "WAITING_FOR_REGISTER_LOGPASS":
                sendResponse(chatId, registrationUser(chatId, messageText), messageId);
                userStates.remove(chatId);
                break;
            case "WAITING_FOR_LOGIN_LOGPASS":
                sendResponse(chatId, loginUser(chatId, messageText), messageId);
                userStates.remove(chatId);
                break;
            case "WAITING_FOR_USER_AUTHORITY":
                sendResponse(chatId, updateUser(chatId, messageText), messageId);
                userStates.remove(chatId);
                break;
            case "WAITING_FOR_USERNAME":
                sendResponse(chatId, deleteUser(chatId, messageText), messageId);
                userStates.remove(chatId);
                break;
            default:
                handleCommand(chatId, messageText, messageId);
        }
    }

    private void handleCommand(long chatId, String messageText, int messageId) {
        switch (messageText) {
            case "/start":
                sendResponse(chatId, "Добро пожаловать! Я бот с шутками шутками", messageId);
                break;
            case "/registration":
                sendResponse(chatId, "Введите логин и пароль через запятую в формате 'username, password'", messageId);
                userStates.put(chatId, "WAITING_FOR_REGISTER_LOGPASS");
                break;
            case "/login":
                sendResponse(chatId, "Введите логин и пароль через запятую в формате 'username, password'", messageId);
                userStates.put(chatId, "WAITING_FOR_LOGIN_LOGPASS");
                break;
            case "/jokes":
                sendResponse(chatId, getAllJokes(), messageId);
                break;
            case "/jokes_id":
                sendResponse(chatId, "Пожалуйста, введите ID шутки.", messageId);
                userStates.put(chatId, "WAITING_FOR_JOKE_ID_FOR_VIEW");
                break;
            case "/jokes_random":
                sendResponse(chatId, getRandomJoke(chatId), messageId);
                break;
            case "/jokes_top":
                sendResponse(chatId, getTop5Jokes(), messageId);
                break;
            case "/jokes_add":
                sendResponse(chatId, "Пожалуйста, введите текст шутки.", messageId);
                userStates.put(chatId, "WAITING_FOR_JOKE_TEXT_FOR_ADD");
                break;
            case "/jokes_update_id":
                sendResponse(chatId, "Пожалуйста, введите ID шутки для изменения", messageId);
                userStates.put(chatId, "WAITING_FOR_JOKE_ID_FOR_UPDATE");
                break;
            case "/jokes_delete_id":
                sendResponse(chatId, "Пожалуйста, введите ID шутки для удаления", messageId);
                userStates.put(chatId, "WAITING_FOR_JOKE_ID_FOR_DELETE");
                break;
            case "/users":
                sendResponse(chatId, getAllUsers(chatId), messageId);
                break;
            case "/users_update":
                sendResponse(chatId, "Пожалуйста, введите пользователя и роль, которую хотите выдать в формате 'username, role'\nВозможные роли:\nuser\nmoderator\nadmin", messageId);
                userStates.put(chatId, "WAITING_FOR_USER_AUTHORITY");
                break;
            case "/users_delete":
                sendResponse(chatId, "Пожалуйста, введите пользователя для удаления", messageId);
                userStates.put(chatId, "WAITING_FOR_USERNAME");
                break;
            default:
                sendResponse(chatId, "Неизвестная команда.", messageId);
        }
    }

    private void sendResponse(long chatId, String text, int replyToMessageId) {
        SendMessage request = new SendMessage(chatId, text)
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .disableNotification(true)
                .replyToMessageId(replyToMessageId);
        telegramBot.execute(request);
    }

    private String getAllJokes() {
        Joke[] jokes = restTemplate.getForObject("http://localhost:8080/jokes", Joke[].class);
        if (jokes == null || jokes.length == 0) {
            return "Нет доступных шуток.";
        }
        StringBuilder response = new StringBuilder();
        for (Joke joke : jokes) {
            response.append(joke.getId()).append(": ").append(joke.getText()).append("\n");
        }
        return response.toString();
    }

    private String getTop5Jokes() {
        List<String> topJokes = restTemplate.getForObject("http://localhost:8080/jokes/top", List.class);
        StringBuilder result = new StringBuilder();
        result.append("Топ 5 шуток по запросам пользователей:\n\n");
        for (String joke : topJokes) {
            result.append(joke).append("\n");
        }
        return result.toString();
    }

    private String getJokeById(String id, Long chatId) {
        try {
            Joke joke = restTemplate.getForObject("http://localhost:8080/jokes/" + id + "/" + chatId, Joke.class);
            if (joke == null) {
                return "Шутка с ID " + id + " не найдена.";
            }
            return joke.getText();
        } catch (Exception e) {
            return "Произошла ошибка при получении шутки.";
        }
    }

    private String getRandomJoke(Long chatId) {
        try {
            Joke joke = restTemplate.getForObject("http://localhost:8080/jokes/random/" + chatId, Joke.class);
            return joke.getText(); // Ошибка выбрасывается в сервисе
        } catch (RandomJokeNotFoundException e) {
            return "Шуток нет";
        } catch (Exception e) {
            return "Произошла ошибка";
        }
    }



    private String createJoke(Long chatId, String jokeText) {
        Joke joke = new Joke();
        joke.setText(jokeText);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", usersCollection.get(chatId));
            HttpEntity<Joke> request = new HttpEntity<>(joke, headers);
            ResponseEntity<Void> response = restTemplate.exchange(
                    "http://localhost:8080/jokes",
                    HttpMethod.POST,
                    request,
                    Void.class
            );
            return "Шутка добавлена.";
        } catch (Exception e) {
            return "Произошла ошибка при добавлении шутки.";
        }
    }

    private String updateJoke(Long chatId, String updateStatus, String id, String newJokeText) {
        if (updateStatus.equals("search_message")) {
            try {
                Joke joke = restTemplate.getForObject("http://localhost:8080/jokes/" + id, Joke.class);
                userStates.put(chatId, "WAITING_FOR_JOKE_TEXT_FOR_UPDATE");
                return "Пожалуйста, введите текст шутки для обновления";
            } catch (Exception e) {
                return "Шутки с данным ID не найдено";
            }
        }
        else {
            try {
                Joke joke = new Joke();
                joke.setText(newJokeText);
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", usersCollection.get(chatId));
                HttpEntity<Joke> request = new HttpEntity<>(joke, headers);
                ResponseEntity<Void> response = restTemplate.exchange(
                        "http://localhost:8080/jokes/" + id,
                        HttpMethod.PUT,
                        request,
                        Void.class
                );
                return "Шутка обновлена";
            }
            catch (Exception e) {
                return "Отказано в доступе";
            }
        }
    }

    private String deleteJoke(Long chatId, String id) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", usersCollection.get(chatId));
            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<Void> response = restTemplate.exchange(
                    "http://localhost:8080/jokes/" + id,
                    HttpMethod.DELETE,
                    request,
                    Void.class
            );
            return "Шутка удалена.";
        } catch (Exception e) {
            return "Произошла ошибка при удалении шутки.";
        }
    }

    private String getAllUsers(Long chatId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", usersCollection.get(chatId));
            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<Object[]> response = restTemplate.exchange(
                    "http://localhost:8080/users",
                    HttpMethod.GET,
                    request,
                    Object[].class
            );
            Object[] users = response.getBody();
            if (users == null || users.length == 0) {
                return "Нет пользователей";
            }
            StringBuilder usersList = new StringBuilder();
            for (Object user : users) {
                Map<String, Object> userObject = (Map<String, Object>) user;
                usersList.append(userObject.get("id")).append(": ").append(userObject.get("username")).append("\n");
            }
            return usersList.toString();
        }
        catch (Exception e) {
            return "Произошла ошибка";
        }
    }

    private String updateUser(Long chatId, String message) {
        String[] splitMessage = message.split(", ");
        String username = splitMessage[0];
        String authority = splitMessage[1];
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", usersCollection.get(chatId));
            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<Void> response = restTemplate.exchange(
                    "http://localhost:8080/users/" + username + "/" + authority,
                    HttpMethod.PUT,
                    request,
                    Void.class
            );
            return "Пользователь обновлен";
        } catch (UsernameNotFoundException e) {
            return "Пользователя с таким именем не существует";
        } catch (UserAuthorityNotFoundException e) {
            return "Такой роли не существует";
        } catch (Exception e) {
            return "Произошла ошибка";
        }
    }

    private String deleteUser(Long chatId, String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", usersCollection.get(chatId));
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:8080/users/" + username,
                HttpMethod.DELETE,
                request,
                Void.class
        );
        return "Пользователь удален";
    }

    private String registrationUser(Long chatId, String logpass) {
        String[] full = logpass.split(", ");
        try {
            URI uri = new URI("http://localhost:8080/registration?username=" + full[0] + "&password=" + full[1]);
            restTemplate.postForEntity(uri, null, Void.class);
            String auth = full[0] + ":" + full[1];
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.US_ASCII));
            usersCollection.put(chatId, "Basic " + new String(encodedAuth));
            return "Пользователь зарегистрирован";
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        catch (UsernameAlreadyExistsException e) {
            return "Пользователь с таким именем уже существует";
        }
        catch (Exception e) {
            return "Произошла ошибка";
        }
    }

    public String loginUser(Long chatId, String logpass) {
        String[] full = logpass.split(", ");
        try {

            URI uri = new URI("http://localhost:8080/login");

            String auth = full[0] + ":" + full[1];
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.US_ASCII));
            String authHeader = "Basic " + new String(encodedAuth);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authHeader);

            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<Void> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    request,
                    Void.class
            );
            usersCollection.put(chatId, authHeader);
            return "Пользователь авторизован";
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (UsernameNotFoundException e) {
            return "Пользователь не найден";
        } catch (Exception e) {
            return "Произошла ошибка";
        }
    }
}

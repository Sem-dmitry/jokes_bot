package ru.semoff.ystu.chatbot.jokes.configs;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelegramConfig {

    @Value("${telegram.bot.token}") //Забираем токен бота из конфига
    private String telegramToken;

    @Bean
    TelegramBot telegramBot() {
        return new TelegramBot(telegramToken); //Создаем и передаем нашего бота как бин в Spring
    }
}
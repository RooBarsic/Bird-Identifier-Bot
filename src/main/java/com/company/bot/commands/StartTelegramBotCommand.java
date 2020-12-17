package com.company.bot.commands;

import com.company.bot.BotCommand;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class StartTelegramBotCommand extends DefaultAbsSender implements BotCommand {
    private final String botToken;
    private String WELCOME = "Salam";

    public StartTelegramBotCommand(DefaultBotOptions options, final String botToken) {
        super(options);
        this.botToken = botToken;
    }

    @Override
    public boolean parseCommand(String command) {
        return command.startsWith("/start");
    }

    @Override
    public boolean executeCommand(final @NotNull User telegramUser, Long chatId, String command) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(WELCOME);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public BotCommand copyThis() {
        return new StartTelegramBotCommand(getOptions(), botToken);
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

}
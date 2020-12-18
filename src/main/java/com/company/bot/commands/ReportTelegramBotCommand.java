package com.company.bot.commands;

import com.company.bot.BotCommand;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class ReportTelegramBotCommand extends DefaultAbsSender implements BotCommand {
    private final String botToken;
    private final Long ADMIN_CHAT_ID = -400005789l;
    private int numberOfMessages;

    public ReportTelegramBotCommand(DefaultBotOptions options, final String botToken) {
        super(options);
        this.botToken = botToken;
    }

    @Override
    public boolean parseCommand(String command) {
        return command.startsWith("/report");
    }

    public void setNumberOfMessages(int numberOfMessages) {
        this.numberOfMessages = numberOfMessages;
    }

    @Override
    public boolean executeCommand(final @NotNull User telegramUser, Long chatId, String command) {
        StringBuilder messageText = new StringBuilder("/report\n" +
                "Number of messages : " + numberOfMessages + "\n");
        if (chatId != ADMIN_CHAT_ID) {
            messageText.append( "User : \n")
                    .append("@")
                    .append(telegramUser.getUserName())
                    .append(" ")
                    .append(telegramUser.getFirstName())
                    .append(" ")
                    .append(telegramUser.getLastName())
                    .append("\n");
        }
        sendMessage(ADMIN_CHAT_ID, messageText.toString());
        return true;
    }

    private void sendMessage(Long chatId, String messageStr) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(messageStr);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    @Override
    public BotCommand copyThis() {
        return new ReportTelegramBotCommand(getOptions(), botToken);
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
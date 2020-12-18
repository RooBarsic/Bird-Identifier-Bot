package com.company.bot;

import com.company.bot.BotCommand;
import com.company.bot.commands.*;
import com.company.questions.Question;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import lombok.Getter;
import lombok.Setter;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TelegramBot extends TelegramLongPollingBot {
    final int RECONNECT_PAUSE = 10000;
    private final int MAX_CHILD_THREADS_NUMBER = 10;
    private int numberOfMessages = 0;
    private AtomicInteger numberOfActiveChildThread;
    private final List<Question> questionsList;
    private final StartTelegramBotCommand START_TELEGRAM_BOT_COMMAND;
    private final HelpTelegramBotCommand HELP_TELEGRAM_BOT_COMMAND;
    private final TestTelegramBotCommand TEST_TELEGRAM_BOT_COMMAND;
    private final StartTestTelegramBotCommand START_TEST_TELEGRAM_BOT_COMMAND;
    private final ReportTelegramBotCommand REPORT_TELEGRAM_BOT_COMMAND;

    @Setter
    @Getter
    private String botName;

    @Setter
    private String botToken;

    public TelegramBot(@NotNull String botName, @NotNull String botToken, List<Question> questionsList) {
        this.botName = botName;
        this.botToken = botToken;
        this.questionsList = questionsList;

        numberOfActiveChildThread = new AtomicInteger(0);

        START_TELEGRAM_BOT_COMMAND = new StartTelegramBotCommand(getOptions(), botToken);
        HELP_TELEGRAM_BOT_COMMAND = new HelpTelegramBotCommand(getOptions(), botToken);
        TEST_TELEGRAM_BOT_COMMAND = new TestTelegramBotCommand(getOptions(), botToken, questionsList);
        START_TEST_TELEGRAM_BOT_COMMAND = new StartTestTelegramBotCommand(getOptions(), botToken, questionsList);
        REPORT_TELEGRAM_BOT_COMMAND = new ReportTelegramBotCommand(getOptions(), botToken);
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(" ### Got new request :");
        numberOfMessages++;

        final Long chatId;
        final String inputText;
        final User telegramUser;
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
            inputText = update.getMessage().getText();
            telegramUser = update.getMessage().getFrom();
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            inputText = update.getCallbackQuery().getData();
            telegramUser = update.getCallbackQuery().getFrom();
        } else {
            return;
        }

        System.out.println("    inputText : " + inputText);
        System.out.println("    User : "
                + "\nlogin : " + telegramUser.getUserName() + " "
                + "\nfirstName : " + telegramUser.getFirstName() + " "
                + "\nlastName : " + telegramUser.getLastName() + " "
                + "\nchatId : " + chatId);
        System.out.println("    Number of uniq users : " + numberOfMessages);
        System.out.println("    Number of active child threads : " + numberOfActiveChildThread.get());

        BotCommand foundedBotCommand = null;

        if (START_TELEGRAM_BOT_COMMAND.parseCommand(inputText) == true) {
            foundedBotCommand = START_TELEGRAM_BOT_COMMAND.copyThis();
        }
        else if (TEST_TELEGRAM_BOT_COMMAND.parseCommand(inputText) == true) {
            foundedBotCommand = TEST_TELEGRAM_BOT_COMMAND.copyThis();
        }
        else if(START_TEST_TELEGRAM_BOT_COMMAND.parseCommand(inputText) == true) {
            foundedBotCommand = START_TEST_TELEGRAM_BOT_COMMAND.copyThis();
        }
        else if(REPORT_TELEGRAM_BOT_COMMAND.parseCommand(inputText)) {
            foundedBotCommand = REPORT_TELEGRAM_BOT_COMMAND.copyThis();
        }
        else {
            foundedBotCommand = HELP_TELEGRAM_BOT_COMMAND.copyThis();
        }

        if (foundedBotCommand instanceof ReportTelegramBotCommand || numberOfMessages % 10 == 0) {
            REPORT_TELEGRAM_BOT_COMMAND
                    .setNumberOfMessages(numberOfMessages)
                    .executeCommand(telegramUser, chatId, inputText);
        }

        // Wait in queue
        while (numberOfActiveChildThread.get() == MAX_CHILD_THREADS_NUMBER) {
            // waiting for free thread
        }

        BotCommand finalFoundedBotCommand = foundedBotCommand;
        new Thread(() -> {
            numberOfActiveChildThread.incrementAndGet();
            try {

                finalFoundedBotCommand.executeCommand(telegramUser, chatId, inputText);

            } catch (Exception e) {
                e.printStackTrace();
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText("Due to telegram limitations, we can send you files no larger than 50MB. \n" +
                        "We are currently working on this.\n\n" +
                        "Please select a another file resolution.");
                try {
                    execute(message);
                } catch (TelegramApiException ef) {
                    ef.printStackTrace();
                }
            }

            numberOfActiveChildThread.decrementAndGet();
        }).start();
    }

    @Override
    public String getBotUsername() {
        //log.debug("Bot name: " + botName);
        System.out.println(" ### Request for Bot name");
        return botName;
    }

    @Override
    public String getBotToken() {
        //log.debug("Bot token: " + botToken);
        System.out.println(" ### Request for token");
        return botToken;
    }

    public void botConnect() {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            System.out.println(" ### Bot connecting....");
            telegramBotsApi.registerBot(this);
            //log.info("TelegramAPI started. Bot connected and waiting for messages");
        } catch (TelegramApiRequestException e) {
            //log.error("Cant Connect. Pause " + RECONNECT_PAUSE / 1000 + "sec and try again. Error: " + e.getMessage());
            try {
                Thread.sleep(RECONNECT_PAUSE);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
                return;
            }
            botConnect();
        }
    }
}

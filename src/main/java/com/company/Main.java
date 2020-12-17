package com.company;

import com.company.bot.TelegramBot;
import com.company.questions.Question;
import org.telegram.telegrambots.ApiContextInitializer;

import java.util.List;

public class Main {
    private static String BOT_TOKEN;
    private static String BOT_NAME;

    public static void main(String[] args) {
        System.out.println("Hello friends");

        initTokens();
        System.out.println("ENVS: \n" +
                "BOT_TOKEN = " + BOT_TOKEN + "\n" +
                "BOT_NAME = " + BOT_NAME + "\n");
        runTelegramBot();

        System.out.println("All systems up");
    }

    private static void runTelegramBot() {
        ApiContextInitializer.init();
        List<Question> questionsList = Question.parseQuestionsFromFile("questions.txt");
        TelegramBot telegramBot = new TelegramBot(BOT_NAME, BOT_TOKEN, questionsList);
        telegramBot.botConnect();
    }

    public static void initTokens() {
        BOT_TOKEN = System.getenv("BOT_TOKEN");
        BOT_NAME = System.getenv("BOT_NAME");
    }
}


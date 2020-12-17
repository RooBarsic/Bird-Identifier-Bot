package com.company.bot.commands;

import com.company.bot.BotCommand;
import com.company.questions.Answer;
import com.company.questions.Question;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StartTestTelegramBotCommand extends DefaultAbsSender implements BotCommand {
    private final String botToken;
    private List<Question> questionsList;
    private Map<Integer, Question> questionById;

    public StartTestTelegramBotCommand(DefaultBotOptions options, final String botToken, List<Question> questionsList) {
        super(options);
        this.botToken = botToken;
        this.questionsList = questionsList;

        questionById = new HashMap<>();
        for (int i = 0; i < questionsList.size(); i++) {
            Question question = questionsList.get(i);
            questionById.put(question.getQuestionId(), question);
        }
    }

    @Override
    public boolean parseCommand(String command) {
        return command.startsWith("/test"); // "/question prev_id=4 next_id=1 "
    }

    @Override
    public boolean executeCommand(final @NotNull User telegramUser, Long chatId, String command) {
        int next_id = 1;

        Question question = questionById.get(next_id);

        try {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(question.getQuestionText());

            List<List<InlineKeyboardButton>> keyboardMarkup = new ArrayList<>();

            keyboardMarkup.add(new ArrayList<>());                          // add first row
            for (Answer answer : question.getAnswersList()) {
                keyboardMarkup
                        .get(0)
                        .add(new InlineKeyboardButton()
                                .setText(answer.getAnswerText())   //"/question prev_id=4 next_id=1 "
                                .setCallbackData("/question " +
                                        "prev_id=" + question.getQuestionId() +
                                        " next_id=" + answer.getRelatedQuestionId())
                        );
            }
            keyboardMarkup
                    .get(0)
                    .add(new InlineKeyboardButton()
                            .setText("Go back")   //"/question prev_id=4 next_id=1 "
                            .setCallbackData("/question " +
                                    "prev_id=" + question.getQuestionId() +
                                    " next_id=" + question.getQuestionId())
                    );

            execute(new SendMessage()
                    .setChatId(chatId)
                    .setText(question.getQuestionText())
                    .setReplyMarkup(
                            new InlineKeyboardMarkup()
                                    .setKeyboard(keyboardMarkup)
                    )
            );
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void sendMessage(Long chatId, String messageStr) {
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
        return new StartTestTelegramBotCommand(getOptions(), botToken, questionsList);
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

}

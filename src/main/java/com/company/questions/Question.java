package com.company.questions;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Вопрос :
 *  id :
 *  текст :
 *  Варианты ответов :
 *      назвение ответа_1 :
 *      к какому вопросу этот ответ ведёт_1 :
 *      назвение ответа_2 :
 *      к какому вопросу этот ответ ведёт_2 :
 *      назвение ответа_2 :
 *      к какому вопросу этот ответ ведёт_2 :
 *
 *
 * id : 1
 * текст : Какого цвета шапка
 * Количество Вариантов ответов : 3
 * назвение ответа_1 : белый
 * к какому вопросу этот ответ ведёт_1 : 15
 * назвение ответа_2 : синий
 * к какому вопросу этот ответ ведёт_2 : 66
 * назвение ответа_2 : зеленый
 * к какому вопросу этот ответ ведёт_2 : 13
 */
public class Question {
    private final int questionId;
    private final String questionText;
    private final List<Answer> answersList;

    Question(final int questionId, @NotNull final String questionText, @NotNull final List<Answer> answersList) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.answersList = answersList;
    }

    public static List<Question> parseQuestionsFromFile(@NotNull final String fileName) {
        List<Question> parsedQuestions = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(fileName))) {
            while (scanner.hasNextLine()) {
                int questionId = Integer.parseInt(scanner.nextLine());
                String questionText = scanner.nextLine();
                int numberOfAnswers = Integer.parseInt(scanner.nextLine());
                List<Answer> answersList = new ArrayList<>();
                for (int i = 1; i <= numberOfAnswers; i++) {
                    final String answerText = scanner.nextLine();
                    final int relatedQuestionId = Integer.parseInt(scanner.nextLine());
                    answersList.add(new Answer(answerText, relatedQuestionId));
                }
                parsedQuestions.add(new Question(questionId, questionText, answersList));
                scanner.nextLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return parsedQuestions;
    }

    public int getQuestionId() {
        return questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<Answer> getAnswersList() {
        return answersList;
    }
}

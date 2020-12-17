package com.company.questions;

import org.jetbrains.annotations.NotNull;

public class Answer {
    private final String answerText;
    private final int relatedQuestionId;

    Answer(@NotNull final String answerText, final int relatedQuestionId) {
        this.answerText = answerText;
        this.relatedQuestionId = relatedQuestionId;
    }

    public String getAnswerText() {
        return answerText;
    }

    public int getRelatedQuestionId() {
        return relatedQuestionId;
    }
}

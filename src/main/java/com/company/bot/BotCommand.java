package com.company.bot;

import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.api.objects.User;

public interface BotCommand {
    boolean parseCommand(final String command);
    boolean executeCommand(final @NotNull User telegramUser, final Long chatId, String command);
    BotCommand copyThis();
}

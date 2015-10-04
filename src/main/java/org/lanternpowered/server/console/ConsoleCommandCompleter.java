package org.lanternpowered.server.console;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import jline.console.completer.Completer;

import org.lanternpowered.server.game.LanternGame;

public class ConsoleCommandCompleter implements Completer {

    private final LanternGame game;

    public ConsoleCommandCompleter(LanternGame game) {
        this.game = checkNotNull(game, "game");
    }

    @Override
    public int complete(String buffer, int cursor, List<CharSequence> candidates) {
        int len = buffer.length();
        buffer = buffer.trim();
        if (buffer.isEmpty()) {
            return cursor;
        }

        boolean prefix;
        if (buffer.charAt(0) != '/') {
            buffer = '/' + buffer;
            prefix = false;
        } else {
            prefix = true;
        }

        final String input = buffer;
        Future<List<String>> tabComplete = this.game.getScheduler().callSync(() ->
                game.getCommandDispatcher().getSuggestions(LanternConsoleSource.INSTANCE, input));

        try {
            List<String> completions = tabComplete.get();
            if (prefix) {
                candidates.addAll(completions);
            } else {
                for (String completion : completions) {
                    if (!completion.isEmpty()) {
                        candidates.add(completion.charAt(0) == '/' ? completion.substring(1) : completion);
                    }
                }
            }

            int pos = buffer.lastIndexOf(' ');
            if (pos == -1) {
                return cursor - len;
            } else {
                return cursor - (len - pos);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            LanternGame.log().error("Failed to tab complete", e);
        }

        return cursor;
    }

}
/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.console;

import org.apache.commons.lang3.StringUtils;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.scheduler.LanternScheduler;
import org.spongepowered.api.Sponge;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

final class ConsoleCommandCompleter implements Completer {

    @Override
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
        String buffer = line.line();

        // The content with normalized spaces, the spaces are trimmed
        // from the ends and there are never two spaces directly after each other
        String command = StringUtils.normalizeSpace(buffer);

        boolean hasPrefix = command.startsWith("/");
        // Don't include the '/'
        if (hasPrefix) {
            command = command.substring(1);
        }

        // Keep the last space, it must be there!
        if (buffer.endsWith(" ")) {
            command = command + " ";
        }

        final String command0 = command;
        final Future<List<String>> tabComplete = ((LanternScheduler) Sponge.getScheduler()).callSync(() ->
                Sponge.getCommandManager().getSuggestions(LanternConsoleSource.INSTANCE, command0, null));

        try {
            // Get the suggestions
            final List<String> suggestions = tabComplete.get();
            // If the suggestions are for the command and there was a prefix, then append the prefix
            if (hasPrefix && command.split(" ").length == 1 && !command.endsWith(" ")) {
                for (String completion : suggestions) {
                    if (!completion.isEmpty()) {
                        candidates.add(new Candidate('/' + completion));
                    }
                }
            } else {
                for (String completion : suggestions) {
                    if (!completion.isEmpty()) {
                        candidates.add(new Candidate(completion));
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            Lantern.getLogger().error("Failed to tab complete", e);
        }
    }
}

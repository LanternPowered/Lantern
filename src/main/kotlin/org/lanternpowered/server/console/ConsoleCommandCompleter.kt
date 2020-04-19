/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.console

import org.jline.reader.Candidate
import org.jline.reader.Completer
import org.jline.reader.LineReader
import org.jline.reader.ParsedLine
import org.lanternpowered.api.util.text.normalizeSpaces
import org.lanternpowered.server.game.Lantern
import org.spongepowered.api.Sponge
import java.util.concurrent.ExecutionException

internal class ConsoleCommandCompleter : Completer {

    override fun complete(reader: LineReader, line: ParsedLine, candidates: MutableList<Candidate>) {
        val buffer = line.line()

        // The content with normalized spaces, the spaces are trimmed
        // from the ends and there are never two spaces directly after each other
        var command = buffer.normalizeSpaces()

        val hasPrefix = command.startsWith("/")
        // Don't include the '/'
        if (hasPrefix) {
            command = command.substring(1)
        }

        // Keep the last space, it must be there!
        if (buffer.endsWith(" ")) {
            command = "$command "
        }

        val command0 = command
        val tabComplete = Lantern.getSyncScheduler().submit<List<String>> {
            Sponge.getCommandManager().suggest(LanternConsole, command0)
        }

        try {
            // Get the suggestions
            val suggestions = tabComplete.get()
            // If the suggestions are for the command and there was a prefix, then append the prefix
            if (hasPrefix && command.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().size == 1 && !command.endsWith(" ")) {
                for (completion in suggestions) {
                    if (completion.isNotEmpty()) {
                        candidates.add(Candidate("/$completion"))
                    }
                }
            } else {
                for (completion in suggestions) {
                    if (completion.isNotEmpty()) {
                        candidates.add(Candidate(completion))
                    }
                }
            }
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        } catch (e: ExecutionException) {
            Lantern.getLogger().error("Failed to tab complete", e)
        }
    }
}

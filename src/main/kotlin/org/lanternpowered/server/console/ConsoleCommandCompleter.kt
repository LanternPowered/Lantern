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

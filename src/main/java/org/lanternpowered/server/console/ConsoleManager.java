/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) Contributors
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

import jline.console.ConsoleReader;
import org.lanternpowered.launch.console.ConsoleLaunch;
import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.Sponge;

import java.io.IOException;

public final class ConsoleManager {

    // Whether the command threads are running
    private volatile boolean active;

    public void start() {
        this.active = true;

        // Set the colored console formatter
        ConsoleLaunch.setFormatter(new ColoredConsoleFormatter());

        // Add the command completer
        final ConsoleReader reader = ConsoleLaunch.getReader();
        if (reader == null) {
            return;
        }
        reader.addCompleter(new ConsoleCommandCompleter());

        // Start the command reader thread
        Thread thread = new Thread(this::commandReaderTask);
        thread.setName("ConsoleCommandThread");
        thread.setDaemon(true);
        thread.start();
    }

    public void shutdown() {
        this.active = false;
    }

    /**
     * This task handles the commands that are executed through the console.
     */
    private void commandReaderTask() {
        final ConsoleReader reader = ConsoleLaunch.getReader();
        while (this.active) {
            try {
                String command = reader.readLine();
                if (command != null) {
                    command = command.trim();
                    if (!command.isEmpty()) {
                        final String runCommand = command;
                        Sponge.getScheduler().createTaskBuilder().execute(() -> {
                            Sponge.getCommandManager().process(LanternConsoleSource.INSTANCE, runCommand);
                        }).submit(LanternGame.plugin());
                    }
                }
            } catch (IOException e) {
                LanternGame.log().error("Error while reading commands!", e);
            }
        }
    }

}

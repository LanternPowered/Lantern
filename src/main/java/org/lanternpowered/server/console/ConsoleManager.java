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

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import net.minecrell.terminalconsole.TerminalConsoleAppender;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.io.IoBuilder;
import org.apache.logging.log4j.io.LoggerPrintStream;
import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.reader.LineReader.Option;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.lanternpowered.server.game.DirectoryKeys;
import org.lanternpowered.server.plugin.InternalPluginsInfo.Implementation;
import org.lanternpowered.server.scheduler.LanternScheduler;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.text.channel.MessageChannel;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Singleton
public final class ConsoleManager {

    static final Set<String> REDIRECT_FQCNS = Sets.newHashSet(
            PrintStream.class.getName(), LoggerPrintStream.class.getName());
    static final String REDIRECT_ERR = "STDERR";
    static final String REDIRECT_OUT = "STDOUT";
    static volatile boolean active;

    private static final String HISTORY_FILE_NAME = "console_history.txt";

    private final Path consoleHistoryFile;
    private final Logger logger;
    private final Scheduler scheduler;
    private final CommandManager commandManager;
    private final PluginContainer pluginContainer;

    @Inject
    public ConsoleManager(Logger logger, LanternScheduler scheduler, CommandManager commandManager,
            @Named(DirectoryKeys.CONFIG) Path configFolder,
            @Named(Implementation.IDENTIFIER) PluginContainer pluginContainer) {
        this.consoleHistoryFile = configFolder.resolve(HISTORY_FILE_NAME);
        this.pluginContainer = pluginContainer;
        this.commandManager = commandManager;
        this.scheduler = scheduler;
        this.logger = logger;
    }

    public void init() {
        // Register the fqcn for the console source
        REDIRECT_FQCNS.add(LanternConsoleSource.class.getName());
        // Register the fqcn for the message channel
        REDIRECT_FQCNS.add(MessageChannel.class.getName());

        System.setOut(IoBuilder.forLogger(REDIRECT_OUT).setLevel(Level.INFO).buildPrintStream());
        System.setErr(IoBuilder.forLogger(REDIRECT_ERR).setLevel(Level.ERROR).buildPrintStream());
    }

    public void start() {
        final Terminal terminal = TerminalConsoleAppender.getTerminal();

        if (terminal != null) {
            final LineReader reader = LineReaderBuilder.builder()
                    .appName(Implementation.NAME)
                    .terminal(terminal)
                    .completer(new ConsoleCommandCompleter())
                    .build();
            reader.unsetOpt(Option.INSERT_TAB);
            reader.setVariable(LineReader.HISTORY_FILE, this.consoleHistoryFile);

            TerminalConsoleAppender.setReader(reader);
            active = true;

            final Thread thread = new Thread(this::readCommandTask, "console");
            thread.setDaemon(true);
            thread.start();

            this.scheduler.createAsyncExecutor(this.pluginContainer).scheduleAtFixedRate(
                    this::saveHistory, 120, 120, TimeUnit.SECONDS);
        }
    }

    public void shutdown() {
        active = false;
        saveHistory();
    }

    private void saveHistory() {
        final LineReader reader = TerminalConsoleAppender.getReader();
        if (reader != null) {
            final History history = reader.getHistory();
            try {
                history.save();
            } catch (IOException e) {
                this.logger.error("Error while saving the console history!", e);
            }
        }
    }

    /**
     * This task handles the commands that are executed through the console.
     */
    private void readCommandTask() {
        final LineReader lineReader = TerminalConsoleAppender.getReader();
        while (active) {
            //noinspection ConstantConditions
            String command = lineReader.readLine("> ");
            if (command != null) {
                command = command.trim();
                if (!command.isEmpty()) {
                    final String runCommand = command.startsWith("/") ? command.substring(1) : command;
                    this.scheduler.createTaskBuilder()
                            .execute(() -> this.commandManager.process(LanternConsoleSource.INSTANCE, runCommand))
                            .submit(this.pluginContainer);
                }
            }
        }
    }
}

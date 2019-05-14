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
import net.minecrell.terminalconsole.SimpleTerminalConsole;
import net.minecrell.terminalconsole.TerminalConsoleAppender;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.io.IoBuilder;
import org.apache.logging.log4j.io.LoggerPrintStream;
import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.lanternpowered.server.cause.LanternCauseStack;
import org.lanternpowered.server.game.DirectoryKeys;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.plugin.InternalPluginsInfo.Implementation;
import org.lanternpowered.server.scheduler.LanternScheduler;
import org.lanternpowered.server.util.PrettyPrinter;
import org.lanternpowered.server.util.ThreadHelper;
import org.spongepowered.api.command.manager.CommandManager;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.TaskExecutorService;
import org.spongepowered.api.text.channel.MessageChannel;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

@SuppressWarnings("ConstantConditions")
@Singleton
public final class ConsoleManager extends SimpleTerminalConsole {

    static final Set<String> REDIRECT_FQCNS = Sets.newHashSet(
            PrintStream.class.getName(), LoggerPrintStream.class.getName(), PrettyPrinter.class.getName());
    static final Set<String> IGNORE_FQCNS = new HashSet<>();

    private static final String HISTORY_FILE_NAME = "console_history.txt";
    private static final Duration HISTORY_SAVE_INTERVAL = Duration.ofMinutes(2);

    private final Path consoleHistoryFile;
    private final Logger logger;
    private final CommandManager commandManager;
    private final PluginContainer pluginContainer;

    private final TaskExecutorService syncExecutor;
    private final Object lock = new Object();

    private volatile boolean active;
    private Thread readerThread;
    @Nullable private LineReader lineReader;
    private long lastHistoryWrite = System.currentTimeMillis();

    @Inject
    public ConsoleManager(Logger logger, LanternGame game, CommandManager commandManager,
            @Named(DirectoryKeys.CONFIG) Path configFolder,
            @Named(Implementation.IDENTIFIER) PluginContainer pluginContainer) {
        this.syncExecutor = game.getSyncScheduler().createExecutor(pluginContainer);
        this.consoleHistoryFile = configFolder.resolve(HISTORY_FILE_NAME);
        this.pluginContainer = pluginContainer;
        this.commandManager = commandManager;
        this.logger = logger;
    }

    public void init() {
        // Register the fqcn for the console source
        REDIRECT_FQCNS.add(LanternConsoleSource.class.getName());
        // Register the fqcn for the message channel
        REDIRECT_FQCNS.add(MessageChannel.class.getName());
        // Ignore the cause stack as fqcn, stack traces will
        // already be printed nicely with PrettyPrinter
        IGNORE_FQCNS.add(LanternCauseStack.class.getName());

        System.setOut(IoBuilder.forLogger(this.logger).setLevel(Level.INFO).buildPrintStream());
        System.setErr(IoBuilder.forLogger(this.logger).setLevel(Level.ERROR).buildPrintStream());
    }

    @Override
    public void start() {
        this.active = true;
        synchronized (this.lock) {
            this.readerThread = ThreadHelper.newThread(super::start, "console");
            this.readerThread.setDaemon(true);
            this.readerThread.start();
        }
    }

    @Override
    protected LineReader buildReader(LineReaderBuilder builder) {
        this.lineReader = super.buildReader(builder
                .appName(this.pluginContainer.getName())
                .completer(new ConsoleCommandCompleter()));
        this.lineReader.setVariable(LineReader.HISTORY_FILE, this.consoleHistoryFile);
        return this.lineReader;
    }

    @Override
    protected boolean isRunning() {
        return this.active;
    }

    @Override
    protected void runCommand(String command) {
        command = command.trim();
        if (!command.isEmpty()) {
            final String runCommand = command.startsWith("/") ? command.substring(1) : command;
            this.syncExecutor.execute(() -> this.commandManager.process(LanternConsoleSource.INSTANCE, runCommand));
        }
        final long now = System.currentTimeMillis();
        if ((now - this.lastHistoryWrite) > HISTORY_SAVE_INTERVAL.toMillis()) {
            this.lastHistoryWrite = now;
            saveHistory();
        }
    }

    @Override
    protected void shutdown() {
        this.syncExecutor.execute(() -> Lantern.getServer().shutdown());
    }

    public void stop() {
        synchronized (this.lock) {
            if (this.readerThread == null) {
                return;
            }
            this.active = false;
            // Wait until the read thread finishes
            this.readerThread.interrupt();
            final Terminal terminal = TerminalConsoleAppender.getTerminal();
            if (terminal != null) {
                terminal.writer().println();
            }
            // Now we can safely save the history
            saveHistory();
            // Cleanup
            this.lineReader = null;
            this.readerThread = null;
        }
    }

    private void saveHistory() {
        if (this.lineReader != null) {
            final History history = this.lineReader.getHistory();
            try {
                history.save();
            } catch (IOException e) {
                this.logger.error("Error while saving the console history!", e);
            }
        }
    }
}

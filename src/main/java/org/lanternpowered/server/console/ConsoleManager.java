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

import static jline.TerminalFactory.JLINE_TERMINAL;
import static jline.TerminalFactory.OFF;

import jline.console.ConsoleReader;
import jline.console.CursorBuffer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.fusesource.jansi.AnsiConsole;
import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.Sponge;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.function.Function;

public class ConsoleManager {

    // The formatter, by default used to format colored messages
    private Function<String, String> formatter = new ColoredConsoleFormatter();

    // The console reader
    private ConsoleReader reader;

    // A temp cursor buffer, internal use only
    private volatile CursorBuffer stashed;

    // Whether the command threads are running
    private volatile boolean active;

    // Whether jline is enabled
    private boolean jline;

    public void init() {
        // Whether the console failed to initialize jline
        boolean flag = false;

        // Install the ansi console
        AnsiConsole.systemInstall();
        // Log4j should always skip it, we already installed it
        System.setProperty("log4j.skipJansi", "true");

        if (System.console() != null) {
            try {
                // Create the console reader
                this.reader = new ConsoleReader();
                this.reader.setExpandEvents(false);
                // Set the prompt icon
                this.reader.setPrompt(">");
                // No error caught, we are using jline!
                this.jline = true;
            } catch (Exception e) {
                flag = true;
            }
        }

        if (this.reader == null) {
            try {
                // Disable jline terminal
                System.setProperty(JLINE_TERMINAL, OFF);

                // Create the console reader
                this.reader = new ConsoleReader();
            } catch (IOException e) {
                throw new RuntimeException("Failed to initialize console", e);
            }
        }

        // Setup the print streams and formatting
        System.setOut(new ConsolePrintStream(System.out));
        System.setErr(new ConsolePrintStream(System.err));

        // Initialize the logging system and setup the logging streams
        // Before this point may never the any method in LogManager be accessed,
        // because they will trigger the initialization.

        // This print streams will redirect all the console output through the
        // loggers, if send through System.out or System.err
        System.setOut(new LoggingPrintStream(LogManager.getLogger("System.OUT"), Level.INFO));
        System.setErr(new LoggingPrintStream(LogManager.getLogger("System.ERR"), Level.ERROR));

        if (flag) {
            // We delay this message to avoid initializing the loggers before we are ready
            LanternGame.log().warn("Failed to initialize jline terminal. Using default.");
        }
    }

    private class ConsolePrintStream extends PrintStream {

        private final ConsoleOutputStream out;

        public ConsolePrintStream(PrintStream output) {
            this(new ConsoleOutputStream(output));
        }

        public ConsolePrintStream(ConsoleOutputStream out) {
            super(out, true);
            this.out = out;
        }

        @Override
        public void write(byte[] buf, int off, int len) {
            this.out.flush = false;
            super.write(buf, off, len);
            this.out.flush = true;
        }
    }

    private class ConsoleOutputStream extends ByteArrayOutputStream {

        private final PrintStream output;
        private boolean flush = true;

        public ConsoleOutputStream(PrintStream output) {
            this.output = output;
        }

        @Override
        public void flush() throws IOException {
            if (!this.flush) {
                return;
            }

            String message = this.toString();
            this.reset();

            // The stached field is used to fix the issue that
            // the reader cursor gets messed up between the other lines
            boolean flag = jline && stashed == null;

            if (flag) {
                stashed = reader.getCursorBuffer().copy();
                reader.getOutput().write("\r");
                reader.flush();
            }

            byte[] bytes = formatter.apply(message).getBytes();
            this.output.write(bytes, 0, bytes.length);

            if (flag) {
                reader.resetPromptLine(reader.getPrompt(), stashed.toString(), stashed.cursor);
                stashed = null;
            }
        }
    }

    public void start() {
        this.active = true;

        // Add the command completer
        this.reader.addCompleter(new ConsoleCommandCompleter());

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
        while (this.active) {
            try {
                String command = this.reader.readLine();
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

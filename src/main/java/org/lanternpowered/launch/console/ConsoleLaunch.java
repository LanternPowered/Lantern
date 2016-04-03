/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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
package org.lanternpowered.launch.console;

import static com.google.common.base.Preconditions.checkNotNull;
import static jline.TerminalFactory.OFF;

import com.google.common.collect.Sets;
import jline.TerminalFactory;
import jline.console.ConsoleReader;
import jline.console.CursorBuffer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.fusesource.jansi.AnsiConsole;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nullable;

/**
 * This is the first class that should executed at the start of the
 * application (or at least before log4j2).
 */
public final class ConsoleLaunch {

    // The fqcn of the stream that we use to redirect stream messages through a logger
    static final Set<String> REDIRECT_FQCNS = Sets.newHashSet(LoggingPrintStream.class.getName());

    static final String REDIRECT_ERR = "STDERR";
    static final String REDIRECT_OUT = "STDOUT";

    // Whether jline should be enabled
    private static final boolean ENABLE_JLINE = PropertiesUtil.getProperties().getBooleanProperty("jline.enable", true);

    private static Function<String, String> formatter = Function.identity();
    private static ConsoleReader reader;

    // A temp cursor buffer, internal use only
    private static volatile CursorBuffer stashed;

    // Whether the console is initialized
    private static boolean initialized;

    // Whether advanced jline is used
    private static boolean advancedJline;

    public static void addFqcn(String fqcn) {
        REDIRECT_FQCNS.add(checkNotNull(fqcn, "fqcn"));
    }

    /**
     * Gets the {@link ConsoleReader} instance.
     *
     * @return the console reader
     */
    public static ConsoleReader getReader() {
        return reader;
    }

    /**
     * Sets the console message formatter.
     *
     * @param format the formatter
     */
    public static void setFormatter(@Nullable Function<String, String> format) {
        formatter = format != null ? format : Function.identity();
    }

    public static boolean isInitialized() {
        return initialized;
    }

    /**
     * Initializes the console and log manager.
     */
    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        // We have to delay any messages to avoid initialisation of the log manager
        final Map<String, Throwable> outputQueue = new LinkedHashMap<>();
        if (ENABLE_JLINE) {
            // This is handled by us
            System.setProperty("log4j.skipJansi", "true");

            // Install jansi
            AnsiConsole.systemInstall();

            final boolean hasConsole = System.console() != null;
            if (hasConsole) {
                try {
                    reader = new ConsoleReader();
                    reader.setExpandEvents(false);
                    reader.setPrompt(">");
                    advancedJline = true;
                } catch (Exception e) {
                    outputQueue.put("Failed to initialize terminal. Falling back to default.", e);
                }
            }

            if (reader == null) {
                // Disable advanced jline features
                TerminalFactory.configure(OFF);
                TerminalFactory.reset();

                try {
                    reader = new ConsoleReader();
                    reader.setExpandEvents(false);
                } catch (Exception e) {
                    outputQueue.put("Failed to initialize fallback terminal. Falling back to standard output console.", e);
                }
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
        System.setOut(new LoggingPrintStream(LogManager.getLogger(REDIRECT_OUT), Level.INFO));
        System.setErr(new LoggingPrintStream(LogManager.getLogger(REDIRECT_ERR), Level.ERROR));

        final Logger logger = LogManager.getRootLogger();
        outputQueue.entrySet().forEach(entry -> {
            Object value = entry.getValue();
            if (value != null) {
                logger.warn(entry.getKey(), value);
            } else {
                logger.warn(entry.getKey());
            }
        });
    }

    private static class ConsolePrintStream extends PrintStream {

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

    private static class ConsoleOutputStream extends ByteArrayOutputStream {

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
            // Sadly, this doesn't work on eclipse or intellij :(
            boolean flag = advancedJline && reader != null && stashed == null;

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

}

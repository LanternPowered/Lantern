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
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.fusesource.jansi.AnsiConsole;

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
    private static final boolean FORCE_JLINE = PropertiesUtil.getProperties().getBooleanProperty("jline.force", false);

    static Function<String, String> formatter = Function.identity();
    static ConsoleReader reader;

    // Whether the console is initialized
    static boolean initialized;

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
    static void init(Logger logger) {
        if (initialized) {
            return;
        }
        initialized = true;
        if (ENABLE_JLINE) {
            final boolean hasConsole = System.console() != null;
            if (hasConsole) {
                try {
                    // Install jansi
                    AnsiConsole.systemInstall();
                    reader = new ConsoleReader();
                    reader.setExpandEvents(false);
                } catch (Exception e) {
                    logger.warn("Failed to initialize terminal. Falling back to default.", e);
                }
            }

            if (reader == null) {
                // Eclipse doesn't support colors and characters like \r so enabling jline2 on it will
                // just cause a lot of issues with empty lines and weird characters.
                // Enable jline2 only on IntelliJ IDEA to prevent that.
                //      Also see: https://bugs.eclipse.org/bugs/show_bug.cgi?id=76936

                // Disable advanced jline features
                TerminalFactory.configure(OFF);
                TerminalFactory.reset();

                if (hasConsole || FORCE_JLINE || System.getProperty("java.class.path").contains("idea_rt.jar")) {
                    // Disable advanced jline features
                    TerminalFactory.configure(OFF);
                    TerminalFactory.reset();

                    try {
                        reader = new ConsoleReader();
                    } catch (Exception e) {
                        logger.warn("Failed to initialize fallback terminal. Falling back to standard output console.", e);
                    }
                } else {
                    logger.warn("Disabling terminal, you're running in an unsupported environment.");
                }
            }
        }

        System.setOut(new LoggingPrintStream(LogManager.getLogger(REDIRECT_OUT), Level.INFO));
        System.setErr(new LoggingPrintStream(LogManager.getLogger(REDIRECT_ERR), Level.ERROR));
    }
}

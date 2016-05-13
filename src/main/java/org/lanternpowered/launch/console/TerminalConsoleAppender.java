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

import static jline.console.ConsoleReader.RESET_LINE;
import static org.apache.logging.log4j.core.util.Booleans.parseBoolean;
import static org.fusesource.jansi.Ansi.Color.RED;
import static org.fusesource.jansi.Ansi.Color.YELLOW;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.fusesource.jansi.Ansi;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.io.Writer;

import javax.annotation.Nullable;

@Plugin(name = "TerminalConsole", category = "Core", elementType = "appender", printObject = true)
public class TerminalConsoleAppender extends AbstractAppender {

    private static final String ANSI_RESET = Ansi.ansi().reset().toString();
    private static final String ANSI_ERROR = Ansi.ansi().fg(RED).bold().toString();
    private static final String ANSI_WARN = Ansi.ansi().fg(YELLOW).bold().toString();

    private static final PrintStream out = System.out;

    protected TerminalConsoleAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
    }

    @PluginFactory
    public static TerminalConsoleAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Filters") Filter filter,
            @PluginElement("Layout") @Nullable Layout<? extends Serializable> layout,
            @PluginAttribute("ignoreExceptions") String ignore) {

        if (name == null) {
            LOGGER.error("No name provided for TerminalConsoleAppender");
            return null;
        }
        if (layout == null) {
            layout = PatternLayout.newBuilder().build();
        }

        boolean ignoreExceptions = parseBoolean(ignore, true);

        // This is handled by jline
        System.setProperty("log4j.skipJansi", "true");
        return new TerminalConsoleAppender(name, filter, layout, ignoreExceptions);
    }

    @Override
    public void start() {
        super.start();

        // Init the console if needed
        ConsoleLaunch.init(LOGGER);
    }

    @Override
    public void append(LogEvent event) {
        if (!ConsoleLaunch.initialized) {
            out.print(this.getLayout().toSerializable(event));
            return;
        }

        if (ConsoleLaunch.reader != null) {
            try {
                Writer out = ConsoleLaunch.reader.getOutput();
                out.write(RESET_LINE);
                out.write(this.formatEvent(event));

                ConsoleLaunch.reader.drawLine();
                ConsoleLaunch.reader.flush();
            } catch (IOException ignored) {
            }
        } else {
            out.print(this.formatEvent(event));
        }
    }

    protected String formatEvent(LogEvent event) {
        String formatted = ConsoleLaunch.formatter.apply(this.getLayout().toSerializable(event).toString());
        if (ConsoleLaunch.reader != null) {
            // Colorize log messages if supported
            final int level = event.getLevel().intLevel();
            if (level <= Level.ERROR.intLevel()) {
                return ANSI_ERROR + formatted + ANSI_RESET;
            } else if (level <= Level.WARN.intLevel()) {
                return ANSI_WARN + formatted + ANSI_RESET;
            }
        }
        return formatted;
    }

}

package org.lanternpowered.server.console;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import jline.console.ConsoleReader;
import jline.console.completer.Completer;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Attribute;
import org.fusesource.jansi.Ansi.Color;
import org.fusesource.jansi.AnsiConsole;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.text.LanternTextFactory;
import org.lanternpowered.server.text.LegacyTextRepresentation;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.format.TextStyles;

import com.google.common.collect.ImmutableMap;

import static org.fusesource.jansi.Ansi.ansi;

public class LanternConsoleManager {

    private static final String CONSOLE_DATE = "HH:mm:ss";
    private static final String FILE_DATE = "yyyy/MM/dd HH:mm:ss";

    private final Logger logger = Logger.getLogger("");
    private final Map<String, String> replacements;

    private ConsoleReader reader;

    private boolean jLine;

    // Whether the console is closed
    private final AtomicBoolean closed = new AtomicBoolean(false);

    public LanternConsoleManager() {
        // Install ansi code handler, which makes colors work on Windows
        AnsiConsole.systemInstall();

        for (Handler h : this.logger.getHandlers()) {
            this.logger.removeHandler(h);
        }

        // Add log handler which writes to console
        this.logger.addHandler(new FancyConsoleHandler());

        // Reader must be initialized before standard streams are changed
        try {
            this.reader = new ConsoleReader();
        } catch (IOException ex) {
            this.logger.log(Level.SEVERE, "Exception initializing console reader", ex);
        }
        this.reader.addCompleter(new CommandCompleter());

        // set system output streams
        System.setOut(new PrintStream(new LoggerOutputStream(Level.INFO), true));
        System.setErr(new PrintStream(new LoggerOutputStream(Level.WARNING), true));

        ImmutableMap.Builder<String, String> rep = ImmutableMap.builder();

        // Set up colorization replacements
        rep.put(c(TextColors.AQUA), ansi().a(Attribute.RESET).fg(Color.BLACK).boldOff().toString());
        rep.put(c(TextColors.DARK_BLUE), ansi().a(Attribute.RESET).fg(Color.BLUE).boldOff().toString());
        rep.put(c(TextColors.DARK_GREEN), ansi().a(Attribute.RESET).fg(Color.GREEN).boldOff().toString());
        rep.put(c(TextColors.DARK_AQUA), ansi().a(Attribute.RESET).fg(Color.CYAN).boldOff().toString());
        rep.put(c(TextColors.DARK_RED), ansi().a(Attribute.RESET).fg(Color.RED).boldOff().toString());
        rep.put(c(TextColors.DARK_PURPLE), ansi().a(Attribute.RESET).fg(Color.MAGENTA).boldOff().toString());
        rep.put(c(TextColors.GOLD), ansi().a(Attribute.RESET).fg(Color.YELLOW).boldOff().toString());
        rep.put(c(TextColors.GRAY), ansi().a(Attribute.RESET).fg(Color.WHITE).boldOff().toString());
        rep.put(c(TextColors.DARK_GRAY), ansi().a(Attribute.RESET).fg(Color.BLACK).bold().toString());
        rep.put(c(TextColors.BLUE), ansi().a(Attribute.RESET).fg(Color.BLUE).bold().toString());
        rep.put(c(TextColors.GREEN), ansi().a(Attribute.RESET).fg(Color.GREEN).bold().toString());
        rep.put(c(TextColors.AQUA), ansi().a(Attribute.RESET).fg(Color.CYAN).bold().toString());
        rep.put(c(TextColors.RED), ansi().a(Attribute.RESET).fg(Color.RED).bold().toString());
        rep.put(c(TextColors.LIGHT_PURPLE), ansi().a(Attribute.RESET).fg(Color.MAGENTA).bold().toString());
        rep.put(c(TextColors.YELLOW), ansi().a(Attribute.RESET).fg(Color.YELLOW).bold().toString());
        rep.put(c(TextColors.WHITE), ansi().a(Attribute.RESET).fg(Color.WHITE).bold().toString());
        rep.put(c(TextStyles.OBFUSCATED), ansi().a(Attribute.BLINK_SLOW).toString());
        rep.put(c(TextStyles.BOLD), ansi().a(Attribute.INTENSITY_BOLD).toString());
        rep.put(c(TextStyles.STRIKETHROUGH), ansi().a(Attribute.STRIKETHROUGH_ON).toString());
        rep.put(c(TextStyles.UNDERLINE), ansi().a(Attribute.UNDERLINE).toString());
        rep.put(c(TextStyles.ITALIC), ansi().a(Attribute.ITALIC).toString());
        rep.put(c(TextColors.RESET), ansi().a(Attribute.RESET).toString());

        this.replacements = rep.build();
    }

    @SuppressWarnings("deprecation")
    private static String c(TextColor color) {
        return new StringBuilder().append(Texts.getLegacyChar()).append(
                LegacyTextRepresentation.FORMATS.get(color)).toString();
    }

    @SuppressWarnings("deprecation")
    private static String c(TextStyle.Base style) {
        return new StringBuilder().append(Texts.getLegacyChar()).append(
                LegacyTextRepresentation.FORMATS.get(style)).toString();
    }

    public void startConsole(boolean jLine) {
        this.jLine = jLine;

        Thread thread = new ConsoleCommandThread();
        thread.setName("ConsoleCommandThread");
        thread.setDaemon(true);
        thread.start();
    }

    public void stop() {
        this.closed.set(true);

        for (Handler handler : this.logger.getHandlers()) {
            handler.flush();
            handler.close();
        }
    }

    @SuppressWarnings("deprecation")
    private String colorize(String string) {
        // No colors in the message
        if (string.indexOf(Texts.getLegacyChar()) < 0) {
            return string;
        // Color not supported
        } else if (!this.jLine || !this.reader.getTerminal().isAnsiSupported()) {
            return Texts.stripCodes(string);
        } else {
            // Colorize or strip all colors
            for (Entry<String, String> en : this.replacements.entrySet()) {
                string = string.replaceAll("(?i)" + en.getKey(), en.getValue());
            }
            return string + Ansi.ansi().reset().toString();
        }
    }

    private class LoggerOutputStream extends ByteArrayOutputStream {

        private final String separator = System.getProperty("line.separator");
        private final Level level;

        public LoggerOutputStream(Level level) {
            this.level = level;
        }

        @Override
        public synchronized void flush() throws IOException {
            super.flush();
            String record = this.toString();
            super.reset();

            if (record.length() > 0 && !record.equals(this.separator)) {
                logger.logp(this.level, "LoggerOutputStream", "log" + this.level, record);
            }
        }
    }

    private class FancyConsoleHandler extends ConsoleHandler {

        public FancyConsoleHandler() {
            this.setFormatter(new DateOutputFormatter(CONSOLE_DATE));
            this.setOutputStream(System.out);
        }

        @Override
        public synchronized void flush() {
            try {
                if (jLine) {
                    reader.print(ConsoleReader.RESET_LINE + "");
                    reader.flush();
                    super.flush();
                    try {
                        reader.drawLine();
                    } catch (Throwable ex) {
                        reader.getCursorBuffer().clear();
                    }
                    reader.flush();
                } else {
                    super.flush();
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "I/O exception flushing console output", ex);
            }
        }
    }

    private class CommandCompleter implements Completer {

        @Override
        public int complete(final String buffer, int cursor, List<CharSequence> candidates) {
            try {
                /*
                List<String> suggestions = ((LanternSchedulerQuery) LanternGame.get().getSyncScheduler()).call(new Callable<List<String>>() {

                    @Override
                    public List<String> call() throws Exception {
                        return LanternGame.get().getCommandDispatcher().getSuggestions(LanternConsoleSource.INSTANCE, buffer);
                    }

                });

                // No suggestions
                if (suggestions == null) {
                    return cursor;
                }
                candidates.addAll(suggestions);
                */

                // Location to position the cursor at (before autofilling takes
                // place)
                return buffer.lastIndexOf(' ') + 1;
            } catch (Throwable t) {
                logger.log(Level.WARNING, "Error while tab completing", t);
                return cursor;
            }
        }

    }

    private class ConsoleCommandThread extends Thread {

        @Override
        public void run() {
            String command = "";
            while (!closed.get()) {
                try {
                    if (jLine) {
                        command = reader.readLine(">", null);
                    } else {
                        command = reader.readLine();
                    }

                    if (command == null || command.trim().length() == 0) {
                        continue;
                    }

                    final String command0 = command.trim();
                    LanternGame.get().getScheduler().createTaskBuilder().execute(new Runnable() {
                        @Override
                        public void run() {
                            LanternGame.get().getCommandDispatcher().process(
                                    LanternConsoleSource.INSTANCE, command0);
                        }
                    }).submit(null); // TODO: Use a plugin?
                } catch (InterruptedIOException ignore) {
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, "Error while reading commands!", ex);
                }
            }
        }

    }

    private class DateOutputFormatter extends Formatter {

        private final SimpleDateFormat date;

        public DateOutputFormatter(String pattern) {
            this.date = new SimpleDateFormat(pattern);
        }

        @Override
        public String format(LogRecord record) {
            StringBuilder builder = new StringBuilder();

            builder.append(this.date.format(record.getMillis()));
            builder.append(" [");
            builder.append(record.getLevel().getLocalizedName().toUpperCase());
            builder.append("] ");
            builder.append(colorize(formatMessage(record)));
            builder.append('\n');

            if (record.getThrown() != null) {
                StringWriter writer = new StringWriter();
                record.getThrown().printStackTrace(new PrintWriter(writer));
                builder.append(writer.toString());
            }

            return builder.toString();
        }

    }

}

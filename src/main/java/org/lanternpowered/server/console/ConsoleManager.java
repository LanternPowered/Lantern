package org.lanternpowered.server.console;

import static jline.TerminalFactory.JLINE_TERMINAL;
import static jline.TerminalFactory.OFF;

import java.io.IOException;

import jline.console.ConsoleReader;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.fusesource.jansi.AnsiConsole;
import org.fusesource.jansi.WindowsAnsiOutputStream;
import org.lanternpowered.server.game.LanternGame;

public class ConsoleManager {

    private ConsoleReader reader;
    private volatile boolean active;

    public void init() {
        boolean jansi = true;

        // A small workaround to avoid the "Unable to instantiate org.fusesource.jansi.WindowsAnsiOutputStream"
        // warning to be thrown and to disable jline if disabled
        try {
            // Try to constructor the ansi output stream
            new WindowsAnsiOutputStream(System.out);
        } catch (Throwable ignore) {
            // It failed
            jansi = false;
        }

        try {
            AnsiConsole.systemInstall();
            this.reader = new ConsoleReader();
            this.reader.setExpandEvents(false);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize console", e);
        }

        if (!jansi) {
            System.setProperty("log4j.skipJansi", "true");
            System.setProperty(JLINE_TERMINAL, OFF);
        }

        Formatter formatter = new ConsoleFormatter();

        // Setup the formatter
        System.setOut(new FormatterPrintStream(System.out, formatter));
        System.setErr(new FormatterPrintStream(System.err, formatter));

        // Setup logging streams
        System.setOut(new LoggingPrintStream(LogManager.getLogger("System.OUT"), Level.INFO));
        System.setErr(new LoggingPrintStream(LogManager.getLogger("System.ERR"), Level.ERROR));
    }

    public void start(LanternGame game) {
        this.active = true;

        // Add the command completer
        this.reader.addCompleter(new ConsoleCommandCompleter(game));

        // Start the command reader thread
        Thread thread = new Thread(new CommandReaderTask(game));
        thread.setName("ConsoleCommandThread");
        thread.setDaemon(true);
        thread.start();
    }

    public void shutdown() {
        this.reader.shutdown();
        this.active = false;
    }

    private class CommandReaderTask implements Runnable {

        private final LanternGame game;

        public CommandReaderTask(LanternGame game) {
            this.game = game;
        }

        @Override
        public void run() {
            while (active) {
                try {
                    String command = reader.readLine(); // reader.readLine(">", null);
                    if (command != null) {
                        command = command.trim();
                        if (!command.isEmpty()) {
                            final String runCommand = command;
                            this.game.getScheduler().createTaskBuilder().execute(new Runnable() {
                                @Override
                                public void run() {
                                    LanternGame.get().getCommandDispatcher().process(
                                            LanternConsoleSource.INSTANCE, runCommand);
                                }
                            }).submit(LanternGame.plugin());
                        }
                    }
                } catch (IOException e) {
                    LanternGame.log().error("Error while reading commands!", e);
                }
            }
        }
    }
}

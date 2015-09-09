package org.lanternpowered.server.console;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

public class LoggingOutputStream extends ByteArrayOutputStream {

    private static final String SEPARATOR = System.getProperty("line.separator");

    private final Logger logger;
    private final Level level;

    boolean flush = true;

    public LoggingOutputStream(Logger logger, Level level) {
        this.logger = checkNotNull(logger, "logger");
        this.level = checkNotNull(level, "level");
    }

    @Override
    public void flush() throws IOException {
        if (!this.flush) {
            return;
        }

        String message = this.toString();
        this.reset();

        if (!message.isEmpty() && !message.equals(SEPARATOR)) {
            if (message.endsWith(SEPARATOR)) {
                message = message.substring(0, message.length() - SEPARATOR.length());
            }

            if (message.charAt(message.length() - 1) == '\n') {
                message = message.substring(0, message.length() - 1);
            }

            this.logger.log(this.level, message);
        }
    }
}
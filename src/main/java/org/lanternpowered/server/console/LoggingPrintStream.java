package org.lanternpowered.server.console;

import java.io.PrintStream;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

public class LoggingPrintStream extends PrintStream {

    private final LoggingOutputStream out;

    public LoggingPrintStream(Logger logger, Level level) {
        this(new LoggingOutputStream(logger, level));
    }

    public LoggingPrintStream(LoggingOutputStream out) {
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
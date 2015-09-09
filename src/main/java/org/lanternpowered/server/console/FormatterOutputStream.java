package org.lanternpowered.server.console;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class FormatterOutputStream extends ByteArrayOutputStream {

    private final Formatter formatter;
    private final PrintStream output;

    boolean flush = true;

    public FormatterOutputStream(PrintStream output, Formatter formatter) {
        this.formatter = formatter;
        this.output = output;
    }

    @Override
    public void flush() throws IOException {
        if (!this.flush) {
            return;
        }

        String message = this.toString();
        this.reset();

        byte[] bytes = this.formatter.format(message).getBytes();
        this.output.write(bytes, 0, bytes.length);
    }
}
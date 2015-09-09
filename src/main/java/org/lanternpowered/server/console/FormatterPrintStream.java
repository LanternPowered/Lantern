package org.lanternpowered.server.console;

import java.io.PrintStream;

public class FormatterPrintStream extends PrintStream {

    private final FormatterOutputStream out;

    public FormatterPrintStream(PrintStream output, Formatter formatter) {
        this(new FormatterOutputStream(output, formatter));
    }

    public FormatterPrintStream(FormatterOutputStream out) {
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
package nl.halteradar.util;

import java.io.IOException;
import java.io.OutputStream;

public class UnclosableOutputStream extends OutputStream {
    private final OutputStream original;
    private final boolean flushOnClose;

    public UnclosableOutputStream(OutputStream original, boolean flushOnClose) {
        this.original = original;
        this.flushOnClose = flushOnClose;
    }

    @Override
    public void write(int b) throws IOException {
        original.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        original.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        original.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        original.flush();
    }

    @Override
    public void close() throws IOException {
        /* deliberately ignoring you */
        if (flushOnClose)
            flush();
    }
}

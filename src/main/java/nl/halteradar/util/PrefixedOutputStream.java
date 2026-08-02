package nl.halteradar.util;

import java.io.IOException;
import java.io.OutputStream;

public class PrefixedOutputStream extends OutputStream {
    final byte[] prefix;
    final OutputStream otherStream;
    boolean writePrefix = true;

    PrefixedOutputStream(byte[] prefix, OutputStream otherStream) {
        this.prefix = prefix;
        this.otherStream = otherStream;
    }

    @Override
    public void write(int b) throws IOException {
        if (writePrefix) {
            writePrefix = false;
            otherStream.write(prefix);
        }
        otherStream.write(b);
        if (b == '\n')
            writePrefix = true;
    }
}

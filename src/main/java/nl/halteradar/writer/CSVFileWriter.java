package nl.halteradar.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import nl.halteradar.Table;

public class CSVFileWriter extends CSVWriter {
    private final OutputStream writer;
    private final Path output;

    public CSVFileWriter(Table table, Path outdir) {
        super(table);

        output = Path.of(outdir.toString(), table.name + ".txt");

        OutputStream w = null;
        try {
            w = Files.newOutputStream(output);
        } catch (IOException err) {
            throw new UncheckedIOException(err);
        }
        writer = w;
    }

    @Override
    public Path finish() throws IOException {
        if (writer != null)
            writer.close();

        return output;
    }

    @Override
    public OutputStream getOutputStream() {
        if (writer != null)
            return writer;

        return new OutputStream() {
            @Override
            public void write(int b) throws IOException {
            }
        };
    }
}

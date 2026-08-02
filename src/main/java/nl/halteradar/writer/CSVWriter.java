package nl.halteradar.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import nl.halteradar.Table;

public abstract class CSVWriter implements TableSink {
    final String name;
    final String[] header;

    int rows = 0;
    boolean headerWritten = false;

    public CSVWriter(Table table) {
        name = table.name;
        header = table.header;
    }

    public abstract OutputStream getOutputStream();

    private void writeRow(Table table, String[] row) throws IOException {
        String line = table.writeCsvRow(row) + "\n";
        getOutputStream().write(line.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public synchronized void accept(Table table) throws IOException {
        if (!Arrays.equals(header, table.header))
            throw new IllegalStateException("header mismatch for table " + table.name);

        if (!headerWritten) {
            writeRow(table, header);
            headerWritten = true;
        }

        int before = rows;
        table.rows.forEach(row -> {
            try {
                writeRow(table, row);
                rows++;
            } catch (IOException err) {
                throw new UncheckedIOException(err);
            }
        });
        System.err.printf("%-30s %d -> %d rows\n", table.name + ":", before, rows);
    }
}

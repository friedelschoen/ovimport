package nl.halteradar.table;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.stream.Stream;

import com.opencsv.CSVWriter;

public class CSVTableSink extends CSVWriter implements TableSink<CSVTableSink, CSVTableSink> {
    private final Path path;
    private final Table table;
    private final Writer writer;

    private boolean headerWritten;
    private int rowCount = 0;

    public CSVTableSink(Table table, Path path, Writer writer, boolean skipHeader) {

        super(writer);

        this.table = table;
        this.path = path;
        this.writer = writer;
        this.headerWritten = skipHeader;
    }

    public CSVTableSink(Table table, Path path, Writer writer) {
        this(table, path, writer, false);
    }

    public CSVTableSink(Table table, Path path, boolean skipHeader)
            throws IOException {

        this(table, path, new FileWriter(path.toFile(), false), skipHeader);
    }

    public CSVTableSink(Table table, Path path)
            throws IOException {

        this(table, path, false);
    }

    public Table getTable() {
        return table;
    }

    public Path getPath() {
        return path;
    }

    @Override
    public synchronized void accept(Stream<TableRow> rows)
            throws IOException {

        try (rows) {
            rows.sequential().forEach(row -> {
                if (!row.table().equals(table)) {
                    throw new ApplesOrangesException(
                            "inconsistent tables");
                }

                if (!headerWritten) {
                    writeNext(row.table().getHeader(), false);
                    headerWritten = true;
                }

                rowCount++;
                writeNext(row.row(), false);
            });
        }
    }

    @Override
    public synchronized CSVTableSink combine(
            CSVTableSink other) {

        other.finish();

        try (Reader input = new FileReader(other.path.toFile())) {

            input.transferTo(writer);

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return this;
    }

    @Override
    public synchronized CSVTableSink finish() {
        try {
            close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        System.out.printf("%s: %d rows written\n", table.getName(), rowCount);

        return this;
    }
}

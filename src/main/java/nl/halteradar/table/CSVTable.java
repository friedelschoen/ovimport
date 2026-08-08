package nl.halteradar.table;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public final class CSVTable implements Table {
    private final Path path;
    private final String name;
    private final String[] header;

    public CSVTable(Path path, String name, String[] header) {
        this.path = path;
        this.name = name;
        this.header = header;
    }

    public Path getPath() {
        return path;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String[] getHeader() {
        return header;
    }

    @Override
    public Stream<TableRow> rows() {
        final CSVReader reader;

        try {
            reader = new CSVReader(Files.newBufferedReader(path));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        var spliterator = new Spliterators.AbstractSpliterator<String[]>(
                Long.MAX_VALUE,
                Spliterator.ORDERED | Spliterator.NONNULL) {

            @Override
            public boolean tryAdvance(
                    Consumer<? super String[]> action) {
                try {
                    String[] row = reader.readNext();

                    if (row == null)
                        return false;

                    action.accept(row);
                    return true;
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                } catch (CsvValidationException e) {
                    throw new IllegalStateException(e);
                }
            }
        };

        return StreamSupport
                .stream(spliterator, false)
                .map(row -> new TableRow(this, row))
                .onClose(() -> {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
    }
}

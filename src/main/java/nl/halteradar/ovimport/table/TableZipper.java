package nl.halteradar.ovimport.table;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.opencsv.CSVWriter;

import nl.halteradar.ovimport.util.UnclosableOutputStream;

public final class TableZipper {
    private final Path output;
    private final BiFunction<Table, Writer, TableSink<?>> createSink;

    public TableZipper(Path output, BiFunction<Table, Writer, TableSink<?>> createSink) {
        this.output = output;
        this.createSink = createSink;
    }

    public void write(Stream<? extends Table> tables) {
        try (
                var outputFile = Files.newOutputStream(output);
                var buffered = new BufferedOutputStream(outputFile);
                var zip = new ZipOutputStream(buffered);
                tables) {
            tables.sequential().forEach(table -> {
                try {
                    writeTable(zip, table);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });

        } catch (IOException e) {
            throw new IllegalStateException(
                    "unable to create ZIP archive", e);
        }
    }

    private void writeTable(ZipOutputStream zip, Table table) throws IOException {
        System.err.printf("adding %s.csv to %s%n", table.getName(), output);

        zip.putNextEntry(new ZipEntry(table.getName() + ".csv"));

        OutputStream entryStream = new UnclosableOutputStream(zip, true);

        try (TableSink<?> sink = createSink.apply(table, new OutputStreamWriter(entryStream))) {
            sink.accept(table.rows());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        zip.closeEntry();
    }
}

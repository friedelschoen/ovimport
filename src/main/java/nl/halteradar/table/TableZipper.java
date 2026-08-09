package nl.halteradar.table;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.opencsv.CSVWriter;

public final class TableZipper {
    private final Path output;

    public TableZipper(Path output) {
        this.output = output;
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

    private void writeTable(
            ZipOutputStream zip,
            Table table) throws IOException {

        System.err.printf("adding %s.csv to %s%n", table.getName(), output);

        zip.putNextEntry(new ZipEntry(table.getName() + ".csv"));

        /*
         * Closing CSVWriter would also close the ZipOutputStream,
         * so deliberately do not use try-with-resources here.
         */
        @SuppressWarnings("resource")
        CSVWriter writer = new CSVWriter(new java.io.OutputStreamWriter(zip));

        writer.writeNext(table.getHeader(), false);

        try (var rows = table.rows()) {
            rows.sequential().forEach(row -> writer.writeNext(row.row(), false));
        }

        writer.flush();

        zip.closeEntry();
    }
}

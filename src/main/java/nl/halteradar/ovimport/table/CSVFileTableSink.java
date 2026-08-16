package nl.halteradar.ovimport.table;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.Path;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class CSVFileTableSink extends CSVTableSink<CSVFileTableSink> {
    private final Path path;
    private final Writer writer;

    public CSVFileTableSink(Table table, Path path, Writer writer, boolean skipHeader) {
        super(table, writer, skipHeader);

        this.path = path;
        this.writer = writer;
    }

    public CSVFileTableSink(Table table, Path path, Writer writer) {
        this(table, path, writer, false);
    }

    public CSVFileTableSink(Table table, Path path, boolean skipHeader) throws IOException {

        this(table, path, new FileWriter(path.toFile(), false), skipHeader);
    }

    public CSVFileTableSink(Table table, Path path)
            throws IOException {

        this(table, path, false);
    }

    public Path getPath() {
        return path;
    }

    @Override
    public synchronized CSVFileTableSink combine(CSVFileTableSink other) {
        try {
            other.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        try (
                Reader input = new FileReader(other.path.toFile());
                CSVReader csvreader = new CSVReader(input)) {

            if (!other.skipHeader)
                csvreader.readNext();

            String[] line;
            while ((line = csvreader.readNext()) != null)
                writeNext(line, false);

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }

        return this;
    }
}

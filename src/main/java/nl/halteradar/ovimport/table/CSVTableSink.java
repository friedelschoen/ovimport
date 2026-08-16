package nl.halteradar.ovimport.table;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.stream.Stream;

import com.opencsv.CSVWriter;

public class CSVTableSink<T extends TableSink<T>> extends CSVWriter implements TableSink<T> {
    final Table table;
    final boolean skipHeader;

    boolean headerWritten;
    int rowCount = 0;

    public CSVTableSink(Table table, Writer writer, boolean skipHeader) {
        super(writer);

        this.table = table;
        this.skipHeader = skipHeader;
        this.headerWritten = skipHeader;
    }

    public CSVTableSink(Table table, Writer writer) {
        this(table, writer, false);
    }

    public Table getTable() {
        return table;
    }

    public synchronized void accept(Stream<TableRow> rows) throws IOException {
        try (rows) {
            rows.sequential().forEach(row -> {
                if (!Arrays.equals(row.table().getHeader(), table.getHeader())) {
                    throw new ApplesOrangesException("inconsistent tables: %s != %s");
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
    public synchronized void close() throws IOException {
        super.close();

        System.out.printf("%s: %d rows written\n", table.getName(), rowCount);
    }

    @Override
    public T combine(T other) {
        throw new UnsupportedOperationException("Unimplemented method 'combine'");
    }
}

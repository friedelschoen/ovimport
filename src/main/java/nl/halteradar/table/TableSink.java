package nl.halteradar.table;

import java.io.IOException;
import java.util.stream.Stream;

public interface TableSink<T extends TableSink<T>> extends AutoCloseable {
    T combine(T other);

    void accept(Stream<TableRow> table) throws IOException;
}

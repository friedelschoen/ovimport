package nl.halteradar.writer;

import java.io.IOException;
import java.nio.file.Path;

import nl.halteradar.Table;

public interface TableSink {
    void accept(Table table) throws IOException;

    // returns path to file
    Path finish() throws IOException;
}

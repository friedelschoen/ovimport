package nl.halteradar.ovimport.table;

import java.util.stream.Stream;

public interface Table {
    public String getName();

    public String[] getHeader();

    public Stream<TableRow> rows();
}

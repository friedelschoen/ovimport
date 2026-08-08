package nl.halteradar.table;

import java.util.Arrays;
import java.util.stream.Stream;

public class MemoryTable implements Table {
    public final String name;
    public final Stream<TableRow> rows;
    public final String[] header;

    public MemoryTable(String name, Stream<String[]> rows, String... header) {
        this.name = name;
        this.rows = rows.map(r -> new TableRow(this, r));
        this.header = header;
    }

    @Override
    public int hashCode() {
        int hashcode = name.hashCode();
        for (String h : header)
            hashcode += h.hashCode();

        return hashcode;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Table t) {
            return t.getName().equals(name) && Arrays.equals(t.getHeader(), header);
        }
        return false;
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
        return rows;
    }
}

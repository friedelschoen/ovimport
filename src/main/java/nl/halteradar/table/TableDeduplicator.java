package nl.halteradar.table;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public final class TableDeduplicator implements Table {
    private static TableRow newer(TableRow left, TableRow right) {
        return left.compareTo(right) >= 0
                ? left
                : right;
    }

    private final String name;
    private final String[] header;
    private final Stream<TableRow> original;

    public TableDeduplicator(Table table) {
        name = table.getName();
        header = table.getHeader();
        original = table.rows();
    }

    public String[] filterHeader() {
        int count = 0;
        for (String h : header) {
            if (!h.startsWith("#") && !h.startsWith("%"))
                count++;
        }

        String[] res = new String[count];
        count = 0;
        for (String h : header) {
            if (!h.startsWith("#") && !h.startsWith("%"))
                res[count++] = h.replaceFirst("^\\$", "");
        }
        return res;
    }

    public String[] filterRow(String[] row) {
        int count = 0;
        for (String h : header) {
            if (!h.startsWith("#") && !h.startsWith("%"))
                count++;
        }

        String[] res = new String[count];
        count = 0;
        for (int i = 0; i < header.length; i++) {
            if (!header[i].startsWith("#") && !header[i].startsWith("%"))
                res[count++] = row[i];
        }
        return res;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String[] getHeader() {
        return filterHeader();
    }

    @Override
    public Stream<TableRow> rows() {
        Map<PrimaryKey, TableRow> unique = new HashMap<>();

        try (var rows = original) {
            rows.sequential().forEach(row -> unique.merge(
                    row.getPrimaryKey(), row,
                    TableDeduplicator::newer));
        }

        return unique.values().stream().map(r -> new TableRow(this, filterRow(r.row())));
    }
}

package nl.halteradar;

import java.util.stream.Stream;

public class Table {
    public final String filename;
    public final String name;
    public final Stream<String[]> rows;
    public final String[] header;

    public Table(String filename, String name, Stream<String[]> rows, String... header) {
        this.filename = filename;
        this.name = name;
        this.rows = rows;
        this.header = header;
    }

    private String escapeSpecialCharacters(String data) {
        if (data == null)
            return "";

        String escapedData = data.replaceAll("\\R", " ");
        if (escapedData.contains(",") || escapedData.contains("\"")) {
            escapedData = escapedData.replace("\"", "\"\"");
            escapedData = "\"" + escapedData + "\"";
        }
        return escapedData;
    }

    public String writeCsvRow(String[] row) {
        if (header.length != row.length)
            throw new IllegalStateException(
                    String.format("header mismatch for table %s: %d header cols, but got %d cols",
                            name, header.length, row.length));

        var builder = new StringBuilder();
        for (int i = 0; i < row.length; i++) {
            if (i > 0)
                builder.append(',');

            builder.append(escapeSpecialCharacters(row[i]));
        }
        return builder.toString();
    }
}

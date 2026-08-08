package nl.halteradar.table;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record TableRow(Table table, String[] row) implements Comparable<TableRow> {
    private static int compareIntegersInString(String left, String right) {
        Pattern numberPattern = Pattern.compile("\\d+");
        Matcher leftMatch = numberPattern.matcher(left);
        Matcher rightMatch = numberPattern.matcher(right);

        while (leftMatch.find() && rightMatch.find()) {
            if (!leftMatch.group().equals(rightMatch.group())) {
                return Long.compare(Long.parseUnsignedLong(leftMatch.group()),
                        Long.parseUnsignedLong(rightMatch.group()));
            }
        }
        return left.compareTo(right);
    }

    @Override
    public int compareTo(TableRow other) {
        if (!Arrays.equals(table.getHeader(), other.table.getHeader())) {
            throw new ApplesOrangesException("unable to compare tables with different headers");
        }

        for (int i = 0; i < table.getHeader().length; i++) {
            if (table.getHeader()[i].startsWith("#")) {
                if (!row[i].equals(other.row[i])) {
                    return compareIntegersInString(row[i], other.row[i]);
                }
            }
        }
        return 0;
    }

    public PrimaryKey getPrimaryKey() {
        int keyCount = 0;
        for (String h : table.getHeader())
            if (h.startsWith("$"))
                keyCount++;

        if (keyCount == 0)
            return new PrimaryKey(row);

        String[] keys = new String[keyCount];
        keyCount = 0;
        for (int i = 0; i < table.getHeader().length; i++)
            if (table.getHeader()[i].startsWith("$"))
                keys[keyCount++] = row[i];

        return new PrimaryKey(keys);
    }
}

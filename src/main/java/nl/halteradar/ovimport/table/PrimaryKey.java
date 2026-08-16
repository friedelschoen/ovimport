package nl.halteradar.ovimport.table;

import java.util.Arrays;

public record PrimaryKey(String[] values) {

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PrimaryKey other && Arrays.equals(values, other.values);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(values);
    }
}

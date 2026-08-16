package nl.halteradar.psa;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.GregorianCalendar;
import java.util.function.Function;
import java.util.stream.Stream;

import nl.bisonnl.psa.*;
import nl.halteradar.table.MemoryTable;
import nl.halteradar.table.Table;

public final class PSATabler implements Function<Export, Stream<Table>> {
    private final long now = new GregorianCalendar().getTimeInMillis();

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String date(String value) {
        if (value == null)
            return "";

        return Long.toString(dateFromString(value));
    }

    private long dateFromString(String value) {
        return LocalDateTime.parse(value, formatter)
                .atZone(ZoneId.of("Europe/Amsterdam"))
                .toInstant()
                .toEpochMilli();
    }

    private boolean active(String validFrom) {
        if (validFrom == null)
            return true;

        return dateFromString(validFrom) <= now;
    }

    private Stream<String[]> quay(String stopplaceRef, String stopplaceCode, String quayRef, String quayCode,
            Userstopcodes userstopcodes) {
        if (userstopcodes == null || userstopcodes.getUserstopcodedata() == null)
            return Stream.empty();

        return userstopcodes.getUserstopcodedata().stream()
                .filter(u -> active(u.getValidfrom()))
                .map(r -> new String[] {
                        r.getDataownercode(),
                        r.getUserstopcode(),
                        date(r.getValidfrom()),
                        date(r.getValidthru()),
                        quayCode,
                        stopplaceCode,
                        quayRef,
                        stopplaceRef,
                });
    }

    @Override
    public Stream<Table> apply(Export export) {
        Stream<String[]> quays = Stream.empty();
        Stream<String[]> stopplaces = Stream.empty();

        if (export.getQuays() != null && export.getQuays().getQuay() != null) {
            quays = export.getQuays().getQuay().stream()
                    .flatMap(r -> quay(
                            r.getStopplaceref(),
                            r.getStopplacecode(),
                            r.getQuayref(),
                            r.getQuaycode(),
                            r.getUserstopcodes()));
        }
        if (export.getStopplaces() != null && export.getStopplaces().getStopplace() != null) {
            stopplaces = export.getStopplaces().getStopplace().stream()
                    .flatMap(r -> quay(
                            r.getStopplaceref(),
                            r.getStopplacecode(),
                            null,
                            null,
                            r.getUserstopcodes()));
        }

        Table assignments = new MemoryTable("passengerstopassignments", Stream.concat(quays, stopplaces),
                "$dataownercode", "$userstop_code", "%valid_from", "%valid_thru", "quay_code", "quay_id",
                "stopplace_code", "stopplace_id");

        return Stream.of(assignments);
    }
}

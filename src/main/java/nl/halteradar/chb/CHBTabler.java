package nl.halteradar.chb;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.xml.datatype.XMLGregorianCalendar;

import nl.bisonnl.chb.*;
import nl.halteradar.table.MemoryTable;
import nl.halteradar.table.Table;

public final class CHBTabler implements Function<Export, Stream<Table>> {
    private final StopplaceTabler stopplaceTabler = new StopplaceTabler();

    private final GregorianCalendar now = new GregorianCalendar();

    private String str(Object value) {
        return value == null ? "" : value.toString();
    }

    private String date(XMLGregorianCalendar value) {
        if (value == null)
            return "";

        return Long.toString(
                value.toGregorianCalendar().getTimeInMillis());
    }

    private boolean active(XMLGregorianCalendar validFrom) {
        if (validFrom == null)
            return true;

        return !validFrom.toGregorianCalendar().after(now);
    }

    /*
     * Top-level collections
     */

    private List<Stopplace> stopplaces(Export export) {
        if (export.getStopplaces() == null)
            return List.of();

        return export.getStopplaces().stream()
                .flatMap(group -> group.getStopplace().stream())
                .filter(stop -> active(stop.getValidfrom()))
                .toList();
    }

    private List<Place> places(Export export) {
        if (export.getPlaces() == null)
            return List.of();

        return export.getPlaces().stream()
                .flatMap(group -> group.getPlace().stream())
                .filter(place -> active(place.getValidfrom()))
                .toList();
    }

    private List<Dataowner> dataowners(Export export) {
        if (export.getDataowners() == null)
            return List.of();

        return export.getDataowners().stream()
                .flatMap(group -> group.getDataowner().stream())
                .toList();
    }

    /*
     * Places
     */

    private Table placesTable(List<Place> values) {
        return new MemoryTable(
                "places",
                values.stream().map(p -> new String[] {
                        date(p.getValidfrom()),
                        date(p.getMutationdate()),
                        str(p.getID()),
                        str(p.getPlacecode()),
                        str(p.getDaowcode()),
                        str(p.getPublicname()),
                        str(p.getTown()),
                        str(p.getIconuri()),
                        str(p.getDescription())
                }),
                "#validfrom",
                "#mutationdate",
                "$place_id",
                "place_code",
                "dataowner_code",
                "public_name",
                "town",
                "icon_uri",
                "description");
    }

    /*
     * Data owners
     */

    private Table dataownersTable(List<Dataowner> values) {
        return new MemoryTable(
                "dataowners",
                values.stream().map(d -> new String[] {
                        str(d.getDaowcode()),
                        str(d.getDaowname()),
                        str(d.getDaowtype())
                }),
                "$dataowner_code", "name", "type");
    }

    @Override
    public Stream<Table> apply(Export export) {
        var stopplaces = stopplaces(export);
        var places = places(export);
        var dataowners = dataowners(export);

        return Stream.concat(stopplaceTabler.apply(stopplaces), Stream.of(
                placesTable(places),
                dataownersTable(dataowners)));
    }
}

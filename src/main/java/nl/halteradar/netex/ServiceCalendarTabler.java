package nl.halteradar.netex;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import nl.bisonnl.netex.ServiceCalendarFrame;
import nl.halteradar.util.CommonTabler;
import nl.halteradar.Table;

final class ServiceCalendarTabler extends CommonTabler<ServiceCalendarFrame> {
    Table dayTypes(ServiceCalendarFrame frame, String dataset) {
        Stream<String[]> rows = Stream.empty();

        if (frame.getDayTypes() != null) {
            rows = frame.getDayTypes().getDayType().parallelStream().map(dayType -> {
                Stream<String> dayOfWeek = Stream.of("Everyday");
                Stream<String> weeksOfMonth = Stream.of("EveryWeek");
                String dayOfYear = null;
                Stream<String> holidayType = Stream.of("AnyDay");
                Stream<String> seasons = Stream.of("Perennially");
                Stream<String> tides = Stream.of("AllTides");
                String dayEvent = null;
                String crowding = null;

                if (dayType.getProperties() != null) {
                    for (var prop : dayType.getProperties().getPropertyOfDay()) {
                        if (prop.getDaysOfWeek() != null && !prop.getDaysOfWeek().isEmpty())
                            dayOfWeek = toStringStream(prop.getDaysOfWeek());
                        if (prop.getWeeksOfMonth() != null && !prop.getWeeksOfMonth().isEmpty())
                            weeksOfMonth = toStringStream(prop.getWeeksOfMonth());
                        if (prop.getDayOfYear() != null)
                            dayOfYear = prop.getDayOfYear().toString();
                        if (prop.getHolidayTypes() != null && !prop.getHolidayTypes().isEmpty())
                            holidayType = toStringStream(prop.getHolidayTypes());
                        if (prop.getSeasons() != null && !prop.getSeasons().isEmpty())
                            seasons = toStringStream(prop.getSeasons());
                        if (prop.getTides() != null && !prop.getTides().isEmpty())
                            tides = toStringStream(prop.getTides());
                        if (prop.getDayEvent() != null)
                            dayEvent = prop.getDayEvent().toString();
                        if (prop.getCrowding() != null)
                            crowding = prop.getCrowding().toString();
                    }
                }

                return new String[] {
                        dataset,
                        dayType.getId(),
                        str(dayType.getVersion(), () -> "any"),
                        text(dayType.getName()),
                        text(dayType.getShortName()),
                        dayOfWeek.collect(Collectors.joining(Delimiter)),
                        weeksOfMonth.collect(Collectors.joining(Delimiter)),
                        dayOfYear,
                        holidayType.collect(Collectors.joining(Delimiter)),
                        seasons.collect(Collectors.joining(Delimiter)),
                        tides.collect(Collectors.joining(Delimiter)),
                        dayEvent,
                        crowding,
                };
            });
        }

        return new Table(dataset, "daytypes", rows,
                "dataset", "daytype_id", "version", "name", "shortname", "daysofweek",
                "weeksofmonth", "dayofyear", "holidaytypes", "seasons",
                "tides", "dayevent", "crowding");
    }

    Table dayTypeAssignmens(ServiceCalendarFrame frame, String dataset) {
        Stream<String[]> rows = Stream.empty();

        if (frame.getDayTypeAssignments() != null) {
            rows = frame.getDayTypeAssignments().getDayTypeAssignment().parallelStream().map(assign -> new String[] {
                    dataset,
                    assign.getId(),
                    str(assign.getVersion(), () -> "any"),
                    assign.getDate().toString(),
                    refString(assign.getDayTypeRef())
            });
        }

        return new Table(dataset, "daytypeassignments", rows,
                "dataset", "daytypeassignment_id", "version", "date", "daytype_id");
    }

    Table timebands(ServiceCalendarFrame frame, String dataset) {
        Stream<String[]> rows = Stream.empty();

        if (frame.getTimebands() != null) {
            rows = frame.getTimebands().getTimeband().parallelStream().map(timeband -> new String[] {
                    timeband.getId(),
                    str(timeband.getVersion(), () -> "any"),
                    timeband.getStartTime().toString(),
                    timeband.getEndTime().toString()
            });
        }

        return new Table(dataset, "timebands", rows,
                "dataset", "timeband_id", "version", "starttime", "endtime");
    }

    @Override
    public Stream<Table> apply(ServiceCalendarFrame frame, String dataset) {
        return Stream.of(
                dayTypes(frame, dataset),
                dayTypeAssignmens(frame, dataset));
        // this::timebands);
    }
}

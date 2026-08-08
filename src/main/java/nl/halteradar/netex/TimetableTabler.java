package nl.halteradar.netex;

import java.util.stream.Stream;

import nl.bisonnl.netex.TimetableFrame;
import nl.halteradar.util.CommonTabler;
import nl.halteradar.table.MemoryTable;
import nl.halteradar.table.Table;

class TimetableTabler extends CommonTabler<TimetableFrame> {
    private Table availabilityConditions(TimetableFrame frame, Long timestamp) {
        Stream<String[]> rows = Stream.empty();

        if (frame.getContentValidityConditions() != null)
            rows = frame.getContentValidityConditions().getAvailabilityCondition().parallelStream()
                    .map(cond -> new String[] {
                            timestamp.toString(),
                            cond.getId(),
                            str(cond.getVersion(), () -> "any"),
                            text(cond.getName()),
                            str(cond.getFromDate()),
                            str(cond.getToDate()),
                            cond.getValidDayBits(),
                            binBool(cond.isIsAvailable())
                    });

        return new MemoryTable("availabilityconditions", rows,
                "#frame_timestamp", "$availabilitycondition_id", "#version", "name", "from_date",
                "to_date", "validdays", "availability");
    }

    private Table vehicleJourneys(TimetableFrame frame, Long timestamp) {
        Stream<String[]> rows = Stream.empty();

        if (frame.getVehicleJourneys() != null) {
            Stream<String[]> serviceJourneys = Stream.empty();
            Stream<String[]> deadrunJourneys = Stream.empty();

            if (frame.getVehicleJourneys().getServiceJourney() != null)
                serviceJourneys = frame.getVehicleJourneys().getServiceJourney().parallelStream()
                        .map(journey -> {
                            var condition = new StringBuilder();
                            if (journey.getValidityConditions() != null) {
                                for (var ref : journey.getValidityConditions().getAvailabilityConditionRef()) {
                                    if (condition.length() > 0)
                                        condition.append(Delimiter);
                                    condition.append(refString(ref));
                                }
                            }
                            // derivedFromVersionRef, keyList, dayTypes (-> validityConditions)
                            return new String[] {
                                    timestamp.toString(),
                                    journey.getId(),
                                    str(journey.getVersion(), () -> "any"),
                                    "service",
                                    journey.getDerivedFromObjectRef(),
                                    condition.toString(),
                                    privateCode(journey.getPrivateCodes(), "JourneyNumber"),
                                    str(journey.getDepartureTime()),
                                    journey.getDepartureDayOffset() != null
                                            ? journey.getDepartureDayOffset().toString()
                                            : "0",
                                    refString(journey.getServiceJourneyPatternRef()),
                                    refString(journey.getTimeDemandTypeRef()),
                                    refString(journey.getVehicleTypeRef()),
                                    refString(journey.getOperatorRef()),
                                    val(journey.getDynamic()),
                            };
                        });

            if (frame.getVehicleJourneys().getDeadRun() != null)
                deadrunJourneys = frame.getVehicleJourneys().getDeadRun().parallelStream().map(journey -> {
                    var condition = new StringBuilder();
                    if (journey.getValidityConditions() != null) {
                        for (var ref : journey.getValidityConditions().getAvailabilityConditionRef()) {
                            if (condition.length() > 0)
                                condition.append(Delimiter);
                            condition.append(refString(ref));
                        }
                    }
                    return new String[] {
                            timestamp.toString(),
                            journey.getId(),
                            str(journey.getVersion(), () -> "any"),
                            "deadrun",
                            null,
                            condition.toString(),
                            privateCode(journey.getPrivateCodes(), "JourneyNumber"),
                            journey.getDepartureTime().toString(),
                            journey.getDepartureDayOffset() != null
                                    ? journey.getDepartureDayOffset().toString()
                                    : "0",
                            refString(journey.getDeadRunJourneyPatternRef()),
                            refString(journey.getTimeDemandTypeRef()),
                            refString(journey.getVehicleTypeRef()),
                            null,
                            val(journey.getDeadRunType())
                    };
                });

            rows = Stream.concat(serviceJourneys, deadrunJourneys);
        }

        return new MemoryTable("vehiclejourneys", rows,
                "#frame_timestamp", "$vehiclejourney_id", "#version", "type", "derived_from", "condition",
                "journeynumber", "departuretime", "departuredayoffset",
                "journeypattern_id", "timedemandtype_id", "vehicletype_id",
                "operator_id", "dynamic");
    }

    private Table vehicleJourneyConditions(TimetableFrame frame, Long timestamp) {
        Stream<String[]> rows = Stream.empty();

        if (frame.getVehicleJourneys() != null) {
            Stream<String[]> serviceJourneys = Stream.empty();
            Stream<String[]> deadrunJourneys = Stream.empty();

            if (frame.getVehicleJourneys().getServiceJourney() != null)
                serviceJourneys = frame.getVehicleJourneys().getServiceJourney().parallelStream()
                        .flatMap(journey -> journey.getValidityConditions() != null
                                ? journey.getValidityConditions().getAvailabilityConditionRef().stream().map(
                                        cond -> new String[] { timestamp.toString(), journey.getId(),
                                                str(journey.getVersion(), () -> "any"),
                                                refString(cond) })
                                : Stream.empty());

            if (frame.getVehicleJourneys().getDeadRun() != null)
                deadrunJourneys = frame.getVehicleJourneys().getDeadRun().parallelStream()
                        .flatMap(journey -> journey.getValidityConditions() != null
                                ? journey.getValidityConditions().getAvailabilityConditionRef().stream().map(
                                        cond -> new String[] { timestamp.toString(), journey.getId(),
                                                str(journey.getVersion(), () -> "any"),
                                                refString(cond) })
                                : Stream.empty());

            rows = Stream.concat(serviceJourneys, deadrunJourneys);
        }

        return new MemoryTable("vehiclejourneyconditions", rows,
                "#frame_timestamp", "$vehiclejourney_id", "#version", "$validitycondition_id");
    }

    private Table journeyInterchanges(TimetableFrame frame, Long timestamp) {
        Stream<String[]> rows = Stream.empty();

        if (frame.getJourneyInterchanges() != null)
            rows = frame.getJourneyInterchanges().getServiceJourneyInterchange().parallelStream()
                    .map(change -> new String[] {
                            timestamp.toString(),
                            change.getId(),
                            str(change.getVersion(), () -> "any"),
                            refString(change.getFromPointRef()),
                            refString(change.getToPointRef()),
                            refString(change.getFromJourneyRef()),
                            refString(change.getToJourneyRef()),
                    });

        return new MemoryTable("journeyinterchanges", rows,
                "#frame_timestamp", "$journeyinterchange_id", "#version", "from_point", "to_point",
                "from_journey", "to_journey");
    }

    @Override
    public Stream<Table> apply(TimetableFrame frame, Long timestamp) {
        return Stream.of(
                availabilityConditions(frame, timestamp),
                vehicleJourneys(frame, timestamp),
                vehicleJourneyConditions(frame, timestamp));
        // this::journeyInterchanges);
    }
}

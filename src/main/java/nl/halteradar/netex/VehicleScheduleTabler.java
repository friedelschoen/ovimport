package nl.halteradar.netex;

import java.util.function.Supplier;
import java.util.stream.Stream;

import nl.bisonnl.netex.VehicleScheduleFrame;
import nl.halteradar.util.CommonTabler;
import nl.halteradar.table.MemoryTable;
import nl.halteradar.table.Table;

final class VehicleScheduleTabler extends CommonTabler<VehicleScheduleFrame> {
    Table blocks(VehicleScheduleFrame frame, Long timestamp) {
        Stream<String[]> rows = Stream.empty();

        if (frame.getBlocks() != null) {
            rows = frame.getBlocks().getBlock().parallelStream().map(block -> new String[] {
                    timestamp.toString(),
                    block.getId(),
                    str(block.getVersion(), () -> "any"),
                    privateCode(block.getPrivateCodes(), "BlockCode"),
                    text(block.getName()),
                    text(block.getDescription()),
                    optionalString(block.getPreparationDuration()),
                    optionalString(block.getStartTime()),
                    optionalString(block.getStartTimeDayOffset()),
                    optionalString(block.getFinishingDuration()),
                    optionalString(block.getEndTime()),
                    optionalString(block.getEndTimeDayOffset()),
                    optionalString(block.getStartPointRef(), CommonTabler::refStringClass),
                    optionalString(block.getEndPointRef(), CommonTabler::refStringClass),
            });
        }

        return new MemoryTable("blocks", rows,
                "#frame_timestamp", "$block_id", "#version", "blockcode", "name", "description",
                "preparationduration", "starttime", "starttimedayoffset",
                "finishingduration", "endtime", "endtimedayoffset",
                "startpoint_id", "endpoint_id");
    }

    Table blockValidityCondition(VehicleScheduleFrame frame, Long timestamp) {
        Stream<String[]> rows = Stream.empty();

        if (frame.getBlocks() != null) {
            rows = frame.getBlocks().getBlock().parallelStream().flatMap(block -> block.getValidityConditions() != null
                    ? block.getValidityConditions().getAvailabilityConditionRef().stream()
                            .map(j -> new String[] { timestamp.toString(), block.getId(),
                                    str(block.getVersion(), () -> "any"),
                                    refString(j) })
                    : Stream.empty());
        }

        return new MemoryTable("blockvaliditycondition", rows,
                "#frame_timestamp", "$block_id", "#version", "$validitycondition_id");
    }

    Table blockDayTypes(VehicleScheduleFrame frame, Long timestamp) {
        Stream<String[]> rows = Stream.empty();

        if (frame.getBlocks() != null) {
            rows = frame.getBlocks().getBlock().parallelStream().flatMap(block -> block.getDayTypes() != null
                    ? block.getDayTypes().getDayTypeRef().stream()
                            .map(j -> new String[] { timestamp.toString(), block.getId(),
                                    str(block.getVersion(), () -> "any"),
                                    refString(j) })
                    : Stream.empty());
        }

        return new MemoryTable("blockdaytypes", rows,
                "#frame_timestamp", "$block_id", "#version", "$daytypes");
    }

    Table blockJourneys(VehicleScheduleFrame frame, Long timestamp) {
        Stream<String[]> rows = Stream.empty();

        if (frame.getBlocks() != null) {
            rows = frame.getBlocks().getBlock().parallelStream().flatMap(block -> block.getJourneys() != null
                    ? block.getJourneys().getDeadRunRefOrServiceJourneyRef().stream()
                            .map(j -> new String[] { timestamp.toString(), block.getId(),
                                    str(block.getVersion(), () -> "any"),
                                    refString(j.getValue()) })
                    : Stream.empty());
        }

        return new MemoryTable("blockjourneys", rows,
                "#frame_timestamp", "$block_id", "#version", "$vehiclejourney_id");
    }

    @Override
    public Stream<Table> apply(VehicleScheduleFrame frame, Long timestamp) {
        return Stream.of(
                blocks(frame, timestamp),
                blockValidityCondition(frame, timestamp),
                blockJourneys(frame, timestamp),
                blockDayTypes(frame, timestamp));
    }
}

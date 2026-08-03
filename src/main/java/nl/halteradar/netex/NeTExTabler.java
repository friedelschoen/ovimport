package nl.halteradar.netex;

import java.util.function.Function;
import java.util.stream.Stream;
import nl.bisonnl.netex.CompositeFrame;
import nl.bisonnl.netex.ResourceFrame;
import nl.bisonnl.netex.ServiceCalendarFrame;
import nl.bisonnl.netex.ServiceFrame;
import nl.bisonnl.netex.TimetableFrame;
import nl.bisonnl.netex.VehicleScheduleFrame;
import nl.bisonnl.netex.VersionFrameVersionStructure;
import nl.halteradar.util.FromFile;
import nl.halteradar.Table;

public class NeTExTabler implements Function<FromFile<CompositeFrame>, Stream<Table>> {
    private ResourceTabler resourceTabler = new ResourceTabler();
    private ServiceTabler serviceTabler = new ServiceTabler();
    private ServiceCalendarTabler serviceCalendarTabler = new ServiceCalendarTabler();
    private TimetableTabler timetableTabler = new TimetableTabler();
    private VehicleScheduleTabler vehicleScheduleTabler = new VehicleScheduleTabler();

    private Stream<FromFile<VersionFrameVersionStructure>> frames(FromFile<CompositeFrame> composite) {
        var frames = composite.value().getFrames();
        if (frames == null)
            return Stream.empty();

        return frames.getResourceFrameOrInfrastructureFrameOrSiteFrame().parallelStream()
                .map(f -> new FromFile<>(composite.filename(), f));
    }

    private Stream<Table> tables(FromFile<CompositeFrame> composite,
            FromFile<VersionFrameVersionStructure> frame) {
        String dataset = composite.value().getId() + "#" + composite.value().getVersion();
        return switch (frame.value()) {
            case ServiceFrame serviceFrame ->
                serviceTabler.apply(serviceFrame, dataset);
            case TimetableFrame timetableFrame ->
                timetableTabler.apply(timetableFrame, dataset);
            case ResourceFrame resourceFrame ->
                resourceTabler.apply(resourceFrame, dataset);
            case ServiceCalendarFrame serviceCalendarFrame ->
                serviceCalendarTabler.apply(serviceCalendarFrame, dataset);
            case VehicleScheduleFrame vehicleScheduleFrame ->
                vehicleScheduleTabler.apply(vehicleScheduleFrame, dataset);
            default -> Stream.empty();
        };
    }

    private Table compositeFrame(FromFile<CompositeFrame> composite) {
        String fromDate = null;
        String toDate = null;

        if (composite.value().getValidBetween() != null) {
            if (composite.value().getValidBetween().getToDate() != null)
                fromDate = composite.value().getValidBetween().getFromDate().toString();

            if (composite.value().getValidBetween().getToDate() != null)
                toDate = composite.value().getValidBetween().getToDate().toString();
        }

        String dataset = composite.value().getId() + "#" + composite.value().getVersion();

        String[] row = new String[] {
                dataset,
                composite.filename(),
                fromDate,
                toDate
        };

        return new Table(composite.filename(), "datasets", Stream.<String[]>of(row), "dataset_id",
                "filename", "from_date", "to_date");
    }

    @Override
    public Stream<Table> apply(FromFile<CompositeFrame> composite) {
        return Stream.concat(Stream.of(compositeFrame(composite)),
                frames(composite).flatMap(t -> tables(composite, t)));
    }
}

package nl.halteradar.netex;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.function.Function;
import java.util.stream.Stream;

import nl.bisonnl.netex.CompositeFrame;
import nl.bisonnl.netex.PublicationDelivery;
import nl.bisonnl.netex.ResourceFrame;
import nl.bisonnl.netex.ServiceCalendarFrame;
import nl.bisonnl.netex.ServiceFrame;
import nl.bisonnl.netex.TimetableFrame;
import nl.bisonnl.netex.ValidBetween;
import nl.bisonnl.netex.VehicleScheduleFrame;
import nl.bisonnl.netex.VersionFrameVersionStructure;
import nl.halteradar.table.Table;

public class NeTExTabler implements Function<CompositeFrame, Stream<Table>> {
    private ResourceTabler resourceTabler = new ResourceTabler();
    private ServiceTabler serviceTabler = new ServiceTabler();
    private ServiceCalendarTabler serviceCalendarTabler = new ServiceCalendarTabler();
    private TimetableTabler timetableTabler = new TimetableTabler();
    private VehicleScheduleTabler vehicleScheduleTabler = new VehicleScheduleTabler();

    private final PublicationDelivery publication;

    public NeTExTabler(PublicationDelivery publication) {
        this.publication = publication;
    }

    private Stream<VersionFrameVersionStructure> frames(CompositeFrame composite) {
        var frames = composite.getFrames();
        if (frames == null)
            return Stream.empty();

        return frames.getResourceFrameOrInfrastructureFrameOrSiteFrame().parallelStream();
    }

    private Stream<Table> tables(VersionFrameVersionStructure frame, Long timestamp) {
        return switch (frame) {
            case ServiceFrame serviceFrame ->
                serviceTabler.apply(serviceFrame, timestamp);
            case TimetableFrame timetableFrame ->
                timetableTabler.apply(timetableFrame, timestamp);
            case ResourceFrame resourceFrame ->
                resourceTabler.apply(resourceFrame, timestamp);
            case ServiceCalendarFrame serviceCalendarFrame ->
                serviceCalendarTabler.apply(serviceCalendarFrame, timestamp);
            case VehicleScheduleFrame vehicleScheduleFrame ->
                vehicleScheduleTabler.apply(vehicleScheduleFrame, timestamp);
            default -> Stream.empty();
        };
    }

    private boolean isValid(CompositeFrame frame) {
        if (frame.getValidBetween() == null)
            return true;

        ValidBetween validity = frame.getValidBetween();
        Calendar now = new GregorianCalendar();
        // if (validity.getFromDate() != null &&
        // validity.getFromDate().toGregorianCalendar().after(now))
        // return false;

        if (validity.getToDate() != null && validity.getToDate().toGregorianCalendar().before(now))
            return false;

        return true;
    }

    @Override
    public Stream<Table> apply(CompositeFrame composite) {
        if (!isValid(composite))
            return Stream.empty();

        long pubTimestamp = 0, frameTimestamp = 0;

        if (publication.getPublicationTimestamp() != null)
            pubTimestamp = publication.getPublicationTimestamp().toGregorianCalendar().getTimeInMillis();

        if (composite.getValidBetween() != null && composite.getValidBetween().getFromDate() != null)
            frameTimestamp = composite.getValidBetween().getFromDate().toGregorianCalendar().getTimeInMillis();

        Long timestamp = Long.max(pubTimestamp, frameTimestamp);

        return frames(composite).flatMap(t -> tables(t, timestamp));
    }
}

package nl.halteradar.netex;

import java.util.function.Supplier;
import java.util.stream.Stream;

import nl.bisonnl.netex.PassengerStopAssignment;
import nl.bisonnl.netex.ServiceFrame;
import nl.bisonnl.netex.StopPointInJourneyPattern;
import nl.bisonnl.netex.TimingPointInJourneyPattern;
import nl.halteradar.util.CommonTabler;
import nl.halteradar.util.OrderMap;
import nl.halteradar.table.MemoryTable;
import nl.halteradar.table.Table;

class ServiceTabler extends CommonTabler<ServiceFrame> {
    private Table routePoints(ServiceFrame frame, Long timestamp) {
        Stream<String[]> routePoints = Stream.empty();

        if (frame.getRoutePoints() != null) {
            routePoints = frame.getRoutePoints().getRoutePoint().parallelStream().map(routePoint -> {
                return new String[] {
                        timestamp.toString(),
                        routePoint.getId(),
                        str(routePoint.getVersion(), () -> "any"),
                        "POINT(" + routePoint.getLocation().getPos().getValue() + ")"
                };
            });
        }

        return new MemoryTable("routepoints", routePoints,
                "#frame_timestamp", "$routepoint_id", "#version", "point");
    }

    private Table routeLinks(ServiceFrame frame, Long timestamp) {
        Stream<String[]> rows = Stream.empty();

        if (frame.getRouteLinks() != null) {
            rows = frame.getRouteLinks().getRouteLink().parallelStream().map(link -> new String[] {
                    timestamp.toString(),
                    link.getId(),
                    str(link.getVersion(), () -> "any"),
                    refString(link.getFromPointRef()),
                    refString(link.getToPointRef()),
                    str(link.getDistance()),
                    linestringWkt(link.getLineString()),
                    refString(link.getOperationalContextRef()),
                    str(link.getResponsibilitySetRef()),
            });
        }

        return new MemoryTable("routelinks", rows,
                "#frame_timestamp", "$routelink_id", "#version", "from_routepoint_id", "to_routepoint_id",
                "distance", "linestring", "operational_context_id",
                "responsibilityset_id");
    }

    OrderMap<String> routeOrder = new OrderMap<>();

    private Table routes(ServiceFrame frame, Long timestamp) {
        Stream<String[]> rows = Stream.empty();

        if (frame.getRoutes() != null) {
            rows = frame.getRoutes().getRouteOrFlexibleRoute().parallelStream()
                    .flatMap(route -> {
                        if (route.getPointsInSequence() == null)
                            return Stream.empty();

                        return route.getPointsInSequence().getPointOnRoute().stream()
                                .map(point -> new String[] {
                                        timestamp.toString(),
                                        route.getId(),
                                        str(route.getVersion(), () -> "any"),
                                        refString(route.getLineRef()),
                                        text(route.getName()),
                                        val(route.getDirectionType()),
                                        routeOrder.getOrder(point.getId(), point.getOrder()).toString(),
                                        refString(point.getRoutePointRef()),
                                        refString(point.getOnwardRouteLinkRef()),
                                });
                    });
        }

        return new MemoryTable("routes", rows,
                "#frame_timestamp", "$route_id", "#version", "line_id", "name", "direction",
                "point_order", "routepoint_id", "onward_routelink_id");
    }

    private Table destinationDisplays(ServiceFrame frame, Long timestamp) {
        Stream<String[]> rows = Stream.empty();

        if (frame.getDestinationDisplays() != null) {
            rows = frame.getDestinationDisplays().getDestinationDisplay().parallelStream().map(display -> {
                var colors = presentationColor(display.getPresentation());

                var vias = new StringBuilder();
                if (display.getVias() != null) {
                    for (var via : display.getVias().getVia()) {
                        if (vias.length() > 0)
                            vias.append(Delimiter);
                        vias.append(text(via.getName()));
                    }
                }

                return new String[] {
                        timestamp.toString(),
                        display.getId(),
                        str(display.getVersion(), () -> "any"),
                        privateCode(display.getPrivateCodes(), "DestinationCode"),
                        text(display.getName()),
                        text(display.getSideText()),
                        text(display.getFrontText()),
                        colors[0],
                        colors[1],
                        vias.toString(),
                };
            });
        }

        return new MemoryTable("destinationdisplays", rows,
                "#frame_timestamp", "$display_id", "#version", "destinationcode", "name", "sidetext",
                "fronttext", "color", "textcolor", "vias");
    }

    private Table destinationDisplayVariants(ServiceFrame frame, Long timestamp) {
        Stream<String[]> rows = Stream.empty();

        if (frame.getDestinationDisplays() != null) {
            rows = frame
                    .getDestinationDisplays().getDestinationDisplay().parallelStream().flatMap(
                            display -> display.getVariants() != null
                                    ? display.getVariants().getDestinationDisplayVariant().stream()
                                            .map(v -> new String[] {
                                                    timestamp.toString(),
                                                    display.getId(),
                                                    str(display.getVersion(), () -> "any"),
                                                    v.getDestinationDisplayVariantMediaType().value(),
                                                    v.getExtensions().getMaxLength().toString(),
                                                    text(v.getName()) })
                                    : Stream.empty());
        }

        return new MemoryTable("destinationdisplayvariants", rows,
                "#frame_timestamp", "$display_id", "#version", "$mediatype", "$length", "name");
    }

    private Table scheduledStopPoints(ServiceFrame frame, Long timestamp) {
        Stream<String[]> rows = Stream.empty();

        if (frame.getScheduledStopPoints() != null) {
            rows = frame.getScheduledStopPoints().getScheduledStopPoint().parallelStream().map(stop -> {
                String projection = "";
                if (stop.getProjections() != null && stop.getProjections().getPointProjection() != null)
                    projection = refStringClass(stop.getProjections().getPointProjection().getProjectToPointRef());

                String stopArea = "";
                if (stop.getStopAreas() != null)
                    stopArea = refString(stop.getStopAreas().getStopAreaRef());

                String place = "";
                if (stop.getTopographicPlaceView() != null)
                    place = text(stop.getTopographicPlaceView().getName());

                return new String[] {
                        timestamp.toString(),
                        stop.getId(),
                        str(stop.getVersion(), () -> "any"),
                        privateCode(stop.getPrivateCodes(), "UserStopCode"),
                        text(stop.getName()),
                        pointWkt(stop.getLocation()),
                        projection,
                        stopArea,
                        binBool(Boolean.TRUE.equals(stop.isForAlighting())),
                        binBool(Boolean.TRUE.equals(stop.isForBoarding())),
                        place,
                };
            });
        }

        return new MemoryTable("scheduledstoppoints", rows,
                "#frame_timestamp", "$stoppoint_id", "#version", "userstopcode", "name", "point",
                "projected_routepoint_id", "stoparea_id", "alighting",
                "boarding", "place");
    }

    private Table scheduledStopPointTariffzones(ServiceFrame frame, Long timestamp) {
        Stream<String[]> rows = Stream.empty();

        if (frame.getScheduledStopPoints() != null) {
            rows = frame.getScheduledStopPoints().getScheduledStopPoint().parallelStream()
                    .flatMap(pnt -> pnt.getTariffZones() != null
                            ? pnt.getTariffZones().getTariffZoneRef().stream()
                                    .map(t -> new String[] { timestamp.toString(), pnt.getId(),
                                            str(pnt.getVersion(), () -> "any"),
                                            refString(t) })
                            : Stream.empty());
        }

        return new MemoryTable("scheduledstoppointtariffzones", rows,
                "#frame_timestamp", "$stoppoint_id", "#version", "$tariffzone");
    }

    private Table stopAreas(ServiceFrame frame, Long timestamp) {
        Stream<String[]> rows = Stream.empty();

        if (frame.getStopAreas() != null) {
            rows = frame.getStopAreas().getStopArea().parallelStream().map(stoparea -> {
                String place = "";
                if (stoparea.getTopographicPlaceView() != null)
                    place = text(stoparea.getTopographicPlaceView().getName());

                return new String[] {
                        timestamp.toString(),
                        stoparea.getId(),
                        str(stoparea.getVersion(), () -> "any"),
                        privateCode(stoparea.getPrivateCodes(), "UserStopAreaCode"),
                        text(stoparea.getName()),
                        stoparea.getPublicCode(),
                        place
                };
            });
        }

        return new MemoryTable("stopareas", rows, "#frame_timestamp", "$stoparea_id", "#version", "userstopareacode",
                "name",
                "publiccode", "place");
    }

    private Table stopAssignments(ServiceFrame frame, Long timestamp) {
        Stream<String[]> rows = Stream.empty();

        if (frame.getStopAssignments() != null) {
            rows = frame.getStopAssignments().getPassengerStopAssignmentOrFlexibleStopAssignment().parallelStream()
                    .map(a -> (a instanceof PassengerStopAssignment) ? (PassengerStopAssignment) a : null)
                    .filter(a -> a != null)
                    .map(a -> new String[] {
                            timestamp.toString(),
                            a.getId(),
                            str(a.getVersion(), () -> "any"),
                            refString(a.getScheduledStopPointRef()),
                            refString(a.getQuayRef()),
                            refString(a.getStopPlaceRef()),
                    });
        }

        return new MemoryTable("stopassignments", rows,
                "#frame_timestamp", "$assignment_id", "#version", "stoppoint_id", "quay_id", "stopplace_id");
    }

    private Table timingPoints(ServiceFrame frame, Long timestamp) {
        Stream<String[]> rows = Stream.empty();

        if (frame.getTimingPoints() != null) {
            rows = frame.getTimingPoints().getTimingPoint().parallelStream().map(point -> {
                String projection = "";
                if (point.getProjections() != null && point.getProjections().getPointProjection() != null)
                    projection = refStringClass(point.getProjections().getPointProjection().getProjectToPointRef());

                return new String[] {
                        timestamp.toString(),
                        point.getId(),
                        str(point.getVersion(), () -> "any"),
                        text(point.getName()),
                        pointWkt(point.getLocation()),
                        projection,
                };
            });
        }

        return new MemoryTable("timingpoints", rows,
                "#frame_timestamp", "$timingpoint_id", "#version", "name", "point", "projected_routepoint_id");
    }

    private Table timingLinks(ServiceFrame frame, Long timestamp) {
        Stream<String[]> rows = Stream.empty();

        if (frame.getTimingLinks() != null) {
            rows = frame.getTimingLinks().getTimingLink().parallelStream().map(link -> new String[] {
                    timestamp.toString(),
                    link.getId(),
                    str(link.getVersion(), () -> "any"),
                    refStringOptClass(link.getFromPointRef()),
                    refStringOptClass(link.getToPointRef()),
                    str(link.getDistance()),
                    refString(link.getOperationalContextRef()),
            });
        }

        return new MemoryTable("timinglinks", rows,
                "#frame_timestamp", "$timinglink_id", "#version", "from_point_id", "to_point_id",
                "distance", "operational_context_id");
    }

    OrderMap<String> jpOrder = new OrderMap<>();

    private String[] stoppointInJourneyPattern(Long timestamp, String jpID, String jpVersion,
            String name, String routeRef,
            String dirType,
            String destDispRef, StopPointInJourneyPattern p) {
        return new String[] {
                timestamp.toString(),
                jpID,
                jpVersion,
                name,
                routeRef,
                dirType,
                destDispRef,
                p.getId(),
                jpOrder.getOrder(jpID, p.getOrder()).toString(),
                refString(p.getScheduledStopPointRef()),
                null,
                refString(p.getOnwardTimingLinkRef()),
                binBool(p.isIsWaitPoint()),
        };

    }

    private String[] timingpointInJourneyPattern(Long timestamp, String jpID, String jpVersion,
            String name, String routeRef,
            String dirType,
            String destDispRef, TimingPointInJourneyPattern p) {
        return new String[] {
                timestamp.toString(),
                jpID,
                jpVersion,
                name,
                routeRef,
                dirType,
                destDispRef,
                p.getId(),
                jpOrder.getOrder(jpID, p.getOrder()).toString(),
                null,
                refString(p.getTimingPointRef()),
                refString(p.getOnwardTimingLinkRef()),
                binBool(p.isIsWaitPoint()),
        };

    }

    private Table journeyPatterns(ServiceFrame frame, Long timestamp) {
        Stream<String[]> rows = Stream.empty();

        if (frame.getJourneyPatterns() != null) {
            Stream<String[]> serviceJourneys = frame.getJourneyPatterns().getServiceJourneyPattern().parallelStream()
                    .flatMap(sjp -> {
                        if (sjp.getPointsInSequence() == null)
                            return Stream.empty();

                        return sjp.getPointsInSequence()
                                .getStopPointInJourneyPatternOrTimingPointInJourneyPattern()
                                .stream()
                                .map(p -> switch (p) {
                                    case StopPointInJourneyPattern sp ->
                                        stoppointInJourneyPattern(timestamp, sjp.getId(),
                                                str(sjp.getVersion(), () -> "any"),
                                                text(sjp.getName()),
                                                refString(sjp.getRouteRef()),
                                                val(sjp.getDirectionType()),
                                                refString(sjp.getDestinationDisplayRef()), sp);
                                    case TimingPointInJourneyPattern tp ->
                                        timingpointInJourneyPattern(timestamp, sjp.getId(),
                                                str(sjp.getVersion(), () -> "any"),
                                                text(sjp.getName()),
                                                refString(sjp.getRouteRef()),
                                                val(sjp.getDirectionType()),
                                                refString(sjp.getDestinationDisplayRef()), tp);
                                    default -> null;
                                });
                    });

            Stream<String[]> deadRunJourneys = frame.getJourneyPatterns().getDeadRunJourneyPattern().parallelStream()
                    .flatMap(drp -> {
                        if (drp.getPointsInSequence() == null)
                            return Stream.empty();

                        return drp.getPointsInSequence()
                                .getStopPointInJourneyPatternOrTimingPointInJourneyPattern()
                                .stream()
                                .map(p -> switch (p) {
                                    case StopPointInJourneyPattern sp ->
                                        stoppointInJourneyPattern(timestamp, drp.getId(),
                                                str(drp.getVersion(), () -> "any"),
                                                text(drp.getName()),
                                                null, null, null,
                                                sp);
                                    case TimingPointInJourneyPattern tp ->
                                        timingpointInJourneyPattern(timestamp, drp.getId(),
                                                str(drp.getVersion(), () -> "any"),
                                                text(drp.getName()),
                                                null, null, null,
                                                tp);
                                    default -> null;
                                });
                    });
            rows = Stream.concat(serviceJourneys, deadRunJourneys);
        }

        return new MemoryTable("journeypatterns", rows,
                "#frame_timestamp", "$journeypattern_id", "#version", "name", "route_id", "direction",
                "destinationdisplay_id", "point_id", "point_order",
                "scheduledstoppoint_id", "timingpoint_id",
                "onward_timinglink_id", "is_waitpoint");

    }

    private Table timeDemandTypes(ServiceFrame frame, Long timestamp) {
        Stream<String[]> rows = Stream.empty();

        if (frame.getTimeDemandTypes() != null) {
            rows = frame.getTimeDemandTypes().getTimeDemandType().parallelStream()
                    .flatMap(tdt -> {
                        Stream<String[]> runTimes = Stream.empty();
                        Stream<String[]> waitTimes = Stream.empty();
                        Stream<String[]> layovers = Stream.empty();

                        if (tdt.getRunTimes() != null) {
                            runTimes = tdt.getRunTimes().getJourneyRunTime().stream()
                                    .map(rt -> new String[] {
                                            timestamp.toString(),
                                            tdt.getId(),
                                            str(tdt.getVersion(), () -> "any"),
                                            "run",
                                            rt.getId(),
                                            refString(rt.getTimingLinkRef()),
                                            "",
                                            "",
                                            toString(rt.getRunTime()),
                                    });
                        }

                        if (tdt.getWaitTimes() != null) {
                            waitTimes = tdt.getWaitTimes().getJourneyWaitTime().stream()
                                    .map(wt -> new String[] {
                                            timestamp.toString(),
                                            tdt.getId(),
                                            str(tdt.getVersion(), () -> "any"),
                                            "wait",
                                            wt.getId(),
                                            "",
                                            refString(wt.getScheduledStopPointRef()),
                                            refString(wt.getTimingPointRef()),
                                            toString(wt.getWaitTime()),
                                    });
                        }

                        if (tdt.getLayovers() != null) {
                            layovers = tdt.getLayovers().getJourneyLayover().stream()
                                    .map(lo -> new String[] {
                                            timestamp.toString(),
                                            tdt.getId(),
                                            str(tdt.getVersion(), () -> "any"),
                                            "layover",
                                            lo.getId(),
                                            "",
                                            refString(lo.getScheduledStopPointRef()),
                                            refString(lo.getTimingPointRef()),
                                            toString(lo.getLayover()),
                                    });
                        }

                        return Stream.concat(Stream.concat(runTimes, waitTimes), layovers);
                    });
        }

        return new MemoryTable("timedemandtypes", rows,
                "#frame_timestamp", "$timedemandtype_id", "#version", "type", "entry_id",
                "timinglink_id", "scheduledstoppoint_id", "timingpoint_id", "duration");
    }

    private Table notices(ServiceFrame frame, Long timestamp) {
        Stream<String[]> rows = Stream.empty();

        if (frame.getNotices() != null) {
            rows = frame.getNotices().getNotice().parallelStream().map(n -> new String[] {
                    timestamp.toString(),
                    n.getId(),
                    str(n.getVersion(), () -> "any"),
                    text(n.getName()),
                    text(n.getText()),
            });
        }

        return new MemoryTable("notices", rows,
                "#frame_timestamp", "$notice_id", "#version", "name", "text");
    }

    private Table noticeAssignments(ServiceFrame frame, Long timestamp) {
        Stream<String[]> rows = Stream.empty();

        if (frame.getNoticeAssignments() != null) {
            rows = frame.getNoticeAssignments().getNoticeAssignment().parallelStream().map(a -> new String[] {
                    timestamp.toString(),
                    a.getId(),
                    str(a.getVersion(), () -> "any"),
                    refString(a.getNoticeRef()),
                    refStringClass(a.getNoticedObjectRef()),
                    toString(a.getOrder()),
            });
        }

        return new MemoryTable("noticeassignments", rows,
                "#frame_timestamp", "$assignment_id", "#version", "notice_id", "object_id", "view_index");
    }

    private Table lines(ServiceFrame frame, Long timestamp) {
        Stream<String[]> lines = Stream.empty();

        if (frame.getLines() != null) {
            lines = frame.getLines().getLine().parallelStream().map(line -> {
                String branding = refString(line.getBrandingRef());
                String mode = transportMode(line);
                String lineUrl = line.getUrl();
                String authority = refString(line.getAuthorityRef());
                String label = refString(line.getTypeOfProductCategoryRef());
                String operator = refString(line.getOperatorRef());
                var colors = presentationColor(line.getPresentation());
                String color = colors[0];
                String textColor = colors[1];

                return new String[] {
                        timestamp.toString(),
                        line.getId(),
                        str(line.getVersion(), () -> "any"),
                        privateCode(line.getPrivateCodes(), "LinePlanningNumber"),
                        branding,
                        text(line.getName()),
                        text(line.getShortName()),
                        text(line.getDescription()),
                        mode,
                        lineUrl,
                        line.getPublicCode(),
                        authority,
                        operator,
                        label,
                        binBool(line.isMonitored()),
                        color,
                        textColor,
                };
            });
        }

        return new MemoryTable("lines", lines,
                "#frame_timestamp", "$line_id", "#version", "lineplanningnumber", "branding", "name",
                "shortname", "description", "mode", "url", "publiccode",
                "authority", "operator", "label", "monitored", "color",
                "textcolor");
    }

    @Override
    public Stream<Table> apply(ServiceFrame frame, Long timestamp) {
        return Stream.of(
                routePoints(frame, timestamp),
                routeLinks(frame, timestamp),
                routes(frame, timestamp),
                lines(frame, timestamp),
                destinationDisplays(frame, timestamp),
                destinationDisplayVariants(frame, timestamp),
                scheduledStopPoints(frame, timestamp),
                scheduledStopPointTariffzones(frame, timestamp),
                stopAssignments(frame, timestamp),
                timingPoints(frame, timestamp),
                timingLinks(frame, timestamp),
                journeyPatterns(frame, timestamp),
                timeDemandTypes(frame, timestamp),
                notices(frame, timestamp),
                noticeAssignments(frame, timestamp),
                stopAreas(frame, timestamp));
    }
}

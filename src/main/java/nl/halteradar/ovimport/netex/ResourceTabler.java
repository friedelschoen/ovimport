package nl.halteradar.ovimport.netex;

import java.util.Objects;
import java.util.stream.Stream;

import nl.bisonnl.netex.Authority;
import nl.bisonnl.netex.Operator;
import nl.bisonnl.netex.PassengerCapacity;
import nl.bisonnl.netex.ResourceFrame;
import nl.bisonnl.netex.VersionOfObjectRefStructure;
import nl.halteradar.ovimport.util.CommonTabler;
import nl.halteradar.ovimport.table.MemoryTable;
import nl.halteradar.ovimport.table.Table;

final class ResourceTabler extends CommonTabler<ResourceFrame> {
    private Table branding(ResourceFrame frame, Long timestamp) {
        Stream<String[]> rows = Stream.empty();

        if (frame.getTypesOfValue() != null) {
            Stream<String[]> brandings = frame.getTypesOfValue().getBranding().parallelStream().map(branding -> {
                var colors = presentationColor(branding.getPresentation());
                return new String[] {
                        timestamp.toString(),
                        branding.getId(),
                        str(branding.getVersion(), () -> "any"),
                        "branding",
                        text(branding.getName()),
                        text(branding.getDescription()),
                        branding.getImage(),
                        branding.getUrl(),
                        colors[0],
                        colors[1],
                };
            });

            Stream<String[]> labels = frame.getTypesOfValue().getTypeOfProductCategory().parallelStream().map(label -> {
                var colors = presentationColor(label.getPresentation());
                return new String[] {
                        timestamp.toString(),
                        label.getId(),
                        str(label.getVersion(), () -> "any"),
                        "label",
                        text(label.getName()),
                        text(label.getDescription()),
                        label.getImage(),
                        label.getUrl(),
                        colors[0],
                        colors[1],
                };
            });

            rows = Stream.concat(brandings, labels);
        }

        return new MemoryTable("serviceclasses", rows,
                "%valid_from", "$serviceclass_id", "#version", "type", "name", "description", "image", "url",
                "color", "textcolor");
    }

    private Table organisations(ResourceFrame frame, Long timestamp) {
        Stream<String[]> rows = Stream.empty();

        if (frame.getOrganisations() != null) {
            rows = frame.getOrganisations().getAuthorityOrOperator().parallelStream().map(value -> switch (value) {
                case Authority au -> new String[] {
                        timestamp.toString(),
                        au.getId(),
                        str(au.getVersion(), () -> "any"),
                        "authority",
                        text(au.getName()),
                        text(au.getShortName()),
                        text(au.getDescription()),
                };
                case Operator op -> new String[] {
                        timestamp.toString(),
                        op.getId(),
                        str(op.getVersion(), () -> "any"),
                        "operator",
                        text(op.getName()),
                        text(op.getShortName()),
                        text(op.getDescription()),
                };
                default -> null;
            });
        }

        return new MemoryTable("organisations", rows,
                "%valid_from", "$organisation_id", "#version", "type", "name", "shortname", "description");
    }

    private Table passengerCapacities(ResourceFrame frame, Long timestamp) {
        Stream<String[]> rows = Stream.empty();
        if (frame.getVehicleTypes() != null) {
            rows = frame.getVehicleTypes().getVehicleType().parallelStream()
                    .flatMap(vt -> vt.getCapacities() != null
                            ? vt.getCapacities().getPassengerCapacityRefOrPassengerCapacity().stream()
                            : Stream.empty())
                    .map(cap -> cap instanceof PassengerCapacity pc ? pc : null)
                    .filter(Objects::nonNull)
                    .filter(cap -> cap.getId() != null)
                    .map(c -> new String[] {
                            timestamp.toString(),
                            c.getId(),
                            str(c.getVersion(), () -> "any"),
                            str(c.getFareClass()),
                            str(c.getTotalCapacity()),
                            str(c.getSeatingCapacity()),
                            str(c.getStandingCapacity()),
                            str(c.getSpecialPlaceCapacity()),
                            str(c.getPushchairCapacity()),
                            str(c.getWheelchairPlaceCapacity()),
                    });
        }

        return new MemoryTable("passengercapacities", rows,
                "%valid_from", "$capacity_id", "#version", "fareclass", "total", "seating",
                "standing", "specialplace", "pushchair", "wheelchair");
    }

    private Table vehicleTypes(ResourceFrame frame, Long timestamp) {
        Stream<String[]> rows = Stream.empty();

        if (frame.getVehicleTypes() != null) {
            rows = frame.getVehicleTypes().getVehicleType().parallelStream().map(vt -> {
                return new String[] {
                        timestamp.toString(),
                        vt.getId(),
                        str(vt.getVersion(), () -> "any"),
                        privateCode(vt.getPrivateCodes(), "VoertuigTypeCode"),
                        refString(vt.getBrandingRef()),
                        text(vt.getName()),
                        text(vt.getShortName()),
                        text(vt.getDescription()),
                        vt.getEuroClass(),
                        binBool(vt.isReversingDirection()),
                        binBool(vt.isSelfPropelled()),
                        str(vt.getPropulsionType()),
                        str(vt.getFuelType()),
                        str(vt.getMaximumRange()),
                        str(vt.getTransportMode()),
                        binBool(vt.isLowFloor()),
                        binBool(vt.isHasLiftOrRamp()),
                        binBool(vt.isHasHoist()),
                        str(vt.getBoardingHeight()),
                        str(vt.getGapToPlatform()),
                        str(vt.getLength()),
                        str(vt.getWidth()),
                        str(vt.getHeight()),
                        str(vt.getWeight()),
                        str(vt.getFirstAxleHeight()),
                };
                // faciltities
            });
        }

        return new MemoryTable("vehicletypes", rows,
                "%valid_from", "$vehicletype_id", "#version", "vehicletypecode", "branding", "name",
                "shortname", "description", "euroclass", "reversingdirection",
                "selfpropelled", "propulsiontype", "fueltype", "maximumrange",
                "transportmode", "lowfloor", "liftorramp", "hoist",
                "boardingheight", "gaptoplatform", "length", "width",
                "height", "weight", "firstaxleheight");
    }

    private Table vehicleTypeCapacities(ResourceFrame frame, Long timestamp) {
        Stream<String[]> rows = Stream.empty();

        if (frame.getVehicleTypes() != null) {
            rows = frame.getVehicleTypes().getVehicleType().parallelStream().flatMap(vt -> vt.getCapacities() != null
                    ? vt.getCapacities().getPassengerCapacityRefOrPassengerCapacity().stream()
                            .map(cap -> switch (cap) {
                                case VersionOfObjectRefStructure ref ->
                                    new String[] { timestamp.toString(), vt.getId(), refString(ref) };
                                case PassengerCapacity pc ->
                                    new String[] { timestamp.toString(), vt.getId(), pc.getId() };
                                default -> null;
                            })
                            .filter(Objects::nonNull)
                            .filter(h -> h[2] != null)
                    : Stream.empty());
        }

        return new MemoryTable("vehicletypecapacities", rows,
                "%valid_from", "$vehicletype_id", "$capacity_id");
    }

    private Table vehicles(ResourceFrame frame, Long timestamp) {
        Stream<String[]> rows = Stream.empty();

        if (frame.getVehicles() != null) {
            rows = frame.getVehicles().getVehicle().parallelStream().map(v -> new String[] {
                    timestamp.toString(),
                    v.getId(),
                    str(v.getVersion(), () -> "any"),
                    str(v.getValidBetween().getFromDate()),
                    str(v.getValidBetween().getToDate()),
                    v.getRegistrationNumber(),
                    v.getOperationalNumber(),
                    refString(v.getOperatorRef()),
                    refString(v.getVehicleTypeRef()),
            });
        }

        return new MemoryTable("vehicles", rows,
                "%valid_from", "$vehicle_id", "#version", "from_date", "to_date", "registration",
                "operationalnumber", "operator_id", "vehicletype_id");
    }

    @Override
    public Stream<Table> apply(ResourceFrame frame, Long timestamp) {
        return Stream.of(
                branding(frame, timestamp),
                organisations(frame, timestamp),
                vehicleTypes(frame, timestamp),
                vehicleTypeCapacities(frame, timestamp),
                passengerCapacities(frame, timestamp),
                vehicles(frame, timestamp));
    }
}

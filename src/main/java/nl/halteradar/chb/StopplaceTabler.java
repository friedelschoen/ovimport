package nl.halteradar.chb;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.xml.datatype.XMLGregorianCalendar;

import nl.bisonnl.chb.Quay;
import nl.bisonnl.chb.Stopplace;
import nl.halteradar.table.MemoryTable;
import nl.halteradar.table.Table;

class StopplaceTabler implements Function<List<Stopplace>, Stream<Table>> {
    private final QuayTabler quayTabler = new QuayTabler();

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

    private Table stopplacesTable(List<Stopplace> values) {
        return new MemoryTable(
                "stopplaces",
                values.stream().map(s -> new String[] {
                        date(s.getValidfrom()),
                        date(s.getMutationdate()),
                        str(s.getID()),
                        str(s.getStopplacecode()),
                        str(s.getStopplacetype()),
                        str(s.getPlacecode()),
                        str(s.getUiccode()),
                        str(s.getInternalname()),
                        str(s.getIconuri())
                }),
                "%valid_from",
                "#mutationdate",
                "$stopplace_id",
                "stopplace_code",
                "stopplace_type",
                "place_code",
                "uic_code",
                "internal_name",
                "icon_uri");
    }

    private Table stopplaceNames(List<Stopplace> values) {
        return new MemoryTable(
                "stopplace_names",
                values.stream()
                        .filter(s -> s.getStopplacename() != null)
                        .filter(s -> active(
                                s.getStopplacename().getValidfrom()))
                        .map(s -> {
                            var d = s.getStopplacename();

                            return new String[] {
                                    date(d.getValidfrom()),
                                    date(d.getMutationdate()),
                                    str(s.getID()),
                                    str(d.getPublicname()),
                                    str(d.getPublicnamemedium()),
                                    str(d.getPublicnamelong()),
                                    str(d.getTown()),
                                    str(d.getStreet()),
                                    str(d.getDescription()),
                                    str(d.getStopplaceindication())
                            };
                        }),
                "%valid_from",
                "#mutationdate",
                "$stopplace_id",
                "public_name",
                "public_name_medium",
                "public_name_long",
                "town",
                "street",
                "description",
                "stopplace_indication");
    }

    private Table stopplaceLocations(List<Stopplace> values) {
        return new MemoryTable(
                "stopplace_locations",
                values.stream()
                        .filter(s -> s.getStopplacelocation() != null)
                        .filter(s -> active(
                                s.getStopplacelocation().getValidfrom()))
                        .map(s -> {
                            var d = s.getStopplacelocation();

                            return new String[] {
                                    date(d.getValidfrom()),
                                    date(d.getMutationdate()),
                                    str(s.getID()),
                                    str(d.getLevel()),
                                    str(d.getRdX()),
                                    str(d.getRdY()),
                                    str(d.getRdZ()),
                                    str(d.getLocation())
                            };
                        }),
                "%valid_from",
                "#mutationdate",
                "$stopplace_id",
                "level",
                "rd_x",
                "rd_y",
                "rd_z",
                "location");
    }

    private Table stopplaceMunicipalities(List<Stopplace> values) {
        return new MemoryTable(
                "stopplace_municipalities",
                values.stream()
                        .filter(s -> s.getStopplacemunicipality() != null)
                        .filter(s -> active(
                                s.getStopplacemunicipality().getValidfrom()))
                        .map(s -> {
                            var d = s.getStopplacemunicipality();

                            return new String[] {
                                    date(d.getValidfrom()),
                                    date(d.getMutationdate()),
                                    str(s.getID()),
                                    str(d.getMunicipalitycode())
                            };
                        }),
                "%valid_from",
                "#mutationdate",
                "$stopplace_id",
                "municipality_code");
    }

    private Table stopplaceOwners(List<Stopplace> values) {
        return new MemoryTable(
                "stopplace_owners",
                values.stream()
                        .filter(s -> s.getStopplaceowner() != null)
                        .filter(s -> active(
                                s.getStopplaceowner().getValidfrom()))
                        .map(s -> {
                            var d = s.getStopplaceowner();

                            return new String[] {
                                    date(d.getValidfrom()),
                                    date(d.getMutationdate()),
                                    str(s.getID()),
                                    str(d.getStopplaceownercode())
                            };
                        }),
                "%valid_from",
                "#mutationdate",
                "$stopplace_id",
                "owner_code");
    }

    private Table stopplaceStatuses(List<Stopplace> values) {
        return new MemoryTable(
                "stopplace_statuses",
                values.stream()
                        .filter(s -> s.getStopplacestatusdata() != null)
                        .filter(s -> active(
                                s.getStopplacestatusdata().getValidfrom()))
                        .map(s -> {
                            var d = s.getStopplacestatusdata();

                            return new String[] {
                                    date(d.getValidfrom()),
                                    date(d.getMutationdate()),
                                    str(s.getID()),
                                    str(d.getStopplacestatus())
                            };
                        }),
                "%valid_from",
                "#mutationdate",
                "$stopplace_id",
                "status");
    }

    private Table stopplaceVisualAccessibility(List<Stopplace> values) {
        return new MemoryTable(
                "stopplace_visual_accessibility",
                values.stream()
                        .filter(s -> s.getStopplacevisualaccessibility() != null)
                        .filter(s -> active(
                                s.getStopplacevisualaccessibility()
                                        .getValidfrom()))
                        .map(s -> {
                            var d = s.getStopplacevisualaccessibility();

                            return new String[] {
                                    date(d.getValidfrom()),
                                    date(d.getMutationdate()),
                                    str(s.getID()),
                                    str(d.getVisuallyaccessible()),
                                    str(d.getVisuallyImpairedAccess())
                            };
                        }),
                "%valid_from",
                "#mutationdate",
                "$stopplace_id",
                "visually_accessible",
                "visually_impaired_access");
    }

    private Table stopplaceDisabledAccessibility(
            List<Stopplace> values) {

        return new MemoryTable(
                "stopplace_disabled_accessibility",
                values.stream()
                        .filter(s -> s.getStopplacedisabledaccessibility() != null)
                        .filter(s -> active(
                                s.getStopplacedisabledaccessibility()
                                        .getValidfrom()))
                        .map(s -> {
                            var d = s.getStopplacedisabledaccessibility();

                            return new String[] {
                                    date(d.getValidfrom()),
                                    date(d.getMutationdate()),
                                    str(s.getID()),
                                    str(d.getDisabledaccessible()),
                                    str(d.getStepFreeAccess()),
                                    str(d.getWheelchairAccess())
                            };
                        }),
                "%valid_from",
                "#mutationdate",
                "$stopplace_id",
                "disabled_accessible",
                "step_free_access",
                "wheelchair_access");
    }

    private Table stopplaceAccessibilityAdaptions(
            List<Stopplace> values) {

        return new MemoryTable(
                "stopplace_accessibility_adaptions",
                values.stream()
                        .filter(s -> s.getStopplaceaccessibilityadaptions() != null)
                        .filter(s -> active(
                                s.getStopplaceaccessibilityadaptions()
                                        .getValidfrom()))
                        .map(s -> {
                            var d = s.getStopplaceaccessibilityadaptions();

                            return new String[] {
                                    date(d.getValidfrom()),
                                    date(d.getMutationdate()),
                                    str(s.getID()),
                                    str(d.getHeightwithenvironment()),
                                    str(d.isEnvironmentaccessroute()),
                                    str(d.isGuidelineconnection()),
                                    str(d.isRamp()),
                                    str(d.getRamplength()),
                                    str(d.getRampwidth())
                            };
                        }),
                "%valid_from",
                "#mutationdate",
                "$stopplace_id",
                "height_with_environment",
                "environment_access_route",
                "guideline_connection",
                "ramp",
                "ramp_length",
                "ramp_width");
    }

    private Table stopplaceFacilities(List<Stopplace> values) {
        return new MemoryTable(
                "stopplace_facilities",
                values.stream()
                        .filter(s -> s.getStopplacefacilities() != null)
                        .filter(s -> active(
                                s.getStopplacefacilities()
                                        .getValidfrom()))
                        .map(s -> {
                            var d = s.getStopplacefacilities();

                            return new String[] {
                                    date(d.getValidfrom()),
                                    date(d.getMutationdate()),
                                    str(s.getID()),
                                    str(d.isTimetableinformation()),
                                    str(d.isPassengerinformationdisplay()),
                                    str(d.getPassengerinformationdisplaytype()),
                                    str(d.isEnvironmentinfo()),
                                    str(d.isBicycleparking()),
                                    str(d.getNumberofbicycleplaces()),
                                    str(d.isToiletfacility()),
                                    str(d.isPtbikerental()),
                                    str(d.isBins()),
                                    str(d.isOvccico()),
                                    str(d.isOvccharging())
                            };
                        }),
                "%valid_from",
                "#mutationdate",
                "$stopplace_id",
                "timetable_information",
                "passenger_information_display",
                "passenger_information_display_type",
                "environment_info",
                "bicycle_parking",
                "number_of_bicycle_places",
                "toilet_facility",
                "pt_bike_rental",
                "bins",
                "ovc_cico",
                "ovc_charging");
    }

    private Table stopplaceRemarks(List<Stopplace> values) {
        return new MemoryTable(
                "stopplace_remarks",
                values.stream()
                        .filter(s -> s.getStopplaceremarks() != null)
                        .filter(s -> active(
                                s.getStopplaceremarks().getValidfrom()))
                        .map(s -> {
                            var d = s.getStopplaceremarks();

                            return new String[] {
                                    date(d.getValidfrom()),
                                    date(d.getMutationdate()),
                                    str(s.getID()),
                                    str(d.getRemarks()),
                                    str(d.getRemarkstatus())
                            };
                        }),
                "%valid_from",
                "#mutationdate",
                "$stopplace_id",
                "remarks",
                "remark_status");
    }

    private Table stopplacePhotos(List<Stopplace> values) {
        return new MemoryTable(
                "stopplace_photos",
                values.stream()
                        .filter(s -> s.getStopplacephotos() != null)
                        .flatMap(s -> s.getStopplacephotos()
                                .getStopplacephotodata()
                                .stream()
                                .map(d -> new String[] {
                                        date(d.getMutationdate()),
                                        str(s.getID()),
                                        str(d.getStopplaceimageurl()),
                                        date(d.getStopplaceimagedate()),
                                        str(d.getStopplaceimagedescription())
                                })),
                "#mutationdate",
                "$stopplace_id",
                "$image_url",
                "image_date",
                "image_description");
    }

    /*
     * Quays
     */
    private Table quaysTable(List<Stopplace> stopplaces) {
        Stream<String[]> rows = stopplaces.stream()
                .filter(s -> s.getQuays() != null)
                .flatMap(s -> s.getQuays().getQuay().stream()
                        .filter(q -> active(q.getValidfrom()))
                        .map(q -> new String[] {
                                date(q.getValidfrom()),
                                date(q.getMutationdate()),
                                str(q.getID()),
                                str(s.getID()),
                                str(q.getQuaycode()),
                                str(q.getStopobjectcode()),
                                str(q.getStopinternalcode()),
                                str(q.getStopinternalname()),
                                str(q.getParentquaycode()),
                                str(q.isOnlygetout())
                        }));

        return new MemoryTable(
                "quays",
                rows,
                "%valid_from",
                "#mutationdate",
                "$quay_id",
                "stopplace_id",
                "quay_code",
                "stop_object_code",
                "stop_internal_code",
                "stop_internal_name",
                "parent_quay_code",
                "only_get_out");
    }

    private List<Quay> quays(List<Stopplace> stopplaces) {
        return stopplaces.stream()
                .filter(stop -> stop.getQuays() != null)
                .flatMap(stop -> stop.getQuays()
                        .getQuay()
                        .stream())
                .filter(quay -> active(quay.getValidfrom()))
                .toList();
    }

    @Override
    public Stream<Table> apply(List<Stopplace> stopplaces) {
        var quays = quays(stopplaces);

        return Stream.concat(quayTabler.apply(quays), Stream.of(
                stopplacesTable(stopplaces),
                stopplaceNames(stopplaces),
                stopplaceLocations(stopplaces),
                stopplaceMunicipalities(stopplaces),
                stopplaceOwners(stopplaces),
                stopplaceStatuses(stopplaces),
                stopplaceVisualAccessibility(stopplaces),
                stopplaceDisabledAccessibility(stopplaces),
                stopplaceAccessibilityAdaptions(stopplaces),
                stopplaceFacilities(stopplaces),
                stopplaceRemarks(stopplaces),
                stopplacePhotos(stopplaces),
                quaysTable(stopplaces)));
    }
}

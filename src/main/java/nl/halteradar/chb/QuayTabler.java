package nl.halteradar.chb;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.xml.datatype.XMLGregorianCalendar;

import nl.bisonnl.chb.Quay;
import nl.halteradar.table.MemoryTable;
import nl.halteradar.table.Table;

class QuayTabler implements Function<List<Quay>, Stream<Table>> {
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

    private Table quayTypes(List<Quay> values) {
        return new MemoryTable(
                "quay_types",
                values.stream()
                        .filter(q -> q.getQuaytypedata() != null)
                        .filter(q -> active(
                                q.getQuaytypedata().getValidfrom()))
                        .map(q -> {
                            var d = q.getQuaytypedata();

                            return new String[] {
                                    date(d.getValidfrom()),
                                    date(d.getMutationdate()),
                                    str(q.getID()),
                                    str(d.getQuaytype())
                            };
                        }),
                "%valid_from",
                "#mutationdate",
                "$quay_id",
                "quay_type");
    }

    private Table quayTransportModes(List<Quay> values) {
        return new MemoryTable(
                "quay_transport_modes",
                values.stream()
                        .filter(q -> q.getQuaytransportmodes() != null)
                        .flatMap(q -> q.getQuaytransportmodes()
                                .getTransportmodedata()
                                .stream()
                                .filter(d -> active(d.getValidfrom()))
                                .map(d -> new String[] {
                                        date(d.getValidfrom()),
                                        date(d.getMutationdate()),
                                        str(q.getID()),
                                        str(d.getTransportmode())
                                })),
                "%valid_from",
                "#mutationdate",
                "$quay_id",
                "$transport_mode");
    }

    private Table quayStatuses(List<Quay> values) {
        return new MemoryTable(
                "quay_statuses",
                values.stream()
                        .filter(q -> q.getQuaystatusdata() != null)
                        .filter(q -> active(
                                q.getQuaystatusdata().getValidfrom()))
                        .map(q -> {
                            var d = q.getQuaystatusdata();

                            return new String[] {
                                    date(d.getValidfrom()),
                                    date(d.getMutationdate()),
                                    str(q.getID()),
                                    str(d.getQuaystatus())
                            };
                        }),
                "%valid_from",
                "#mutationdate",
                "$quay_id",
                "status");
    }

    private Table quayLocations(List<Quay> values) {
        return new MemoryTable(
                "quay_locations",
                values.stream()
                        .filter(q -> q.getQuaylocationdata() != null)
                        .filter(q -> active(
                                q.getQuaylocationdata().getValidfrom()))
                        .map(q -> {
                            var d = q.getQuaylocationdata();

                            return new String[] {
                                    date(d.getValidfrom()),
                                    date(d.getMutationdate()),
                                    str(q.getID()),
                                    str(d.getRdX()),
                                    str(d.getRdY()),
                                    str(d.getRdZ()),
                                    str(d.getLevel()),
                                    str(d.getTown()),
                                    str(d.getStreet()),
                                    str(d.getLocation())
                            };
                        }),
                "%valid_from",
                "#mutationdate",
                "$quay_id",
                "rd_x",
                "rd_y",
                "rd_z",
                "level",
                "town",
                "street",
                "location");
    }

    private Table quayBearings(List<Quay> values) {
        return new MemoryTable(
                "quay_bearings",
                values.stream()
                        .filter(q -> q.getQuaybearing() != null)
                        .filter(q -> active(
                                q.getQuaybearing().getValidfrom()))
                        .map(q -> {
                            var d = q.getQuaybearing();

                            return new String[] {
                                    date(d.getValidfrom()),
                                    date(d.getMutationdate()),
                                    str(q.getID()),
                                    str(d.getCompassdirection())
                            };
                        }),
                "%valid_from",
                "#mutationdate",
                "$quay_id",
                "compass_direction");
    }

    private Table quayVisualAccessibility(List<Quay> values) {
        return new MemoryTable(
                "quay_visual_accessibility",
                values.stream()
                        .filter(q -> q.getQuayvisuallyaccessible() != null)
                        .filter(q -> active(
                                q.getQuayvisuallyaccessible()
                                        .getValidfrom()))
                        .map(q -> {
                            var d = q.getQuayvisuallyaccessible();

                            return new String[] {
                                    date(d.getValidfrom()),
                                    date(d.getMutationdate()),
                                    str(q.getID()),
                                    str(d.getVisuallyaccessible()),
                                    str(d.getVisuallyImpairedAccess())
                            };
                        }),
                "%valid_from",
                "#mutationdate",
                "$quay_id",
                "visually_accessible",
                "visually_impaired_access");
    }

    private Table quayDisabledAccessibility(List<Quay> values) {
        return new MemoryTable(
                "quay_disabled_accessibility",
                values.stream()
                        .flatMap(q -> q.getQuaydisabledaccessible()
                                .stream()
                                .filter(d -> active(d.getValidfrom()))
                                .map(d -> new String[] {
                                        date(d.getValidfrom()),
                                        date(d.getMutationdate()),
                                        str(q.getID()),
                                        str(d.getTransportmode()),
                                        str(d.getDisabledaccessible()),
                                        str(d.getStepFreeAccess()),
                                        str(d.getWheelchairAccess())
                                })),
                "%valid_from",
                "#mutationdate",
                "$quay_id",
                "$transport_mode",
                "disabled_accessible",
                "step_free_access",
                "wheelchair_access");
    }

    private Table quayMunicipalities(List<Quay> values) {
        return new MemoryTable(
                "quay_municipalities",
                values.stream()
                        .filter(q -> q.getQuaymunicipality() != null)
                        .filter(q -> active(
                                q.getQuaymunicipality().getValidfrom()))
                        .map(q -> {
                            var d = q.getQuaymunicipality();

                            return new String[] {
                                    date(d.getValidfrom()),
                                    date(d.getMutationdate()),
                                    str(q.getID()),
                                    str(d.getMunicipalitycode())
                            };
                        }),
                "%valid_from",
                "#mutationdate",
                "$quay_id",
                "municipality_code");
    }

    private Table quayOwners(List<Quay> values) {
        return new MemoryTable(
                "quay_owners",
                values.stream()
                        .filter(q -> q.getQuayowner() != null)
                        .filter(q -> active(q.getQuayowner().getValidfrom()))
                        .map(q -> {
                            var d = q.getQuayowner();

                            return new String[] {
                                    date(d.getValidfrom()),
                                    date(d.getMutationdate()),
                                    str(q.getID()),
                                    str(d.getQuayownercode())
                            };
                        }),
                "%valid_from",
                "#mutationdate",
                "$quay_id",
                "owner_code");
    }

    private Table quayConcessionProviders(List<Quay> values) {
        return new MemoryTable(
                "quay_concession_providers",
                values.stream()
                        .filter(q -> q.getQuayconcessionprovider() != null)
                        .filter(q -> active(
                                q.getQuayconcessionprovider()
                                        .getValidfrom()))
                        .map(q -> {
                            var d = q.getQuayconcessionprovider();

                            return new String[] {
                                    date(d.getValidfrom()),
                                    date(d.getMutationdate()),
                                    str(q.getID()),
                                    str(d.getConcessionprovidercode())
                            };
                        }),
                "%valid_from",
                "#mutationdate",
                "$quay_id",
                "concession_provider_code");
    }

    private Table quayNames(List<Quay> values) {
        return new MemoryTable(
                "quay_names",
                values.stream()
                        .filter(q -> q.getQuaynamedata() != null)
                        .filter(q -> active(
                                q.getQuaynamedata().getValidfrom()))
                        .map(q -> {
                            var d = q.getQuaynamedata();

                            return new String[] {
                                    date(d.getValidfrom()),
                                    date(d.getMutationdate()),
                                    str(q.getID()),
                                    str(d.getQuayname()),
                                    str(d.getStopsidecode()),
                                    str(d.getIconuri())
                            };
                        }),
                "%valid_from",
                "#mutationdate",
                "$quay_id",
                "quay_name",
                "stop_side_code",
                "icon_uri");
    }

    private Table quayAccessibilityAdaptions(List<Quay> values) {
        return new MemoryTable(
                "quay_accessibility_adaptions",
                values.stream()
                        .filter(q -> q.getQuayaccessibilityadaptions() != null)
                        .filter(q -> active(
                                q.getQuayaccessibilityadaptions()
                                        .getValidfrom()))
                        .map(q -> {
                            var d = q.getQuayaccessibilityadaptions();

                            return new String[] {
                                    date(d.getValidfrom()),
                                    date(d.getMutationdate()),
                                    str(q.getID()),
                                    str(d.getQuayshapetype()),
                                    str(d.getBaylength()),
                                    str(d.isMarkedkerb()),
                                    str(d.isLift()),
                                    str(d.isGuidelines()),
                                    str(d.isGroundsurfaceindicator()),
                                    str(d.isStopplaceaccessroute()),
                                    str(d.getEmbaymentwidth()),
                                    str(d.getBayentranceangles()),
                                    str(d.getBayexitangles()),
                                    str(d.getKerbheight()),
                                    str(d.getBoardingpositionwidth()),
                                    str(d.getAlightingpositionwidth()),
                                    str(d.getLiftedpartlength()),
                                    str(d.getNarrowestpassagewidth()),
                                    str(d.isFulllengthguideline()),
                                    str(d.isGuidelinestopplaceconnection()),
                                    str(d.isTactilegroundsurfaceindicator()),
                                    str(d.isRamp()),
                                    str(d.getRamplength()),
                                    str(d.getHeightwithenvironment()),
                                    str(d.getRampwidth())
                            };
                        }),
                "%valid_from",
                "#mutationdate",
                "$quay_id",
                "quay_shape_type",
                "bay_length",
                "marked_kerb",
                "lift",
                "guidelines",
                "ground_surface_indicator",
                "stopplace_access_route",
                "embayment_width",
                "bay_entrance_angles",
                "bay_exit_angles",
                "kerb_height",
                "boarding_position_width",
                "alighting_position_width",
                "lifted_part_length",
                "narrowest_passage_width",
                "full_length_guideline",
                "guideline_stopplace_connection",
                "tactile_ground_surface_indicator",
                "ramp",
                "ramp_length",
                "height_with_environment",
                "ramp_width");
    }

    private Table quayFacilities(List<Quay> values) {
        return new MemoryTable(
                "quay_facilities",
                values.stream()
                        .filter(q -> q.getQuayfacilities() != null)
                        .filter(q -> active(
                                q.getQuayfacilities().getValidfrom()))
                        .map(q -> {
                            var d = q.getQuayfacilities();

                            return new String[] {
                                    date(d.getValidfrom()),
                                    date(d.getMutationdate()),
                                    str(q.getID()),
                                    str(d.isStopsign()),
                                    str(d.getStopsigntype()),
                                    str(d.isShelter()),
                                    str(d.isShelterpublicity()),
                                    str(d.isIlluminatedstop()),
                                    str(d.isSeatavailable()),
                                    str(d.isLeantosupport()),
                                    str(d.isTimetableinformation()),
                                    str(d.isInfounit()),
                                    str(d.isRoutenetworkmap()),
                                    str(d.isPassengerinformationdisplay()),
                                    str(d.getPassengerinformationdisplaytype()),
                                    str(d.isAudiobutton()),
                                    str(d.isBicycleparking()),
                                    str(d.getNumberofbicycleplaces()),
                                    str(d.isBins()),
                                    str(d.isOvccico()),
                                    str(d.isOvccharging())
                            };
                        }),
                "%valid_from",
                "#mutationdate",
                "$quay_id",
                "stop_sign",
                "stop_sign_type",
                "shelter",
                "shelter_publicity",
                "illuminated_stop",
                "seat_available",
                "lean_to_support",
                "timetable_information",
                "info_unit",
                "route_network_map",
                "passenger_information_display",
                "passenger_information_display_type",
                "audio_button",
                "bicycle_parking",
                "number_of_bicycle_places",
                "bins",
                "ovc_cico",
                "ovc_charging");
    }

    private Table quayExtraAttributes(List<Quay> values) {
        return new MemoryTable(
                "quay_extra_attributes",
                values.stream()
                        .filter(q -> q.getQuayextraattributes() != null)
                        .filter(q -> active(
                                q.getQuayextraattributes()
                                        .getValidfrom()))
                        .map(q -> {
                            var d = q.getQuayextraattributes();

                            return new String[] {
                                    date(d.getValidfrom()),
                                    date(d.getMutationdate()),
                                    str(q.getID()),
                                    str(d.getRoadcode()),
                                    str(d.getHectometersign()),
                                    str(d.isGreenstop()),
                                    str(d.isLiftedbicyclepath())
                            };
                        }),
                "%valid_from",
                "#mutationdate",
                "$quay_id",
                "road_code",
                "hectometer_sign",
                "green_stop",
                "lifted_bicycle_path");
    }

    private Table quayRemarks(List<Quay> values) {
        return new MemoryTable(
                "quay_remarks",
                values.stream()
                        .filter(q -> q.getQuayremarks() != null)
                        .filter(q -> active(q.getQuayremarks().getValidfrom()))
                        .map(q -> {
                            var d = q.getQuayremarks();

                            return new String[] {
                                    date(d.getValidfrom()),
                                    date(d.getMutationdate()),
                                    str(q.getID()),
                                    str(d.getRemarks()),
                                    str(d.getRemarkstatus())
                            };
                        }),
                "%valid_from",
                "#mutationdate",
                "$quay_id",
                "remarks",
                "remark_status");
    }

    private Table quayPhotos(List<Quay> values) {
        return new MemoryTable(
                "quay_photos",
                values.stream()
                        .filter(q -> q.getQuayphotos() != null)
                        .flatMap(q -> q.getQuayphotos()
                                .getQuayphotodata()
                                .stream()
                                .map(d -> new String[] {
                                        date(d.getMutationdate()),
                                        str(q.getID()),
                                        str(d.getQuayimageurl()),
                                        date(d.getQuayimagedate()),
                                        str(d.getQuayimagedescription())
                                })),
                "#mutationdate",
                "$quay_id",
                "$image_url",
                "image_date",
                "image_description");
    }

    @Override
    public Stream<Table> apply(List<Quay> quays) {
        return Stream.of(
                quayTypes(quays),
                quayTransportModes(quays),
                quayStatuses(quays),
                quayLocations(quays),
                quayBearings(quays),
                quayVisualAccessibility(quays),
                quayDisabledAccessibility(quays),
                quayMunicipalities(quays),
                quayOwners(quays),
                quayConcessionProviders(quays),
                quayNames(quays),
                quayAccessibilityAdaptions(quays),
                quayFacilities(quays),
                quayExtraAttributes(quays),
                quayRemarks(quays),
                quayPhotos(quays));
    }
}

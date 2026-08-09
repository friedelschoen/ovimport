CREATE SCHEMA IF NOT EXISTS :schema;
SET search_path to :schema, public;

-- HalteRadar static timetable schema

CREATE TABLE IF NOT EXISTS availabilityconditions (
    availabilitycondition_id TEXT PRIMARY KEY,
    name TEXT,
    from_date TIMESTAMP,
    to_date TIMESTAMP,
    validdays TEXT,
    availability TEXT
);

CREATE TABLE IF NOT EXISTS daytypes (
    daytype_id TEXT PRIMARY KEY,
    name TEXT,
    shortname TEXT,
    daysofweek TEXT,
    weeksofmonth TEXT,
    dayofyear TEXT,
    holidaytypes TEXT,
    seasons TEXT,
    tides TEXT,
    dayevent TEXT,
    crowding TEXT
);

CREATE TABLE IF NOT EXISTS daytypeassignments (
    daytypeassignment_id TEXT PRIMARY KEY,
    date DATE NOT NULL,
    daytype_id TEXT NOT NULL
);
CREATE INDEX IF NOT EXISTS daytypeassignments_daytype_idx ON daytypeassignments (daytype_id);
CREATE INDEX IF NOT EXISTS daytypeassignments_date_idx ON daytypeassignments (date);

CREATE TABLE IF NOT EXISTS serviceclasses (
    serviceclass_id TEXT PRIMARY KEY,
    type TEXT,
    name TEXT,
    description TEXT,
    image TEXT,
    url TEXT,
    color TEXT,
    textcolor TEXT
);

CREATE TABLE IF NOT EXISTS organisations (
    organisation_id TEXT PRIMARY KEY,
    type TEXT,
    name TEXT,
    shortname TEXT,
    description TEXT
);

CREATE TABLE IF NOT EXISTS passengercapacities (
    passengercapacity_id TEXT PRIMARY KEY,
    fareclass TEXT,
    total INTEGER,
    seating INTEGER,
    standing INTEGER,
    specialplace INTEGER,
    pushchair INTEGER,
    wheelchair INTEGER
);

CREATE TABLE IF NOT EXISTS vehicletypes (
    vehicletype_id TEXT PRIMARY KEY,
    vehicletypecode TEXT,
    branding_id TEXT,
    name TEXT,
    shortname TEXT,
    description TEXT,
    euroclass TEXT,
    reversingdirection TEXT,
    selfpropelled TEXT,
    propulsiontype TEXT,
    fueltype TEXT,
    maximumrange TEXT,
    transportmode TEXT,
    lowfloor TEXT,
    liftorramp TEXT,
    hoist TEXT,
    boardingheight NUMERIC,
    gaptoplatform NUMERIC,
    length NUMERIC,
    width NUMERIC,
    height NUMERIC,
    weight NUMERIC,
    firstaxleheight NUMERIC
);
CREATE INDEX IF NOT EXISTS vehicletypes_branding_idx ON vehicletypes (branding_id);

CREATE TABLE IF NOT EXISTS vehicletypecapacities (
    vehicletype_id TEXT NOT NULL,
    passengercapacity_id TEXT NOT NULL,
    PRIMARY KEY (vehicletype_id, passengercapacity_id)
);
CREATE INDEX IF NOT EXISTS vehicletypecapacities_capacity_idx ON vehicletypecapacities (passengercapacity_id);

CREATE TABLE IF NOT EXISTS vehicles (
    vehicle_id TEXT PRIMARY KEY,
    from_date TIMESTAMP,
    to_date TIMESTAMP,
    registration TEXT,
    operationalnumber TEXT,
    operator_id TEXT,
    vehicletype_id TEXT
);
CREATE INDEX IF NOT EXISTS vehicles_operationalnumber_idx ON vehicles (operationalnumber);
CREATE INDEX IF NOT EXISTS vehicles_operator_idx ON vehicles (operator_id);
CREATE INDEX IF NOT EXISTS vehicles_type_idx ON vehicles (vehicletype_id);

CREATE TABLE IF NOT EXISTS lines (
    line_id TEXT PRIMARY KEY,
    lineplanningnumber TEXT,
    branding_id TEXT,
    name TEXT,
    shortname TEXT,
    description TEXT,
    mode TEXT,
    url TEXT,
    publiccode TEXT,
    authority_id TEXT,
    operator_id TEXT,
    label_id TEXT,
    monitored TEXT,
    color TEXT,
    textcolor TEXT
);
CREATE INDEX IF NOT EXISTS lines_planningnumber_idx ON lines (lineplanningnumber);
CREATE INDEX IF NOT EXISTS lines_publiccode_idx ON lines (publiccode);
CREATE INDEX IF NOT EXISTS lines_operator_idx ON lines (operator_id);

CREATE TABLE IF NOT EXISTS routepoints (
    routepoint_id TEXT PRIMARY KEY,
    point TEXT
);

CREATE TABLE IF NOT EXISTS routelinks (
    routelink_id TEXT PRIMARY KEY,
    from_routepoint_id TEXT,
    to_routepoint_id TEXT,
    distance NUMERIC,
    linestring TEXT,
    operationalcontext_id TEXT,
    responsibilityset_id TEXT
);
CREATE INDEX IF NOT EXISTS routelinks_from_idx ON routelinks (from_routepoint_id);
CREATE INDEX IF NOT EXISTS routelinks_to_idx ON routelinks (to_routepoint_id);

CREATE TABLE IF NOT EXISTS routes (
    route_id TEXT NOT NULL,
    line_id TEXT,
    name TEXT,
    direction TEXT,
    point_id TEXT NOT NULL,
    point_order INTEGER,
    routepoint_id TEXT,
    onward_routelink_id TEXT,
    PRIMARY KEY (route_id, point_id)
);
CREATE INDEX IF NOT EXISTS routes_line_idx ON routes (line_id);
CREATE INDEX IF NOT EXISTS routes_route_order_idx ON routes (route_id, point_order);
CREATE INDEX IF NOT EXISTS routes_routepoint_idx ON routes (routepoint_id);

CREATE TABLE IF NOT EXISTS destinationdisplays (
    destinationdisplay_id TEXT PRIMARY KEY,
    destinationcode TEXT,
    name TEXT,
    sidetext TEXT,
    fronttext TEXT,
    color TEXT,
    textcolor TEXT,
    vias TEXT
);
CREATE INDEX IF NOT EXISTS destinationdisplays_code_idx ON destinationdisplays (destinationcode);

CREATE TABLE IF NOT EXISTS destinationdisplayvariants (
    destinationdisplay_id TEXT NOT NULL,
    mediatype TEXT NOT NULL,
    length TEXT NOT NULL,
    name TEXT,
    PRIMARY KEY (destinationdisplay_id, mediatype, length)
);

CREATE TABLE IF NOT EXISTS stopareas (
    stoparea_id TEXT PRIMARY KEY,
    userstopareacode TEXT,
    name TEXT,
    publiccode TEXT,
    place TEXT
);
CREATE INDEX IF NOT EXISTS stopareas_usercode_idx ON stopareas (userstopareacode);

CREATE TABLE IF NOT EXISTS scheduledstoppoints (
    scheduledstoppoint_id TEXT PRIMARY KEY,
    userstopcode TEXT,
    name TEXT,
    point TEXT,
    projected_routepoint_id TEXT,
    stoparea_id TEXT,
    alighting TEXT,
    boarding TEXT,
    place TEXT
);
CREATE INDEX IF NOT EXISTS scheduledstoppoints_userstopcode_idx ON scheduledstoppoints (userstopcode);
CREATE INDEX IF NOT EXISTS scheduledstoppoints_stoparea_idx ON scheduledstoppoints (stoparea_id);
CREATE INDEX IF NOT EXISTS scheduledstoppoints_routepoint_idx ON scheduledstoppoints (projected_routepoint_id);

CREATE TABLE IF NOT EXISTS scheduledstoppointtariffzones (
    scheduledstoppoint_id TEXT NOT NULL,
    tariffzone TEXT NOT NULL,
    PRIMARY KEY (scheduledstoppoint_id, tariffzone)
);

CREATE TABLE IF NOT EXISTS stopassignments (
    stopassignment_id TEXT PRIMARY KEY,
    scheduledstoppoint_id TEXT,
    quay_id TEXT,
    stopplace_id TEXT
);
CREATE INDEX IF NOT EXISTS stopassignments_stoppoint_idx ON stopassignments (scheduledstoppoint_id);
CREATE INDEX IF NOT EXISTS stopassignments_quay_idx ON stopassignments (quay_id);
CREATE INDEX IF NOT EXISTS stopassignments_stopplace_idx ON stopassignments (stopplace_id);

CREATE TABLE IF NOT EXISTS timingpoints (
    timingpoint_id TEXT PRIMARY KEY,
    name TEXT,
    point TEXT,
    projected_routepoint_id TEXT
);

CREATE TABLE IF NOT EXISTS timinglinks (
    timinglink_id TEXT PRIMARY KEY,
    frompoint_id TEXT,
    topoint_id TEXT,
    distance NUMERIC,
    operationalcontext_id TEXT
);
CREATE INDEX IF NOT EXISTS timinglinks_from_idx ON timinglinks (frompoint_id);
CREATE INDEX IF NOT EXISTS timinglinks_to_idx ON timinglinks (topoint_id);

CREATE TABLE IF NOT EXISTS journeypatterns (
    journeypattern_id TEXT NOT NULL,
    name TEXT,
    route_id TEXT,
    direction TEXT,
    destinationdisplay_id TEXT,
    point_id TEXT NOT NULL,
    point_order INTEGER,
    scheduledstoppoint_id TEXT,
    timingpoint_id TEXT,
    onward_timinglink_id TEXT,
    is_waitpoint TEXT,
    PRIMARY KEY (journeypattern_id, point_id)
);
CREATE INDEX IF NOT EXISTS journeypatterns_route_idx ON journeypatterns (route_id);
CREATE INDEX IF NOT EXISTS journeypatterns_order_idx ON journeypatterns (journeypattern_id, point_order);
CREATE INDEX IF NOT EXISTS journeypatterns_stoppoint_idx ON journeypatterns (scheduledstoppoint_id);
CREATE INDEX IF NOT EXISTS journeypatterns_timingpoint_idx ON journeypatterns (timingpoint_id);

CREATE TABLE IF NOT EXISTS timedemandtypes (
    timedemandtype_id TEXT NOT NULL,
    type TEXT,
    entry_id TEXT,
    timinglink_id TEXT,
    scheduledstoppoint_id TEXT,
    timingpoint_id TEXT,
    duration TEXT,
    PRIMARY KEY (timedemandtype_id)
);
CREATE INDEX IF NOT EXISTS timedemandtypes_timinglink_idx ON timedemandtypes (timinglink_id);

CREATE TABLE IF NOT EXISTS notices (
    notice_id TEXT PRIMARY KEY,
    name TEXT,
    text TEXT
);

CREATE TABLE IF NOT EXISTS noticeassignments (
    assignment_id TEXT PRIMARY KEY,
    notice_id TEXT,
    object_id TEXT,
    view_index INTEGER
);
CREATE INDEX IF NOT EXISTS noticeassignments_notice_idx ON noticeassignments (notice_id);
CREATE INDEX IF NOT EXISTS noticeassignments_object_idx ON noticeassignments (object_id);

CREATE TABLE IF NOT EXISTS vehiclejourneys (
    vehiclejourney_id TEXT PRIMARY KEY,
    type TEXT,
    derived_from TEXT,
    journeynumber TEXT,
    departuretime TIME,
    departuredayoffset INTEGER,
    journeypattern_id TEXT,
    timedemandtype_id TEXT,
    vehicletype_id TEXT,
    operator_id TEXT,
    dynamic TEXT
);
CREATE INDEX IF NOT EXISTS vehiclejourneys_pattern_idx ON vehiclejourneys (journeypattern_id);
CREATE INDEX IF NOT EXISTS vehiclejourneys_timedemand_idx ON vehiclejourneys (timedemandtype_id);
CREATE INDEX IF NOT EXISTS vehiclejourneys_number_idx ON vehiclejourneys (journeynumber);

CREATE TABLE IF NOT EXISTS vehiclejourneyconditions (
    vehiclejourney_id TEXT NOT NULL,
    availabilitycondition_id TEXT NOT NULL,
    PRIMARY KEY (vehiclejourney_id, availabilitycondition_id)
);
CREATE INDEX IF NOT EXISTS vehiclejourneyconditions_condition_idx ON vehiclejourneyconditions (availabilitycondition_id);

CREATE TABLE IF NOT EXISTS blocks (
    block_id TEXT PRIMARY KEY,
    blockcode TEXT,
    name TEXT,
    description TEXT,
    preparationduration TEXT,
    starttime TIME,
    starttimedayoffset INTEGER,
    finishingduration TEXT,
    endtime TIME,
    endtimedayoffset INTEGER,
    startpoint_id TEXT,
    endpoint_id TEXT
);
CREATE INDEX IF NOT EXISTS blocks_code_idx ON blocks (blockcode);

CREATE TABLE IF NOT EXISTS blockvaliditycondition (
    block_id TEXT NOT NULL,
    availabilitycondition_id TEXT NOT NULL,
    PRIMARY KEY (block_id, availabilitycondition_id)
);

CREATE TABLE IF NOT EXISTS blockdaytypes (
    block_id TEXT NOT NULL,
    daytype_id TEXT NOT NULL,
    PRIMARY KEY (block_id, daytype_id)
);

CREATE TABLE IF NOT EXISTS blockjourneys (
    block_id TEXT NOT NULL,
    vehiclejourney_id TEXT NOT NULL,
    PRIMARY KEY (block_id, vehiclejourney_id)
);
CREATE INDEX IF NOT EXISTS blockjourneys_journey_idx ON blockjourneys (vehiclejourney_id);

-- CHB
CREATE TABLE IF NOT EXISTS dataowners (
    dataowner_code TEXT PRIMARY KEY,
    name TEXT,
    type TEXT
);

CREATE TABLE IF NOT EXISTS places (
    place_id TEXT PRIMARY KEY,
    place_code TEXT,
    dataowner_code TEXT,
    public_name TEXT,
    town TEXT,
    icon_uri TEXT,
    description TEXT
);
CREATE INDEX IF NOT EXISTS places_code_idx ON places (place_code);
CREATE INDEX IF NOT EXISTS places_owner_idx ON places (dataowner_code);

CREATE TABLE IF NOT EXISTS stopplaces (
    stopplace_id TEXT PRIMARY KEY,
    stopplace_code TEXT,
    stopplace_type TEXT,
    place_code TEXT,
    uic_code TEXT,
    internal_name TEXT,
    icon_uri TEXT
);
CREATE INDEX IF NOT EXISTS stopplaces_code_idx ON stopplaces (stopplace_code);
CREATE INDEX IF NOT EXISTS stopplaces_place_idx ON stopplaces (place_code);
CREATE INDEX IF NOT EXISTS stopplaces_uic_idx ON stopplaces (uic_code);

CREATE TABLE IF NOT EXISTS stopplace_names (
    stopplace_id TEXT PRIMARY KEY,
    public_name TEXT,
    public_name_medium TEXT,
    public_name_long TEXT,
    town TEXT,
    street TEXT,
    description TEXT,
    stopplace_indication TEXT
);

CREATE TABLE IF NOT EXISTS stopplace_locations (
    stopplace_id TEXT PRIMARY KEY,
    level TEXT,
    rd_x NUMERIC,
    rd_y NUMERIC,
    rd_z NUMERIC,
    location TEXT
);

CREATE TABLE IF NOT EXISTS stopplace_municipalities (
    stopplace_id TEXT PRIMARY KEY,
    municipality_code TEXT
);

CREATE TABLE IF NOT EXISTS stopplace_owners (
    stopplace_id TEXT PRIMARY KEY,
    owner_code TEXT
);

CREATE TABLE IF NOT EXISTS stopplace_statuses (
    stopplace_id TEXT PRIMARY KEY,
    status TEXT
);

CREATE TABLE IF NOT EXISTS stopplace_visual_accessibility (
    stopplace_id TEXT PRIMARY KEY,
    visually_accessible TEXT,
    visually_impaired_access TEXT
);

CREATE TABLE IF NOT EXISTS stopplace_disabled_accessibility (
    stopplace_id TEXT PRIMARY KEY,
    disabled_accessible TEXT,
    step_free_access TEXT,
    wheelchair_access TEXT
);

CREATE TABLE IF NOT EXISTS stopplace_accessibility_adaptions (
    stopplace_id TEXT PRIMARY KEY,
    height_with_environment NUMERIC,
    environment_access_route TEXT,
    guideline_connection TEXT,
    ramp TEXT,
    ramp_length NUMERIC,
    ramp_width NUMERIC
);

CREATE TABLE IF NOT EXISTS stopplace_facilities (
    stopplace_id TEXT PRIMARY KEY,
    timetable_information TEXT,
    passenger_information_display TEXT,
    passenger_information_display_type TEXT,
    environment_info TEXT,
    bicycle_parking TEXT,
    number_of_bicycle_places INTEGER,
    toilet_facility TEXT,
    pt_bike_rental TEXT,
    bins TEXT,
    ovc_cico TEXT,
    ovc_charging TEXT
);

CREATE TABLE IF NOT EXISTS stopplace_remarks (
    stopplace_id TEXT PRIMARY KEY,
    remarks TEXT,
    remark_status TEXT
);

CREATE TABLE IF NOT EXISTS stopplace_photos (
    stopplace_id TEXT NOT NULL,
    image_url TEXT NOT NULL,
    image_date TIMESTAMP,
    image_description TEXT,
    PRIMARY KEY (stopplace_id, image_url)
);

CREATE TABLE IF NOT EXISTS quays (
    quay_id TEXT PRIMARY KEY,
    stopplace_id TEXT,
    quay_code TEXT,
    stop_object_code TEXT,
    stop_internal_code TEXT,
    stop_internal_name TEXT,
    parent_quay_code TEXT,
    only_get_out TEXT
);
CREATE INDEX IF NOT EXISTS quays_stopplace_idx ON quays (stopplace_id);
CREATE INDEX IF NOT EXISTS quays_code_idx ON quays (quay_code);
CREATE INDEX IF NOT EXISTS quays_parent_idx ON quays (parent_quay_code);

CREATE TABLE IF NOT EXISTS quay_types (
    quay_id TEXT PRIMARY KEY,
    quay_type TEXT
);

CREATE TABLE IF NOT EXISTS quay_transport_modes (
    quay_id TEXT NOT NULL,
    transport_mode TEXT NOT NULL,
    PRIMARY KEY (quay_id, transport_mode)
);

CREATE TABLE IF NOT EXISTS quay_statuses (
    quay_id TEXT PRIMARY KEY,
    status TEXT
);

CREATE TABLE IF NOT EXISTS quay_locations (
    quay_id TEXT PRIMARY KEY,
    rd_x NUMERIC,
    rd_y NUMERIC,
    rd_z NUMERIC,
    level TEXT,
    town TEXT,
    street TEXT,
    location TEXT
);

CREATE TABLE IF NOT EXISTS quay_bearings (
    quay_id TEXT PRIMARY KEY,
    compass_direction NUMERIC
);

CREATE TABLE IF NOT EXISTS quay_visual_accessibility (
    quay_id TEXT PRIMARY KEY,
    visually_accessible TEXT,
    visually_impaired_access TEXT
);

CREATE TABLE IF NOT EXISTS quay_disabled_accessibility (
    quay_id TEXT NOT NULL,
    transport_mode TEXT NOT NULL,
    disabled_accessible TEXT,
    step_free_access TEXT,
    wheelchair_access TEXT,
    PRIMARY KEY (quay_id, transport_mode)
);

CREATE TABLE IF NOT EXISTS quay_municipalities (
    quay_id TEXT PRIMARY KEY,
    municipality_code TEXT
);

CREATE TABLE IF NOT EXISTS quay_owners (
    quay_id TEXT PRIMARY KEY,
    owner_code TEXT
);

CREATE TABLE IF NOT EXISTS quay_concession_providers (
    quay_id TEXT PRIMARY KEY,
    concession_provider_code TEXT
);

CREATE TABLE IF NOT EXISTS quay_names (
    quay_id TEXT PRIMARY KEY,
    quay_name TEXT,
    stop_side_code TEXT,
    icon_uri TEXT
);

CREATE TABLE IF NOT EXISTS quay_accessibility_adaptions (
    quay_id TEXT PRIMARY KEY,
    quay_shape_type TEXT,
    bay_length NUMERIC,
    marked_kerb TEXT,
    lift TEXT,
    guidelines TEXT,
    ground_surface_indicator TEXT,
    stopplace_access_route TEXT,
    embayment_width NUMERIC,
    bay_entrance_angles NUMERIC,
    bay_exit_angles NUMERIC,
    kerb_height NUMERIC,
    boarding_position_width NUMERIC,
    alighting_position_width NUMERIC,
    lifted_part_length NUMERIC,
    narrowest_passage_width NUMERIC,
    full_length_guideline TEXT,
    guideline_stopplace_connection TEXT,
    tactile_ground_surface_indicator TEXT,
    ramp TEXT,
    ramp_length NUMERIC,
    height_with_environment NUMERIC,
    ramp_width NUMERIC
);

CREATE TABLE IF NOT EXISTS quay_facilities (
    quay_id TEXT PRIMARY KEY,
    stop_sign TEXT,
    stop_sign_type TEXT,
    shelter TEXT,
    shelter_publicity TEXT,
    illuminated_stop TEXT,
    seat_available TEXT,
    lean_to_support TEXT,
    timetable_information TEXT,
    info_unit TEXT,
    route_network_map TEXT,
    passenger_information_display TEXT,
    passenger_information_display_type TEXT,
    audio_button TEXT,
    bicycle_parking TEXT,
    number_of_bicycle_places INTEGER,
    bins TEXT,
    ovc_cico TEXT,
    ovc_charging TEXT
);

CREATE TABLE IF NOT EXISTS quay_extra_attributes (
    quay_id TEXT PRIMARY KEY,
    road_code TEXT,
    hectometer_sign TEXT,
    green_stop TEXT,
    lifted_bicycle_path TEXT
);

CREATE TABLE IF NOT EXISTS quay_remarks (
    quay_id TEXT PRIMARY KEY,
    remarks TEXT,
    remark_status TEXT
);

CREATE TABLE IF NOT EXISTS quay_photos (
    quay_id TEXT NOT NULL,
    image_url TEXT NOT NULL,
    image_date TIMESTAMP,
    image_description TEXT,
    PRIMARY KEY (quay_id, image_url)
);



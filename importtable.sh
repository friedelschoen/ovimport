#!/bin/bash

TABLES=(
    daytypes
    serviceclasses
    stopplace_municipalities
    stopplace_locations
    quay_owners
    scheduledstoppoints
    destinationdisplays
    lines
    stopplace_disabled_accessibility
    quay_types
    quays
    stopplace_remarks
    stopplace_accessibility_adaptions
    daytypeassignments
    quay_names
    quay_bearings
    vehicletypes
    noticeassignments
    dataowners
    quay_remarks
    quay_accessibility_adaptions
    blocks
    vehiclejourneyconditions
    routes
    stopareas
    stopassignments
    stopplace_photos
    quay_photos
    quay_disabled_accessibility
    quay_municipalities
    passengercapacities
    routelinks
    blockjourneys
    availabilityconditions
    stopplace_visual_accessibility
    quay_transport_modes
    quay_visual_accessibility
    stopplaces
    quay_facilities
    organisations
    vehicles
    vehicletypecapacities
    routepoints
    quay_statuses
    blockdaytypes
    quay_locations
    vehiclejourneys
    timedemandtypes
    stopplace_owners
    quay_extra_attributes
    notices
    blockvaliditycondition
    timingpoints
    stopplace_statuses
    journeypatterns
    scheduledstoppointtariffzones
    quay_concession_providers
    places
    stopplace_facilities
    timinglinks
    destinationdisplayvariants
    stopplace_names
)

SCHEMA=netex

INPUT=$1

load_table() {
    table=$1

    unzip -p ${INPUT} ${table}.csv | \
        psql -c "TRUNCATE ${SCHEMA}.${table}; COPY ${SCHEMA}.${table} FROM STDIN (FORMAT csv, HEADER, ON_ERROR ignore)"
}

for table in ${TABLES[@]}; do
    load_table ${table} &
done

wait

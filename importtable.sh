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
    passengerstopassignments
)
SCHEMA=netex
JOBS=8

INPUT=${1:-netex.zip}

load_table() {
    local table="$1"

    unzip -p "$INPUT" "${table}.csv" |
        psql -v ON_ERROR_STOP=1 \
            -c "TRUNCATE ${SCHEMA}.${table};
                COPY ${SCHEMA}.${table}
                FROM STDIN (FORMAT csv, HEADER);"
}

export -f load_table
export INPUT SCHEMA

parallel --jobs "$JOBS" load_table ::: "${TABLES[@]}"

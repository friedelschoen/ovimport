ovimport
============================================================

`ovimport` converts Dutch NeTEx publications into a flat CSV representation suitable for bulk loading into PostgreSQL or
other relational databases.

The converter preserves the original NeTEx object model where practical and writes one CSV file per table. The resulting
archive is intended as an intermediate format for further processing, not as a public data model.

Building
------------------------------------------------------------

```sh
mvn package
```

Usage
------------------------------------------------------------

```sh
java -jar target/ovimport.jar [options] INPUT...
```

Example:

```sh
java -jar target/ovimport.jar \
    --output netex.zip \
    NeTEx_QBUZZ_*.xml.gz
```

### Options

```
-o, --output FILE    Write the generated ZIP archive.
-w, --workdir PATH   Directory for temporary CSV files.
-h, --help           Show this help message.
```

Output
------------------------------------------------------------

The generated ZIP archive contains one CSV file for each exported table. To remain compability with GTFS-archives,
tables are written as `"<table>.txt"` instead of the usual `.csv`-extension.

Tables
------------------------------------------------------------

### availabilityconditions.txt

- `dataset_id`
- `availabilitycondition_id`
- `version`
- `name`
- `from_date`
- `to_date`
- `validdays`
- `availability`  

### blockdaytypes.txt

- `dataset_id`

- `block_id`

- `version`

- `daytypes`

### blockjourneys.txt

- `dataset_id`

- `block_id`

- `version`

- `vehiclejourney_id`

### blocks.txt

- `dataset_id`

- `block_id`

- `version`

- `blockcode`

- `name`

- `description`

- `preparationduration`

- `starttime`

- `starttimedayoffset`

- `finishingduration`

- `endtime`

- `endtimedayoffset`

- `startpoint_id`

- `endpoint_id`

### blockvaliditycondition.txt

- `dataset_id`

- `block_id`

- `version`

- `validitycondition_id`

### datasets.txt

- `dataset_id`

- `filename`

- `from_date`

- `to_date`

### daytypeassignments.txt

- `dataset_id`

- `daytypeassignment_id`

- `version`

- `date`

- `daytype_id`

### daytypes.txt

- `dataset_id`

- `daytype_id`

- `version`

- `name`

- `shortname`

- `daysofweek`

- `weeksofmonth`

- `dayofyear`

- `holidaytypes`

- `seasons`

- `tides`

- `dayevent`

- `crowding`

### destinationdisplays.txt

- `dataset_id`

- `display_id`

- `version`

- `destinationcode`

- `name`

- `sidetext`

- `fronttext`

- `color`

- `textcolor`

- `vias`

### destinationdisplayvariants.txt

- `dataset_id`

- `display_id`

- `version`

- `mediatype`

- `length`

- `name`

### journeypatterns.txt

- `dataset_id`

- `journeypattern_id`

- `version`

- `name`

- `route_id`

- `direction`

- `destinationdisplay_id`

- `point_id`

- `point_order`

- `scheduledstoppoint_id`

- `timingpoint_id`

- `onward_timinglink_id`

- `is_waitpoint`

### lines.txt

- `dataset_id`

- `line_id`

- `version`

- `lineplanningnumber`

- `branding`

- `name`

- `shortname`

- `description`

- `mode`

- `url`

- `publiccode`

- `authority`

- `operator`

- `label`

- `monitored`

- `color`

- `textcolor`

### noticeassignments.txt

- `dataset_id`

- `assignment_id`

- `version`

- `notice_id`

- `object_id`

- `view_index`

### notices.txt

- `dataset_id`

- `notice_id`

- `version`

- `name`

- `text`

### organisation.txt

- `dataset`

- `organisation_id`

- `version`

- `type`

- `name`

- `shortname`

- `description`

### organisations.txt

- `dataset_id`

- `organisation_id`

- `version`

- `type`

- `name`

- `shortname`

- `description`

### passengercapacities.txt

- `dataset_id`

- `capacity_id`

- `version`

- `fareclass`

- `total`

- `seating`

- `standing`

- `specialplace`

- `pushchair`

- `wheelchair`

### routelinks.txt

- `dataset_id`

- `routelink_id`

- `version`

- `from_routepoint_id`

- `to_routepoint_id`

- `distance`

- `linestring`

- `operational_context_id`

- `responsibilityset_id`

### routepoints.txt

- `dataset_id`

- `routepoint_id`

- `version`

- `point`

### routes.txt

- `dataset_id`

- `route_id`

- `version`

- `line_id`

- `name`

- `direction`

- `point_order`

- `routepoint_id`

- `onward_routelink_id`

### scheduledstoppoints.txt

- `dataset_id`

- `stoppoint_id`

- `version`

- `userstopcode`

- `name`

- `point`

- `projected_routepoint_id`

- `stoparea_id`

- `alighting`

- `boarding`

- `place`

### scheduledstoppointtariffzones.txt

- `dataset_id`

- `stoppoint_id`

- `version`

- `tariffzone`

### serviceclasses.txt

- `dataset_id`

- `serviceclass_id`

- `version`

- `type`

- `name`

- `description`

- `image`

- `url`

- `color`

- `textcolor`

### stopareas.txt

- `dataset_id`

- `stoparea_id`

- `version`

- `userstopareacode`

- `name`

- `publiccode`

- `place`

### stopassignments.txt

- `dataset_id`

- `assignment_id`

- `version`

- `stoppoint_id`

- `quay_id`

- `stopplace_id`

### timedemandtypes.txt

- `dataset_id`

- `timedemandtype_id`

- `version`

- `type`

- `entry_id`

- `timinglink_id`

- `scheduledstoppoint_id`

- `timingpoint_id`

- `duration`

### timinglinks.txt

- `dataset_id`

- `timinglink_id`

- `version`

- `from_point_id`

- `to_point_id`

- `distance`

- `operational_context_id`

### timingpoints.txt

- `dataset_id`

- `timingpoint_id`

- `version`

- `name`

- `point`

- `projected_routepoint_id`

### vehiclejourneyconditions.txt

- `dataset_id`

- `vehiclejourney_id`

- `version`

- `validitycondition_id`

### vehiclejourneys.txt

- `dataset_id`

- `vehiclejourney_id`

- `version`

- `type`

- `derived_from`

- `condition`

- `journeynumber`

- `departuretime`

- `departuredayoffset`

- `journeypattern_id`

- `timedemandtype_id`

- `vehicletype_id`

- `operator_id`

- `dynamic`

### vehicles.txt

- `dataset_id`

- `vehicle_id`

- `version`

- `from_date`

- `to_date`

- `registration`

- `operationalnumber`

- `operator_id`

- `vehicletype_id`

### vehicletypecapacities.txt

- `dataset_id`

- `vehicletype_id`

- `capacity_id`

### vehicletypes.txt

- `dataset_id`

- `vehicletype_id`

- `version`

- `vehicletypecode`

- `branding`

- `name`

- `shortname`

- `description`

- `euroclass`

- `reversingdirection`

- `selfpropelled`

- `propulsiontype`

- `fueltype`

- `maximumrange`

- `transportmode`

- `lowfloor`

- `liftorramp`

- `hoist`

- `boardingheight`

- `gaptoplatform`

- `length`

- `width`

- `height`

- `weight`

- `firstaxleheight`

License
------------------------------------------------------------

The converter source code is licensed under the zlib License.

Bundled NeTEx XSD files are third-party material and remain licensed under their original license. See `LICENSE` for
details.

CREATE SCHEMA IF NOT EXISTS :schema;
SET search_path to :schema, public;

CREATE TABLE IF NOT EXISTS datasets(
  dataset text,
  feed_index text,
  filename text,
  fromdate date,
  todate date
);


CREATE TABLE IF NOT EXISTS serviceclasses(
  dataset text NOT NULL,
  serviceclass_id text NOT NULL,
  version text NOT NULL,
  type text NOT NULL,
  name text NOT NULL,
  description text,
  image text,
  url text,
  color text,
  textcolor text
  -- PRIMARY KEY(dataset, version, serviceclass_id)
);


CREATE TABLE IF NOT EXISTS organisation(
  dataset text NOT NULL,
  organisation_id text NOT NULL,
  version text NOT NULL,
  type text NOT NULL,
  name text NOT NULL,
  shortname text,
  description text
  -- PRIMARY KEY(dataset, version, organisation_id)
);


CREATE TABLE IF NOT EXISTS vehicletypes(
  dataset text NOT NULL,
  vehicletype_id text NOT NULL,
  version text NOT NULL,
  vehicletypecode text,
  branding text,
  name text NOT NULL,
  shortname text,
  description text,
  euroclass text,
  reversingdirection boolean,
  selfpropelled boolean,
  propulsiontype text,
  fueltype text,
  maximumrange numeric,
  transportmode text NOT NULL,
  lowfloor boolean NOT NULL,
  liftorramp boolean NOT NULL,
  hoist boolean,
  boardingheight numeric,
  gaptoplatform numeric,
  length numeric NOT NULL,
  width numeric,
  height numeric,
  weight numeric,
  firstaxleheight numeric
  -- PRIMARY KEY(dataset, vehicletype_id, version)
);


CREATE TABLE IF NOT EXISTS vehicletypecapacities(
  dataset text NOT NULL,
  vehicletype_id text NOT NULL,
  capacity_id text NOT NULL
  -- PRIMARY KEY(dataset, vehicletype_id, capacity_id)
);


CREATE TABLE IF NOT EXISTS passengercapacities(
  dataset text NOT NULL,
  capacity_id text NOT NULL,
  version text NOT NULL,
  fareclass text,
  total integer,
  seating integer,
  standing integer,
  specialplace integer,
  pushchair integer,
  wheelchair integer
  -- PRIMARY KEY(dataset, capacity_id, version)
);


CREATE TABLE IF NOT EXISTS vehicles(
  dataset text NOT NULL,
  vehicle_id text NOT NULL,
  version text NOT NULL,
  fromdate timestamp
  WITH
    time zone NOT NULL,
    todate timestamp
  WITH
    time zone,
    registration text,
    operationalnumber text,
    operator_id text NOT NULL,
    vehicletype_id text NOT NULL
--    PRIMARY KEY(dataset, vehicle_id, version)
);


CREATE TABLE IF NOT EXISTS routepoints(
  dataset text NOT NULL,
  routepoint_id text NOT NULL,
  version text NOT NULL,
  point geometry(point, 4326) NOT NULL
  -- PRIMARY KEY(dataset, routepoint_id, version)
);


CREATE TABLE IF NOT EXISTS routelinks(
  dataset text NOT NULL,
  routelink_id text NOT NULL,
  version text NOT NULL,
  from_routepoint_id text NOT NULL,
  to_routepoint_id text NOT NULL,
  distance numeric,
  linestring geometry(linestring, 4326),
  operational_context_id text,
  responsibilityset_id text
  -- PRIMARY KEY(dataset, routelink_id, version)
);


CREATE TABLE IF NOT EXISTS routes(
  dataset text NOT NULL,
  route_id text NOT NULL,
  version text NOT NULL,
  line_id text NOT NULL,
  name text,
  direction text NOT NULL,
  point_order integer,
  routepoint_id text NOT NULL,
  onward_routelink_id text
  -- PRIMARY KEY(dataset, route_id, version, point_order)
);


CREATE TABLE IF NOT EXISTS lines(
  dataset text NOT NULL,
  line_id text NOT NULL,
  version text NOT NULL,
  lineplanningnumber text,
  branding text,
  name text NOT NULL,
  shortname text,
  description text,
  mode text,
  url text,
  publiccode text,
  authority text,
  operator text,
  label text,
  monitored boolean NOT NULL,
  color text,
  textcolor text
  -- PRIMARY KEY(dataset, line_id, version)
);


CREATE TABLE IF NOT EXISTS destinationdisplays(
  dataset text NOT NULL,
  display_id text NOT NULL,
  version text NOT NULL,
  destinationcode text,
  name text NOT NULL,
  sidetext text,
  fronttext text,
  color text,
  textcolor text,
  vias text
  -- PRIMARY KEY(dataset, display_id, version)
);


CREATE TABLE IF NOT EXISTS destinationdisplayvariants(
  dataset text NOT NULL,
  display_id text NOT NULL,
  version text NOT NULL,
  mediatype text NOT NULL,
  length text NOT NULL,
  name text NOT NULL
  -- PRIMARY KEY(dataset, display_id, version, mediatype, length)
);


CREATE TABLE IF NOT EXISTS scheduledstoppoints(
  dataset text NOT NULL,
  stoppoint_id text NOT NULL,
  version text NOT NULL,
  userstopcode text,
  name text NOT NULL,
  point geometry(point, 4326),
  projected_routepoint_id text,
  stoparea_id text,
  alighting boolean NOT NULL,
  boarding boolean NOT NULL,
  place text
  -- PRIMARY KEY(dataset, stoppoint_id, version)
);


CREATE TABLE IF NOT EXISTS scheduledstoppointtariffzones(
  dataset text NOT NULL,
  stoppoint_id text NOT NULL,
  version text NOT NULL,
  tariffzone text NOT NULL
  -- PRIMARY KEY(dataset, stoppoint_id, version, tariffzone)
);


CREATE TABLE IF NOT EXISTS stopassignments(
  dataset text NOT NULL,
  assignment_id text NOT NULL,
  version text NOT NULL,
  stoppoint_id text NOT NULL,
  quay_id text,
  stopplace_id text
  -- PRIMARY KEY(dataset, assignment_id, version)
);


CREATE TABLE IF NOT EXISTS timingpoints(
  dataset text NOT NULL,
  timingpoint_id text NOT NULL,
  version text NOT NULL,
  name text,
  point geometry(point, 4326) NOT NULL,
  projected_routepoint_id text NOT NULL
  -- PRIMARY KEY(dataset, timingpoint_id, version)
);


CREATE TABLE IF NOT EXISTS timinglinks(
  dataset text NOT NULL,
  timinglink_id text NOT NULL,
  version text NOT NULL,
  from_point_id text NOT NULL,
  to_point_id text NOT NULL,
  distance numeric,
  operational_context_id text
  -- PRIMARY KEY(dataset, timinglink_id, version)
);


CREATE TABLE IF NOT EXISTS journeypatterns(
  dataset text NOT NULL,
  journeypattern_id text NOT NULL,
  version text NOT NULL,
  name text,
  route_id text,
  direction text,
  destinationdisplay_id text,
  point_id text NOT NULL,
  point_order integer,
  scheduledstoppoint_id text,
  timingpoint_id text,
  onward_timinglink_id text,
  is_waitpoint boolean
  -- PRIMARY KEY(dataset, journeypattern_id, version, point_order)
);


CREATE TABLE IF NOT EXISTS timedemandtypes(
  dataset text NOT NULL,
  timedemandtype_id text NOT NULL,
  version text NOT NULL,
  type text NOT NULL,
  entry_id text,
  timinglink_id text,
  scheduledstoppoint_id text,
  timingpoint_id text,
  duration interval NOT NULL
  -- PRIMARY KEY(    dataset, timedemandtype_id,    version,    entry_id,     type  )
);


CREATE TABLE IF NOT EXISTS notices(
  dataset text NOT NULL,
  notice_id text NOT NULL,
  version text NOT NULL,
  name text,
  text text NOT NULL
  -- PRIMARY KEY(dataset, notice_id, version)
);


CREATE TABLE IF NOT EXISTS noticeassignments(
  dataset text NOT NULL,
  assignment_id text NOT NULL,
  version text NOT NULL,
  notice_id text NOT NULL,
  object_id text NOT NULL,
  view integer
  -- PRIMARY KEY(dataset, assignment_id, version)
);


CREATE TABLE IF NOT EXISTS stopareas(
  dataset text NOT NULL,
  stoparea_id text NOT NULL,
  version text NOT NULL,
  userstopareacode text,
  name text NOT NULL,
  publiccode text,
  place text
  -- PRIMARY KEY(dataset, stoparea_id, version)
);


CREATE TABLE IF NOT EXISTS availabilityconditions(
  dataset text NOT NULL,
  availabilitycondition_id text NOT NULL,
  version text NOT NULL,
  name text,
  from_date timestamp
  WITH
    time zone,
    to_date timestamp
  WITH
    time zone,
    validdays bit varying,
    availability boolean
--    PRIMARY KEY(dataset, availabilitycondition_id, version)
);


CREATE TABLE IF NOT EXISTS vehiclejourneys(
  dataset text NOT NULL,
  vehiclejourney_id text NOT NULL,
  version text NOT NULL,
  type text NOT NULL,
  derived_from text,
  condition text,
  journeynumber text,
  departuretime time,
  departuredayoffset smallint NOT NULL,
  journeypattern_id text NOT NULL,
  timedemandtype_id text,
  vehicletype_id text,
  operator_id text,
  dynamic text
  -- PRIMARY KEY(dataset, vehiclejourney_id, version)
);


CREATE TABLE IF NOT EXISTS vehiclejourneyconditions(
  dataset text NOT NULL,
  vehiclejourney_id text NOT NULL,
  version text NOT NULL,
  validitycondition_id text NOT NULL
  -- PRIMARY KEY(  dataset, vehiclejourney_id,    version,    validitycondition_id  )
);


CREATE TABLE IF NOT EXISTS daytypes(
  dataset text NOT NULL,
  daytype_id text NOT NULL,
  version text NOT NULL,
  name text,
  shortname text,
  daysofweek text NOT NULL,
  weeksofmonth text NOT NULL,
  dayofyear integer,
  holidaytypes text NOT NULL,
  seasons text NOT NULL,
  tides text NOT NULL,
  dayevent text,
  crowding text
  -- PRIMARY KEY(dataset, daytype_id, version)
);


CREATE TABLE IF NOT EXISTS daytypeassignments(
  dataset text NOT NULL,
  daytypeassignment_id text NOT NULL,
  version text NOT NULL,
  date date NOT NULL,
  daytype_id text NOT NULL
  -- PRIMARY KEY(dataset, daytypeassignment_id, version)
);


CREATE TABLE IF NOT EXISTS blocks(
  dataset text NOT NULL,
  block_id text NOT NULL,
  version text NOT NULL,
  blockcode text,
  name text,
  description text,
  preparationduration interval,
  starttime time,
  starttimedayoffset smallint,
  finishingduration interval,
  endtime time,
  endtimedayoffset smallint,
  startpoint_id text,
  endpoint_id text
  -- PRIMARY KEY(dataset, block_id, version)
);


CREATE TABLE IF NOT EXISTS blockvaliditycondition(
  dataset text NOT NULL,
  block_id text NOT NULL,
  version text NOT NULL,
  validitycondition_id text NOT NULL
  -- PRIMARY KEY(dataset, block_id, version, validitycondition_id)
);


CREATE TABLE IF NOT EXISTS blockjourneys(
  dataset text NOT NULL,
  block_id text NOT NULL,
  version text NOT NULL,
  journey_id text NOT NULL
  -- PRIMARY KEY(dataset, block_id, version, journey_id)
);


CREATE TABLE IF NOT EXISTS blockdaytypes(
  dataset text NOT NULL,
  block_id text NOT NULL,
  version text NOT NULL,
  daytypes text NOT NULL
  -- PRIMARY KEY(dataset, block_id, version, daytypes)
);

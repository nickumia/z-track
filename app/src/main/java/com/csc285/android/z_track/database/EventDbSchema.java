package com.csc285.android.z_track.database;

/**
 *
 * Created by nick on 10/30/2017.
 */

public class EventDbSchema {
    public static final class EventTable {
        public static final String NAME = "events";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String DATE = "date";
            public static final String TIME = "time";
            public static final String DISTANCE = "distance";
            public static final String PACE = "pace";
            public static final String ELEVATION = "elevation";
            public static final String TOP_VELOCITY = "velocity_top";
            public static final String VELOCITY = "velocity";
            public static final String START_LOC_LAT = "location_start_lat";
            public static final String START_LOC_LON = "location_start_lon";
            public static final String END_LOC_LAT = "location_end_lat";
            public static final String END_LOC_LON = "location_end_lon";
            public static final String MARKER_LOC = "location_markers";
            public static final String START_TIME = "time_start";
            public static final String END_TIME = "time_end";
        }
    }

    public static final class VelocityTable {
        public static final String NAME = "velocity";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String NUM = "num";
            public static final String VEL = "vel";
            public static final String PACE = "pace";
        }
    }

    public static final class ElevationTable {
        public static final String NAME = "elevation";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String NUM = "num";
            public static final String ELEVATION = "elevation";
        }
    }

    public static final class MarkerTable {
        public static final String NAME = "markers";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String LATITUDE = "latitude";
            public static final String LONGITUDE = "longitude";
        }
    }
}

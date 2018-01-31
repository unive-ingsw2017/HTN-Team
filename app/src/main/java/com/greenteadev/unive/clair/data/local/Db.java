package com.greenteadev.unive.clair.data.local;

import android.content.ContentValues;
import android.database.Cursor;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import com.greenteadev.unive.clair.data.model.Coordinate;
import com.greenteadev.unive.clair.data.model.MeasureData;
import com.greenteadev.unive.clair.data.model.MeasurePlotData;
import com.greenteadev.unive.clair.data.model.Station;

public class Db {

    public Db() {
    }

    public abstract static class StationTable {
        public static final String TABLE_NAME = "station";

        public static final String COLUMN_CODE = "code";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_COMUNE = "comune";
        public static final String COLUMN_PROVINCIA = "provincia";
        public static final String COLUMN_LAT = "latitude";
        public static final String COLUMN_LON = "longitude";
        public static final String COLUMN_TYPE = "type";

        public static final String CREATE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        COLUMN_CODE + " TEXT PRIMARY KEY, " +
                        COLUMN_NAME + " TEXT NOT NULL, " +
                        COLUMN_LOCATION + " TEXT NOT NULL, " +
                        COLUMN_COMUNE + " TEXT  NOT NULL, " +
                        COLUMN_PROVINCIA + " TEXT NOT NULL, " +
                        COLUMN_LAT + " TEXT, " +
                        COLUMN_LON + " TEXT, " +
                        COLUMN_TYPE + " TEXT NOT NULL" +
                        " ); ";

        public static ContentValues toContentValues(Station station) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CODE, station.id());
            values.put(COLUMN_NAME, station.nome());
            values.put(COLUMN_LOCATION, station.localita());
            values.put(COLUMN_COMUNE, station.comune());
            values.put(COLUMN_PROVINCIA, station.provincia());

            if (station.coordinate() != null) {
                values.put(COLUMN_LAT, station.coordinate().lat());
                values.put(COLUMN_LON, station.coordinate().lon());
            }

            values.put(COLUMN_TYPE, station.tipozona());
            return values;
        }

        public static Station parseCursor(Cursor cursor) {
            Coordinate coord = Coordinate.create(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CODE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LON)));

            return Station.builder()
                    .setCoordinate(coord)
                    .setId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CODE)))
                    .setNome(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)))
                    .setLocalita(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION)))
                    .setComune(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMUNE)))
                    .setProvincia(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROVINCIA)))
                    .setTipozona(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)))
                    .build();
        }
    }

    public abstract static class BookmarkStationTable {
        public static final String TABLE_NAME = "station_bookmark";

        public static final String COLUMN_CODE = "code";

        public static final String CREATE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        COLUMN_CODE + " TEXT PRIMARY KEY" +
                        " ); ";

        public static ContentValues toContentValues(String id) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CODE, id);

            return values;
        }

        public static String parseCursor(Cursor cursor) {
            return cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CODE));
        }
    }

    public abstract static class DataTable {
        public static final String TABLE_NAME = "data";

        public static final String COLUMN_CODE = "code";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_VALUE = "value";

        // Additional columns for group by
        public static final String COLUMN_AVG = "avg";
        public static final String COLUMN_MIN = "min";
        public static final String COLUMN_MAX = "max";

        public static final String CREATE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        COLUMN_CODE + " TEXT NOT NULL, " +
                        COLUMN_DATE + " TEXT NOT NULL, " +
                        COLUMN_TIME + " TEXT NOT NULL, " +
                        COLUMN_TYPE + " TEXT NOT NULL, " +
                        COLUMN_VALUE + " FLOAT NOT NULL, " +
                        "PRIMARY KEY (" + COLUMN_CODE
                        + ", " + COLUMN_DATE
                        + ", " + COLUMN_TIME
                        + ", " + COLUMN_TYPE
                        + ")); ";

        public static ContentValues toContentValues(MeasureData measure) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CODE, measure.stationId());
            values.put(COLUMN_DATE, measure.date().toLocalDate().toString());
            values.put(COLUMN_TIME, measure.date().toLocalTime().toString());
            values.put(COLUMN_TYPE, measure.type().toString());
            values.put(COLUMN_VALUE, measure.valueNum());

            return values;
        }

        public static MeasureData parseCursor(Cursor cursor) {
            LocalDate date = LocalDate.parse(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)));
            LocalTime time = LocalTime.parse(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME)));

            return MeasureData.builder()
                    .setDate(date.toLocalDateTime(time))
                    .setValueNum(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_VALUE)))
                    .setType(MeasureData.MeasureType.valueOf(
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE))))
                    .setStationId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CODE)))
                    .build();
        }

        public static MeasurePlotData parsePlotCursor(Cursor cursor) {
            LocalDate date = LocalDate.parse(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)));

            return MeasurePlotData.builder()
                    .setDate(date)
                    .setAvg(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_AVG)))
                    .setMin(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_MIN)))
                    .setMax(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_MAX)))
                    .setType(MeasureData.MeasureType.valueOf(
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE))))
                    .build();
        }
    }
}

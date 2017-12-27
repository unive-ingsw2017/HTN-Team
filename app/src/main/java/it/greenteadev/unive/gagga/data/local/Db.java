package it.greenteadev.unive.gagga.data.local;

import android.content.ContentValues;
import android.database.Cursor;

import it.greenteadev.unive.gagga.data.model.Coordinate;
import it.greenteadev.unive.gagga.data.model.Station;

public class Db {

    public Db() { }

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
                "CREATE TABLE " + TABLE_NAME + " (" +
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
            values.put(COLUMN_CODE, station.codseqst());
            values.put(COLUMN_NAME, station.nome());
            values.put(COLUMN_LOCATION, station.localita());
            values.put(COLUMN_COMUNE, station.comune());
            values.put(COLUMN_PROVINCIA, station.provincia());

            if(station.coordinate() != null){
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
                    .setCodseqst(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CODE)))
                    .setNome(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)))
                    .setLocalita(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION)))
                    .setComune(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMUNE)))
                    .setProvincia(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROVINCIA)))
                    .setTipozona(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)))
                    .build();
        }
    }
}

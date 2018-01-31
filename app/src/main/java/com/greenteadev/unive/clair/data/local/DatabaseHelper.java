package com.greenteadev.unive.clair.data.local;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.VisibleForTesting;

import com.greenteadev.unive.clair.data.local.exception.DatabaseInsertException;
import com.greenteadev.unive.clair.data.model.MeasureData;
import com.greenteadev.unive.clair.data.model.MeasurePlotData;
import com.greenteadev.unive.clair.data.model.Station;
import com.squareup.sqlbrite2.BriteDatabase;
import com.squareup.sqlbrite2.SqlBrite;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class DatabaseHelper {

    private final BriteDatabase mDb;

    @Inject
    public DatabaseHelper(DbOpenHelper dbOpenHelper) {
        this(dbOpenHelper, Schedulers.io());
    }

    @VisibleForTesting
    public DatabaseHelper(DbOpenHelper dbOpenHelper, Scheduler scheduler) {
        SqlBrite.Builder briteBuilder = new SqlBrite.Builder();
        mDb = briteBuilder.build().wrapDatabaseHelper(dbOpenHelper, scheduler);
    }

    public BriteDatabase getBriteDb() {
        return mDb;
    }

    public Observable<List<Station>> setStations(final List<Station> newStations) {
        return Observable.create(emitter -> {
            if (emitter.isDisposed()) return;

            BriteDatabase.Transaction transaction = mDb.newTransaction();
            try {
                //mDb.delete(Db.StationTable.TABLE_NAME, null);
                for (Station station : newStations) {
                    long result = mDb.insert(Db.StationTable.TABLE_NAME,
                            Db.StationTable.toContentValues(station),
                            SQLiteDatabase.CONFLICT_REPLACE);
                    if (result < 0) emitter.onError(new DatabaseInsertException());
                }


                transaction.markSuccessful();
            } finally {
                transaction.end();
                emitter.onNext(newStations);
                emitter.onComplete();
            }
        });
    }

    public Observable<List<Station>> getStations() {
        return mDb.createQuery(Db.StationTable.TABLE_NAME,
                "SELECT * FROM " + Db.StationTable.TABLE_NAME)
                .mapToList(Db.StationTable::parseCursor);
    }

    public Observable<Station> getStation(String stationId) {
        return mDb.createQuery(Db.StationTable.TABLE_NAME,
                "SELECT * FROM " + Db.StationTable.TABLE_NAME
                        + " WHERE " + Db.StationTable.COLUMN_CODE + " = '" + stationId + "'"
                        + " LIMIT 1 "
        ).mapToOne(Db.StationTable::parseCursor);
    }

    public Observable<String> isStationBookmark(String stationId) {
        return mDb.createQuery(Db.BookmarkStationTable.TABLE_NAME,
                "SELECT * FROM " + Db.BookmarkStationTable.TABLE_NAME
                        + " WHERE " + Db.BookmarkStationTable.COLUMN_CODE + " = '" + stationId + "'"
                        + " LIMIT 1 "
        ).mapToOne(Db.BookmarkStationTable::parseCursor);
    }

    public Observable<Boolean> addStationBookmark(String stationId) {
        long result = mDb.insert(Db.BookmarkStationTable.TABLE_NAME,
                Db.BookmarkStationTable.toContentValues(stationId));
        return Observable.just(result >= 0);
    }

    public Observable<Boolean> removeStationBookmark(String stationId) {
        long result = mDb.delete(Db.BookmarkStationTable.TABLE_NAME,
                Db.BookmarkStationTable.COLUMN_CODE + " = '" + stationId + "'");
        return Observable.just(result >= 0);
    }

    public Observable<List<Station>> getStationsBookmarked() {
        List<String> tables = new ArrayList<>();
        tables.add(Db.StationTable.TABLE_NAME);
        tables.add(Db.BookmarkStationTable.TABLE_NAME);

        return mDb.createQuery(tables,
                "SELECT * FROM " + Db.StationTable.TABLE_NAME
                        + " INNER JOIN " + Db.BookmarkStationTable.TABLE_NAME
                        + " ON " + Db.StationTable.TABLE_NAME + "." + Db.StationTable.COLUMN_CODE
                        + " = " + Db.BookmarkStationTable.TABLE_NAME + "." + Db.BookmarkStationTable.COLUMN_CODE
        ).mapToList(Db.StationTable::parseCursor);
    }

    public Observable<List<MeasureData>> setStationData(List<MeasureData> newData) {
        return Observable.create(emitter -> {
            if (emitter.isDisposed()) return;

            BriteDatabase.Transaction transaction = mDb.newTransaction();
            try {
                for (MeasureData measure : newData) {
                    long result = mDb.insert(Db.DataTable.TABLE_NAME,
                            Db.DataTable.toContentValues(measure),
                            SQLiteDatabase.CONFLICT_REPLACE);
                    if (result < 0) emitter.onError(new DatabaseInsertException());
                }

                transaction.markSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                transaction.end();
                emitter.onNext(newData);
                emitter.onComplete();
            }
        });
    }

    public Observable<List<MeasureData>> getStationData() {
        return mDb.createQuery(Db.DataTable.TABLE_NAME,
                "SELECT * FROM " + Db.DataTable.TABLE_NAME
        ).mapToList(Db.DataTable::parseCursor);
    }

    public Observable<List<MeasureData>> getStationData(String stationId) {
        return mDb.createQuery(Db.DataTable.TABLE_NAME,
                "SELECT * FROM " + Db.DataTable.TABLE_NAME
                        + " WHERE " + Db.DataTable.COLUMN_CODE + " = '" + stationId + "'"
        ).mapToList(Db.DataTable::parseCursor);
    }

    public Observable<List<MeasureData>> getStationData(String stationId, String type) {
        return mDb.createQuery(Db.DataTable.TABLE_NAME,
                "SELECT * FROM " + Db.DataTable.TABLE_NAME
                        + " WHERE " + Db.DataTable.COLUMN_CODE + " = '" + stationId + "'"
                        + " AND " + Db.DataTable.COLUMN_TYPE + " = '" + type + "'"
        ).mapToList(Db.DataTable::parseCursor);
    }

    public Observable<List<MeasurePlotData>> getPlotStationData(String stationId) {
        return mDb.createQuery(Db.DataTable.TABLE_NAME,
                "SELECT " + Db.DataTable.COLUMN_DATE + ", " + Db.DataTable.COLUMN_TYPE
                        + ", min(" + Db.DataTable.COLUMN_VALUE + ") AS " + Db.DataTable.COLUMN_MIN
                        + ", max(" + Db.DataTable.COLUMN_VALUE + ") AS " + Db.DataTable.COLUMN_MAX
                        + ", avg(" + Db.DataTable.COLUMN_VALUE + ") AS " + Db.DataTable.COLUMN_AVG
                        + " FROM " + Db.DataTable.TABLE_NAME
                        + " WHERE " + Db.DataTable.COLUMN_CODE + " = '" + stationId + "'"
                        + " GROUP BY " + Db.DataTable.COLUMN_DATE
                        + ", " + Db.DataTable.COLUMN_TYPE + ""
        ).mapToList(Db.DataTable::parsePlotCursor);
    }

    public Observable<List<MeasurePlotData>> getPlotStationData(String stationId, String type) {
        return mDb.createQuery(Db.StationTable.TABLE_NAME,
                "SELECT " + Db.DataTable.COLUMN_DATE + ", " + Db.DataTable.COLUMN_TYPE
                        + ", min(" + Db.DataTable.COLUMN_VALUE + ") AS " + Db.DataTable.COLUMN_MIN
                        + ", max(" + Db.DataTable.COLUMN_VALUE + ") AS " + Db.DataTable.COLUMN_MAX
                        + ", avg(" + Db.DataTable.COLUMN_VALUE + ") AS " + Db.DataTable.COLUMN_AVG
                        + " WHERE " + Db.DataTable.COLUMN_CODE + " = '" + stationId + "'"
                        + " AND " + Db.DataTable.COLUMN_TYPE + " = '" + type + "'"
                        + " GROUP BY " + Db.DataTable.COLUMN_DATE
                        + ", " + Db.DataTable.COLUMN_TYPE + ""
        ).mapToList(Db.DataTable::parsePlotCursor);
    }

    public Observable<Boolean> clearStationData() {
        int rows = mDb.delete(Db.DataTable.TABLE_NAME, null, null);
        return Observable.just(rows > 0);
    }

    public Observable<Boolean> clearStation() {
        int rows = mDb.delete(Db.StationTable.TABLE_NAME, null, null);
        return Observable.just(rows > 0);
    }
}

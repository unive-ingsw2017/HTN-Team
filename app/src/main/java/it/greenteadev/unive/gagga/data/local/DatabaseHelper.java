package it.greenteadev.unive.gagga.data.local;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.VisibleForTesting;

import com.squareup.sqlbrite2.BriteDatabase;
import com.squareup.sqlbrite2.SqlBrite;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import it.greenteadev.unive.gagga.data.local.exception.DatabaseInsertException;
import it.greenteadev.unive.gagga.data.model.Station;
import timber.log.Timber;

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
                mDb.delete(Db.StationTable.TABLE_NAME, null);
                for (Station station : newStations) {
                    long result = mDb.insert(Db.StationTable.TABLE_NAME,
                            Db.StationTable.toContentValues(station),
                            SQLiteDatabase.CONFLICT_REPLACE);
                    if (result < 0) emitter.onError(new DatabaseInsertException());
                }

                emitter.onNext(newStations);
                transaction.markSuccessful();
                emitter.onComplete();

            } finally {
                transaction.end();
            }
        });
    }

    public Observable<List<Station>> getStations() {
        return mDb.createQuery(Db.StationTable.TABLE_NAME,
                "SELECT * FROM " + Db.StationTable.TABLE_NAME)
                .mapToList(cursor -> Db.StationTable.parseCursor(cursor));
    }

    public Observable<Station> getStation(String id) {
        return mDb.createQuery(Db.StationTable.TABLE_NAME,
                "SELECT * FROM " + Db.StationTable.TABLE_NAME
                        + " WHERE " + Db.StationTable.COLUMN_CODE + " = '" + id + "'"
                        + " LIMIT 1 "
        ).mapToOne(Db.StationTable::parseCursor);
    }
}

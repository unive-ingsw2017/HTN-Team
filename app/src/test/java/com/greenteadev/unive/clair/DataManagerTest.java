package com.greenteadev.unive.clair;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.greenteadev.unive.clair.data.DataManager;
import com.greenteadev.unive.clair.data.local.DatabaseHelper;
import com.greenteadev.unive.clair.data.local.PreferencesHelper;
import com.greenteadev.unive.clair.data.remote.ApiService;

import static org.mockito.Mockito.verify;

/**
 * This test class performs local unit tests without dependencies on the Android framework
 * For testing methods in the DataManager follow this approach:
 * 1. Stub mock helper classes that your method relies on. e.g. RetrofitServices or DatabaseHelper
 * 2. Test the Observable using TestSubscriber
 * 3. Optionally write a SEPARATE test that verifies that your method is calling the right helper
 * using Mockito.verify()
 */
@RunWith(MockitoJUnitRunner.class)
public class DataManagerTest {

    @Mock DatabaseHelper mMockDatabaseHelper;
    @Mock PreferencesHelper mMockPreferencesHelper;
    @Mock
    ApiService mMockApiService;
    private DataManager mDataManager;

    @Before
    public void setUp() {
        mDataManager = new DataManager(mMockApiService, mMockPreferencesHelper,
                mMockDatabaseHelper);
    }

    @Test
    public void syncRibotsEmitsValues() {
        /*List<Ribot> ribots = Arrays.asList(TestDataFactory.makeRibot("r1"),
                TestDataFactory.makeRibot("r2"));
        stubSyncRibotsHelperCalls(ribots);

        TestObserver<Ribot> result = new TestObserver<>();
        mDataManager.syncStations().subscribe(result);
        result.assertNoErrors();
        result.assertValueSequence(ribots);*/
    }

    @Test
    public void syncRibotsCallsApiAndDatabase() {
        /*List<Ribot> ribots = Arrays.asList(TestDataFactory.makeRibot("r1"),
                TestDataFactory.makeRibot("r2"));
        stubSyncRibotsHelperCalls(ribots);

        mDataManager.syncStations().subscribe();
        // Verify right calls to helper methods
        verify(mMockApiService).getRibots();
        verify(mMockDatabaseHelper).setStations(ribots);*/
    }

    @Test
    public void syncRibotsDoesNotCallDatabaseWhenApiFails() {
        /*when(mMockApiService.getRibots())
                .thenReturn(Observable.<List<Ribot>>error(new RuntimeException()));

        mDataManager.syncStations().subscribe(new TestObserver<Ribot>());
        // Verify right calls to helper methods
        verify(mMockApiService).getRibots();
        verify(mMockDatabaseHelper, never()).setStations(ArgumentMatchers.<Ribot>anyList());*/
    }

    /*private void stubSyncRibotsHelperCalls(List<Ribot> ribots) {
        // Stub calls to the ribot service and database helper.
        when(mMockApiService.getRibots())
                .thenReturn(Observable.just(ribots));
        when(mMockDatabaseHelper.setStations(ribots))
                .thenReturn(Observable.fromIterable(ribots));
    }*/

}

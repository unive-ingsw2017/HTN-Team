package it.greenteadev.unive.gagga;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import it.greenteadev.unive.gagga.data.DataManager;
import it.greenteadev.unive.gagga.test.common.TestDataFactory;
import it.greenteadev.unive.gagga.ui.main.MainMvpView;
import it.greenteadev.unive.gagga.ui.main.MainPresenter;
import it.greenteadev.unive.gagga.util.RxSchedulersOverrideRule;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MainPresenterTest {

    @Mock MainMvpView mMockMainMvpView;
    @Mock DataManager mMockDataManager;
    private MainPresenter mMainPresenter;

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    @Before
    public void setUp() {
        mMainPresenter = new MainPresenter(mMockDataManager);
        mMainPresenter.attachView(mMockMainMvpView);
    }

    @After
    public void tearDown() {
        mMainPresenter.detachView();
    }

    @Test
    public void loadRibotsReturnsRibots() {
        /*List<Ribot> ribots = TestDataFactory.makeListRibots(10);
        when(mMockDataManager.getRibots())
                .thenReturn(Observable.just(ribots));

        mMainPresenter.loadRibots();
        verify(mMockMainMvpView).showStations(ribots);
        verify(mMockMainMvpView, never()).showStationsEmpty();
        verify(mMockMainMvpView, never()).showError();*/
    }

    @Test
    public void loadRibotsReturnsEmptyList() {
        /*when(mMockDataManager.getRibots())
                .thenReturn(Observable.just(Collections.<Ribot>emptyList()));

        mMainPresenter.loadRibots();
        verify(mMockMainMvpView).showStationsEmpty();
        verify(mMockMainMvpView, never()).showStations(ArgumentMatchers.<Ribot>anyList());
        verify(mMockMainMvpView, never()).showError();*/
    }

    @Test
    public void loadRibotsFails() {
        /*when(mMockDataManager.getRibots())
                .thenReturn(Observable.<List<Ribot>>error(new RuntimeException()));

        mMainPresenter.loadRibots();
        verify(mMockMainMvpView).showError();
        verify(mMockMainMvpView, never()).showStationsEmpty();
        verify(mMockMainMvpView, never()).showStations(ArgumentMatchers.<Ribot>anyList());*/
    }

}

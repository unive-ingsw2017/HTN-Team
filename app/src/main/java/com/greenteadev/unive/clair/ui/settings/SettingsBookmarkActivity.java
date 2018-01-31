package com.greenteadev.unive.clair.ui.settings;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.evernote.android.state.StateSaver;
import com.squareup.leakcanary.RefWatcher;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.greenteadev.unive.clair.R;

import com.greenteadev.unive.clair.data.model.Station;
import com.greenteadev.unive.clair.ui.base.BaseActivity;
import com.greenteadev.unive.clair.util.DialogFactory;
import com.greenteadev.unive.clair.util.ui.RecyclerTouchListener;

import timber.log.Timber;

/**
 * Created by Hitech95 on 30/01/2018.
 */

public class SettingsBookmarkActivity extends BaseActivity implements BookmarkMvpView {

    @Inject
    BookmarkPresenter mBookmarkPresenter;

    @Inject
    RefWatcher mRefWatcher;

    @BindView(R.id.main_content)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.rv_bookmark)
    RecyclerView mRecyclerView;

    @BindView(R.id.tv_empty)
    AppCompatTextView mEmptyTextView;

    List<Station> stationList;
    StationAdapter mAdapter;

    Station lastRemoved;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StateSaver.restoreInstanceState(this, savedInstanceState);
        activityComponent().inject(this);
        setContentView(R.layout.activity_bookmark);
        ButterKnife.bind(this);
        mBookmarkPresenter.attachView(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                mRecyclerView.getContext(), linearLayoutManager.getOrientation());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mAdapter = new StationAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                mRecyclerView, new RecyclerTouchListener.ClickListener() {

            @Override
            public void onClick(View view, int position) {
                Timber.d("onClick %d", position);
                lastRemoved = stationList.get(position);

                DialogFactory.createSimpleConfirmDialog(SettingsBookmarkActivity.this,
                        getString(R.string.settings_bookmark_station_remove_confirm_title),
                        getString(R.string.settings_bookmark_station_remove_confirm_body),
                        (dialog, which) ->
                                mBookmarkPresenter.bookmarkStation(
                                        stationList.get(position).id(), false))
                        .show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        mBookmarkPresenter.loadBookmarkedStations();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBookmarkPresenter.detachView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    /***** MVP View methods implementation *****/

    @Override
    public void showStations(List<Station> stations) {
        stationList = stations;

        mRecyclerView.setVisibility(View.VISIBLE);
        mEmptyTextView.setVisibility(View.GONE);

        mAdapter.clear();
        mAdapter.addAll(stations);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showStationsEmpty() {
        mEmptyTextView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void showBookmarkRemoved(Boolean result, boolean status) {
        if (!status) {
            if (result) {
                Snackbar snackbar = Snackbar
                        .make(mCoordinatorLayout, getString(R.string.settings_bookmark_station_removed),
                                Snackbar.LENGTH_LONG);
                snackbar.setAction(getString(R.string.dialog_undo), (View view) -> {
                    mBookmarkPresenter.bookmarkStation(lastRemoved.id(), true);
                    snackbar.dismiss();
                });
                snackbar.show();
                return;
            } else {
                DialogFactory.createGenericErrorDialog(this,
                        getString(R.string.error_generic))
                        .show();
            }
        } else {
            lastRemoved = null;
        }
    }

    @Override
    public void showError(boolean networkError) {
        DialogFactory.createGenericErrorDialog(this,
                getString(R.string.error_generic))
                .show();
    }
}

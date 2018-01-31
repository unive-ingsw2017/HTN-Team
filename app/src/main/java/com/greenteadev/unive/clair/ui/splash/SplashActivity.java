package com.greenteadev.unive.clair.ui.splash;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.evernote.android.state.StateSaver;
import com.greenteadev.unive.clair.ui.base.BaseActivity;
import com.greenteadev.unive.clair.ui.main.MainActivity;
import com.greenteadev.unive.clair.util.DialogFactory;

import javax.inject.Inject;

import butterknife.ButterKnife;
import it.greenteadev.unive.clair.R;

/**
 * Created by Hitech95 on 31/01/2018.
 */

public class SplashActivity extends BaseActivity implements SplashMvpView {

    @Inject
    SplashPresenter mSplashPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StateSaver.restoreInstanceState(this, savedInstanceState);
        activityComponent().inject(this);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        mSplashPresenter.attachView(this);

        // Create default Settings
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_notification, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_data_sync, false);

        mSplashPresenter.synkData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSplashPresenter.detachView();
    }

    /***** MVP View methods implementation *****/


    @Override
    public void dataReceived(boolean success) {
        if (success) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        } else {
            Dialog genericErrorDialog = DialogFactory.createGenericErrorDialog(this,
                    getString(R.string.error_loading_generics));
            genericErrorDialog.setOnDismissListener(dialog -> finish());
            genericErrorDialog.show();
        }
    }
}

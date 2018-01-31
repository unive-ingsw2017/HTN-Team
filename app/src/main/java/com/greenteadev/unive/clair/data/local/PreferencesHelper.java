package com.greenteadev.unive.clair.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.greenteadev.unive.clair.injection.ApplicationContext;

import javax.inject.Inject;
import javax.inject.Singleton;

import it.greenteadev.unive.clair.R;

@Singleton
public class PreferencesHelper {


    private Context mContext;
    private final SharedPreferences mPref;

    @Inject
    public PreferencesHelper(@ApplicationContext Context context) {
        mContext = context;
        mPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void clear() {
        mPref.edit().clear().apply();
    }

    public int synkInterval() {
        return mPref.getInt(mContext.getString(R.string.pref_data_synk_frequency), 60);
    }

}

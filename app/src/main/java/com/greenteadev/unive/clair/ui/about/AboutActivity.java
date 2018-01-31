package com.greenteadev.unive.clair.ui.about;

import android.os.Bundle;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.greenteadev.unive.clair.R;
import com.greenteadev.unive.clair.ui.base.BaseActivity;

/**
 * Created by Hitech95 on 30/01/2018.
 */

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.ivBack)
    public void onBackClick(){
        onBackPressed();
    }
}

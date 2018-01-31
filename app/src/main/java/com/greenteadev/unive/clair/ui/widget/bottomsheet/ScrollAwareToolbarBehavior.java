package com.greenteadev.unive.clair.ui.widget.bottomsheet;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;


/**
 *
 */
public class ScrollAwareToolbarBehavior extends AppBarLayout.ScrollingViewBehavior {
    private Context mContext;
    private boolean mVisible = true;

    private ValueAnimator mToolbarAlphaAnimator;

    public ScrollAwareToolbarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    public Parcelable onSaveInstanceState(CoordinatorLayout parent, View child) {
        return new SavedState(super.onSaveInstanceState(parent, child), mVisible);
    }

    @Override
    public void onRestoreInstanceState(CoordinatorLayout parent, View child, Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(parent, child, ss.getSuperState());
        this.mVisible = ss.mVisible;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        try {
            BottomSheetBehavior.from(dependency);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        try {
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(dependency);
            boolean prevState = mVisible;
            setToolbarVisible(child, dependency.getTop() >= dependency.getHeight() - behavior.getPeekHeight());
            return prevState != mVisible;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private void setToolbarVisible(final View child, final boolean visible) {

        if (visible == mVisible)
            return;

        if (mToolbarAlphaAnimator != null && mToolbarAlphaAnimator.isRunning()) {
            mToolbarAlphaAnimator.end();
        }

        float offsetApha = (visible) ? 1f : 0f;
        mToolbarAlphaAnimator = ValueAnimator.ofFloat(child.getAlpha(), offsetApha);
        mToolbarAlphaAnimator.setDuration(mContext.getResources().getInteger(android.R.integer.config_shortAnimTime));
        mToolbarAlphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                child.setAlpha((Float) animation.getAnimatedValue());
            }
        });

        mToolbarAlphaAnimator.start();
        mVisible = visible;
    }

    protected static class SavedState extends View.BaseSavedState {

        final boolean mVisible;

        public SavedState(Parcel source) {
            super(source);
            mVisible = source.readByte() != 0;
        }

        public SavedState(Parcelable superState, boolean visible) {
            super(superState);
            this.mVisible = visible;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeByte((byte) (mVisible ? 1 : 0));
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    @Override
                    public SavedState createFromParcel(Parcel source) {
                        return new SavedState(source);
                    }

                    @Override
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }
}
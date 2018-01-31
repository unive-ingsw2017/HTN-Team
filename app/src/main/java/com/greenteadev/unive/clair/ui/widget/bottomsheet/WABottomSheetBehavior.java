package com.greenteadev.unive.clair.ui.widget.bottomsheet;

import android.content.Context;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import timber.log.Timber;

/**
 * Created by Hitech95 on 28/01/2018.
 */

public class WABottomSheetBehavior<V extends View> extends BottomSheetBehavior<V> {
    private boolean mAllowUserDragging = true;

    /**
     * Default constructor for instantiating BottomSheetBehaviors.
     */
    public WABottomSheetBehavior() {
        super();
    }

    /**
     * Default constructor for inflating BottomSheetBehaviors from layout.
     *
     * @param context The {@link Context}.
     * @param attrs   The {@link AttributeSet}.
     */
    public WABottomSheetBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setLocked(boolean locked) {
        mAllowUserDragging = !locked;
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        if (!mAllowUserDragging) {
            Timber.d("onInterceptTouchEvent() %b", mAllowUserDragging);
            return true;
        }

        return super.onInterceptTouchEvent(parent, child, event);
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        if (!mAllowUserDragging) {
            Timber.d("onTouchEvent() %b", mAllowUserDragging);
            return true;
        }

        return super.onTouchEvent(parent, child, event);
    }

    /**
     * A utility function to get the {@link BottomSheetBehavior} associated with the {@code view}.
     *
     * @param view The {@link View} with {@link BottomSheetBehavior}.
     * @return The {@link BottomSheetBehavior} associated with the {@code view}.
     */
    @SuppressWarnings("unchecked")
    public static <V extends View> WABottomSheetBehavior<V> from(V view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (!(params instanceof CoordinatorLayout.LayoutParams)) {
            throw new IllegalArgumentException("The view is not a child of CoordinatorLayout");
        }
        CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) params)
                .getBehavior();
        if (!(behavior instanceof WABottomSheetBehavior)) {
            throw new IllegalArgumentException(
                    "The view is not associated with BottomSheetBehavior");
        }
        return (WABottomSheetBehavior<V>) behavior;
    }
}

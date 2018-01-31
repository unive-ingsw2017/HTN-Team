package com.greenteadev.unive.clair.ui.widget.bottomsheet;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;

import it.greenteadev.unive.clair.R;

/**
 * ~ Licensed under the Apache License, Version 2.0 (the "License");
 * ~ you may not use this file except in compliance with the License.
 * ~ You may obtain a copy of the License at
 * ~
 * ~      http://www.apache.org/licenses/LICENSE-2.0
 * ~
 * ~ Unless required by applicable law or agreed to in writing, software
 * ~ distributed under the License is distributed on an "AS IS" BASIS,
 * ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * ~ See the License for the specific language governing permissions and
 * ~ limitations under the License.
 * ~
 * ~ https://github.com/miguelhincapie/CustomBottomSheetBehavior
 */

/**
 * This class only cares about hide or unhide the FAB because the anchor behavior is something
 * already in FAB.
 */
public class ScrollAwareFABBehavior extends AppBarLayout.ScrollingViewBehavior {
    private static final boolean AUTO_HIDE_DEFAULT = true;
    private boolean mVisible = true;

    private boolean mAutoHideEnabled;

    public ScrollAwareFABBehavior() {
        super();
        mAutoHideEnabled = AUTO_HIDE_DEFAULT;
    }

    public ScrollAwareFABBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.FloatingActionButton_Behavior_Layout);
        mAutoHideEnabled = a.getBoolean(
                R.styleable.FloatingActionButton_Behavior_Layout_behavior_autoHide,
                AUTO_HIDE_DEFAULT);
        a.recycle();
    }

    /**
     * Sets whether the associated FloatingActionButton automatically hides when there is
     * not enough space to be displayed. This works with {@link AppBarLayout}
     * and {@link BottomSheetBehavior}.
     *
     * @attr ref android.support.design.R.styleable#FloatingActionButton_Behavior_Layout_behavior_autoHide
     * @param autoHide true to enable automatic hiding
     */
    public void setAutoHideEnabled(boolean autoHide) {
        mAutoHideEnabled = autoHide;
    }

    /**
     * Returns whether the associated FloatingActionButton automatically hides when there is
     * not enough space to be displayed.
     *
     * @attr ref android.support.design.R.styleable#FloatingActionButton_Behavior_Layout_behavior_autoHide
     * @return true if enabled
     */
    public boolean isAutoHideEnabled() {
        return mAutoHideEnabled;
    }

    @Override
    public Parcelable onSaveInstanceState(CoordinatorLayout parent, View child) {
        return new ScrollAwareToolbarBehavior.SavedState(super.onSaveInstanceState(parent, child), mVisible);
    }

    @Override
    public void onRestoreInstanceState(CoordinatorLayout parent, View child, Parcelable state) {
        ScrollAwareToolbarBehavior.SavedState ss = (ScrollAwareToolbarBehavior.SavedState) state;
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
        if(child instanceof FloatingActionButton) {
            try {
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(dependency);
                return setToolbarVisible((FloatingActionButton) child, dependency.getTop() >= dependency.getHeight() - behavior.getPeekHeight());

            } catch (IllegalArgumentException e) {
                return false;
            }
        }

        return false;
    }

    private boolean shouldUpdateVisibility(View child) {
        if (!isAutoHideEnabled()) {
            return false;
        }

        //noinspection RedundantIfStatement
        if (child.getVisibility() != View.VISIBLE && mVisible) {
            // The view isn't set to be visible so skip changing its visibility
            return false;
        }

        return true;
    }

    private boolean setToolbarVisible(final FloatingActionButton child, final boolean visible) {

        if (!shouldUpdateVisibility(child)) {
            return false;
        }

        if (visible)
            child.show();
        else
            child.hide();

        mVisible = visible;
        return true;
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

        public static final Parcelable.Creator<ScrollAwareToolbarBehavior.SavedState> CREATOR =
                new Parcelable.Creator<ScrollAwareToolbarBehavior.SavedState>() {
                    @Override
                    public ScrollAwareToolbarBehavior.SavedState createFromParcel(Parcel source) {
                        return new ScrollAwareToolbarBehavior.SavedState(source);
                    }

                    @Override
                    public ScrollAwareToolbarBehavior.SavedState[] newArray(int size) {
                        return new ScrollAwareToolbarBehavior.SavedState[size];
                    }
                };
    }
}
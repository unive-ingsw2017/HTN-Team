package it.greenteadev.unive.gagga.lib;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;

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
public class AnchorAwareFABBehavior extends FloatingActionButton.Behavior {
    private boolean mVisible = true;

    public AnchorAwareFABBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        try {
            BottomSheetAnchorBehavior<View> behavior = BottomSheetAnchorBehavior.from(dependency);
            updateFabVisibilityForBottomSheet(dependency, child, behavior);
            return true;
        } catch (IllegalArgumentException e) {
            return super.onDependentViewChanged(parent, child, dependency);
        }
    }

    private boolean shouldUpdateVisibility(View dependency, FloatingActionButton child) {
        final CoordinatorLayout.LayoutParams lp =
                (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        if (!super.isAutoHideEnabled()) {
            return false;
        }

        if (lp.getAnchorId() != dependency.getId()) {
            // The anchor ID doesn't match the dependency, so we won't automatically
            // show/hide the FAB
            return false;
        }

        //noinspection RedundantIfStatement
        if (child.getVisibility() != View.VISIBLE && mVisible) {
            // The view isn't set to be visible so skip changing its visibility
            return false;
        }

        return true;
    }

    private boolean updateFabVisibilityForBottomSheet(View bottomSheet, FloatingActionButton child,
                                                      BottomSheetAnchorBehavior<View> behavior) {
        if (!shouldUpdateVisibility(bottomSheet, child)) {
            return false;
        }

        if (bottomSheet.getY() >= bottomSheet.getHeight() - behavior.getAnchorHeight()||
                behavior.getState() == BottomSheetAnchorBehavior.STATE_ANCHORED) {
            child.show();
            mVisible = true;
        } else {
            child.hide();
            mVisible = false;
        }
        return true;
    }
}
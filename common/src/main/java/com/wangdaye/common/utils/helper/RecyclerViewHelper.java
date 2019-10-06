package com.wangdaye.common.utils.helper;

import android.content.Context;

import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.wangdaye.common.utils.DisplayUtils;
import com.wangdaye.component.ComponentFactory;

public class RecyclerViewHelper {

    public static StaggeredGridLayoutManager getDefaultStaggeredGridLayoutManager(Context context) {
        return getDefaultStaggeredGridLayoutManager(getGirdColumnCount(context));
    }

    public static StaggeredGridLayoutManager getDefaultStaggeredGridLayoutManager(int column) {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(
                column, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        return layoutManager;
    }

    public static int getGirdColumnCount(Context context) {
        if (DisplayUtils.isLandscape(context)) {
            if (ComponentFactory.getSettingsService().isShowGridInLand()) {
                if (DisplayUtils.isTabletDevice(context)) {
                    return 3;
                } else {
                    return 2;
                }
            } else {
                return 1;
            }
        } else  {
            if (ComponentFactory.getSettingsService().isShowGridInPort()
                    && DisplayUtils.isTabletDevice(context)) {
                return 2;
            } else {
                return 1;
            }
        }
    }
}

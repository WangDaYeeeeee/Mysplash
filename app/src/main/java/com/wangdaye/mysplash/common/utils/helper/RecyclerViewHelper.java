package com.wangdaye.mysplash.common.utils.helper;

import android.content.Context;

import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.manager.SettingsOptionManager;

public class RecyclerViewHelper {

    public static int getGirdColumnCount(Context context) {
        if (DisplayUtils.isLandscape(context)) {
            if (SettingsOptionManager.getInstance(context).isShowGridInLand()) {
                if (DisplayUtils.isTabletDevice(context)) {
                    return 3;
                } else {
                    return 2;
                }
            } else {
                return 1;
            }
        } else  {
            if (SettingsOptionManager.getInstance(context).isShowGridInPort()
                    && DisplayUtils.isTabletDevice(context)) {
                return 2;
            } else {
                return 1;
            }
        }
    }
}

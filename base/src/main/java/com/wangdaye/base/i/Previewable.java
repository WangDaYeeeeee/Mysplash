package com.wangdaye.base.i;

import android.content.Context;

/**
 * Previewable.
 * */

public interface Previewable {

    String getRegularUrl(Context context);
    String getFullUrl(Context context);
    String getDownloadUrl(Context context);

    int getWidth(Context context);
    int getHeight(Context context);
}

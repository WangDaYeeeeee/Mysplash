package com.wangdaye.mysplash.common._basic;

/**
 * Previewable.
 *
 * If an Object needs to be sent to {@link com.wangdaye.mysplash.common.ui.activity.PreviewActivity},
 * it should implement this interface.
 *
 * */

public interface Previewable {

    String getRegularUrl();
    String getFullUrl();
    String getDownloadUrl();

    int getWidth();
    int getHeight();
}

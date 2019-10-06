package com.wangdaye.base.i;

/**
 * Previewable.
 * */

public interface Previewable {

    String getRegularUrl();
    String getFullUrl();
    String getDownloadUrl();

    int getWidth();
    int getHeight();
}

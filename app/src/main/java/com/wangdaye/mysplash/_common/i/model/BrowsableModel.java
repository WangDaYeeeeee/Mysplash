package com.wangdaye.mysplash._common.i.model;

import android.net.Uri;

/**
 * Browsable model.
 * */

public interface BrowsableModel {

    Uri getIntentUri();
    boolean isBrowsable();

    String getBrowsableDataKey();
    Object getService();
}

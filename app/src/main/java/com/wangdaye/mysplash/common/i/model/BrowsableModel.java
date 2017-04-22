package com.wangdaye.mysplash.common.i.model;

import android.net.Uri;

import java.util.List;

/**
 * Browsable model.
 *
 * Model for {@link com.wangdaye.mysplash.common.i.view.BrowsableView}.
 *
 * */

public interface BrowsableModel {

    /**
     * Get if the browsable activity is opened from website and need to request data.
     *
     * @return If it is browsable.
     * */
    boolean isBrowsable();

    /**
     * Get the Service class to request the browsable data.
     *
     * @return Service class.
     * */
    Object getService();

    /**
     * Get the uri from intent which is used to start the browsable activity.
     *
     * @return The uri.
     * */
    Uri getIntentUri();

    /**
     * Get the key words in browsable intent data, like a username, or a photo id.
     *
     * @return Key words.
     * */
    List<String> getBrowsableDataKey();
}

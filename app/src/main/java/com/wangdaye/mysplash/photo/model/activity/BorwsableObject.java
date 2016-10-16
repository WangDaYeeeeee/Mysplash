package com.wangdaye.mysplash.photo.model.activity;

import android.content.Intent;
import android.net.Uri;

import com.wangdaye.mysplash._common.data.service.PhotoService;
import com.wangdaye.mysplash._common.i.model.BrowsableModel;

/**
 * Browsable object.
 * */

public class BorwsableObject
        implements BrowsableModel {
    // data
    private Uri intentUri;
    private PhotoService service;

    /** <br> life cycle. */

    public BorwsableObject(Intent intent) {
        if (intent.getDataString() == null) {
            intentUri = null;
        } else {
            intentUri = Uri.parse(intent.getDataString());
        }
        service = PhotoService.getService();
    }

    /** <br> model. */

    @Override
    public Uri getIntentUri() {
        return intentUri;
    }

    @Override
    public boolean isBrowsable() {
        return intentUri != null;
    }

    @Override
    public String getBrowsableDataKey() {
        return intentUri.getLastPathSegment();
    }

    @Override
    public Object getService() {
        return service;
    }
}

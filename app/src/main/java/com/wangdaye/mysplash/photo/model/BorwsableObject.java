package com.wangdaye.mysplash.photo.model;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.wangdaye.mysplash._common.data.service.PhotoInfoService;
import com.wangdaye.mysplash._common.i.model.BrowsableModel;
import com.wangdaye.mysplash.photo.view.activity.PhotoActivity;

/**
 * Browsable object.
 * */

public class BorwsableObject
        implements BrowsableModel {
    // data
    private Uri intentUri;
    private PhotoInfoService service;

    /** <br> life cycle. */

    public BorwsableObject(Intent intent) {
        if (intent.getDataString() == null) {
            String id = intent.getStringExtra(PhotoActivity.KEY_PHOTO_ACTIVITY_ID);
            if (TextUtils.isEmpty(id)) {
                intentUri = null;
            } else {
                intentUri = Uri.parse("https://unsplash.com/photos/" + id);
            }
        } else {
            intentUri = Uri.parse(intent.getDataString());
        }
        service = PhotoInfoService.getService();
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

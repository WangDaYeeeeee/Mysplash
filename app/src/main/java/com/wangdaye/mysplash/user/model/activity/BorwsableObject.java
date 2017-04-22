package com.wangdaye.mysplash.user.model.activity;

import android.content.Intent;
import android.net.Uri;

import com.wangdaye.mysplash.common.data.service.UserService;
import com.wangdaye.mysplash.common.i.model.BrowsableModel;

import java.util.List;

/**
 * Browsable object.
 * */

public class BorwsableObject
        implements BrowsableModel {

    private Uri intentUri;
    private UserService service;

    public BorwsableObject(Intent intent) {
        if (intent.getDataString() == null) {
            intentUri = null;
        } else {
            intentUri = Uri.parse(intent.getDataString());
        }
        service = UserService.getService();
    }

    @Override
    public Uri getIntentUri() {
        return intentUri;
    }

    @Override
    public boolean isBrowsable() {
        return intentUri != null;
    }

    @Override
    public List<String> getBrowsableDataKey() {
        return intentUri.getPathSegments();
    }

    @Override
    public Object getService() {
        return service;
    }
}

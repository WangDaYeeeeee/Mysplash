package com.wangdaye.mysplash.collection.model.activity;

import android.content.Intent;
import android.net.Uri;

import com.wangdaye.mysplash._common.data.service.CollectionService;
import com.wangdaye.mysplash._common.i.model.BrowsableModel;

import java.util.List;

/**
 * Browsable object.
 * */

public class BorwsableObject
        implements BrowsableModel {
    // data
    private Uri intentUri;
    private CollectionService service;

    /** <br> life cycle. */

    public BorwsableObject(Intent intent) {
        if (intent.getDataString() == null) {
            intentUri = null;
        } else {
            intentUri = Uri.parse(intent.getDataString());
        }
        service = CollectionService.getService();
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
        List<String> segmentList = intentUri.getPathSegments();
        StringBuilder result = new StringBuilder(segmentList.get(0));
        for (int i = 1; i < segmentList.size(); i ++) {
            result.append(",").append(segmentList.get(i));
        }
        return result.toString();
    }

    @Override
    public Object getService() {
        return service;
    }
}

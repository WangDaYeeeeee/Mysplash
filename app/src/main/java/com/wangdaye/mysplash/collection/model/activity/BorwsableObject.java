package com.wangdaye.mysplash.collection.model.activity;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.wangdaye.mysplash.common.data.service.network.CollectionService;
import com.wangdaye.mysplash.common.i.model.BrowsableModel;
import com.wangdaye.mysplash.collection.view.activity.CollectionActivity;

import java.util.List;

/**
 * Browsable object.
 * */

public class BorwsableObject
        implements BrowsableModel {

    private Uri intentUri;
    private CollectionService service;

    public BorwsableObject(Intent intent) {
        if (intent.getDataString() == null) {
            String id = intent.getStringExtra(CollectionActivity.KEY_COLLECTION_ACTIVITY_ID);
            if (TextUtils.isEmpty(id)) {
                intentUri = null;
            } else {
                int collectionId = Integer.parseInt(id);
                if (collectionId < 1000) {
                    intentUri = Uri.parse("https://unsplash.com/collections/curated/" + id);
                } else {
                    intentUri = Uri.parse("https://unsplash.com/collections/" + id);
                }
            }
        } else {
            intentUri = Uri.parse(intent.getDataString());
        }
        service = CollectionService.getService();
    }

    @Override
    public boolean isBrowsable() {
        return intentUri != null;
    }

    @Override
    public Object getService() {
        return service;
    }

    @Override
    public Uri getIntentUri() {
        return intentUri;
    }

    @Override
    public List<String> getBrowsableDataKey() {
        return intentUri.getPathSegments();
    }
}

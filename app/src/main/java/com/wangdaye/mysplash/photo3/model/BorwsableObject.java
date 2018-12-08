package com.wangdaye.mysplash.photo3.model;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.wangdaye.mysplash.common.data.service.network.PhotoService;
import com.wangdaye.mysplash.common.i.model.BrowsableModel;
import com.wangdaye.mysplash.photo3.view.activity.PhotoActivity3;

import java.util.ArrayList;
import java.util.List;

/**
 * Browsable object.
 * */

public class BorwsableObject
        implements BrowsableModel {

    private Uri intentUri;
    private PhotoService service;

    public BorwsableObject(Intent intent) {
        if (intent.getDataString() == null) {
            String id = intent.getStringExtra(PhotoActivity3.KEY_PHOTO_ACTIVITY_2_ID);
            if (TextUtils.isEmpty(id)) {
                intentUri = null;
            } else {
                intentUri = Uri.parse("https://unsplash.com/photos/" + id);
            }
        } else {
            intentUri = Uri.parse(intent.getDataString());
        }
        service = PhotoService.getService();
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
        List<String> resultList = new ArrayList<>();
        resultList.add(intentUri.getLastPathSegment());
        return resultList;
    }

    @Override
    public Object getService() {
        return service;
    }
}

package com.wangdaye.mysplash.main.model.fragment;

import com.wangdaye.mysplash._common.data.api.PhotoApi;
import com.wangdaye.mysplash._common.i.model.SearchBarModel;

/**
 * Search bar object.
 * */

public class SearchBarObject
        implements SearchBarModel {
    // data
    private String orientation;

    /** <br> life cycle. */

    public SearchBarObject() {
        orientation = PhotoApi.LANDSCAPE_ORIENTATION;
    }

    @Override
    public String getOrientation() {
        return orientation;
    }

    @Override
    public void setOrientation(String o) {
        orientation = o;
    }
}

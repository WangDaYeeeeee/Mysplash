package com.wangdaye.mysplash.main.model.fragment;

import com.wangdaye.mysplash.common.data.api.PhotoApi;
import com.wangdaye.mysplash.main.model.fragment.i.SearchModel;

/**
 * Search object.
 * */

public class SearchObject
        implements SearchModel {
    // data
    private String query = "";
    private String orientation = PhotoApi.LANDSCAPE_ORIENTATION;

    /** <br> model. */

    // query.

    @Override
    public String getQuery() {
        return query;
    }

    @Override
    public void setQuery(String q) {
        this.query = q;
    }

    // orientation.

    @Override
    public String getOrientation() {
        return orientation;
    }

    @Override
    public void setOrientation(String o) {
        this.orientation = o;
    }
}

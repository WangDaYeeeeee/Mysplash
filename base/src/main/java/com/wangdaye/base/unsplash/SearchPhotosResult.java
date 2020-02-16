package com.wangdaye.base.unsplash;

import java.io.Serializable;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * Search photos result.
 * */

public class SearchPhotosResult implements Serializable {

    /**
     * total : 237
     * total_pages : 12
     */
    public int total;
    public int total_pages;

    @Nullable public List<Photo> results;
}

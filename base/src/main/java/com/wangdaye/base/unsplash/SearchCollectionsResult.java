package com.wangdaye.base.unsplash;

import java.io.Serializable;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * Search collections result.
 * */

public class SearchCollectionsResult implements Serializable {

    /**
     * total : 237
     * total_pages : 12
     */
    public int total;
    public int total_pages;

    @Nullable public List<Collection> results;
}

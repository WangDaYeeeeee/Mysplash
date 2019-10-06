package com.wangdaye.base.unsplash;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * Search user result.
 * */

public class SearchUsersResult {

    /**
     * total : 237
     * total_pages : 12
     */
    public int total;
    public int total_pages;

    @Nullable public List<User> results;
}

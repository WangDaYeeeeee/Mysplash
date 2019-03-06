package com.wangdaye.mysplash.common.network.json;

import java.util.List;

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

    public List<User> results;
}

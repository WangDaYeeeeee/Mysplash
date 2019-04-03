package com.wangdaye.mysplash.common.basic.model;

import com.wangdaye.mysplash.common.ui.adapter.tag.TagAdapter;

/**
 * Tag.
 *
 * If an Object need to be displayed in a RecyclerView with
 * {@link TagAdapter}, it should implement this interface.
 *
 * */

public interface Tag {

    String getTitle();
    String getRegularUrl();
    String getThumbnailUrl();
}

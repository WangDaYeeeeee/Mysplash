package com.wangdaye.search.vm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wangdaye.base.resource.ListResource;

public interface SearchPagerViewModel<T> {

    boolean init(@NonNull ListResource<T> resource, String defaultQuery);

    @Nullable
    String getQuery();

    void setQuery(@Nullable String query);
}

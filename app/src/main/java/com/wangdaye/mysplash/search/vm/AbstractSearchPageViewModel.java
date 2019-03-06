package com.wangdaye.mysplash.search.vm;

import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.basic.vm.PagerViewModel;

import androidx.annotation.NonNull;

public abstract class AbstractSearchPageViewModel<T> extends PagerViewModel<T> {

    private String query;

    public void init(@NonNull ListResource<T> resource, String defaultQuery) {
        super.init(resource);
        if (query == null) {
            query = defaultQuery;
        }
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}

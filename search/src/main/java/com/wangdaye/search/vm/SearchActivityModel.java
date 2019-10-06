package com.wangdaye.search.vm;

import com.wangdaye.common.base.vm.PagerManageViewModel;

import javax.inject.Inject;

import androidx.lifecycle.MutableLiveData;

public class SearchActivityModel extends PagerManageViewModel {

    private MutableLiveData<String> searchQuery;

    @Inject
    public SearchActivityModel() {
        super();
        searchQuery = null;
    }

    public void init(int defaultPosition, String defaultQuery) {
        super.init(defaultPosition);
        if (searchQuery == null) {
            searchQuery = new MutableLiveData<>();
            searchQuery.setValue(defaultQuery);
        }
    }

    public MutableLiveData<String> getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }
}

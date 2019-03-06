package com.wangdaye.mysplash.search.repository;

import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.network.callback.Callback;
import com.wangdaye.mysplash.common.network.json.Collection;
import com.wangdaye.mysplash.common.network.json.SearchCollectionsResult;
import com.wangdaye.mysplash.common.network.service.SearchService;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

public class CollectionSearchPageViewRepository {

    private SearchService service;

    @Inject
    public CollectionSearchPageViewRepository(SearchService service) {
        this.service = service;
    }

    public void getCollections(@NonNull MutableLiveData<ListResource<Collection>> current,
                               String query, boolean refresh) {
        assert current.getValue() != null;
        if (refresh) {
            current.setValue(ListResource.refreshing(current.getValue()));
        } else {
            current.setValue(ListResource.loading(current.getValue()));
        }

        service.cancel();
        service.searchCollections(
                query,
                current.getValue().dataPage + 1,
                new Callback<SearchCollectionsResult>() {

                    @Override
                    public void onSucceed(SearchCollectionsResult searchCollectionsResult) {
                        if (refresh) {
                            current.setValue(
                                    ListResource.refreshSuccess(
                                            current.getValue(),
                                            searchCollectionsResult.results));
                        } else if (searchCollectionsResult.results.size() == current.getValue().perPage) {
                            current.setValue(
                                    ListResource.loadSuccess(
                                            current.getValue(),
                                            searchCollectionsResult.results));
                        } else {
                            current.setValue(
                                    ListResource.allLoaded(
                                            current.getValue(),
                                            searchCollectionsResult.results));
                        }
                    }

                    @Override
                    public void onFailed() {
                        if (refresh) {
                            current.setValue(ListResource.refreshError(current.getValue()));
                        } else {
                            current.setValue(ListResource.loadError(current.getValue()));
                        }
                    }
                });
    }

    public void cancel() {
        service.cancel();
    }
}

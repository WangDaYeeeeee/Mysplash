package com.wangdaye.search.repository;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.base.unsplash.Collection;
import com.wangdaye.base.unsplash.SearchCollectionsResult;
import com.wangdaye.common.network.observer.BaseObserver;
import com.wangdaye.common.network.service.SearchService;

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
                current.getValue().getRequestPage(),
                new BaseObserver<SearchCollectionsResult>() {
                    @Override
                    public void onSucceed(SearchCollectionsResult searchCollectionsResult) {
                        if (searchCollectionsResult.results == null) {
                            onFailed();
                            return;
                        }
                        if (refresh) {
                            current.setValue(
                                    ListResource.refreshSuccess(
                                            current.getValue(),
                                            searchCollectionsResult.results
                                    )
                            );
                        } else if (searchCollectionsResult.results.size() == current.getValue().perPage) {
                            current.setValue(
                                    ListResource.loadSuccess(
                                            current.getValue(),
                                            searchCollectionsResult.results
                                    )
                            );
                        } else {
                            current.setValue(
                                    ListResource.allLoaded(
                                            current.getValue(),
                                            searchCollectionsResult.results
                                    )
                            );
                        }
                    }

                    @Override
                    public void onFailed() {
                        current.setValue(ListResource.error(current.getValue()));
                    }
                }
        );
    }

    public void cancel() {
        service.cancel();
    }
}

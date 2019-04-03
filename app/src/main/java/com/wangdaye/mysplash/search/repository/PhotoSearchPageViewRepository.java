package com.wangdaye.mysplash.search.repository;

import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.network.json.SearchPhotosResult;
import com.wangdaye.mysplash.common.network.observer.BaseObserver;
import com.wangdaye.mysplash.common.network.service.SearchService;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

public class PhotoSearchPageViewRepository {

    private SearchService service;

    @Inject
    public PhotoSearchPageViewRepository(SearchService service) {
        this.service = service;
    }

    public void getPhotos(@NonNull MutableLiveData<ListResource<Photo>> current,
                          String query, boolean refresh) {
        assert current.getValue() != null;
        if (refresh) {
            current.setValue(ListResource.refreshing(current.getValue()));
        } else {
            current.setValue(ListResource.loading(current.getValue()));
        }

        service.cancel();
        service.searchPhotos(
                query,
                current.getValue().getRequestPage(),
                new BaseObserver<SearchPhotosResult>() {
                    @Override
                    public void onSucceed(SearchPhotosResult searchPhotosResult) {
                        if (searchPhotosResult.results == null) {
                            onFailed();
                            return;
                        }
                        if (refresh) {
                            current.setValue(
                                    ListResource.refreshSuccess(
                                            current.getValue(),
                                            searchPhotosResult.results
                                    )
                            );
                        } else if (searchPhotosResult.results.size() == current.getValue().perPage) {
                            current.setValue(
                                    ListResource.loadSuccess(
                                            current.getValue(),
                                            searchPhotosResult.results
                                    )
                            );
                        } else {
                            current.setValue(
                                    ListResource.allLoaded(
                                            current.getValue(),
                                            searchPhotosResult.results
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

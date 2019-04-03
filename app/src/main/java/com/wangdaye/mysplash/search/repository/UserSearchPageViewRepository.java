package com.wangdaye.mysplash.search.repository;

import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.network.json.User;
import com.wangdaye.mysplash.common.network.json.SearchUsersResult;
import com.wangdaye.mysplash.common.network.observer.BaseObserver;
import com.wangdaye.mysplash.common.network.service.SearchService;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

public class UserSearchPageViewRepository {

    private SearchService service;

    @Inject
    public UserSearchPageViewRepository(SearchService service) {
        this.service = service;
    }

    public void getUsers(@NonNull MutableLiveData<ListResource<User>> current,
                         String query, boolean refresh) {
        assert current.getValue() != null;
        if (refresh) {
            current.setValue(ListResource.refreshing(current.getValue()));
        } else {
            current.setValue(ListResource.loading(current.getValue()));
        }

        service.cancel();
        service.searchUsers(
                query,
                current.getValue().getRequestPage(),
                new BaseObserver<SearchUsersResult>() {
                    @Override
                    public void onSucceed(SearchUsersResult searchUsersResult) {
                        if (searchUsersResult.results == null) {
                            onFailed();
                            return;
                        }
                        if (refresh) {
                            current.setValue(
                                    ListResource.refreshSuccess(
                                            current.getValue(),
                                            searchUsersResult.results
                                    )
                            );
                        } else if (searchUsersResult.results.size() == current.getValue().perPage) {
                            current.setValue(
                                    ListResource.loadSuccess(
                                            current.getValue(),
                                            searchUsersResult.results
                                    )
                            );
                        } else {
                            current.setValue(
                                    ListResource.allLoaded(
                                            current.getValue(),
                                            searchUsersResult.results
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

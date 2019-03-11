package com.wangdaye.mysplash.main.following;

import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.network.observer.ListResourceObserver;
import com.wangdaye.mysplash.common.network.service.FeedService;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

public class FollowingFeedViewRepository {

    private FeedService service;

    @Inject
    public FollowingFeedViewRepository(FeedService service) {
        this.service = service;
    }

    public void getFollowingFeeds(@NonNull MutableLiveData<ListResource<Photo>> current,
                                  boolean refresh) {
        assert current.getValue() != null;
        if (refresh) {
            current.setValue(ListResource.refreshing(current.getValue()));
        } else {
            current.setValue(ListResource.loading(current.getValue()));
        }

        service.cancel();
        service.requestFollowingFeed(
                current.getValue().getRequestPage(),
                current.getValue().perPage,
                new ListResourceObserver<>(current, refresh));
    }

    public void cancel() {
        service.cancel();
    }
}

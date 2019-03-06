package com.wangdaye.mysplash.main.following;

import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.basic.vm.PagerViewModel;
import com.wangdaye.mysplash.common.network.json.Photo;

import javax.inject.Inject;

import androidx.annotation.NonNull;

/**
 * Following feed view model.
 * */
public class FollowingFeedViewModel extends PagerViewModel<Photo> {

    private FollowingFeedViewRepository repository;

    @Inject
    public FollowingFeedViewModel(FollowingFeedViewRepository repository) {
        super();
        this.repository = repository;
    }

    @Override
    public boolean init(@NonNull ListResource<Photo> resource) {
        if (super.init(resource)) {
            refresh();
            return true;
        }
        return false;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.cancel();
    }

    @Override
    public void refresh() {
        repository.getFollowingFeeds(getListResource(), true);
    }

    @Override
    public void load() {
        repository.getFollowingFeeds(getListResource(), false);
    }
}

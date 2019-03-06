package com.wangdaye.mysplash.me.vm;

import com.wangdaye.mysplash.me.repository.MyFollowUserViewRepository;

import javax.inject.Inject;

public class MyFollowingViewModel extends MyFollowerViewModel {

    @Inject
    public MyFollowingViewModel(MyFollowUserViewRepository repository) {
        super(repository);
    }

    @Override
    public void refresh() {
        getRepository().getFollowing(getListResource(), true);
    }

    @Override
    public void load() {
        getRepository().getFollowing(getListResource(), false);
    }
}

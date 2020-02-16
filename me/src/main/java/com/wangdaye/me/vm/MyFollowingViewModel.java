package com.wangdaye.me.vm;

import com.wangdaye.me.repository.MyFollowUserViewRepository;

import javax.inject.Inject;

public class MyFollowingViewModel extends MyFollowerViewModel {

    @Inject
    public MyFollowingViewModel(MyFollowUserViewRepository repository) {
        super(repository);
    }

    @Override
    public void refresh() {
        getRepository().getFollowing(this, true);
    }

    @Override
    public void load() {
        getRepository().getFollowing(this, false);
    }
}

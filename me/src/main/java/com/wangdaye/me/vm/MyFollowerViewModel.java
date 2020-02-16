package com.wangdaye.me.vm;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.base.vm.pager.UsersPagerViewModel;
import com.wangdaye.me.repository.MyFollowUserViewRepository;

import javax.inject.Inject;

import androidx.annotation.NonNull;

public class MyFollowerViewModel extends UsersPagerViewModel {

    private MyFollowUserViewRepository repository;

    @Inject
    public MyFollowerViewModel(MyFollowUserViewRepository repository) {
        super();
        this.repository = repository;
    }

    @Override
    public boolean init(@NonNull ListResource<User> resource) {
        if (super.init(resource)) {
            refresh();
            return true;
        }
        return false;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        getRepository().cancel();
    }

    @Override
    public void refresh() {
        getRepository().getFollowers(this, true);
    }

    @Override
    public void load() {
        getRepository().getFollowers(this, false);
    }

    MyFollowUserViewRepository getRepository() {
        return repository;
    }
}

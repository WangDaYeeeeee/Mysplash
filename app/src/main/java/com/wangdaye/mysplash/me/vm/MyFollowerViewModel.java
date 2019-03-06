package com.wangdaye.mysplash.me.vm;

import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.basic.vm.PagerViewModel;
import com.wangdaye.mysplash.me.repository.MyFollowUserViewRepository;
import com.wangdaye.mysplash.me.ui.MyFollowAdapter;

import javax.inject.Inject;

import androidx.annotation.NonNull;

public class MyFollowerViewModel extends PagerViewModel<MyFollowAdapter.MyFollowUser> {

    private MyFollowUserViewRepository repository;

    @Inject
    public MyFollowerViewModel(MyFollowUserViewRepository repository) {
        super();
        this.repository = repository;
    }

    @Override
    public boolean init(@NonNull ListResource<MyFollowAdapter.MyFollowUser> resource) {
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
        getRepository().getFollowers(getListResource(), true);
    }

    @Override
    public void load() {
        getRepository().getFollowers(getListResource(), false);
    }

    MyFollowUserViewRepository getRepository() {
        return repository;
    }
}

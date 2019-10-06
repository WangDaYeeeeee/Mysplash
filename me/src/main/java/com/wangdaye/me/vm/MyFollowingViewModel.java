package com.wangdaye.me.vm;

import com.wangdaye.common.presenter.event.UserEventResponsePresenter;
import com.wangdaye.me.repository.MyFollowUserViewRepository;

import javax.inject.Inject;

public class MyFollowingViewModel extends MyFollowerViewModel {

    @Inject
    public MyFollowingViewModel(MyFollowUserViewRepository repository,
                                UserEventResponsePresenter presenter) {
        super(repository, presenter);
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

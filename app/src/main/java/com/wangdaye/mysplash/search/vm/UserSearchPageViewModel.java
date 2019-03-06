package com.wangdaye.mysplash.search.vm;

import com.wangdaye.mysplash.common.network.json.User;
import com.wangdaye.mysplash.search.repository.UserSearchPageViewRepository;

import javax.inject.Inject;

public class UserSearchPageViewModel extends AbstractSearchPageViewModel<User> {

    private UserSearchPageViewRepository repository;

    @Inject
    public UserSearchPageViewModel(UserSearchPageViewRepository repository) {
        super();
        this.repository = repository;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.cancel();
    }

    @Override
    public void refresh() {
        repository.getUsers(getListResource(), getQuery(), true);
    }

    @Override
    public void load() {
        repository.getUsers(getListResource(), getQuery(), false);
    }
}

package com.wangdaye.user.vm;

import android.text.TextUtils;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.base.unsplash.Collection;
import com.wangdaye.common.base.vm.pager.CollectionsPagerViewModel;
import com.wangdaye.user.repository.UserCollectionsViewRepository;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class UserCollectionsViewModel extends CollectionsPagerViewModel
        implements UserPagerViewModel<Collection> {

    private UserCollectionsViewRepository repository;
    private String username;

    @Inject
    public UserCollectionsViewModel(UserCollectionsViewRepository repository) {
        super();
        this.repository = repository;
    }

    @Override
    public boolean init(@NonNull ListResource<Collection> defaultResource, String defaultUsername) {
        if (super.init(defaultResource)) {
            setUsername(defaultUsername);
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
        if (TextUtils.isEmpty(getUsername())) {
            return;
        }
        repository.getUserCollections(this, getUsername(), true);
    }

    @Override
    public void load() {
        if (TextUtils.isEmpty(getUsername())) {
            return;
        }
        repository.getUserCollections(this, getUsername(), false);
    }

    @Nullable
    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(@Nullable String username) {
        this.username = username;
    }
}

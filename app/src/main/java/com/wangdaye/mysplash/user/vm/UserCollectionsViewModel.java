package com.wangdaye.mysplash.user.vm;

import android.text.TextUtils;

import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.basic.vm.PagerViewModel;
import com.wangdaye.mysplash.common.network.json.Collection;
import com.wangdaye.mysplash.user.repository.UserCollectionsViewRepository;

import javax.inject.Inject;

import androidx.annotation.NonNull;

public class UserCollectionsViewModel extends PagerViewModel<Collection> {

    private UserCollectionsViewRepository repository;
    private String username;

    @Inject
    public UserCollectionsViewModel(UserCollectionsViewRepository repository) {
        super();
        this.repository = repository;
    }

    public void init(@NonNull ListResource<Collection> defaultResource, String defaultUsername) {
        boolean init = super.init(defaultResource);

        if (username == null) {
            username = defaultUsername;
        }

        if (init) {
            refresh();
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        getRepository().cancel();
    }

    @Override
    public void refresh() {
        if (TextUtils.isEmpty(getUsername())) {
            return;
        }
        getRepository().getUserCollections(getListResource(), getUsername(), true);
    }

    @Override
    public void load() {
        if (TextUtils.isEmpty(getUsername())) {
            return;
        }
        getRepository().getUserCollections(getListResource(), getUsername(), false);
    }

    UserCollectionsViewRepository getRepository() {
        return repository;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

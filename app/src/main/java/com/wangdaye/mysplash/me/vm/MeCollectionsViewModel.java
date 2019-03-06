package com.wangdaye.mysplash.me.vm;

import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.basic.vm.PagerViewModel;
import com.wangdaye.mysplash.common.network.json.Collection;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.me.repository.MeCollectionsViewRepository;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MeCollectionsViewModel extends PagerViewModel<Collection> {

    private MeCollectionsViewRepository repository;

    @Nullable private String username;

    @Inject
    public MeCollectionsViewModel(MeCollectionsViewRepository repository) {
        super();
        this.repository = repository;
        this.username = null;
    }

    @Override
    public boolean init(@NonNull ListResource<Collection> resource) {
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
        setUsername(AuthManager.getInstance().getUsername());
        repository.getUserCollections(getListResource(), true);
    }

    @Override
    public void load() {
        setUsername(AuthManager.getInstance().getUsername());
        repository.getUserCollections(getListResource(), false);
    }

    MeCollectionsViewRepository getRepository() {
        return repository;
    }

    @Nullable
    public String getUsername() {
        return username;
    }

    protected void setUsername(@Nullable String username) {
        this.username = username;
    }
}

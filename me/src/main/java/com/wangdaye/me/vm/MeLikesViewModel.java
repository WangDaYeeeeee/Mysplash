package com.wangdaye.me.vm;

import com.wangdaye.common.utils.manager.AuthManager;
import com.wangdaye.me.repository.MePhotosViewRepository;

import javax.inject.Inject;

public class MeLikesViewModel extends MePhotosViewModel {

    @Inject
    public MeLikesViewModel(MePhotosViewRepository repository) {
        super(repository);
    }

    @Override
    public void refresh() {
        setUsername(AuthManager.getInstance().getUsername());
        getRepository().getUserLikes(this, true);
    }

    @Override
    public void load() {
        setUsername(AuthManager.getInstance().getUsername());
        getRepository().getUserLikes(this, false);
    }
}

package com.wangdaye.main.selected;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.common.base.vm.PagerViewModel;
import com.wangdaye.base.unsplash.Collection;

import javax.inject.Inject;

import androidx.annotation.NonNull;

public class SelectedViewModel extends PagerViewModel<Collection> {

    private SelectedViewRepository repository;

    @Inject
    public SelectedViewModel(SelectedViewRepository repository) {
        super();
        this.repository = repository;
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
        repository.cancel();
    }

    @Override
    public void refresh() {
        repository.getSelected(getListResource(), true);
    }

    @Override
    public void load() {
        repository.getSelected(getListResource(), false);
    }
}

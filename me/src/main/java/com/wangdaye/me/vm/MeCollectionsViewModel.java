package com.wangdaye.me.vm;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.base.unsplash.Collection;
import com.wangdaye.common.base.vm.pager.CollectionsPagerViewModel;
import com.wangdaye.common.bus.MessageBus;
import com.wangdaye.common.bus.event.CollectionEvent;
import com.wangdaye.common.utils.manager.AuthManager;
import com.wangdaye.me.repository.MeCollectionsViewRepository;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Me collections view model.
 * */
public class MeCollectionsViewModel extends CollectionsPagerViewModel
        implements MePagerViewModel<Collection>, Consumer<CollectionEvent> {

    private MeCollectionsViewRepository repository;
    private Disposable disposable;
    private String username;

    @Inject
    public MeCollectionsViewModel(MeCollectionsViewRepository repository) {
        super();
        this.repository = repository;
        this.disposable = MessageBus.getInstance()
                .toObservable(CollectionEvent.class)
                .subscribe(this);
    }

    @Override
    public boolean init(@NonNull ListResource<Collection> resource) {
        if (super.init(resource)) {
            setUsername(AuthManager.getInstance().getUsername());
            refresh();
            return true;
        }
        return false;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.cancel();
        disposable.dispose();
    }

    @Override
    public void refresh() {
        setUsername(AuthManager.getInstance().getUsername());
        repository.getUserCollections(this, true);
    }

    @Override
    public void load() {
        setUsername(AuthManager.getInstance().getUsername());
        repository.getUserCollections(this, false);
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

    // interface.

    @Override
    public void accept(CollectionEvent collectionEvent) {
        Collection collection = collectionEvent.collection;
        switch (collectionEvent.event) {
            case UPDATE:
                asynchronousWriteDataList((writer, resource) -> {
                    for (int i = 0; i < resource.dataList.size(); i ++) {
                        if (resource.dataList.get(i).id == collection.id) {
                            writer.postListResource(
                                    ListResource.changeItem(resource, collection, i)
                            );
                            break;
                        }
                    }
                });
                break;

            case CREATE:
                asynchronousWriteDataList((writer, resource) -> {
                    boolean exist = false;
                    for (int i = 0; i < resource.dataList.size(); i ++) {
                        if (resource.dataList.get(i).id == collection.id) {
                            exist = true;
                            break;
                        }
                    }

                    if (!exist) {
                        writer.postListResource(
                                ListResource.insertItem(resource, collection, 0));
                    }
                });
                break;

            case DELETE:
                asynchronousWriteDataList((writer, resource) -> {
                    for (int i = 0; i < resource.dataList.size(); i ++) {
                        if (resource.dataList.get(i).id == collection.id) {
                            writer.postListResource(
                                    ListResource.removeItem(resource, i)
                            );
                            break;
                        }
                    }
                });
                break;
        }
    }
}

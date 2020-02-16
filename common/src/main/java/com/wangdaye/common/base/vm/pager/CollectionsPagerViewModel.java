package com.wangdaye.common.base.vm.pager;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.base.unsplash.Collection;
import com.wangdaye.common.bus.MessageBus;
import com.wangdaye.common.bus.event.CollectionEvent;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public abstract class CollectionsPagerViewModel extends PagerViewModel<Collection>
        implements Consumer<CollectionEvent> {

    private Disposable disposable;

    public CollectionsPagerViewModel() {
        this.disposable = MessageBus.getInstance()
                .toObservable(CollectionEvent.class)
                .subscribe(this);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.dispose();
    }

    @Override
    public void accept(CollectionEvent collectionEvent) {
        switch (collectionEvent.event) {
            case CREATE:
                onCreateCollection(collectionEvent.collection);
                break;

            case UPDATE:
                onUpdateCollection(collectionEvent.collection);
                break;

            case DELETE:
                onDeleteCollection(collectionEvent.collection);
                break;
        }
    }

    protected void onCreateCollection(Collection collection) {
        // postListResource(ListResource.insertItem(getListResource(), collection, 0));
    }

    protected void onUpdateCollection(Collection collection) {
        asynchronousWriteDataList((writer, resource) -> {
            for (int i = 0; i < resource.dataList.size(); i ++) {
                if (resource.dataList.get(i).id == collection.id) {
                    writer.postListResource(ListResource.changeItem(resource, collection, i));
                }
            }
        });
    }

    protected void onDeleteCollection(Collection collection) {
        asynchronousWriteDataList((writer, resource) -> {
            for (int i = resource.dataList.size() - 1; i >= 0; i --) {
                if (resource.dataList.get(i).id == collection.id) {
                    writer.postListResource(ListResource.removeItem(resource, i));
                }
            }
        });
    }
}

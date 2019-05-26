package com.wangdaye.mysplash.search.vm;

import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.basic.vm.PagerViewModel;
import com.wangdaye.mysplash.common.bus.MessageBus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public abstract class AbstractSearchPageViewModel<T, E> extends PagerViewModel<T>
        implements Consumer<E> {

    @NonNull private Disposable disposable;
    @Nullable private String query;

    public AbstractSearchPageViewModel(Class<E> eventType) {
        this.disposable = MessageBus.getInstance()
                .toObservable(eventType)
                .subscribe(this);
        this.query = null;
    }

    public boolean init(@NonNull ListResource<T> resource, String defaultQuery) {
        if (query == null) {
            query = defaultQuery;
        }
        return super.init(resource);
    }

    @Override
    protected final boolean init(@NonNull ListResource<T> resource) {
        return init(resource, "");
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.dispose();
    }

    @Nullable
    public String getQuery() {
        return query;
    }

    public void setQuery(@NonNull String query) {
        this.query = query;
    }
}

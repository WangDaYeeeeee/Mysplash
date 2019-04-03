package com.wangdaye.mysplash.user.vm;

import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.basic.vm.PagerViewModel;
import com.wangdaye.mysplash.common.utils.bus.MessageBus;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public abstract class AbstractUserViewModel<T, E> extends PagerViewModel<T>
        implements Consumer<E> {

    @NonNull private Disposable disposable;
    @Nullable private String username;

    public AbstractUserViewModel(Class<E> eventType) {
        super();
        this.disposable = MessageBus.getInstance()
                .toObservable(eventType)
                .subscribe(this);
        this.username = null;
    }

    @CallSuper
    public boolean init(@NonNull ListResource<T> resource, String defaultUsername) {
        if (username == null) {
            username = defaultUsername;
        }
        return super.init(resource);
    }

    @Override
    protected final boolean init(@NonNull ListResource<T> resource) {
        return init(resource, null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.dispose();
    }

    @Nullable
    public String getUsername() {
        return username;
    }

    public void setUsername(@Nullable String username) {
        this.username = username;
    }
}

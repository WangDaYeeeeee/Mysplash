package com.wangdaye.me.vm;

import com.wangdaye.base.resource.Resource;
import com.wangdaye.common.base.vm.BrowsableViewModel;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.utils.manager.AuthManager;
import com.wangdaye.common.bus.MessageBus;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Emitter;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MeActivityModel extends BrowsableViewModel<User>
        implements AuthManager.OnAuthDataChangedListener, Consumer<User> {

    @NonNull private Disposable busDisposable;
    @Nullable private Disposable timerDisposable;

    private static final int DEFAULT_REQUEST_INTERVAL_SECOND = 5;

    @Inject
    public MeActivityModel() {
        super();
        this.busDisposable = MessageBus.getInstance()
                .toObservable(User.class)
                .subscribe(this);
    }

    public void init() {
        boolean init = super.init(Resource.success(AuthManager.getInstance().getUser()));

        if (init) {
            AuthManager.getInstance().addOnWriteDataListener(this);
            if (AuthManager.getInstance().getState() == AuthManager.State.FREE) {
                AuthManager.getInstance().requestPersonalProfile();
            }
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        AuthManager.getInstance().removeOnWriteDataListener(this);
        busDisposable.dispose();
        if (timerDisposable != null) {
            timerDisposable.dispose();
        }
    }

    // interface.

    // on auth data changed listener.

    @Override
    public void onUpdateAccessToken() {
        // do nothing.
    }

    @Override
    public void onUpdateUser() {
        setResource(Resource.success(AuthManager.getInstance().getUser()));
    }

    @Override
    public void onUpdateFailed() {
        if (AuthManager.getInstance().getUser() == null) {
            timerDisposable = Observable.create(Emitter::onComplete)
                    .delay(DEFAULT_REQUEST_INTERVAL_SECOND, TimeUnit.SECONDS)
                    .doOnComplete(() -> AuthManager.getInstance().requestPersonalProfile())
                    .subscribe();
        } else {
            setResource(Resource.success(AuthManager.getInstance().getUser()));
        }
    }

    @Override
    public void onLogout() {
        setResource(Resource.error(null));
    }

    // consumer.

    @Override
    public void accept(User user) {
        if (AuthManager.getInstance().getUser() != null
                && AuthManager.getInstance().getUser().username.equals(user.username)) {
            AuthManager.getInstance().updateUser(user);
        }
    }
}

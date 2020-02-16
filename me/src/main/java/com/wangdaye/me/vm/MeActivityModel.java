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

public class MeActivityModel extends BrowsableViewModel<User>
        implements AuthManager.OnAuthDataChangedListener {

    @NonNull private Disposable userEventDisposable;
    @Nullable private Disposable timerDisposable;

    private static final int DEFAULT_REQUEST_INTERVAL_SECOND = 5;

    @Inject
    public MeActivityModel() {
        super();
        this.userEventDisposable = MessageBus.getInstance()
                .toObservable(User.class)
                .subscribe(user -> {
                    User current = AuthManager.getInstance().getUser();
                    if (current != null && current.username.equals(user.username)) {
                        AuthManager.getInstance().updateUser(user);
                    }
                });
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
        userEventDisposable.dispose();
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
}

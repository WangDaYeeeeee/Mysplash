package com.wangdaye.mysplash.me.vm;

import com.wangdaye.mysplash.common.basic.model.Resource;
import com.wangdaye.mysplash.common.basic.vm.BrowsableViewModel;
import com.wangdaye.mysplash.common.network.json.User;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;

import javax.inject.Inject;

public class MeActivityModel extends BrowsableViewModel<User>
        implements AuthManager.OnAuthDataChangedListener {

    @Inject
    public MeActivityModel() {
        super();
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
    }

    // interface.

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
        AuthManager.getInstance().requestPersonalProfile();
    }

    @Override
    public void onLogout() {
        setResource(Resource.error(null));
    }
}

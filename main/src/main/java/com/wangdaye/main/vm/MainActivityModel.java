package com.wangdaye.main.vm;

import com.wangdaye.base.resource.Resource;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.base.vm.PagerManageViewModel;
import com.wangdaye.common.utils.manager.AuthManager;

import javax.inject.Inject;

import androidx.lifecycle.MutableLiveData;

/**
 * Main activity model.
 * */
public class MainActivityModel extends PagerManageViewModel
        implements AuthManager.OnAuthDataChangedListener {

    private MutableLiveData<Resource<User>> userResource;

    @Inject
    public MainActivityModel() {
        userResource = null;
    }

    public void init(Resource<User> resource, int defaultPosition) {
        super.init(defaultPosition);
        boolean init = false;

        if (userResource == null) {
            init = true;
            userResource = new MutableLiveData<>();
            userResource.setValue(resource);
        }

        if (init) {
            AuthManager.getInstance().addOnWriteDataListener(MainActivityModel.this);
            checkToRequestAuthInformation();
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        AuthManager.getInstance().removeOnWriteDataListener(this);
    }

    public void checkToRequestAuthInformation() {
        if (AuthManager.getInstance().isAuthorized()
                && AuthManager.getInstance().getState() == AuthManager.State.FREE
                && AuthManager.getInstance().getUser() == null) {
            AuthManager.getInstance().requestPersonalProfile();
        }
    }

    public MutableLiveData<Resource<User>> getUserResource() {
        return userResource;
    }

    // interface.

    @Override
    public void onUpdateAccessToken() {
        // do nothing.
    }

    @Override
    public void onUpdateUser() {
        userResource.setValue(Resource.success(AuthManager.getInstance().getUser()));
    }

    @Override
    public void onUpdateFailed() {
        userResource.setValue(Resource.error(null));
    }

    @Override
    public void onLogout() {
        userResource.setValue(Resource.error(null));
    }
}

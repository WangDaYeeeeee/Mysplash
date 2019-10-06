package com.wangdaye.main;

import com.wangdaye.base.resource.Resource;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.utils.manager.AuthManager;

import javax.inject.Inject;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Main activity model.
 * */
public class MainActivityModel extends ViewModel
        implements AuthManager.OnAuthDataChangedListener {

    private MutableLiveData<Integer> drawerSelectedId;
    private MutableLiveData<Resource<User>> userResource;

    @Inject
    public MainActivityModel() {
        drawerSelectedId = null;
        userResource = null;
    }

    public void init(int defaultId, Resource<User> resource) {
        boolean init = false;
        if (drawerSelectedId == null) {
            drawerSelectedId = new MutableLiveData<>();
            drawerSelectedId.setValue(defaultId);
            init = true;
        }

        if (userResource == null) {
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

    void checkToRequestAuthInformation() {
        if (AuthManager.getInstance().isAuthorized()
                && AuthManager.getInstance().getState() == AuthManager.State.FREE
                && AuthManager.getInstance().getUser() == null) {
            AuthManager.getInstance().requestPersonalProfile();
        }
    }

    MutableLiveData<Integer> getDrawerSelectedId() {
        return drawerSelectedId;
    }

    void selectDrawerItem(int id) {
        Integer currentId = drawerSelectedId.getValue();
        if (currentId != null && currentId != id) {
            drawerSelectedId.setValue(id);
        }
    }

    MutableLiveData<Resource<User>> getUserResource() {
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

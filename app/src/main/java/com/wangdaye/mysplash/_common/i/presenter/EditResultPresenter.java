package com.wangdaye.mysplash._common.i.presenter;

/**
 * Edit result presenter.
 * */

public interface EditResultPresenter {

    void createSomething(Object newKey);
    void updateSomething(Object newKey);
    void deleteSomething(Object oldKey);

    Object getEditKey();
}

package com.wangdaye.mysplash.photo.presenter.i;

import android.content.Context;
import android.view.View;

/**
 * Photo information presenter.
 * */

public interface PhotoInfoPresenter {

    void showWeb(Context c);
    void showAuthorInfo(Context c);
    void showMenu(Context c, View anchor);
}

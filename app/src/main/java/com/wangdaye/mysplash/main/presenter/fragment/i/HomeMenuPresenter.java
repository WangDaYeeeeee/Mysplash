package com.wangdaye.mysplash.main.presenter.fragment.i;

import android.content.Context;

/**
 * Home menu presenter.
 * */

public interface HomeMenuPresenter {

    void clickSearchItem();
    void clickOrderItem(Context c);
    void clickRandomItem(Context c);
    void clickNormalItem(Context c);
}

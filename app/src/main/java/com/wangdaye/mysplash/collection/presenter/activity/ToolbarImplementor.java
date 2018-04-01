package com.wangdaye.mysplash.collection.presenter.activity;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common._basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.i.presenter.ToolbarPresenter;
import com.wangdaye.mysplash.common.utils.ShareUtils;
import com.wangdaye.mysplash.collection.view.activity.CollectionActivity;

/**
 * Toolbar implementor.
 * */

public class ToolbarImplementor
        implements ToolbarPresenter {

    @Override
    public void touchNavigatorIcon(MysplashActivity a) {
        a.finishSelf(true);
    }

    @Override
    public void touchToolbar(MysplashActivity a) {
        // do nothing.
    }

    @Override
    public boolean touchMenuItem(MysplashActivity a, int itemId) {
        switch (itemId) {
            case R.id.action_share: {
                Collection c = ((CollectionActivity) a).getCollection();
                ShareUtils.shareCollection(c);
                break;
            }
            case R.id.action_menu: {
                ((CollectionActivity) a).showPopup();
                break;
            }
        }
        return true;
    }
}

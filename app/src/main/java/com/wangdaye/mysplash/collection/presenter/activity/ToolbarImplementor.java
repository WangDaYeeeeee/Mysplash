package com.wangdaye.mysplash.collection.presenter.activity;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common._basic.MysplashActivity;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.i.presenter.ToolbarPresenter;
import com.wangdaye.mysplash.common.ui.dialog.UpdateCollectionDialog;
import com.wangdaye.mysplash.common.utils.ShareUtils;
import com.wangdaye.mysplash.collection.view.activity.CollectionActivity;

/**
 * Toolbar implementor.
 * */

public class ToolbarImplementor
        implements ToolbarPresenter {

    /** <br> presenter. */

    @Override
    public void touchNavigatorIcon(MysplashActivity a) {
        a.finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
    }

    @Override
    public void touchToolbar(MysplashActivity a) {
        // do nothing.
    }

    @Override
    public boolean touchMenuItem(MysplashActivity a, int itemId) {
        switch (itemId) {
            case R.id.action_edit:
                UpdateCollectionDialog dialog = new UpdateCollectionDialog();
                dialog.setCollection(((CollectionActivity) a).getCollection());
                dialog.setOnCollectionChangedListener((CollectionActivity) a);
                dialog.show(a.getFragmentManager(), null);
                break;

            case R.id.action_share: {
                Collection c = ((CollectionActivity) a).getCollection();
                ShareUtils.shareCollection(c);
                break;
            }

            case R.id.action_download:
                ((CollectionActivity) a).downloadCollection();
                break;
        }
        return true;
    }
}

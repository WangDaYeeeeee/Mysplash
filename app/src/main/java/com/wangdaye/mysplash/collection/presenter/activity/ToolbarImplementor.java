package com.wangdaye.mysplash.collection.presenter.activity;

import android.os.Build;
import android.support.design.widget.Snackbar;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.ui._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash._common.utils.NotificationUtils;
import com.wangdaye.mysplash._common.utils.helper.DatabaseHelper;
import com.wangdaye.mysplash._common.utils.helper.DownloadHelper;
import com.wangdaye.mysplash._common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash._common.i.presenter.ToolbarPresenter;
import com.wangdaye.mysplash._common.ui.dialog.UpdateCollectionDialog;
import com.wangdaye.mysplash._common.utils.ShareUtils;
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
        ((CollectionActivity) a).backToTop();
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
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    ((CollectionActivity) a).downloadCollection();
                } else {
                    ((CollectionActivity) a).requestPermission(Mysplash.WRITE_EXTERNAL_STORAGE, itemId);
                }
                break;
        }
        return true;
    }
}

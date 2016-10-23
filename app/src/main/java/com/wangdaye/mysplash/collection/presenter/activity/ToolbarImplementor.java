package com.wangdaye.mysplash.collection.presenter.activity;

import android.app.Activity;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.utils.helper.DownloadHelper;
import com.wangdaye.mysplash._common.data.entity.Collection;
import com.wangdaye.mysplash._common.i.presenter.ToolbarPresenter;
import com.wangdaye.mysplash._common.ui.dialog.UpdateCollectionDialog;
import com.wangdaye.mysplash._common.ui.widget.SwipeBackLayout;
import com.wangdaye.mysplash._common.utils.ShareUtils;
import com.wangdaye.mysplash.collection.view.activity.CollectionActivity;

/**
 * Toolbar implementor.
 * */

public class ToolbarImplementor
        implements ToolbarPresenter {

    /** <br> presenter. */

    @Override
    public void touchNavigatorIcon(Activity a) {
        ((CollectionActivity) a).finishActivity(SwipeBackLayout.DOWN_DIR, false);
    }

    @Override
    public void touchToolbar(Activity a) {
        ((CollectionActivity) a).backToTop();
    }

    @Override
    public boolean touchMenuItem(Activity a, int itemId) {
        switch (itemId) {
            case R.id.action_edit:
                UpdateCollectionDialog dialog = new UpdateCollectionDialog();
                dialog.setCollection(((CollectionActivity) a).getCollection());
                dialog.setOnCollectionChangedListener((UpdateCollectionDialog.OnCollectionChangedListener) a);
                dialog.show(a.getFragmentManager(), null);
                break;

            case R.id.action_share: {
                Collection c = ((CollectionActivity) a).getCollection();
                ShareUtils.shareCollection(c);
                break;
            }

            case R.id.action_download:
                Collection c = ((CollectionActivity) a).getCollection();
                DownloadHelper.getInstance(a).downloadCollection(a, c);
                break;
        }
        return true;
    }
}

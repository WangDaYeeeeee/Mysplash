package com.wangdaye.common.presenter;

import com.wangdaye.base.unsplash.Collection;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.bus.MessageBus;
import com.wangdaye.common.bus.event.CollectionEvent;
import com.wangdaye.common.bus.event.PhotoEvent;
import com.wangdaye.common.ui.dialog.SelectCollectionDialog;
import com.wangdaye.common.utils.manager.AuthManager;

public class DispatchCollectionsChangedPresenter
        implements SelectCollectionDialog.OnCollectionsChangedListener {

    @Override
    public void onAddCollection(Collection c) {
        MessageBus.getInstance().post(new CollectionEvent(c, CollectionEvent.Event.CREATE));

        User user = AuthManager.getInstance().getUser();
        if (user != null) {
            user.total_collections ++;
            MessageBus.getInstance().post(user);
        }
    }

    @Override
    public void onUpdateCollection(Collection c, User u, Photo p) {
        MessageBus.getInstance().post(PhotoEvent.collectOrRemove(p, c));
        MessageBus.getInstance().post(new CollectionEvent(c, CollectionEvent.Event.UPDATE));
        MessageBus.getInstance().post(u);
    }
}

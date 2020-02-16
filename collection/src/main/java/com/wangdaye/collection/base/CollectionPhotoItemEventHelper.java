package com.wangdaye.collection.base;

import com.wangdaye.collection.CollectionActivity;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.collection.vm.CollectionPhotosViewModel;
import com.wangdaye.common.bus.MessageBus;
import com.wangdaye.common.bus.event.CollectionEvent;
import com.wangdaye.common.bus.event.PhotoEvent;
import com.wangdaye.common.ui.adapter.photo.PhotoItemEventHelper;
import com.wangdaye.common.ui.dialog.DeleteCollectionPhotoDialog;

public class CollectionPhotoItemEventHelper extends PhotoItemEventHelper {

    private CollectionActivity activity;

    public CollectionPhotoItemEventHelper(CollectionActivity activity, CollectionPhotosViewModel viewModel,
                                          DownloadExecutor executor) {
        super(activity, viewModel, executor);
        this.activity = activity;
    }

    @Override
    public void onDeleteButtonClicked(Photo photo, int adapterPosition) {
        DeleteCollectionPhotoDialog dialog = new DeleteCollectionPhotoDialog();
        dialog.setDeleteInfo(activity.getCollection(), photo);
        dialog.setOnDeleteCollectionListener(result -> {
            MessageBus.getInstance().post(result.user);

            MessageBus.getInstance().post(new CollectionEvent(
                    result.collection, CollectionEvent.Event.UPDATE));

            MessageBus.getInstance().post(PhotoEvent.collectOrRemove(result.photo, result.collection));
        });
        dialog.show(activity.getSupportFragmentManager(), null);
    }
}

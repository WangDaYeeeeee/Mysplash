package com.wangdaye.photo.ui.adapter.pager;

import android.content.Context;

import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.base.adapter.BaseAdapter;
import com.wangdaye.common.presenter.LikePhotoPresenter;
import com.wangdaye.component.ComponentFactory;

public class PagerModel implements BaseAdapter.ViewModel {

    protected Photo photo;
    protected String id;
    protected boolean liked;
    protected boolean likeProgressing;
    protected boolean collected;
    protected boolean downloading;

    public PagerModel(Context context, Photo photo) {
        this.photo = photo;
        this.id = photo.id;
        this.liked = photo.liked_by_user;
        this.likeProgressing = LikePhotoPresenter.getInstance().isInProgress(photo);
        this.collected = photo.current_user_collections != null && photo.current_user_collections.size() != 0;
        this.downloading = ComponentFactory.getDownloaderService().isDownloading(context, photo.id);
    }

    @Override
    public boolean areItemsTheSame(BaseAdapter.ViewModel newModel) {
        return newModel instanceof PagerModel && ((PagerModel) newModel).id.equals(id);
    }

    @Override
    public boolean areContentsTheSame(BaseAdapter.ViewModel newModel) {
        return newModel instanceof PagerModel
                && ((PagerModel) newModel).liked == liked
                && ((PagerModel) newModel).likeProgressing == likeProgressing
                && ((PagerModel) newModel).collected == collected
                && ((PagerModel) newModel).downloading == downloading;
    }

    @Override
    public Object getChangePayload(BaseAdapter.ViewModel newModel) {
        return 1;
    }
}

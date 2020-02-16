package com.wangdaye.photo.ui.adapter.photo.model;

import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.base.adapter.BaseAdapter;
import com.wangdaye.photo.ui.adapter.photo.PhotoInfoAdapter3;

public class InfoModel extends PhotoInfoAdapter3.ViewModel {

    public String views;
    public long viewsAnimDuration;

    public String downloads;
    public long downloadsAnimDuration;

    public String likes;
    public long likesAnimDuration;
    public boolean enableAnim;

    public InfoModel(Photo photo) {
        super(photo);
        views = String.valueOf(photo.views);
        viewsAnimDuration = (long) (2000 * tanh(photo.views / 30000));

        downloads = String.valueOf(photo.downloads);
        downloadsAnimDuration = (long) (2000 * tanh(photo.downloads / 3000));

        likes = String.valueOf(photo.likes);
        likesAnimDuration = (long) (2000 * tanh(photo.likes / 300));

        enableAnim = false;
    }

    private static float tanh(float x) {
        return (float) ((Math.exp(x) - Math.exp(-x)) / (Math.exp(x) + Math.exp(-x)));
    }

    @Override
    public boolean areItemsTheSame(BaseAdapter.ViewModel newModel) {
        return newModel instanceof InfoModel;
    }

    @Override
    public boolean areContentsTheSame(BaseAdapter.ViewModel newModel) {
        return ((InfoModel) newModel).views.equals(views)
                && ((InfoModel) newModel).downloads.equals(downloads)
                && ((InfoModel) newModel).likes.equals(likes);
    }

    @Override
    public Object getChangePayload(BaseAdapter.ViewModel newModel) {
        return null;
    }
}

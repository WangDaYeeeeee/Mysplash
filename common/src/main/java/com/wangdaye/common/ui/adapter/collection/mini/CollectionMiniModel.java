package com.wangdaye.common.ui.adapter.collection.mini;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.Size;

import com.wangdaye.base.unsplash.Collection;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.R;
import com.wangdaye.common.base.adapter.BaseAdapter;
import com.wangdaye.common.image.ImageHelper;

class CollectionMiniModel implements BaseAdapter.ViewModel {

    @Nullable String coverUrl;
    @Size int[] coverSize;
    String title;
    String subtitle;

    Collection collection;
    boolean progressing;
    boolean collected;
    boolean privateCollection;

    boolean header;

    CollectionMiniModel() {
        coverUrl = null;
        coverSize = new int[] {0, 0};
        title = "";
        subtitle = "";
        collection = null;
        progressing = false;
        collected = false;
        privateCollection = false;
        header = true;
    }

    CollectionMiniModel(Context context, Photo photo, Collection collection, boolean progressing) {
        if (collection.cover_photo != null) {
            coverUrl = collection.cover_photo.getRegularUrl();
            coverSize = collection.cover_photo.getRegularSize();
        } else {
            coverUrl = null;
            coverSize = new int[] {10, 6};
        }

        title = collection.title.toUpperCase();
        subtitle = collection.total_photos + " " + context.getResources().getStringArray(R.array.user_tabs)[0];

        this.collection = collection;
        this.progressing = progressing;
        this.collected = false;
        if (photo.current_user_collections != null) {
            for (Collection c : photo.current_user_collections) {
                if (collection.id == c.id) {
                    this.collected = true;
                    break;
                }
            }
        }
        this.privateCollection = collection.privateX;

        this.header = false;
    }

    @Override
    public boolean areItemsTheSame(BaseAdapter.ViewModel newModel) {
        if (!(newModel instanceof CollectionMiniModel)) {
            return false;
        }
        if (((CollectionMiniModel) newModel).header && header) {
            return true;
        } else if (!((CollectionMiniModel) newModel).header && !header) {
            return ((CollectionMiniModel) newModel).collection.id == collection.id;
        }
        return false;
    }

    @Override
    public boolean areContentsTheSame(BaseAdapter.ViewModel newModel) {
        if (!(newModel instanceof CollectionMiniModel)) {
            return false;
        }
        if (((CollectionMiniModel) newModel).header && header) {
            return true;
        } else if (!((CollectionMiniModel) newModel).header && !header) {
            return ImageHelper.isSameUrl(((CollectionMiniModel) newModel).coverUrl, coverUrl)
                    && ((CollectionMiniModel) newModel).title.equals(title)
                    && ((CollectionMiniModel) newModel).subtitle.equals(subtitle)
                    && ((CollectionMiniModel) newModel).progressing == progressing
                    && ((CollectionMiniModel) newModel).collected == collected
                    && ((CollectionMiniModel) newModel).privateCollection == privateCollection;
        }
        return false;
    }

    @Override
    public Object getChangePayload(BaseAdapter.ViewModel newModel) {
        if (!(newModel instanceof CollectionMiniModel)) {
            return null;
        }
        if (((CollectionMiniModel) newModel).header && header) {
            return null;
        } else if (!((CollectionMiniModel) newModel).header && !header) {
            if (ImageHelper.isSameUrl(((CollectionMiniModel) newModel).coverUrl, coverUrl)) {
                return 1;
            }
        }
        return null;
    }
}

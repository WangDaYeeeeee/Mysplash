package com.wangdaye.mysplash._common.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common._basic.MysplashActivity;
import com.wangdaye.mysplash.photo.view.activity.PhotoActivity;
import com.wangdaye.mysplash.photo.view.holder.BaseHolder;
import com.wangdaye.mysplash.photo.view.holder.ExifHolder;
import com.wangdaye.mysplash.photo.view.holder.MoreHolder;
import com.wangdaye.mysplash.photo.view.holder.ProgressHolder;
import com.wangdaye.mysplash.photo.view.holder.StoryHolder;
import com.wangdaye.mysplash.photo.view.holder.TagHolder;
import com.wangdaye.mysplash.photo.view.holder.TouchHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Photo info adapter.
 * */

public class PhotoInfoAdapter extends RecyclerView.Adapter<PhotoInfoAdapter.ViewHolder> {
    // widget
    private PhotoActivity a;
    private OnScrollListener scrollListener;

    // data
    private Photo photo;
    private List<Integer> typeList;
    private boolean complete;
    private boolean needShowInitAnim;
    private boolean moreImageHasFadedIn;

    /** <br> data. */

    public PhotoInfoAdapter(PhotoActivity a, Photo photo) {
        this.a = a;
        this.scrollListener = new OnScrollListener();
        this.photo = photo;
        this.complete = photo != null && photo.complete;
        this.needShowInitAnim = true;
        this.moreImageHasFadedIn = false;
        buildTypeList();
    }

    /** <br> UI. */

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TouchHolder.TYPE_TOUCH:
                return new TouchHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_touch, parent, false));

            case BaseHolder.TYPE_BASE:
                return new BaseHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_base, parent, false));

            case ProgressHolder.TYPE_PROGRESS:
                return new ProgressHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_progress, parent, false));

            case StoryHolder.TYPE_STORY:
                return new StoryHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_story, parent, false));

            case TagHolder.TYPE_TAG:
                return new TagHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_tag, parent, false));

            case MoreHolder.TYPE_MORE:
                return new MoreHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_more, parent, false));

            default:
                return new ExifHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_exif, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.onBindView(a, photo);
        if (needShowInitAnim && getItemViewType(position) == BaseHolder.TYPE_BASE) {
            needShowInitAnim = false;
            ((BaseHolder) holder).showInitAnim();
        } else if (getItemViewType(position) >= ExifHolder.TYPE_EXIF) {
            ((ExifHolder) holder).drawExif(getItemViewType(position), photo);
        } else if (getItemViewType(position) == TagHolder.TYPE_TAG) {
            ((TagHolder) holder).scrollTo(scrollListener.scrollX, 0);
            ((TagHolder) holder).setScrollListener(scrollListener);
        } else if (getItemViewType(position) == MoreHolder.TYPE_MORE) {
            ((MoreHolder) holder).loadMoreImage(a, photo, moreImageHasFadedIn, new MoreHolder.OnLoadImageCallback() {
                @Override
                public void onLoadImageSucceed() {
                    moreImageHasFadedIn = true;
                }
            });
        }
    }

    /** <br> data. */

    @Override
    public int getItemCount() {
        return typeList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return typeList.get(position);
    }

    private void buildTypeList() {
        typeList = new ArrayList<>();
        typeList.add(TouchHolder.TYPE_TOUCH);
        typeList.add(BaseHolder.TYPE_BASE);
        if (complete) {
            if (photo.story != null
                    && !TextUtils.isEmpty(photo.story.title)
                    && !TextUtils.isEmpty(photo.story.description)) {
                typeList.add(StoryHolder.TYPE_STORY);
            }
            for (int i = 0; i < 4; i ++) {
                typeList.add(ExifHolder.TYPE_EXIF + i);
            }
            typeList.add(TagHolder.TYPE_TAG);
            if ((photo.related_photos != null && photo.related_photos.results.size() > 0)
                    || (photo.related_collections != null && photo.related_collections.results.size() > 0)) {
                typeList.add(MoreHolder.TYPE_MORE);
            }
        } else {
            typeList.add(ProgressHolder.TYPE_PROGRESS);
        }
    }

    public void updatePhoto(Photo photo) {
        this.photo = photo;
        this.complete = photo != null;
        buildTypeList();
    }

    public boolean isComplete() {
        return complete;
    }

    /** <br> interface. */

    // on scroll swipeListener.

    private class OnScrollListener extends RecyclerView.OnScrollListener {
        // data
        int scrollX;

        OnScrollListener() {
            reset();
        }

        // data.

        void reset() {
            scrollX = 0;
        }

        // interface.

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            scrollX += dx;
        }
    }

    // on load image callback.

    /** <br> inner class. */

    public static abstract class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        protected abstract void onBindView(MysplashActivity a, Photo photo);
    }
}

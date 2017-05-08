package com.wangdaye.mysplash.common.ui.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common._basic.MysplashActivity;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.photo.view.activity.PhotoActivity;
import com.wangdaye.mysplash.photo.view.holder.BaseHolder;
import com.wangdaye.mysplash.photo.view.holder.BaseLandscapeHolder;
import com.wangdaye.mysplash.photo.view.holder.ExifHolder;
import com.wangdaye.mysplash.photo.view.holder.MoreHolder;
import com.wangdaye.mysplash.photo.view.holder.MoreLandscapeHolder;
import com.wangdaye.mysplash.photo.view.holder.ProgressHolder;
import com.wangdaye.mysplash.photo.view.holder.StoryHolder;
import com.wangdaye.mysplash.photo.view.holder.TagHolder;
import com.wangdaye.mysplash.photo.view.holder.TouchHolder;
import com.wangdaye.mysplash.photo.view.holder.TouchLandscapeHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Photo info adapter.
 *
 * Adapter for {@link RecyclerView} to show details of a photo.
 *
 * */

public class PhotoInfoAdapter extends RecyclerView.Adapter<PhotoInfoAdapter.ViewHolder> {

    private PhotoActivity a;
    private OnScrollListener tagListener, moreListener;

    private Photo photo;
    private List<Integer> typeList; // information of view holder.

    private boolean complete; // if true, means the photo object is completely. (has data like exif)
    private boolean needShowInitAnim; // need do the initialize animation when first bind basic view.

    private MoreHolder.MoreHolderModel moreHolderModel;

    public static abstract class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        protected abstract void onBindView(MysplashActivity a, Photo photo);

        protected abstract void onRecycled();
    }

    public static class SpanSizeLookup extends GridLayoutManager.SpanSizeLookup {

        private PhotoInfoAdapter adapter;
        private int columnCount;

        public SpanSizeLookup(PhotoInfoAdapter adapter, int columnCount) {
            this.adapter = adapter;
            this.columnCount = columnCount;
        }

        @Override
        public int getSpanSize(int position) {
            if (adapter.typeList.get(position) >= ExifHolder.TYPE_EXIF) {
                return 1;
            } else {
                return columnCount;
            }
        }
    }

    public PhotoInfoAdapter(PhotoActivity a, Photo photo) {
        this.a = a;
        this.tagListener = new OnScrollListener();
        this.moreListener = new OnScrollListener();
        this.photo = photo;
        this.complete = photo != null && photo.complete;
        this.needShowInitAnim = true;
        this.moreHolderModel = null;
        buildTypeList();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TouchHolder.TYPE_TOUCH:
                return new TouchHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_photo_touch, parent, false));

            case TouchLandscapeHolder.TYPE_TOUCH_LANDSCAPE:
                return new TouchLandscapeHolder(
                        a,
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_photo_touch_landscape, parent, false));

            case BaseHolder.TYPE_BASE:
                return new BaseHolder(
                        a,
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_photo_base, parent, false));

            case BaseLandscapeHolder.TYPE_BASE_LANDSCAPE:
                return new BaseLandscapeHolder(
                        a,
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_photo_base_landscape, parent, false));

            case ProgressHolder.TYPE_PROGRESS:
                return new ProgressHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_photo_progress, parent, false));

            case StoryHolder.TYPE_STORY:
                return new StoryHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_photo_story, parent, false));

            case TagHolder.TYPE_TAG:
                return new TagHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_photo_tag, parent, false),
                        a);

            case MoreHolder.TYPE_MORE:
                return new MoreHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_photo_more, parent, false),
                        photo,
                        moreHolderModel);

            case MoreLandscapeHolder.TYPE_MORE_LANDSCAPE:
                return new MoreLandscapeHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_photo_more_landscape, parent, false),
                        a);

            default:
                return new ExifHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_photo_exif, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.onBindView(a, photo);
        if (needShowInitAnim && getItemViewType(position) == BaseHolder.TYPE_BASE) {
            needShowInitAnim = false;
            ((BaseHolder) holder).showInitAnim();
        } else if (getItemViewType(position) >= ExifHolder.TYPE_EXIF) {
            ((ExifHolder) holder).drawExif(a, getItemViewType(position), photo);
        } else if (getItemViewType(position) == TagHolder.TYPE_TAG) {
            ((TagHolder) holder).scrollTo(tagListener.scrollX, 0);
            ((TagHolder) holder).setScrollListener(tagListener);
        } else if (getItemViewType(position) == MoreLandscapeHolder.TYPE_MORE_LANDSCAPE) {
            ((MoreLandscapeHolder) holder).scrollTo(moreListener.scrollX, 0);
            ((MoreLandscapeHolder) holder).setScrollListener(moreListener);
        }
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.onRecycled();
        if (holder instanceof MoreHolder) {
            this.moreHolderModel = ((MoreHolder) holder).saveModel();
        }
    }

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
        if (DisplayUtils.isLandscape(a)) {
            typeList.add(TouchLandscapeHolder.TYPE_TOUCH_LANDSCAPE);
            typeList.add(BaseLandscapeHolder.TYPE_BASE_LANDSCAPE);
        } else {
            typeList.add(TouchHolder.TYPE_TOUCH);
            typeList.add(BaseHolder.TYPE_BASE);
        }
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
            if (photo.related_collections != null && photo.related_collections.results.size() > 0) {
                if (DisplayUtils.isLandscape(a)) {
                    typeList.add(MoreLandscapeHolder.TYPE_MORE_LANDSCAPE);
                } else {
                    typeList.add(MoreHolder.TYPE_MORE);
                }
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

    // interface.

    // on scroll swipeListener.

    /**
     * A scroll listener to saved scroll position of the {@link TagHolder}.
     * */
    private class OnScrollListener extends RecyclerView.OnScrollListener {

        int scrollX;

        OnScrollListener() {
            reset();
        }

        void reset() {
            scrollX = 0;
        }

        // interface.

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            scrollX += dx;
        }
    }
}

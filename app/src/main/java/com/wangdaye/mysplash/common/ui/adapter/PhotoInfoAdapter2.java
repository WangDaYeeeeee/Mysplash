package com.wangdaye.mysplash.common.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.FooterAdapter;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.photo2.view.holder.BaseHolder;
import com.wangdaye.mysplash.photo2.view.holder.ExifHolder;
import com.wangdaye.mysplash.photo2.view.holder.InfoHolder;
import com.wangdaye.mysplash.photo2.view.holder.LocationHolder;
import com.wangdaye.mysplash.photo2.view.holder.MoreHolder;
import com.wangdaye.mysplash.photo2.view.holder.MoreLandscapeHolder;
import com.wangdaye.mysplash.photo2.view.holder.ProgressHolder;
import com.wangdaye.mysplash.photo2.view.holder.StoryHolder;
import com.wangdaye.mysplash.photo2.view.holder.TagHolder;
import com.wangdaye.mysplash.photo2.view.activity.PhotoActivity2;

import java.util.ArrayList;
import java.util.List;

/**
 * Photo info adapter.
 *
 * Adapter for {@link RecyclerView} to show details of a photo.
 *
 * */

public class PhotoInfoAdapter2 extends FooterAdapter<RecyclerView.ViewHolder> {

    private PhotoActivity2 a;
    private OnScrollListener tagListener, moreListener;

    private Photo photo;
    private List<Integer> typeList; // information of view holder.

    private int marginHorizontal;
    private int columnCount;

    private boolean complete; // if true, means the photo object is completely. (has data like exif)
    private boolean needShowInitAnim; // need do the initialize animation when first bind basic view.
    private boolean numberTextAnimEnable; // need to show animation when first bind info view.

    private MoreHolder.MoreHolderModel moreHolderModel;

    public static final int COLUMN_COUNT_VERTICAL = 2;
    public static final int COLUMN_COUNT_HORIZONTAL = 4;

    public static abstract class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView, int marginHorizontal, int columnCount) {
            super(itemView);
        }

        protected abstract void onBindView(PhotoActivity2 a, Photo photo);

        protected abstract void onRecycled();
    }

    public static class SpanSizeLookup extends GridLayoutManager.SpanSizeLookup {

        private PhotoInfoAdapter2 adapter;
        private int columnCount;

        public SpanSizeLookup(PhotoInfoAdapter2 adapter, int columnCount) {
            this.adapter = adapter;
            this.columnCount = columnCount;
        }

        @Override
        public int getSpanSize(int position) {
            if (position < adapter.typeList.size()
                    && adapter.typeList.get(position) >= ExifHolder.TYPE_EXIF) {
                return columnCount / 2;
            } else {
                return columnCount;
            }
        }
    }

    public PhotoInfoAdapter2(PhotoActivity2 a, Photo photo, int marginHorizontal, int columnCount) {
        this.a = a;
        this.tagListener = new OnScrollListener();
        this.moreListener = new OnScrollListener();
        this.photo = photo;
        this.marginHorizontal = marginHorizontal;
        this.columnCount = columnCount;
        this.complete = photo != null && photo.complete;
        this.needShowInitAnim = true;
        this.numberTextAnimEnable = true;
        this.moreHolderModel = null;
        buildTypeList();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case -1:
                return FooterHolder.buildInstance(parent);

            case BaseHolder.TYPE_BASE:
                return new BaseHolder(
                        a,
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_photo_2_base, parent, false),
                        marginHorizontal,
                        columnCount);

            case ProgressHolder.TYPE_PROGRESS:
                return new ProgressHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_photo_2_progress, parent, false),
                        marginHorizontal,
                        columnCount);

            case StoryHolder.TYPE_STORY:
                return new StoryHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_photo_2_story, parent, false),
                        marginHorizontal,
                        columnCount);

            case LocationHolder.TYPE_LOCATION:
                return new LocationHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_photo_2_location, parent, false),
                        marginHorizontal,
                        columnCount);

            case InfoHolder.TYPE_INFO:
                return new InfoHolder(
                        a,
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_photo_2_info, parent, false),
                        marginHorizontal,
                        columnCount);

            case TagHolder.TYPE_TAG:
                return new TagHolder(
                        a,
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_photo_2_tag, parent, false),
                        marginHorizontal,
                        columnCount);

            case MoreHolder.TYPE_MORE:
                return new MoreHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_photo_2_more, parent, false),
                        photo, moreHolderModel, marginHorizontal, columnCount);

            case MoreLandscapeHolder.TYPE_MORE_LANDSCAPE:
                return new MoreLandscapeHolder(
                        a,
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_photo_2_more_landscape, parent, false),
                        marginHorizontal,
                        columnCount);

            default:
                if (columnCount == COLUMN_COUNT_HORIZONTAL) {
                    return new ExifHolder(
                            LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_photo_2_exif_horizontal, parent, false),
                            marginHorizontal,
                            columnCount,
                            viewType);
                } else {
                    return new ExifHolder(
                            LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_photo_2_exif, parent, false),
                            marginHorizontal,
                            columnCount,
                            viewType);
                }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).onBindView(a, photo);
            if (needShowInitAnim && getItemViewType(position) == BaseHolder.TYPE_BASE) {
                needShowInitAnim = false;
                ((BaseHolder) holder).onScrolling(0);
                ((BaseHolder) holder).showInitAnim();
            } else if (numberTextAnimEnable && getItemViewType(position) == InfoHolder.TYPE_INFO) {
                numberTextAnimEnable = false;
                ((InfoHolder) holder).setEnableAnim(false);
            } else if (getItemViewType(position) >= ExifHolder.TYPE_EXIF) {
                ((ExifHolder) holder).drawExif(a, getItemViewType(position), photo);
            } else if (getItemViewType(position) == TagHolder.TYPE_TAG) {
                ((TagHolder) holder).scrollTo(tagListener.scrollX, 0);
                ((TagHolder) holder).setScrollListener(tagListener);
            } else if (getItemViewType(position) == MoreLandscapeHolder.TYPE_MORE_LANDSCAPE) {
                ((MoreLandscapeHolder) holder).scrollTo(moreListener.scrollX, 0);
                ((MoreLandscapeHolder) holder).setScrollListener(moreListener);
            }
        } else if (holder instanceof FooterHolder) {
            ((FooterHolder) holder).setColor(ThemeManager.getRootColor(a));
        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).onRecycled();
            if (holder instanceof MoreHolder) {
                this.moreHolderModel = ((MoreHolder) holder).saveModel();
            }
        }
    }

    @Override
    protected boolean hasFooter() {
        return !DisplayUtils.isLandscape(a)
                && DisplayUtils.getNavigationBarHeight(a.getResources()) != 0
                && (!photo.complete
                || photo.related_collections == null
                || photo.related_collections.results == null
                || photo.related_collections.results.size() == 0);
    }

    @Override
    public int getRealItemCount() {
        return typeList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < typeList.size()) {
            return typeList.get(position);
        } else {
            return -1;
        }
    }

    private void buildTypeList() {
        typeList = new ArrayList<>();
        typeList.add(BaseHolder.TYPE_BASE);
        if (complete) {
            if ((photo.story != null && !TextUtils.isEmpty(photo.story.title) && !TextUtils.isEmpty(photo.story.description))
                    || !TextUtils.isEmpty(photo.description)) {
                typeList.add(StoryHolder.TYPE_STORY);
            } else if (photo.location != null && !TextUtils.isEmpty(photo.location.title)) {
                typeList.add(LocationHolder.TYPE_LOCATION);
            }
            typeList.add(InfoHolder.TYPE_INFO);
            for (int i = 0; i < 8; i ++) {
                typeList.add(ExifHolder.TYPE_EXIF + i);
            }
            typeList.add(TagHolder.TYPE_TAG);
            if (photo.related_collections != null && photo.related_collections.results.size() > 0) {
                if (!DisplayUtils.isLandscape(a) && !DisplayUtils.isTabletDevice(a)) {
                    typeList.add(MoreHolder.TYPE_MORE);
                } else {
                    typeList.add(MoreLandscapeHolder.TYPE_MORE_LANDSCAPE);
                }
            }
        } else {
            typeList.add(ProgressHolder.TYPE_PROGRESS);
        }
    }

    public void reset(Photo photo) {
        updatePhoto(photo);
        this.needShowInitAnim = true;
        this.numberTextAnimEnable = true;
        this.moreHolderModel = null;
    }

    public void updatePhoto(Photo photo) {
        this.photo = photo;
        this.complete = photo.exif != null;
        buildTypeList();
    }

    public boolean isComplete() {
        return complete;
    }

    public int getMarginHorizontal() {
        return marginHorizontal;
    }

    public int getColumnCount() {
        return columnCount;
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
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            scrollX += dx;
        }
    }
}

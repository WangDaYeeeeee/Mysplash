package com.wangdaye.photo.ui.adapter.photo;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.base.adapter.BaseAdapter;
import com.wangdaye.photo.R;
import com.wangdaye.photo.activity.PhotoActivity;
import com.wangdaye.photo.ui.adapter.photo.holder.DragFlagHolder;
import com.wangdaye.photo.ui.adapter.photo.holder.ExifHolder;
import com.wangdaye.photo.ui.adapter.photo.holder.InfoHolder;
import com.wangdaye.photo.ui.adapter.photo.holder.LocationHolder;
import com.wangdaye.photo.ui.adapter.photo.holder.MoreHolder;
import com.wangdaye.photo.ui.adapter.photo.holder.ProgressHolder;
import com.wangdaye.photo.ui.adapter.photo.holder.StoryHolder;
import com.wangdaye.photo.ui.adapter.photo.holder.TagHolder;
import com.wangdaye.photo.ui.adapter.photo.model.DragFlagModel;
import com.wangdaye.photo.ui.adapter.photo.model.ExifModel;
import com.wangdaye.photo.ui.adapter.photo.model.InfoModel;
import com.wangdaye.photo.ui.adapter.photo.model.LocationModel;
import com.wangdaye.photo.ui.adapter.photo.model.MoreModel;
import com.wangdaye.photo.ui.adapter.photo.model.ProgressModel;
import com.wangdaye.photo.ui.adapter.photo.model.StoryModel;
import com.wangdaye.photo.ui.adapter.photo.model.TagModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Photo info adapter.
 *
 * Adapter for {@link RecyclerView} to show details of activity photo.
 *
 * */

public class PhotoInfoAdapter3 extends RecyclerView.Adapter<PhotoInfoAdapter3.ViewHolder> {

    private PhotoActivity activity;
    private List<ViewModel> viewModelList;
    private String photoId;
    private int columnCount;
    private List<ViewHolder.Factory> factoryList;

    private @Nullable TagModel tagModel;

    public static final int COLUMN_COUNT_VERTICAL = 2;
    public static final int COLUMN_COUNT_HORIZONTAL = 4;

    public static abstract class ViewModel implements BaseAdapter.ViewModel {

        public Photo photo;

        public ViewModel(Photo photo) {
            this.photo = photo;
        }
    }

    public static abstract class ViewHolder extends RecyclerView.ViewHolder {

        public interface Factory {

            @NonNull ViewHolder createHolder(@NonNull ViewGroup parent);
            boolean isMatch(ViewModel model);
        }

        public ViewHolder(ViewGroup parent, @LayoutRes int layoutId) {
            super(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
        }

        protected abstract void onBindView(PhotoActivity a, ViewModel viewModel);

        protected abstract void onRecycled();
    }

    public PhotoInfoAdapter3(PhotoActivity activity, Photo photo, boolean progressing, int columnCount) {
        super();

        this.activity = activity;
        this.viewModelList = getViewModelList(activity, photo, progressing, null);
        this.photoId = photo.id;
        this.columnCount = columnCount;

        this.factoryList = new ArrayList<>();
        factoryList.add(new ExifHolder.Factory());
        factoryList.add(new InfoHolder.Factory());
        factoryList.add(new LocationHolder.Factory());
        factoryList.add(new MoreHolder.Factory());
        factoryList.add(new ProgressHolder.Factory());
        factoryList.add(new StoryHolder.Factory());
        factoryList.add(new TagHolder.Factory());
        factoryList.add(new DragFlagHolder.Factory());

        this.tagModel = null;
    }

    @NonNull
    @Override
    public PhotoInfoAdapter3.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return factoryList.get(viewType).createHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ViewModel model = viewModelList.get(position);
        holder.onBindView(activity, model);
        if (model instanceof TagModel) {
            this.tagModel = (TagModel) model;
        }
    }

    @Override
    public void onViewRecycled(@NonNull PhotoInfoAdapter3.ViewHolder holder) {
        holder.onRecycled();
    }

    @Override
    public int getItemCount() {
        return viewModelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        for (int i = 0; i < factoryList.size(); i ++) {
            if (factoryList.get(i).isMatch(viewModelList.get(position))) {
                return i;
            }
        }

        throw new RuntimeException("Invalid type of ViewHolder.");
    }

    private static List<ViewModel> getViewModelList(PhotoActivity activity, Photo photo, boolean progressing,
                                                    @Nullable TagModel tagModel) {
        List<ViewModel> list = new ArrayList<>();
        list.add(new DragFlagModel(photo));
        if (photo.isComplete()) {
            if ((
                    photo.story != null
                            && !TextUtils.isEmpty(photo.story.title)
                            && !TextUtils.isEmpty(photo.story.description)
            ) || !TextUtils.isEmpty(photo.description)) {
                list.add(new StoryModel(activity, photo));
            } else if (photo.location != null && !TextUtils.isEmpty(photo.location.title)) {
                list.add(new LocationModel(photo));
            }

            list.add(new InfoModel(photo));
            list.addAll(buildExifModelList(activity, photo));
            list.add(new TagModel(photo, tagModel == null ? 0 : tagModel.scrollX));

            if (photo.related_collections != null && photo.related_collections.results.size() > 0) {
                for (int i = 0; i < photo.related_collections.results.size(); i ++) {
                    list.add(new MoreModel(photo, i));
                }
            }
        } else {
            list.add(new ProgressModel(photo, !progressing));
        }
        return list;
    }

    private static List<ViewModel> buildExifModelList(Context context, Photo photo) {
        List<ViewModel> list = new ArrayList<>(8);
        list.add(
                new ExifModel(
                        photo,
                        R.drawable.ic_camera,
                        context.getString(R.string.feedback_camera_make),
                        photo.exif.make == null ? "Unknown" : photo.exif.make));
        list.add(
                new ExifModel(
                        photo,
                        R.drawable.ic_film,
                        context.getString(R.string.feedback_camera_model),
                        photo.exif.model == null ? "Unknown" : photo.exif.model));
        list.add(
                new ExifModel(
                        photo,
                        R.drawable.ic_size,
                        context.getString(R.string.feedback_size),
                        photo.width + " × " + photo.height));
        list.add(
                new ExifModel(
                        photo,
                        R.drawable.ic_focal,
                        context.getString(R.string.feedback_focal),
                        photo.exif.focal_length == null ? "Unknown" : (photo.exif.focal_length + "mm")));
        list.add(
                new ExifModel(
                        photo,
                        R.drawable.ic_aperture,
                        context.getString(R.string.feedback_aperture),
                        photo.exif.aperture == null ? "Unknown" : ("f/" + photo.exif.aperture)));
        list.add(
                new ExifModel(
                        photo,
                        R.drawable.ic_exposure,
                        context.getString(R.string.feedback_exposure),
                        photo.exif.exposure_time == null ? "Unknown" : (photo.exif.exposure_time + "s")));
        list.add(
                new ExifModel(
                        photo,
                        R.drawable.ic_iso,
                        context.getString(R.string.feedback_iso),
                        photo.exif.iso == 0 ? "Unknown" : String.valueOf(photo.exif.iso)));
        list.add(
                new ExifModel(
                        photo,
                        R.drawable.ic_color,
                        context.getString(R.string.feedback_color),
                        photo.color,
                        Color.parseColor(photo.color)));
        return list;
    }

    public void update(Photo photo, boolean progressing) {
        photoId = photo.id;

        List<ViewModel> list = getViewModelList(activity, photo, progressing, tagModel);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return viewModelList.size();
            }

            @Override
            public int getNewListSize() {
                return list.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return viewModelList.get(oldItemPosition).areItemsTheSame(list.get(newItemPosition));
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return viewModelList.get(oldItemPosition).areContentsTheSame(list.get(newItemPosition));
            }
        }, false);
        viewModelList.clear();
        viewModelList.addAll(list);
        result.dispatchUpdatesTo(this);
    }

    public String getPhotoId() {
        return photoId;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public GridLayoutManager.SpanSizeLookup getSpanSizeLookup(boolean landscape) {
        return new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (viewModelList.get(position) instanceof ExifModel) {
                    return landscape ? columnCount / 2 : 1;
                } else {
                    return columnCount;
                }
            }
        };
    }
}

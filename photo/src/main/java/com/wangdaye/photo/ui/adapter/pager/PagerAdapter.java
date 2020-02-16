package com.wangdaye.photo.ui.adapter.pager;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.base.adapter.BaseAdapter;
import com.wangdaye.photo.activity.PhotoActivity;

import java.util.List;

public class PagerAdapter extends BaseAdapter<Photo, PagerModel, PagerHolder> {

    private boolean executeEnterTransition;

    public PagerAdapter(PhotoActivity activity, @NonNull List<Photo> list) {
        super(activity, list);
        this.executeEnterTransition = true;
    }

    @NonNull
    @Override
    public PagerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PagerHolder(parent);
    }

    @Override
    protected void onBindViewHolder(@NonNull PagerHolder holder, PagerModel model) {
        holder.onBindViewHolder((PhotoActivity) getContext(), model, false, executeEnterTransition);
        executeEnterTransition = false;
    }

    @Override
    protected void onBindViewHolder(@NonNull PagerHolder holder, PagerModel model,
                                    @NonNull List<Object> payloads) {
        holder.onBindViewHolder((PhotoActivity) getContext(), model, !payloads.isEmpty(), executeEnterTransition);
        executeEnterTransition = false;
    }

    @Override
    public void onViewRecycled(@NonNull PagerHolder holder) {
        holder.onRecycledView();
    }

    @Override
    protected PagerModel getViewModel(Photo model) {
        return new PagerModel(getContext(), model);
    }
}

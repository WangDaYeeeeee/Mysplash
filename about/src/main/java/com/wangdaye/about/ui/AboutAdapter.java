package com.wangdaye.about.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wangdaye.about.R;
import com.wangdaye.about.model.AboutModel;
import com.wangdaye.about.presenter.CreateAboutModelPresenter;
import com.wangdaye.about.ui.holder.AppHolder;
import com.wangdaye.about.ui.holder.CategoryHolder;
import com.wangdaye.about.ui.holder.HeaderHolder;
import com.wangdaye.about.ui.holder.LibraryHolder;
import com.wangdaye.about.ui.holder.TranslatorHolder;
import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.common.base.adapter.BaseAdapter;

import java.util.List;

/**
 * About adapter.
 *
 * Adapter for {@link RecyclerView} to show {@link AboutModel}.
 *
 * */

public class AboutAdapter extends BaseAdapter<AboutModel, AboutModel, AboutAdapter.ViewHolder> {

    private final MysplashActivity a;

    /**
     * Basic CollectionHolder class for {@link AboutAdapter}.
     * */
    public static abstract class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        protected abstract void onBindView(MysplashActivity a, AboutModel model);

        protected abstract void onRecycled();
    }

    public AboutAdapter(MysplashActivity a) {
        super(a, CreateAboutModelPresenter.createModelList(a));
        this.a = a;
    }

    @Override
    protected AboutModel getViewModel(AboutModel model) {
        return model;
    }

    @NonNull
    @Override
    public AboutAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case AboutModel.TYPE_HEADER:
                return new HeaderHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_about_header, parent, false));

            case AboutModel.TYPE_CATEGORY:
                return new CategoryHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_abuot_category, parent, false));

            case AboutModel.TYPE_APP:
                return new AppHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_about_app, parent, false));

            case AboutModel.TYPE_TRANSLATOR:
                return new TranslatorHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_about_translator, parent, false)
                );

            case AboutModel.TYPE_LIBRARY:
            default:
                return new LibraryHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_about_library, parent, false));
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, AboutModel model) {
        holder.onBindView(a, model);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, AboutModel model, @NonNull List<Object> payloads) {
        onBindViewHolder(holder, model);
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        holder.onRecycled();
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType();
    }
}

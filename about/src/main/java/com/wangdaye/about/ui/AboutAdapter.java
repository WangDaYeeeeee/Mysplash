package com.wangdaye.about.ui;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wangdaye.about.R;
import com.wangdaye.about.ui.holder.AppHolder;
import com.wangdaye.about.ui.holder.TranslatorHolder;
import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.about.model.AboutModel;
import com.wangdaye.about.presenter.CreateAboutModelPresenter;
import com.wangdaye.about.ui.holder.CategoryHolder;
import com.wangdaye.about.ui.holder.HeaderHolder;
import com.wangdaye.about.ui.holder.LibraryHolder;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * About adapter.
 *
 * Adapter for {@link RecyclerView} to show {@link AboutModel}.
 *
 * */

public class AboutAdapter extends RecyclerView.Adapter<AboutAdapter.ViewHolder> {

    private MysplashActivity a;
    private List<AboutModel> itemList;

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
        this.a = a;
        this.itemList = CreateAboutModelPresenter.createModelList(a);
    }

    @NotNull
    @Override
    public AboutAdapter.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
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
    public void onBindViewHolder(@NotNull AboutAdapter.ViewHolder holder, int position) {
        holder.onBindView(a, itemList.get(position));
    }

    @Override
    public void onViewRecycled(@NotNull ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.onRecycled();
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return itemList.get(position).getType();
    }
}

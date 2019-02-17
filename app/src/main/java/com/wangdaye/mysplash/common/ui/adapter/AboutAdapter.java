package com.wangdaye.mysplash.common.ui.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.i.model.AboutModel;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.about.presenter.CreateAboutModelImplementor;
import com.wangdaye.mysplash.about.view.holder.AppHolder;
import com.wangdaye.mysplash.about.view.holder.CategoryHolder;
import com.wangdaye.mysplash.about.view.holder.HeaderHolder;
import com.wangdaye.mysplash.about.view.holder.LibraryHolder;
import com.wangdaye.mysplash.about.view.holder.TranslatorHolder;

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
     * Basic ViewHolder class for {@link AboutAdapter}.
     * */
    public static abstract class ViewHolder extends RecyclerView.ViewHolder {

        // life cycle.

        public ViewHolder(View itemView) {
            super(itemView);
        }

        // UI.

        protected abstract void onBindView(MysplashActivity a, AboutModel model);

        protected abstract void onRecycled();
    }

    public AboutAdapter(MysplashActivity a) {
        this.a = a;
        this.itemList = CreateAboutModelImplementor.createModelList(a);
    }

    @Override
    public AboutAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (itemList.get(viewType).getType()) {
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
                return new LibraryHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_about_library, parent, false));

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(AboutAdapter.ViewHolder holder, int position) {
        holder.onBindView(a, itemList.get(position));
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.onRecycled();
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}

package com.wangdaye.mysplash._common.ui.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.i.model.AboutModel;
import com.wangdaye.mysplash.about.model.AppAboutObject;
import com.wangdaye.mysplash.about.model.CategoryAboutObject;
import com.wangdaye.mysplash.about.model.LibraryObject;
import com.wangdaye.mysplash.about.model.TranslatorObject;
import com.wangdaye.mysplash.about.presenter.CreateAboutModelImplementor;
import com.wangdaye.mysplash.about.view.holder.AppHolder;
import com.wangdaye.mysplash.about.view.holder.CategoryHolder;
import com.wangdaye.mysplash.about.view.holder.HeaderHolder;
import com.wangdaye.mysplash.about.view.holder.LibraryHolder;
import com.wangdaye.mysplash.about.view.holder.TranslatorHolder;

import java.util.List;

/**
 * About adapter. (Recycler view)
 * */

public class AboutAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // widget
    private Activity a;
    private List<AboutModel> itemList;

    /** <br> data. */

    public AboutAdapter(Activity a) {
        this.a = a;
        this.itemList = CreateAboutModelImplementor.createModelList(a);
    }

    /** <br> UI. */

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (itemList.get(viewType).getType()) {
            case AboutModel.TYPE_HEADER:
                return new HeaderHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_about_header, parent, false));

            case AboutModel.TYPE_CATEGORY:
                return new CategoryHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_abuot_category, parent, false),
                        (CategoryAboutObject) itemList.get(viewType));

            case AboutModel.TYPE_APP:
                return new AppHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_about_app, parent, false),
                        (AppAboutObject) itemList.get(viewType));

            case AboutModel.TYPE_TRANSLATOR:
                return new TranslatorHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_about_translator, parent, false),
                        (TranslatorObject) itemList.get(viewType));

            case AboutModel.TYPE_LIBRARY:
                return new LibraryHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_about_library, parent, false),
                        (LibraryObject) itemList.get(viewType));

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (itemList.get(position).getType()) {
            case AboutModel.TYPE_HEADER:
                HeaderHolder header = (HeaderHolder) holder;
                Glide.with(a)
                        .load(R.drawable.ic_launcher)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(header.appIcon);
                break;

            case AboutModel.TYPE_TRANSLATOR:
                TranslatorHolder translator = (TranslatorHolder) holder;
                TranslatorObject object = (TranslatorObject) itemList.get(position);
                Glide.with(a)
                        .load(object.avatarUrl)
                        .error(R.drawable.default_avatar)
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(translator.avatar);
                Glide.with(a)
                        .load(object.flagId)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(translator.flag);
                break;
        }
    }

    /** <br> data. */

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}

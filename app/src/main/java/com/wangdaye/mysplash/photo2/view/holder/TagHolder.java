package com.wangdaye.mysplash.photo2.view.holder;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.Tag;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.ui.adapter.PhotoInfoAdapter2;
import com.wangdaye.mysplash.common.ui.adapter.TagAdapter;
import com.wangdaye.mysplash.common.ui.widget.SwipeSwitchLayout;
import com.wangdaye.mysplash.photo2.view.activity.PhotoActivity2;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Tag holder.
 * */

public class TagHolder extends PhotoInfoAdapter2.ViewHolder {

    @BindView(R.id.item_photo_2_tag)
    SwipeSwitchLayout.RecyclerView recyclerView;

    public static final int TYPE_TAG = 6;

    public TagHolder(View itemView, MysplashActivity a) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(
                        a,
                        LinearLayoutManager.HORIZONTAL,
                        false));
    }

    @Override
    protected void onBindView(PhotoActivity2 a, Photo photo) {
        List<Tag> tagList = new ArrayList<>();
        if (photo.categories != null) {
            tagList.addAll(photo.categories);
        }
        if (photo.tags != null) {
            tagList.addAll(photo.tags);
        }
        recyclerView.setAdapter(new TagAdapter(tagList));
    }

    @Override
    protected void onRecycled() {
        // do nothing.
    }

    public void setScrollListener(RecyclerView.OnScrollListener listener) {
        recyclerView.clearOnScrollListeners();
        recyclerView.addOnScrollListener(listener);
    }

    public void scrollTo(int x, int y) {
        recyclerView.scrollTo(x, y);
    }
}

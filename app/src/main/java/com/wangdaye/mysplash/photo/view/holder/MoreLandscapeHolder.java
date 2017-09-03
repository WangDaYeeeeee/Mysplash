package com.wangdaye.mysplash.photo.view.holder;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common._basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.ui.adapter.MoreHorizontalAdapter;
import com.wangdaye.mysplash.common.ui.adapter.PhotoInfoAdapter;
import com.wangdaye.mysplash.common.ui.widget.SwipeSwitchLayout;
import com.wangdaye.mysplash.photo.view.activity.PhotoActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * More landscape holder.
 * */

public class MoreLandscapeHolder extends PhotoInfoAdapter.ViewHolder {

    @BindView(R.id.item_photo_more_landscape_recyclerView)
    SwipeSwitchLayout.RecyclerView recyclerView;

    public static final int TYPE_MORE_LANDSCAPE = 10;

    public MoreLandscapeHolder(View itemView, MysplashActivity a) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(
                        a,
                        LinearLayoutManager.HORIZONTAL,
                        false));
    }

    @Override
    protected void onBindView(PhotoActivity a, Photo photo) {
        List<Collection> collectionList = new ArrayList<>();
        if (photo.related_collections != null) {
            for (int i = 0; i < photo.related_collections.results.size(); i ++) {
                collectionList.add(photo.related_collections.results.get(i));
            }
        }
        recyclerView.setAdapter(new MoreHorizontalAdapter(a, collectionList));
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

package com.wangdaye.mysplash.photo2.view.holder;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.ui.adapter.MoreHorizontalAdapter2;
import com.wangdaye.mysplash.common.ui.adapter.PhotoInfoAdapter2;
import com.wangdaye.mysplash.common.ui.widget.SwipeSwitchLayout;
import com.wangdaye.mysplash.common.ui.widget.coordinatorView.NavigationBarView;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.photo2.view.activity.PhotoActivity2;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * More landscape holder.
 * */

public class MoreLandscapeHolder extends PhotoInfoAdapter2.ViewHolder {

    @BindView(R.id.item_photo_2_more_landscape_recyclerView)
    SwipeSwitchLayout.RecyclerView recyclerView;

    @BindView(R.id.item_photo_2_more_landscape_navigationBar)
    NavigationBarView navigationBar;

    public static final int TYPE_MORE_LANDSCAPE = 8;

    public MoreLandscapeHolder(View itemView, PhotoActivity2 a) {
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
        List<Collection> collectionList = new ArrayList<>();
        if (photo.related_collections != null) {
            collectionList.addAll(photo.related_collections.results);
        }
        recyclerView.setAdapter(new MoreHorizontalAdapter2(a, collectionList));

        if (DisplayUtils.isLandscape(a)) {
            navigationBar.setVisibility(View.GONE);
        } else {
            navigationBar.setVisibility(View.VISIBLE);
        }
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

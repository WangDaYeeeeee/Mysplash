package com.wangdaye.photo.ui.holder;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.wangdaye.component.ComponentFactory;
import com.wangdaye.base.i.Tag;
import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.photo.R2;
import com.wangdaye.photo.ui.adapter.PhotoInfoAdapter3;
import com.wangdaye.common.ui.adapter.tag.TagAdapter;
import com.wangdaye.common.ui.widget.SwipeSwitchLayout;
import com.wangdaye.photo.activity.PhotoActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Tag holder.
 * */

public class TagHolder extends PhotoInfoAdapter3.ViewHolder {

    @BindView(R2.id.item_photo_3_tag_container) RelativeLayout container;
    @BindView(R2.id.item_photo_3_tag) SwipeSwitchLayout.RecyclerView recyclerView;

    public static final int TYPE_TAG = 6;

    public TagHolder(MysplashActivity a, View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(
                        a,
                        LinearLayoutManager.HORIZONTAL,
                        false
                )
        );
    }

    @Override
    protected void onBindView(PhotoActivity a, Photo photo) {
        List<Tag> tagList = new ArrayList<>();
        if (photo.tags != null) {
            tagList.addAll(photo.tags);
        }
        recyclerView.setAdapter(new TagAdapter(
                tagList,
                (view, tag) -> ComponentFactory.getSearchModule().startSearchActivity(a, view, tag)        ));
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

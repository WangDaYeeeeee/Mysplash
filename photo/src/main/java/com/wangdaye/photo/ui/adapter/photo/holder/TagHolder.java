package com.wangdaye.photo.ui.adapter.photo.holder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import com.wangdaye.component.ComponentFactory;
import com.wangdaye.photo.R;
import com.wangdaye.photo.R2;
import com.wangdaye.photo.ui.adapter.photo.PhotoInfoAdapter3;
import com.wangdaye.common.ui.adapter.tag.TagAdapter;
import com.wangdaye.photo.activity.PhotoActivity;
import com.wangdaye.photo.ui.adapter.photo.model.TagModel;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Tag holder.
 * */

public class TagHolder extends PhotoInfoAdapter3.ViewHolder {

    @BindView(R2.id.item_photo_3_tag) RecyclerView recyclerView;

    public static class Factory implements PhotoInfoAdapter3.ViewHolder.Factory {

        @NonNull
        @Override
        public PhotoInfoAdapter3.ViewHolder createHolder(@NonNull ViewGroup parent) {
            return new TagHolder(parent);
        }

        @Override
        public boolean isMatch(PhotoInfoAdapter3.ViewModel model) {
            return model instanceof TagModel;
        }
    }

    public TagHolder(ViewGroup parent) {
        super(parent, R.layout.item_photo_3_tag);
        ButterKnife.bind(this, itemView);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(
                        parent.getContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                )
        );
    }

    @Override
    protected void onBindView(PhotoActivity a, PhotoInfoAdapter3.ViewModel viewModel) {
        TagModel model = (TagModel) viewModel;
        recyclerView.setAdapter(
                new TagAdapter(
                        a,
                        model.list,
                        (view, tag) -> ComponentFactory.getSearchModule().startSearchActivity(a, view, tag)
                )
        );
        recyclerView.clearOnScrollListeners();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private int scrollX = 0;

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                scrollX += dx;
                model.scrollX = scrollX;
            }
        });
        recyclerView.scrollTo(model.scrollX, 0);
    }

    @Override
    protected void onRecycled() {
        // do nothing.
    }
}

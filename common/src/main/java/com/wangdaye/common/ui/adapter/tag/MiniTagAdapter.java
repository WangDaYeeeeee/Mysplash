package com.wangdaye.common.ui.adapter.tag;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.wangdaye.common.R;
import com.wangdaye.common.R2;
import com.wangdaye.base.unsplash.Tag;
import com.wangdaye.common.base.adapter.BaseAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Mini tag adapter.
 *
 * Adapter for {@link RecyclerView} to show Tags with mini style.
 *
 * */

public class MiniTagAdapter extends BaseAdapter<Tag, MiniTagAdapter.ViewModel, MiniTagAdapter.ViewHolder> {

    private TagItemEventCallback callback;

    class ViewModel implements com.wangdaye.base.i.Tag, BaseAdapter.ViewModel {

        Tag tag;

        ViewModel(Tag tag) {
            this.tag = tag;
        }

        @Override
        public String getTitle() {
            return tag.getTitle();
        }

        @Override
        public String getRegularUrl() {
            return tag.getRegularUrl();
        }

        @Override
        public String getThumbnailUrl() {
            return tag.getThumbnailUrl();
        }

        @Override
        public boolean areItemsTheSame(BaseAdapter.ViewModel newModel) {
            return newModel instanceof ViewModel && ((ViewModel) newModel).getTitle().equals(getTitle());
        }

        @Override
        public boolean areContentsTheSame(BaseAdapter.ViewModel newModel) {
            return true;
        }

        @Override
        public Object getChangePayload(BaseAdapter.ViewModel newModel) {
            return null;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.item_tag_mini_card) CardView background;
        @BindView(R2.id.item_tag_mini_text) TextView text;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void onBindView(com.wangdaye.base.i.Tag tag) {
            text.setText(tag.getTitle());
        }

        // interface.

        @OnClick(R2.id.item_tag_mini_card) void clickItem() {
            callback.onItemClicked(background, text.getText().toString());
        }
    }

    public MiniTagAdapter(Context context, List<Tag> list, @NonNull TagItemEventCallback c) {
        super(context, list);
        callback = c;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tag_mini, parent, false);
        return new ViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, ViewModel model) {
        holder.onBindView(model);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, ViewModel model,
                                    @NonNull List<Object> payloads) {
        onBindViewHolder(holder, model);
    }

    @Override
    protected ViewModel getViewModel(Tag model) {
        return new ViewModel(model);
    }
}
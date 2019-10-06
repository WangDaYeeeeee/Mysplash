package com.wangdaye.common.ui.adapter.tag;

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

public class MiniTagAdapter extends RecyclerView.Adapter<MiniTagAdapter.ViewHolder> {

    private List<Tag> itemList;
    private TagItemEventCallback callback;

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.item_tag_mini_card) CardView background;
        @BindView(R2.id.item_tag_mini_text) TextView text;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void onBindView(Tag tag) {
            text.setText(tag.getTitle());
        }

        // interface.

        @OnClick(R2.id.item_tag_mini_card) void clickItem() {
            callback.onItemClicked(background, text.getText().toString());
        }
    }

    public MiniTagAdapter(List<Tag> list, @NonNull TagItemEventCallback c) {
        itemList = list;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBindView(itemList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
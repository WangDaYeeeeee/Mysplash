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
import com.wangdaye.base.i.Tag;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Tag adapter.
 *
 * Adapter for {@link RecyclerView} to show Tags.
 *
 * */

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder> {

    private List<Tag> itemList;
    private TagItemEventCallback callback;

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.item_tag_card) CardView background;
        @BindView(R2.id.item_tag_text) TextView text;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void onBindView(int position) {
            text.setText(itemList.get(position).getTitle());
        }

        @OnClick(R2.id.item_tag_card) void clickItem() {
            callback.onItemClicked(background, text.getText().toString());
        }
    }

    public TagAdapter(List<Tag> list, @NonNull TagItemEventCallback c) {
        itemList = list;
        callback = c;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tag, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBindView(position);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
package com.wangdaye.mysplash.common.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.Tag;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;

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

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_tag_mini_text)
        TextView text;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void onBindView(int position) {
            text.setText(itemList.get(position).getTitle());
        }

        // interface.

        @OnClick(R.id.item_tag_mini_card) void clickItem() {
            IntentHelper.startSearchActivity(
                    Mysplash.getInstance().getTopActivity(),
                    itemList.get(getAdapterPosition()).getTitle());
        }
    }

    public MiniTagAdapter(List<Tag> list) {
        this.itemList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag_mini, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.onBindView(position);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
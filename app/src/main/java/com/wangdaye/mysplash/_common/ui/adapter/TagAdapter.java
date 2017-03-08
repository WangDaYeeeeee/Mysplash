package com.wangdaye.mysplash._common.ui.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common._basic.Tag;
import com.wangdaye.mysplash._common.utils.helper.ImageHelper;
import com.wangdaye.mysplash._common.utils.helper.IntentHelper;


import java.util.List;

/**
 * Tag adapter.
 * */

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder> {
    // widget
    private Context context;
    private List<Tag> itemList;

    /** <br> life cycle. */

    public TagAdapter(Context context, List<Tag> list) {
        this.context = context;
        this.itemList = list;
    }

    /** <br> UI. */

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.onBindView(position);
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.onRecycled();
    }

    /** <br> data. */

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    /** <br> inner class. */

    // view holder.

    class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        // widget
        CardView card;
        TextView text;
        TextView layoutText;
        ImageView image;

        // life cycle.

        ViewHolder(View itemView) {
            super(itemView);

            this.card = (CardView) itemView.findViewById(R.id.item_tag_card);
            card.setOnClickListener(this);

            this.text = (TextView) itemView.findViewById(R.id.item_tag_text);
            this.layoutText = (TextView) itemView.findViewById(R.id.item_tag_layoutText);
            this.image = (ImageView) itemView.findViewById(R.id.item_tag_image);
        }

        // UI.

        void onBindView(int position) {
            text.setText(itemList.get(position).getTitle());
            layoutText.setText(itemList.get(position).getTitle());

            ImageHelper.loadPhoto(context, image, itemList.get(position).getUrl(), true, null);
        }

        void onRecycled() {
            ImageHelper.releaseImageView(image);
        }

        // interface.

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.item_tag_card:
                    IntentHelper.startTagActivity(
                            Mysplash.getInstance().getTopActivity(),
                            itemList.get(getAdapterPosition()).getTitle());
                    break;
            }
        }
    }
}
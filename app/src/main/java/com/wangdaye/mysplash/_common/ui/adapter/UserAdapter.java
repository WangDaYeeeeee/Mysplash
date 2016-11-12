package com.wangdaye.mysplash._common.ui.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.User;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.utils.helper.IntentHelper;
import com.wangdaye.mysplash._common.ui.widget.CircleImageView;

import java.util.List;

/**
 * User adapter. (Recycler view)
 * */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    // widget
    private Context a;
    private List<User> itemList;

    /** <br> life cycle. */

    public UserAdapter(Context a, List<User> list) {
        this.a = a;
        this.itemList = list;
    }

    /** <br> UI. */

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint({"RecyclerView", "SetTextI18n"})
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.title.setText(itemList.get(position).name);
        if (TextUtils.isEmpty(itemList.get(position).bio)) {
            holder.subtitle.setText(
                    itemList.get(position).total_photos + a.getResources().getStringArray(R.array.user_tabs)[0] +
                            + itemList.get(position).total_collections + " Collections, "
                            + itemList.get(position).total_likes + " Likes");
        } else {
            holder.subtitle.setText(itemList.get(position).bio);
        }

        if (TextUtils.isEmpty(itemList.get(position).portfolio_url)) {
            holder.portfolioBtn.setVisibility(View.GONE);
        } else {
            holder.portfolioBtn.setVisibility(View.VISIBLE);
        }

        if (Mysplash.getInstance().isLightTheme()) {
            holder.portfolioBtn.setImageResource(R.drawable.ic_item_earth_light);
        } else {
            holder.portfolioBtn.setImageResource(R.drawable.ic_item_earth_dark);
        }

        if (itemList.get(position).profile_image != null) {
            Glide.with(a)
                    .load(itemList.get(position).profile_image.large)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model,
                                                       Target<GlideDrawable> target,
                                                       boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .override(128, 128)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(holder.avatar);
        } else {
            Glide.with(a)
                    .load(R.drawable.default_avatar)
                    .override(128, 128)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(holder.avatar);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.avatar.setTransitionName(itemList.get(position).username + "-avatar");
            holder.background.setTransitionName(itemList.get(position).username + "-background");
        }
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        Glide.clear(holder.avatar);
    }

    public void setActivity(Activity a) {
        this.a = a;
    }

    /** <br> data. */

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void insertItem(User u, int position) {
        itemList.add(position, u);
        notifyItemInserted(position);
    }

    public void clearItem() {
        itemList.clear();
        notifyDataSetChanged();
    }

    public int getRealItemCount() {
        return itemList.size();
    }

    /** <br> inner class. */

    // view holder.

    class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        // widget
        RelativeLayout background;
        CircleImageView avatar;
        ImageButton portfolioBtn;

        public TextView title;
        public TextView subtitle;

        ViewHolder(View itemView) {
            super(itemView);

            this.background = (RelativeLayout) itemView.findViewById(R.id.item_user_background);
            background.setOnClickListener(this);

            this.avatar = (CircleImageView) itemView.findViewById(R.id.item_user_avatar);

            this.portfolioBtn = (ImageButton) itemView.findViewById(R.id.item_user_portfolio);
            portfolioBtn.setOnClickListener(this);

            this.title = (TextView) itemView.findViewById(R.id.item_user_title);

            this.subtitle = (TextView) itemView.findViewById(R.id.item_user_subtitle);
            DisplayUtils.setTypeface(itemView.getContext(), subtitle);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.item_user_background:
                    Activity activity = (Activity) a;
                    View avatar = ((RelativeLayout) view).getChildAt(0);
                    IntentHelper.startUserActivity(
                            activity,
                            avatar,
                            itemList.get(getAdapterPosition()));
                    break;

                case R.id.item_user_portfolio:
                    if (!TextUtils.isEmpty(itemList.get(getAdapterPosition()).portfolio_url)) {
                        Uri uri = Uri.parse(itemList.get(getAdapterPosition()).portfolio_url);
                        a.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    }
                    break;
            }
        }
    }
}
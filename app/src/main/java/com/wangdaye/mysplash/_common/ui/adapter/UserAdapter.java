package com.wangdaye.mysplash._common.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common._basic.FooterAdapter;
import com.wangdaye.mysplash._common.data.entity.unsplash.User;
import com.wangdaye.mysplash._common._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.widget.CircleImageView;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.utils.helper.ImageHelper;
import com.wangdaye.mysplash._common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.user.view.activity.UserActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * User adapter. (Recycler view)
 * */

public class UserAdapter extends FooterAdapter<RecyclerView.ViewHolder> {
    // widget
    private Context a;
    private List<User> itemList;

    /** <br> life cycle. */

    public UserAdapter(Context a, List<User> list) {
        this.a = a;
        this.itemList = list;
    }

    @Override
    protected boolean hasFooter() {
        return DisplayUtils.getNavigationBarHeight(a.getResources()) != 0;
    }

    @Override
    public int getRealItemCount() {
        return itemList.size();
    }

    /** <br> UI. */

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        if (isFooter(position)) {
            // footer.
            return FooterHolder.buildInstance(parent);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
            return new ViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).onBindView(position);
        }
    }

    public void setActivity(MysplashActivity a) {
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

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).onRecycled();
        }
    }

    public void insertItem(User u, int position) {
        itemList.add(position, u);
        notifyItemInserted(position);
    }

    public void clearItem() {
        itemList.clear();
        notifyDataSetChanged();
    }

    public void setUserData(List<User> list) {
        itemList.clear();
        itemList.addAll(list);
        notifyDataSetChanged();
    }

    public List<User> getUserData() {
        List<User> list = new ArrayList<>();
        list.addAll(itemList);
        return list;
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

        // life cycle.

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

        // UI.

        void onBindView(final int position) {
            title.setText(itemList.get(position).name);
            if (TextUtils.isEmpty(itemList.get(position).bio)) {
                subtitle.setText(
                        itemList.get(position).total_photos + a.getResources().getStringArray(R.array.user_tabs)[0] +
                                + itemList.get(position).total_collections + " " + a.getResources().getStringArray(R.array.user_tabs)[1] + ", "
                                + itemList.get(position).total_likes + " " + a.getResources().getStringArray(R.array.user_tabs)[2]);
            } else {
                subtitle.setText(itemList.get(position).bio);
            }

            if (TextUtils.isEmpty(itemList.get(position).portfolio_url)) {
                portfolioBtn.setVisibility(View.GONE);
            } else {
                portfolioBtn.setVisibility(View.VISIBLE);
            }

            if (Mysplash.getInstance().isLightTheme()) {
                portfolioBtn.setImageResource(R.drawable.ic_item_earth_light);
            } else {
                portfolioBtn.setImageResource(R.drawable.ic_item_earth_dark);
            }

            ImageHelper.loadAvatar(a, avatar, itemList.get(position), new ImageHelper.OnLoadImageListener() {
                @Override
                public void onLoadSucceed() {
                    if (!itemList.get(position).hasFadedIn) {
                        itemList.get(position).hasFadedIn = true;
                        ImageHelper.startSaturationAnimation(a, avatar);
                    }
                }

                @Override
                public void onLoadFailed() {
                    // do nothing.
                }
            });

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                avatar.setTransitionName(itemList.get(position).username + "-avatar");
                background.setTransitionName(itemList.get(position).username + "-background");
            }
        }

        void onRecycled() {
            ImageHelper.releaseImageView(avatar);
        }

        // interface.

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.item_user_background:
                    if (a instanceof MysplashActivity) {
                        IntentHelper.startUserActivity(
                                (MysplashActivity) a,
                                avatar,
                                itemList.get(getAdapterPosition()),
                                UserActivity.PAGE_PHOTO);
                    }
                    break;

                case R.id.item_user_portfolio:
                    if (!TextUtils.isEmpty(itemList.get(getAdapterPosition()).portfolio_url)) {
                        IntentHelper.startWebActivity(a, itemList.get(getAdapterPosition()).portfolio_url);
                    }
                    break;
            }
        }
    }
}
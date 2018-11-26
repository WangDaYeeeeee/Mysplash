package com.wangdaye.mysplash.common.ui.adapter;

import android.annotation.SuppressLint;
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

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.FooterAdapter;
import com.wangdaye.mysplash.common.data.entity.unsplash.User;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.ui.widget.CircleImageView;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.helper.ImageHelper;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.user.view.activity.UserActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * User adapter.
 *
 * Adapter for {@link RecyclerView} to show users.
 *
 * */

public class UserAdapter extends FooterAdapter<RecyclerView.ViewHolder> {

    private Context a;
    private List<User> itemList;

    class ViewHolder extends RecyclerView.ViewHolder
            implements ImageHelper.OnLoadImageListener<User> {

        @BindView(R.id.item_user_background)
        RelativeLayout background;

        @BindView(R.id.item_user_avatar)
        CircleImageView avatar;

        @BindView(R.id.item_user_portfolio)
        ImageButton portfolioBtn;

        @BindView(R.id.item_user_title)
        TextView title;

        @BindView(R.id.item_user_subtitle)
        TextView subtitle;

        private User user;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @SuppressLint("SetTextI18n")
        void onBindView(int position) {
            if (itemList.size() <= position || itemList.get(position) == null) {
                return;
            }

            this.user = itemList.get(position);

            title.setText(user.name);
            if (TextUtils.isEmpty(user.bio)) {
                subtitle.setText(
                        user.total_photos
                                + " " + a.getResources().getStringArray(R.array.user_tabs)[0] + ", "
                                + user.total_collections
                                + " " + a.getResources().getStringArray(R.array.user_tabs)[1] + ", "
                                + user.total_likes
                                + " " + a.getResources().getStringArray(R.array.user_tabs)[2]);
            } else {
                subtitle.setText(user.bio);
            }

            if (TextUtils.isEmpty(user.portfolio_url)) {
                portfolioBtn.setVisibility(View.GONE);
            } else {
                portfolioBtn.setVisibility(View.VISIBLE);
            }

            ThemeManager.setImageResource(
                    portfolioBtn, R.drawable.ic_item_earth_light, R.drawable.ic_item_earth_dark);

            ImageHelper.loadAvatar(avatar.getContext(), avatar, user, position, this);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                avatar.setTransitionName(user.username + "-avatar");
                background.setTransitionName(user.username + "-background");
            }
        }

        void onRecycled() {
            ImageHelper.releaseImageView(avatar);
        }

        // interface.

        @OnClick(R.id.item_user_background) void clickItem() {
            if (a instanceof MysplashActivity) {
                IntentHelper.startUserActivity(
                        (MysplashActivity) a,
                        avatar,
                        background,
                        itemList.get(getAdapterPosition()),
                        UserActivity.PAGE_PHOTO);
            }
        }

        @OnClick(R.id.item_user_portfolio) void checkPortfolioWebPage() {
            if (!TextUtils.isEmpty(itemList.get(getAdapterPosition()).portfolio_url)) {
                IntentHelper.startWebActivity(a, itemList.get(getAdapterPosition()).portfolio_url);
            }
        }

        // on load image listener.

        @Override
        public void onLoadImageSucceed(User newT, int index) {
            if (user.updateLoadInformation(newT)) {
                User u = itemList.get(index);
                u.hasFadedIn = newT.hasFadedIn;
                itemList.set(index, u);
            }
        }

        @Override
        public void onLoadImageFailed(User originalT, int index) {
            // do nothing.
        }
    }

    public UserAdapter(Context a, List<User> list) {
        this.a = a;
        this.itemList = list;
    }

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

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).onRecycled();
        }
    }

    @Override
    public int getRealItemCount() {
        return itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    protected boolean hasFooter() {
        return !DisplayUtils.isLandscape(a)
                && DisplayUtils.getNavigationBarHeight(a.getResources()) != 0;
    }

    public void setActivity(MysplashActivity a) {
        this.a = a;
    }

    public void insertItem(User u, int position) {
        if (position <= itemList.size()) {
            itemList.add(position, u);
            notifyItemInserted(position);
        }
    }

    public void clearItem() {
        itemList.clear();
        notifyDataSetChanged();
    }

    public void updateUser(User u, boolean refreshView, boolean probablyRepeat) {
        for (int i = 0; i < getRealItemCount(); i ++) {
            if (itemList.get(i).id.equals(u.id)) {
                u.hasFadedIn = itemList.get(i).hasFadedIn;
                itemList.set(i, u);
                if (refreshView) {
                    notifyItemChanged(i);
                }
                if (!probablyRepeat) {
                    return;
                }
            }
        }
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
}
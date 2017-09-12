package com.wangdaye.mysplash.common.ui.adapter;

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
import com.wangdaye.mysplash.common._basic.FooterAdapter;
import com.wangdaye.mysplash.common.data.entity.unsplash.User;
import com.wangdaye.mysplash.common._basic.activity.MysplashActivity;
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

    class ViewHolder extends RecyclerView.ViewHolder {

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

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            DisplayUtils.setTypeface(itemView.getContext(), subtitle);
        }

        void onBindView(final int position) {
            if (itemList.size() <= position || itemList.get(position) == null) {
                return;
            }
            title.setText(itemList.get(position).name);
            if (TextUtils.isEmpty(itemList.get(position).bio)) {
                subtitle.setText(
                        itemList.get(position).total_photos
                                + " " + a.getResources().getStringArray(R.array.user_tabs)[0] + ", "
                                + itemList.get(position).total_collections
                                + " " + a.getResources().getStringArray(R.array.user_tabs)[1] + ", "
                                + itemList.get(position).total_likes
                                + " " + a.getResources().getStringArray(R.array.user_tabs)[2]);
            } else {
                subtitle.setText(itemList.get(position).bio);
            }

            if (TextUtils.isEmpty(itemList.get(position).portfolio_url)) {
                portfolioBtn.setVisibility(View.GONE);
            } else {
                portfolioBtn.setVisibility(View.VISIBLE);
            }

            ThemeManager.setImageResource(
                    portfolioBtn, R.drawable.ic_item_earth_light, R.drawable.ic_item_earth_dark);

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

        @OnClick(R.id.item_user_background) void clickItem() {
            if (a instanceof MysplashActivity) {
                IntentHelper.startUserActivity(
                        (MysplashActivity) a,
                        avatar,
                        itemList.get(getAdapterPosition()),
                        UserActivity.PAGE_PHOTO);
            }
        }

        @OnClick(R.id.item_user_portfolio) void checkPortfolioWebPage() {
            if (!TextUtils.isEmpty(itemList.get(getAdapterPosition()).portfolio_url)) {
                IntentHelper.startWebActivity(a, itemList.get(getAdapterPosition()).portfolio_url);
            }
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
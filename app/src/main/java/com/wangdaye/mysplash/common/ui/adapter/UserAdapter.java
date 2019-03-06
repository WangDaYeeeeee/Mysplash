package com.wangdaye.mysplash.common.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.adapter.FooterAdapter;
import com.wangdaye.mysplash.common.network.json.User;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.ui.widget.CircleImageView;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.image.ImageHelper;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.user.ui.UserActivity;

import org.jetbrains.annotations.NotNull;

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

    private List<User> itemList;

    public UserAdapter(Context context, List<User> list) {
        super(context);
        this.itemList = list;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int position) {
        if (isFooter(position)) {
            // footer.
            return FooterHolder.buildInstance(parent);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
            return new UserHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
        if (position >= itemList.size()) {
            return;
        }
        if (holder instanceof UserHolder) {
            ((UserHolder) holder).onBindView(itemList.get(position));
        }
    }

    @Override
    public void onViewRecycled(@NotNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof UserHolder) {
            ((UserHolder) holder).onRecycled();
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
    protected boolean hasFooter(Context context) {
        return !DisplayUtils.isLandscape(context)
                && DisplayUtils.getNavigationBarHeight(context.getResources()) != 0;
    }

    public void updateUser(User newUser, boolean refreshView, boolean probablyRepeat) {
        for (int i = 0; i < itemList.size(); i ++) {
            if (itemList.get(i).id.equals(newUser.id)) {
                newUser.hasFadedIn = itemList.get(i).hasFadedIn;
                itemList.set(i, newUser);
                if (refreshView) {
                    notifyItemChanged(i);
                }
                if (!probablyRepeat) {
                    return;
                }
            }
        }
    }
}

class UserHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.item_user_background) RelativeLayout background;
    @BindView(R.id.item_user_avatar) CircleImageView avatar;
    @BindView(R.id.item_user_portfolio) AppCompatImageButton portfolioBtn;
    @BindView(R.id.item_user_title) TextView title;
    @BindView(R.id.item_user_subtitle) TextView subtitle;

    private User user;

    UserHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @SuppressLint("SetTextI18n")
    void onBindView(User user) {
        Context context = itemView.getContext();

        this.user = user;

        title.setText(user.name);
        if (TextUtils.isEmpty(user.bio)) {
            subtitle.setText(
                    user.total_photos
                            + " " + context.getResources().getStringArray(R.array.user_tabs)[0] + ", "
                            + user.total_collections
                            + " " + context.getResources().getStringArray(R.array.user_tabs)[1] + ", "
                            + user.total_likes
                            + " " + context.getResources().getStringArray(R.array.user_tabs)[2]);
        } else {
            subtitle.setText(user.bio);
        }

        if (TextUtils.isEmpty(user.portfolio_url)) {
            portfolioBtn.setVisibility(View.GONE);
        } else {
            portfolioBtn.setVisibility(View.VISIBLE);
        }

        ImageHelper.loadAvatar(avatar.getContext(), avatar, user, () -> {
            if (!user.hasFadedIn) {
                user.hasFadedIn = true;
                ImageHelper.startSaturationAnimation(avatar.getContext(), avatar);
            }
        });

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
        MysplashActivity activity = Mysplash.getInstance().getTopActivity();
        if (activity != null) {
            IntentHelper.startUserActivity(activity, avatar, background, user, UserActivity.PAGE_PHOTO);
        }
    }

    @OnClick(R.id.item_user_portfolio) void checkPortfolioWebPage() {
        MysplashActivity activity = Mysplash.getInstance().getTopActivity();
        if (activity != null
                && !TextUtils.isEmpty(user.portfolio_url)) {
            IntentHelper.startWebActivity(activity, user.portfolio_url);
        }
    }
}
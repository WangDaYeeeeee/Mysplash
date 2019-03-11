package com.wangdaye.mysplash.me.ui;

import android.content.Context;
import android.os.Build;

import androidx.recyclerview.widget.RecyclerView;
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
import com.wangdaye.mysplash.common.ui.widget.rippleButton.RippleButton;
import com.wangdaye.mysplash.common.image.ImageHelper;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.common.utils.bus.MessageBus;
import com.wangdaye.mysplash.user.ui.UserActivity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * My follow adapter.
 *
 * Adapter for {@link RecyclerView} to show follow information.
 *
 * */

public class MyFollowAdapter extends FooterAdapter<MyFollowHolder> {

    private List<User> itemList;
    @Nullable private ItemEventCallback callback;

    public MyFollowAdapter(Context context, List<User> list) {
        super(context);
        this.itemList = list;
    }

    @NotNull
    @Override
    public MyFollowHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_follow_user, parent, false);
        return new MyFollowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NotNull MyFollowHolder holder, int position) {
        holder.onBindView(itemList.get(position), callback);
    }

    public void onViewRecycled(@NotNull MyFollowHolder holder) {
        super.onViewRecycled(holder);
        holder.onRecycled();
    }

    @Override
    protected boolean hasFooter(Context context) {
        return false;
    }

    @Override
    public int getRealItemCount() {
        return itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface ItemEventCallback {
        void onFollowUserOrCancel(User user, int adapterPosition, boolean follow);
    }

    public void setItemEventCallback(@Nullable ItemEventCallback c) {
        callback = c;
    }
}

class MyFollowHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.item_my_follow_user_background) RelativeLayout background;
    @OnClick(R.id.item_my_follow_user_background) void clickItem() {
        MysplashActivity activity = Mysplash.getInstance().getTopActivity();
        if (activity != null) {
            IntentHelper.startUserActivity(
                    activity, avatar, background, user, UserActivity.PAGE_PHOTO);
        }
    }
    @BindView(R.id.item_my_follow_user_avatar) CircleImageView avatar;
    @BindView(R.id.item_my_follow_user_title) TextView title;
    @BindView(R.id.item_my_follow_user_button) RippleButton rippleButton;

    private User user;

    MyFollowHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    void onBindView(User user, @Nullable MyFollowAdapter.ItemEventCallback callback) {
        this.user = user;

        ImageHelper.loadAvatar(avatar.getContext(), avatar, user, null);

        title.setText(user.name);

        if (user.settingFollow) {
            rippleButton.setState(user.followed_by_user
                    ? RippleButton.State.TRANSFORM_TO_OFF : RippleButton.State.TRANSFORM_TO_ON);
        } else {
            rippleButton.setState(user.followed_by_user
                    ? RippleButton.State.ON : RippleButton.State.OFF);
        }
        rippleButton.setOnSwitchListener(current -> {
            user.settingFollow = true;
            MessageBus.getInstance().post(user);
            if (callback != null) {
                callback.onFollowUserOrCancel(
                        user, getAdapterPosition(), !user.followed_by_user);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            avatar.setTransitionName(user.username + "-" + getAdapterPosition() + "-avatar");
            background.setTransitionName(user.username + "-" + getAdapterPosition() + "-background");
        }
    }

    void onRecycled() {
        ImageHelper.releaseImageView(avatar);
    }
}
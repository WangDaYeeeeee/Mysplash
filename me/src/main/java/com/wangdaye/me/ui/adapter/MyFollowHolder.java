package com.wangdaye.me.ui.adapter;

import android.os.Build;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wangdaye.common.image.ImageHelper;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.ui.widget.CircularImageView;
import com.wangdaye.common.ui.widget.rippleButton.RippleButton;
import com.wangdaye.me.R2;

import org.jetbrains.annotations.Nullable;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

class MyFollowHolder extends RecyclerView.ViewHolder {

    @BindView(R2.id.item_my_follow_user_background) RelativeLayout background;
    @BindView(R2.id.item_my_follow_user_avatar) CircularImageView avatar;
    @BindView(R2.id.item_my_follow_user_title) TextView title;
    @BindView(R2.id.item_my_follow_user_button) RippleButton rippleButton;

    MyFollowHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    void onBindView(User user, @Nullable MyFollowAdapter.ItemEventCallback callback) {
        background.setOnClickListener(v -> {
            if (callback != null) {
                callback.onFollowItemClicked(avatar, background, user);
            }
        });

        ImageHelper.loadAvatar(avatar.getContext(), avatar, user, null);

        title.setText(user.name);

        if (user.settingFollow) {
            rippleButton.setState(
                    user.followed_by_user
                            ? RippleButton.State.TRANSFORM_TO_OFF
                            : RippleButton.State.TRANSFORM_TO_ON
            );
        } else {
            rippleButton.setState(
                    user.followed_by_user
                            ? RippleButton.State.ON
                            : RippleButton.State.OFF
            );
        }
        rippleButton.setOnSwitchListener(current -> {
            if (callback != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                callback.onFollowUserOrCancel(user, getAdapterPosition(), !user.followed_by_user);
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

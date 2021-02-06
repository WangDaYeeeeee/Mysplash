package com.wangdaye.me.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.wangdaye.common.image.ImageHelper;
import com.wangdaye.common.image.transformation.CircleTransformation;
import com.wangdaye.common.ui.widget.CircularImageView;
import com.wangdaye.common.ui.widget.rippleButton.RippleButton;
import com.wangdaye.me.R2;

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

    void onBindView(MyFollowModel model, @Nullable MyFollowAdapter.ItemEventCallback callback) {
        Context context = itemView.getContext();

        background.setOnClickListener(v -> {
            if (callback != null) {
                callback.onFollowItemClicked(avatar, background, model.user);
            }
        });

        if (TextUtils.isEmpty(model.avatarUrl)) {
            ImageHelper.loadImage(context, avatar, com.wangdaye.common.R.drawable.default_avatar,
                    model.avatarSize, new BitmapTransformation[]{new CircleTransformation(context)}, null);
        } else {
            ImageHelper.loadImage(context, avatar, model.avatarUrl, com.wangdaye.common.R.drawable.default_avatar_round,
                    model.avatarSize, new BitmapTransformation[]{new CircleTransformation(context)}, null);
        }

        title.setText(model.title);

        if (model.progressing) {
            rippleButton.setState(
                    model.switchOn
                            ? RippleButton.State.TRANSFORM_TO_OFF
                            : RippleButton.State.TRANSFORM_TO_ON
            );
        } else {
            rippleButton.setState(
                    model.switchOn
                            ? RippleButton.State.ON
                            : RippleButton.State.OFF
            );
        }
        rippleButton.setOnSwitchListener(current -> {
            if (callback != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                callback.onFollowUserOrCancel(model.user, getAdapterPosition(), !model.user.followed_by_user);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            avatar.setTransitionName(model.user.username + "-" + getAdapterPosition() + "-avatar");
            background.setTransitionName(model.user.username + "-" + getAdapterPosition() + "-background");
        }
    }

    void onRecycled() {
        ImageHelper.releaseImageView(avatar);
    }
}

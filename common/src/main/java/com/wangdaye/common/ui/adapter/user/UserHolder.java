package com.wangdaye.common.ui.adapter.user;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.wangdaye.common.R;
import com.wangdaye.common.R2;
import com.wangdaye.base.pager.ProfilePager;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.image.transformation.CircleTransformation;
import com.wangdaye.common.image.ImageHelper;
import com.wangdaye.common.ui.widget.CircularImageView;
import com.wangdaye.component.ComponentFactory;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

class UserHolder extends RecyclerView.ViewHolder {

    @BindView(R2.id.item_user_background) RelativeLayout background;
    @BindView(R2.id.item_user_avatar) CircularImageView avatar;
    @BindView(R2.id.item_user_portfolio) AppCompatImageButton portfolioBtn;
    @BindView(R2.id.item_user_title) TextView title;
    @BindView(R2.id.item_user_subtitle) TextView subtitle;

    private User user;
    @Nullable private UserAdapter.ItemEventCallback callback;

    UserHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @SuppressLint("SetTextI18n")
    void onBindView(UserModel model, @Nullable UserAdapter.ItemEventCallback callback) {
        Context context = itemView.getContext();

        this.user = model.user;
        this.callback = callback;

        title.setText(model.title);
        subtitle.setText(model.subtitle);

        if (TextUtils.isEmpty(model.portfolioUrl)) {
            portfolioBtn.setVisibility(View.GONE);
        } else {
            portfolioBtn.setVisibility(View.VISIBLE);
        }

        if (TextUtils.isEmpty(model.avatarUrl)) {
            ImageHelper.loadImage(context, avatar, R.drawable.default_avatar,
                    model.avatarSize, new BitmapTransformation[]{new CircleTransformation(context)}, null);
        } else {
            ImageHelper.setImageViewSaturation(avatar, model.hasFadeIn ? 1 : 0);
            ImageHelper.loadImage(context, avatar, model.avatarUrl, R.drawable.default_avatar_round,
                    model.avatarSize, new BitmapTransformation[]{new CircleTransformation(context)}, () -> {
                if (!model.hasFadeIn) {
                    model.hasFadeIn = true;
                    long duration = Long.parseLong(
                            ComponentFactory.getSettingsService().getSaturationAnimationDuration());
                    ImageHelper.startSaturationAnimation(context, avatar, duration);
                }
            });
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            avatar.setTransitionName(user.username + "-avatar");
            background.setTransitionName(user.username + "-background");
        }
    }

    void onRecycled() {
        ImageHelper.releaseImageView(avatar);
    }

    // interface.

    @OnClick(R2.id.item_user_background) void clickItem() {
        if (callback != null) {
            callback.onStartUserActivity(avatar, background, user, ProfilePager.PAGE_PHOTO);
        }
    }

    @OnClick(R2.id.item_user_portfolio) void checkPortfolioWebPage() {
        if (callback != null) {
            callback.onPortfolioButtonClicked(user);
        }
    }
}

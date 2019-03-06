package com.wangdaye.mysplash.me.ui;

import android.os.Build;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.network.json.User;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.ui.widget.CircleImageView;
import com.wangdaye.mysplash.common.ui.widget.rippleButton.RippleButton;
import com.wangdaye.mysplash.common.image.ImageHelper;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.user.ui.UserActivity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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

public class MyFollowAdapter extends RecyclerView.Adapter<MyFollowHolder> {

    private List<MyFollowUser> itemList;
    @Nullable private ItemEventCallback callback;

    public static class MyFollowUser {

        public boolean requesting;
        public boolean switchTo;
        public User user;

        public MyFollowUser(User u) {
            this.requesting = false;
            this.switchTo = false;
            this.user = u;
        }

        public static List<MyFollowUser> getMyFollowUserList(List<User> list) {
            List<MyFollowUser> result = new ArrayList<>();
            for (int i = 0; i < list.size(); i ++) {
                result.add(new MyFollowUser(list.get(i)));
            }
            return result;
        }
    }

    public MyFollowAdapter(List<MyFollowUser> list) {
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
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void updateItem(User user, boolean refreshView, boolean probablyRepeat) {
        for (int i = 0; i < itemList.size(); i ++) {
            if (itemList.get(i).user.username.equals(user.username)) {
                MyFollowUser newFollowUser = new MyFollowUser(user);
                newFollowUser.requesting = itemList.get(i).requesting;
                newFollowUser.switchTo = itemList.get(i).switchTo;
                itemList.set(i, newFollowUser);
                if (refreshView) {
                    notifyItemChanged(i);
                }
                if (!probablyRepeat) {
                    return;
                }
            }
        }
    }

    public interface ItemEventCallback {
        void onFollowUserOrCancel(MyFollowUser myFollowUser, int adapterPosition, boolean follow);
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
                    activity, avatar, background, myFollowUser.user, UserActivity.PAGE_PHOTO);
        }
    }
    @BindView(R.id.item_my_follow_user_avatar) CircleImageView avatar;
    @BindView(R.id.item_my_follow_user_title) TextView title;
    @BindView(R.id.item_my_follow_user_button) RippleButton rippleButton;

    private MyFollowAdapter.MyFollowUser myFollowUser;

    MyFollowHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    void onBindView(MyFollowAdapter.MyFollowUser myFollowUser, @Nullable MyFollowAdapter.ItemEventCallback callback) {
        this.myFollowUser = myFollowUser;

        ImageHelper.loadAvatar(avatar.getContext(), avatar, myFollowUser.user, null);

        title.setText(myFollowUser.user.name);

        if (myFollowUser.requesting) {
            rippleButton.setState(myFollowUser.switchTo
                    ? RippleButton.State.TRANSFORM_TO_ON : RippleButton.State.TRANSFORM_TO_OFF);
        } else {
            rippleButton.setState(myFollowUser.user.followed_by_user
                    ? RippleButton.State.ON : RippleButton.State.OFF);
        }
        rippleButton.setOnSwitchListener(current -> {
            myFollowUser.requesting = true;
            myFollowUser.switchTo = current != RippleButton.State.ON;
            if (callback != null) {
                rippleButton.setState(current == RippleButton.State.ON
                        ? RippleButton.State.TRANSFORM_TO_OFF : RippleButton.State.TRANSFORM_TO_ON);
                callback.onFollowUserOrCancel(
                        myFollowUser, getAdapterPosition(), myFollowUser.switchTo);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            avatar.setTransitionName(myFollowUser.user.username + "-" + getAdapterPosition() + "-avatar");
            background.setTransitionName(myFollowUser.user.username + "-" + getAdapterPosition() + "-background");
        }
    }

    void onRecycled() {
        ImageHelper.releaseImageView(avatar);
    }
}
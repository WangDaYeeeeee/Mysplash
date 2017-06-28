package com.wangdaye.mysplash.common.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.item.MyFollowUser;
import com.wangdaye.mysplash.common.data.entity.unsplash.User;
import com.wangdaye.mysplash.common.data.service.FollowingService;
import com.wangdaye.mysplash.common._basic.MysplashActivity;
import com.wangdaye.mysplash.common.ui.widget.CircleImageView;
import com.wangdaye.mysplash.common.ui.widget.rippleButton.RippleButton;
import com.wangdaye.mysplash.common.utils.helper.NotificationHelper;
import com.wangdaye.mysplash.common.utils.helper.ImageHelper;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.user.view.activity.UserActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * My follow adapter.
 *
 * Adapter for {@link RecyclerView} to show follow information.
 *
 * */

public class MyFollowAdapter extends RecyclerView.Adapter<MyFollowAdapter.ViewHolder> {

    private Context a;
    private List<MyFollowUser> itemList;
    private OnFollowStateChangedListener listener;

    private FollowingService service;

    public class ViewHolder extends RecyclerView.ViewHolder
            implements RippleButton.OnSwitchListener {

        @BindView(R.id.item_my_follow_user_background)
        RelativeLayout background;

        @BindView(R.id.item_my_follow_user_avatar)
        CircleImageView avatar;

        @BindView(R.id.item_my_follow_user_title)
        TextView title;

        @BindView(R.id.item_my_follow_user_button)
        RippleButton rippleButton;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void onBindView(int position) {
            ImageHelper.loadAvatar(a, avatar, itemList.get(position).user, null);

            title.setText(itemList.get(position).user.name);

            if (itemList.get(position).requesting) {
                rippleButton.forceProgress(itemList.get(position).switchTo);
            } else {
                rippleButton.forceSwitch(itemList.get(position).user.followed_by_user);
            }
            rippleButton.setOnSwitchListener(this);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                avatar.setTransitionName(itemList.get(position).user.username + "-avatar");
                background.setTransitionName(itemList.get(position).user.username + "-background");
            }
        }

        void onRecycled() {
            ImageHelper.releaseImageView(avatar);
        }

        public void setSwitchResult(boolean result) {
            rippleButton.setSwitchResult(result);
        }

        // interface.

        @OnClick(R.id.item_my_follow_user_background) void clickItem() {
            if (a instanceof MysplashActivity) {
                IntentHelper.startUserActivity(
                        (MysplashActivity) a,
                        avatar,
                        itemList.get(getAdapterPosition()).user,
                        UserActivity.PAGE_PHOTO);
            }
        }

        @Override
        public void onSwitch(boolean switchTo) {
            MyFollowUser myFollowUser = itemList.get(getAdapterPosition());
            myFollowUser.requesting = true;
            myFollowUser.switchTo = switchTo;
            itemList.set(getAdapterPosition(), myFollowUser);
            service.setFollowUser(
                    itemList.get(getAdapterPosition()).user.username,
                    switchTo,
                    new OnSetFollowListener(
                            itemList.get(getAdapterPosition()).user.username,
                            switchTo));
        }
    }

    public MyFollowAdapter(Context a, List<MyFollowUser> list, OnFollowStateChangedListener l) {
        this.a = a;
        this.itemList = list;
        this.listener = l;
        this.service = FollowingService.getService();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_follow_user, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.onBindView(position);
    }

    public void onViewRecycled(ViewHolder holder) {
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

    public void setActivity(MysplashActivity a) {
        this.a = a;
    }

    public void insertItem(User u, int position) {
        if (position <= itemList.size()) {
            itemList.add(position, new MyFollowUser(u));
            notifyItemInserted(position);
        }
    }

    public void clearItem() {
        itemList.clear();
        notifyDataSetChanged();
    }

    // interface.

    // on follow state changed listener.

    public interface OnFollowStateChangedListener {
        void onFollowStateChanged(String username, int position, boolean switchTo, boolean succeed);
    }

    // on set follow listener.

    private class OnSetFollowListener implements FollowingService.OnFollowListener {
        // data
        private String username;
        private boolean switchTo;

        // life cycle.

        OnSetFollowListener(String username, boolean switchTo) {
            this.username = username;
            this.switchTo = switchTo;
        }

        // data.

        @Override
        public void onFollowSuccess(Call<ResponseBody> call, Response<ResponseBody> response) {
            if (Mysplash.getInstance() != null && Mysplash.getInstance().getTopActivity() != null) {
                for (int i = 0; i < itemList.size(); i ++) {
                    if (itemList.get(i).user.username.equals(username)) {
                        User user = itemList.get(i).user;
                        user.followed_by_user = true;
                        itemList.set(i, new MyFollowUser(user));
                        if (listener != null) {
                            listener.onFollowStateChanged(username, i, switchTo, true);
                        }
                    }
                }
            }
        }

        @Override
        public void onCancelFollowSuccess(Call<ResponseBody> call, Response<ResponseBody> response) {
            if (Mysplash.getInstance() != null && Mysplash.getInstance().getTopActivity() != null) {
                for (int i = 0; i < itemList.size(); i ++) {
                    if (itemList.get(i).user.username.equals(username)) {
                        User user = itemList.get(i).user;
                        user.followed_by_user = false;
                        itemList.set(i, new MyFollowUser(user));
                        if (listener != null) {
                            listener.onFollowStateChanged(username, i, switchTo, true);
                        }
                    }
                }
            }
        }

        @Override
        public void onFollowFailed(Call<ResponseBody> call, Throwable t) {
            if (Mysplash.getInstance() != null && Mysplash.getInstance().getTopActivity() != null) {
                NotificationHelper.showSnackbar(a.getString(R.string.feedback_follow_failed));
                for (int i = 0; i < itemList.size(); i ++) {
                    if (itemList.get(i).user.username.equals(username)) {
                        User user = itemList.get(i).user;
                        itemList.set(i, new MyFollowUser(user));
                        if (listener != null) {
                            listener.onFollowStateChanged(username, i, switchTo, false);
                        }
                    }
                }
            }
        }

        @Override
        public void onCancelFollowFailed(Call<ResponseBody> call, Throwable t) {
            if (Mysplash.getInstance() != null && Mysplash.getInstance().getTopActivity() != null) {
                NotificationHelper.showSnackbar(a.getString(R.string.feedback_cancel_follow_failed));
                for (int i = 0; i < itemList.size(); i ++) {
                    if (itemList.get(i).user.username.equals(username)) {
                        User user = itemList.get(i).user;
                        itemList.set(i, new MyFollowUser(user));
                        if (listener != null) {
                            listener.onFollowStateChanged(username, i, switchTo, false);
                        }
                    }
                }
            }
        }
    }
}
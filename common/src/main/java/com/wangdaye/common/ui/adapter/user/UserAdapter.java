package com.wangdaye.common.ui.adapter.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.wangdaye.common.R;
import com.wangdaye.common.base.adapter.BaseAdapter;
import com.wangdaye.base.unsplash.User;

import org.jetbrains.annotations.NotNull;

import java.util.List;


/**
 * User adapter.
 *
 * Adapter for {@link RecyclerView} to show users.
 *
 * */

public class UserAdapter extends BaseAdapter<User, UserModel, UserHolder> {

    @Nullable private ItemEventCallback callback;

    public UserAdapter(Context context, List<User> list) {
        super(context, list);
    }

    @NotNull
    @Override
    public UserHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new UserHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_user, parent, false)
        );
    }

    @Override
    protected void onBindViewHolder(@NonNull UserHolder holder, UserModel model) {
        holder.onBindView(model, callback);
    }

    @Override
    protected void onBindViewHolder(@NonNull UserHolder holder, UserModel model,
                                    @NonNull List<Object> payloads) {
        onBindViewHolder(holder, model);
    }

    @Override
    public void onViewRecycled(@NotNull UserHolder holder) {
        holder.onRecycled();
    }

    @Override
    protected UserModel getViewModel(User model) {
        return new UserModel(getContext(), model);
    }

    public interface ItemEventCallback {
        void onStartUserActivity(View avatar, View background, User user, int index);
        void onPortfolioButtonClicked(User user);
    }

    public UserAdapter setItemEventCallback(@Nullable ItemEventCallback c) {
        this.callback = c;
        return this;
    }
}


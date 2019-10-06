package com.wangdaye.common.ui.adapter.multipleState;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wangdaye.common.R;
import com.wangdaye.common.R2;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MiniErrorStateAdapter extends RecyclerView.Adapter<MiniErrorStateAdapter.ViewHolder> {

    @NonNull private OnRetryListener listener;

    class ViewHolder extends RecyclerView.ViewHolder {

        @OnClick(R2.id.item_multiple_state_error_mini_retryButton) void retry() {
            listener.onRetry();
        }

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void onBindView() {
            // do nothing.
        }
    }

    public MiniErrorStateAdapter(@NonNull OnRetryListener l) {
        this.listener = l;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_multiple_state_error_mini, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBindView();
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public interface OnRetryListener {
        void onRetry();
    }
}

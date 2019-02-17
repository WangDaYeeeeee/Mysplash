package com.wangdaye.mysplash.common.ui.adapter.multipleState;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.utils.DisplayUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LargeLoadingStateAdapter extends RecyclerView.Adapter<LargeLoadingStateAdapter.ViewHolder> {

    private Context context;
    private int marginBottomDp;

    private View.OnClickListener onClickListener;

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_multiple_state_loading_large_container)
        RelativeLayout container;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) container.getLayoutParams();
            params.setMargins(0, 0, 0, (int) new DisplayUtils(context).dpToPx(marginBottomDp));
            container.setLayoutParams(params);
        }

        void onBindView() {
            // do nothing.
        }

        @OnClick(R.id.item_multiple_state_loading_large_container) void click() {
            if (onClickListener != null) {
                onClickListener.onClick(container);
            }
        }
    }

    public LargeLoadingStateAdapter(Context context, int marginBottomDp) {
        this(context, marginBottomDp, null);
    }

    public LargeLoadingStateAdapter(Context context, int marginBottomDp, View.OnClickListener l) {
        this.context = context;
        this.marginBottomDp = marginBottomDp;
        this.onClickListener = l;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_multiple_state_loading_large, parent, false);
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
}

package com.wangdaye.common.ui.adapter.multipleState;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.wangdaye.common.R;
import com.wangdaye.common.R2;
import com.wangdaye.common.image.ImageHelper;
import com.wangdaye.common.utils.DisplayUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LargeErrorStateAdapter extends RecyclerView.Adapter<LargeErrorStateAdapter.ViewHolder> {

    private Context context;

    private int marginBottomDp;

    private int feedbackImageResId;
    private String feedbackText;
    private String feedbackButton;

    private boolean showFeedbackText;
    private boolean showFeedbackButton;

    @Nullable private View.OnClickListener onClickListener;
    @NonNull private OnRetryListener onRetryListener;

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.item_multiple_state_error_large_container) RelativeLayout container;
        @OnClick(R2.id.item_multiple_state_error_large_container) void click() {
            if (onClickListener != null) {
                onClickListener.onClick(container);
            }
        }

        @BindView(R2.id.item_multiple_state_error_large_feedbackImg) AppCompatImageView feedbackImg;
        @BindView(R2.id.item_multiple_state_error_large_feedbackTxt) TextView feedbackTxt;

        @BindView(R2.id.item_multiple_state_error_large_feedbackBtn) TextView feedbackBtn;
        @OnClick(R2.id.item_multiple_state_error_large_feedbackBtn) void retry() {
            onRetryListener.onRetry();
        }

        ViewHolder(Context context, View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            RelativeLayout container = itemView.findViewById(R.id.item_multiple_state_error_large_container);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) container.getLayoutParams();
            params.setMargins(0, 0, 0, (int) new DisplayUtils(context).dpToPx(marginBottomDp));
            container.setLayoutParams(params);

            if (showFeedbackText) {
                feedbackTxt.setVisibility(View.VISIBLE);
            } else {
                feedbackTxt.setVisibility(View.GONE);
            }

            if (showFeedbackButton) {
                feedbackBtn.setVisibility(View.VISIBLE);
            } else {
                feedbackBtn.setVisibility(View.GONE);
            }
        }

        void onBindView(Context context) {
            ImageHelper.loadResourceImage(context, feedbackImg, feedbackImageResId);
            feedbackTxt.setText(feedbackText);
            feedbackBtn.setText(feedbackButton);
        }
    }

    public LargeErrorStateAdapter(Context context,
                                  int marginBottomDp,
                                  int feedbackImageResId, String feedbackText, String feedbackButton,
                                  @NonNull OnRetryListener l) {
        this(context, marginBottomDp, feedbackImageResId, feedbackText, feedbackButton,
                true, true, null, l);
    }

    public LargeErrorStateAdapter(Context context,
                                  int marginBottomDp,
                                  int feedbackImageResId, String feedbackText, String feedbackButton,
                                  boolean showFeedbackText, boolean showFeedbackButton,
                                  @Nullable View.OnClickListener onClickListener,
                                  @NonNull OnRetryListener onRetryListener) {
        this.context = context;
        this.marginBottomDp = marginBottomDp;
        this.feedbackImageResId = feedbackImageResId;
        this.feedbackText = feedbackText;
        this.feedbackButton = feedbackButton;
        this.showFeedbackText = showFeedbackText;
        this.showFeedbackButton = showFeedbackButton;
        this.onClickListener = onClickListener;
        this.onRetryListener = onRetryListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_multiple_state_error_large, parent, false);
        return new ViewHolder(context, v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBindView(context);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public interface OnRetryListener {
        void onRetry();
    }
}

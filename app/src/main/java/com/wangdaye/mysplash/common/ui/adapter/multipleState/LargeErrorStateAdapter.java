package com.wangdaye.mysplash.common.ui.adapter.multipleState;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.helper.ImageHelper;

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

    private View.OnClickListener onClickListener;
    private OnRetryListener onRetryListener;

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_multiple_state_error_large_container)
        RelativeLayout container;

        @BindView(R.id.item_multiple_state_error_large_feedbackImg)
        AppCompatImageView feedbackImg;

        @BindView(R.id.item_multiple_state_error_large_feedbackTxt)
        TextView feedbackTxt;

        @BindView(R.id.item_multiple_state_error_large_feedbackBtn)
        TextView feedbackBtn;

        ViewHolder(Context context, View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            RelativeLayout container = ButterKnife.findById(
                    itemView, R.id.item_multiple_state_error_large_container);
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

        @OnClick({R.id.item_multiple_state_error_large_container}) void click() {
            if (onClickListener != null) {
                onClickListener.onClick(container);
            }
        }

        @OnClick({R.id.item_multiple_state_error_large_feedbackBtn}) void retry() {
            onRetryListener.onRetry();
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

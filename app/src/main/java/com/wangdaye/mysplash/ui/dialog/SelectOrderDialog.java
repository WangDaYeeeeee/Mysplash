package com.wangdaye.mysplash.ui.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.data.unslpash.api.PhotoApi;
import com.wangdaye.mysplash.utils.ValueUtils;

/**
 * Select order dialog.
 * */

public class SelectOrderDialog extends BottomSheetDialog implements View.OnClickListener {
    // widget
    private OnOrderSelectedListener listener;

    /** <br> life cycle. */

    public SelectOrderDialog(@NonNull Context context, String order, boolean normalMode) {
        super(context);
        this.initialize(order, normalMode);
    }

    @SuppressLint("SetTextI18n")
    private void initialize(String order, boolean normalMode) {
        setContentView(R.layout.dialog_select_order);

        ImageButton navigationIcon = (ImageButton) findViewById(R.id.dialog_select_order_navigationIcon);
        assert navigationIcon != null;
        navigationIcon.setOnClickListener(this);

        TextView nowText = (TextView) findViewById(R.id.dialog_select_order_nowTxt);
        assert nowText != null;
        nowText.setText("now : " + ValueUtils.getOrderName(getContext(), order));

        TextView latestTxt = (TextView) findViewById(R.id.dialog_select_order_latestTxt);
        assert latestTxt != null;
        latestTxt.setText(ValueUtils.getOrderName(getContext(), "latest"));

        TextView oldestTxt = (TextView) findViewById(R.id.dialog_select_order_oldestTxt);
        assert oldestTxt != null;
        oldestTxt.setText(ValueUtils.getOrderName(getContext(), "oldest"));

        TextView popularTxt = (TextView) findViewById(R.id.dialog_select_order_popularTxt);
        assert popularTxt != null;
        popularTxt.setText(ValueUtils.getOrderName(getContext(), "popular"));

        RelativeLayout[] orderContainers = new RelativeLayout[] {
                (RelativeLayout) findViewById(R.id.dialog_select_order_latest),
                (RelativeLayout) findViewById(R.id.dialog_select_order_oldest),
                (RelativeLayout) findViewById(R.id.dialog_select_order_popular)};
        for (RelativeLayout orderContainer : orderContainers) {
            orderContainer.setOnClickListener(this);
        }

        if (!normalMode) {
            Toast.makeText(
                    getContext(),
                    getContext().getString(R.string.feedback_random_effect),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /** <br> listener. */

    public interface OnOrderSelectedListener {
        void onOrderSelect(String order);
    }

    public void setOnOrderSelectedListener(OnOrderSelectedListener l) {
        this.listener = l;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_select_order_navigationIcon:
                dismiss();
                break;

            case R.id.dialog_select_order_latest:
                if (listener != null) {
                    listener.onOrderSelect(PhotoApi.ORDER_BY_LATEST);
                }
                dismiss();
                break;

            case R.id.dialog_select_order_oldest:
                if (listener != null) {
                    listener.onOrderSelect(PhotoApi.ORDER_BY_OLDEST);
                }
                dismiss();
                break;

            case R.id.dialog_select_order_popular:
                if (listener != null) {
                    listener.onOrderSelect(PhotoApi.ORDER_BY_POPULAR);
                }
                dismiss();
                break;
        }
    }
}

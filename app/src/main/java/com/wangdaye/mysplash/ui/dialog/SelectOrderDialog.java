package com.wangdaye.mysplash.ui.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.data.unslpash.api.PhotoApi;

/**
 * Select order dialog.
 * */

public class SelectOrderDialog extends BottomSheetDialog implements View.OnClickListener {
    // widget
    private OnOrderSelectedListener listener;

    /** <br> life cycle. */

    public SelectOrderDialog(@NonNull Context context, String order) {
        super(context);
        this.initialize(order);
    }

    public SelectOrderDialog(@NonNull Context context, @StyleRes int theme) {
        super(context, theme);
        this.initialize(PhotoApi.ORDER_BY_LATEST);
    }

    protected SelectOrderDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.initialize(PhotoApi.ORDER_BY_LATEST);
    }

    @SuppressLint("SetTextI18n")
    private void initialize(String order) {
        setContentView(R.layout.dialog_select_order);

        ImageButton navigationIcon = (ImageButton) findViewById(R.id.dialog_select_order_navigationIcon);
        assert navigationIcon != null;
        navigationIcon.setOnClickListener(this);

        TextView nowText = (TextView) findViewById(R.id.dialog_select_order_nowTxt);
        assert nowText != null;
        nowText.setText("now : " + order.toUpperCase());

        RelativeLayout[] orderContainers = new RelativeLayout[] {
                (RelativeLayout) findViewById(R.id.dialog_select_order_latest),
                (RelativeLayout) findViewById(R.id.dialog_select_order_oldest),
                (RelativeLayout) findViewById(R.id.dialog_select_order_popular)};
        for (RelativeLayout orderContainer : orderContainers) {
            orderContainer.setOnClickListener(this);
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

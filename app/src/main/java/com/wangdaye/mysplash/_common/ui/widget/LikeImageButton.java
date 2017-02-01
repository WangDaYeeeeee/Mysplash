package com.wangdaye.mysplash._common.ui.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.utils.helper.IntentHelper;
import com.wangdaye.mysplash._common.utils.manager.AuthManager;

/**
 * Like image button.
 * */

public class LikeImageButton extends ImageButton
        implements View.OnClickListener {
    // widget
    private OnLikeListener listener;

    // data
    private boolean likeState;
    private boolean animating;

    /** <br> life cycle. */

    public LikeImageButton(Context context) {
        super(context);
        this.initialize();
    }

    public LikeImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public LikeImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LikeImageButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initialize();
    }

    private void initialize() {
        this.animating = false;

        setOnClickListener(this);
    }

    /** <br> UI. */

    private void animHide() {
        Animation hide = AnimationUtils.loadAnimation(getContext(), R.anim.heart_hide);
        hide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // do nothing.
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setLikeState(!likeState);
                if (listener != null) {
                    listener.onClickLikeButton(likeState);
                }
                animShow();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // do nothing.
            }
        });
        startAnimation(hide);
    }

    private void animShow() {
        Animation show = AnimationUtils.loadAnimation(getContext(), R.anim.heart_show);
        show.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // do nothing.
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animating = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // do nothing.
            }
        });
        startAnimation(show);
    }

    public void initLikeState(boolean like) {
        clearAnimation();
        setScaleX(1);
        setScaleY(1);

        this.animating = false;
        this.likeState = like;
        if (likeState) {
            setImageResource(R.drawable.ic_item_heart_red);
        } else {
            setImageResource(R.drawable.ic_item_heart_outline);
        }
    }

    private void setLikeState(boolean like) {
        this.likeState = like;
        if (likeState) {
            setImageResource(R.drawable.ic_item_heart_red);
        } else {
            setImageResource(R.drawable.ic_item_heart_broken);
        }
    }

    /** <br> interface. */

    public interface OnLikeListener {
        void onClickLikeButton(boolean newLikeState);
    }

    public void setOnLikeListener(OnLikeListener l) {
        listener = l;
    }

    @Override
    public void onClick(View v) {
        if (!AuthManager.getInstance().isAuthorized()) {
            IntentHelper.startLoginActivity(Mysplash.getInstance().getTopActivity());
        } else if (!animating) {
            animating = true;
            animHide();
        }
    }
}
